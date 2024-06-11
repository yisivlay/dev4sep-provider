/**
 *    Copyright 2024 DEV4Sep
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.dev4sep.base.config.datasource.database.migration;

import com.dev4sep.base.config.Properties;
import com.dev4sep.base.config.boot.Profiles;
import com.dev4sep.base.config.datasource.database.domain.PlatformTenant;
import com.dev4sep.base.config.datasource.database.tenant.TenantDetailsService;
import liquibase.exception.LiquibaseException;
import liquibase.integration.spring.SpringLiquibase;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static org.apache.commons.collections4.CollectionUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author YISivlay
 */
@Slf4j
@Service
public class SpringLiquibaseMigrationService implements InitializingBean {

    private static final String TENANT_STORE_DB_CONTEXT = "tenant_store";
    private static final String DEFAULT_DB_CONTEXT = "default_store";
    private static final String INITIAL_SWITCH_CONTEXT = "initial_switch";

    private final DataSource dataSource;
    private final Environment environment;
    private final Properties properties;
    private final TenantDetailsService tenantDetailsService;
    private final TenantDataSourceFactory tenantDataSourceFactory;
    private final ExtendedSpringLiquibaseFactory extendedSpringLiquibaseFactory;
    private final DatabaseStateVerifier databaseStateVerifier;

    @Autowired
    public SpringLiquibaseMigrationService(@Qualifier("hikariDataSource") final DataSource dataSource,
                                           final Environment environment,
                                           final Properties properties,
                                           final TenantDetailsService tenantDetailsService,
                                           final TenantDataSourceFactory tenantDataSourceFactory,
                                           final ExtendedSpringLiquibaseFactory extendedSpringLiquibaseFactory,
                                           final DatabaseStateVerifier databaseStateVerifier) {
        this.dataSource = dataSource;
        this.environment = environment;
        this.properties = properties;
        this.tenantDetailsService = tenantDetailsService;
        this.tenantDataSourceFactory = tenantDataSourceFactory;
        this.extendedSpringLiquibaseFactory = extendedSpringLiquibaseFactory;
        this.databaseStateVerifier = databaseStateVerifier;
    }

    private boolean notLiquibaseOnlyMode() {
        List<String> activeProfiles = Arrays.asList(environment.getActiveProfiles());
        return !activeProfiles.contains(Profiles.LIQUIBASE_ONLY);
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        if (notLiquibaseOnlyMode()) {
            if (databaseStateVerifier.isLiquibaseDisabled() || !properties.getMode().isWriteEnabled()) {
                log.warn("Liquibase is disabled. Not upgrading any database.");
                if (!properties.getMode().isWriteEnabled()) {
                    log.warn("Liquibase is disabled because the current instance is configured as a non-write instance");
                }
                return;
            }
        }
        try {
            upgradeTenantStore();
            upgradeIndividualTenants();
        } catch (LiquibaseException e) {
            throw new RuntimeException("Error while migrating the schema", e);
        }
    }

    private void upgradeTenantStore() throws LiquibaseException {
        log.info("Upgrading tenant database at {}:{}", properties.getTenant().getHost(), properties.getTenant().getPort());
        logTenantStoreDetails();
        if (databaseStateVerifier.isFirstLiquibaseMigration(dataSource)) {
            var liquibase = extendedSpringLiquibaseFactory.create(dataSource, TENANT_STORE_DB_CONTEXT, INITIAL_SWITCH_CONTEXT);
            applyInitialLiquibase(dataSource, liquibase, "tenant store",
                    (ds) -> !databaseStateVerifier.isTenantStoreOnLatestUpgradableVersion(ds));
        }
        var liquibase = extendedSpringLiquibaseFactory.create(dataSource, TENANT_STORE_DB_CONTEXT);
        liquibase.afterPropertiesSet();
        log.info("Tenant database upgrade finished");
    }

    private void upgradeIndividualTenant(PlatformTenant tenant) throws LiquibaseException {
        log.info("Upgrade for default database {} has started", tenant.getTenantIdentifier());
        var tenantDataSource = tenantDataSourceFactory.create(tenant);
        if (databaseStateVerifier.isFirstLiquibaseMigration(tenantDataSource)) {
            var liquibase = extendedSpringLiquibaseFactory.create(tenantDataSource, DEFAULT_DB_CONTEXT, INITIAL_SWITCH_CONTEXT, tenant.getTenantIdentifier());
            applyInitialLiquibase(tenantDataSource, liquibase, tenant.getTenantIdentifier(),
                    (ds) -> !databaseStateVerifier.isTenantOnLatestUpgradableVersion(ds));
        }
        var tenantLiquibase = extendedSpringLiquibaseFactory.create(tenantDataSource, DEFAULT_DB_CONTEXT, tenant.getTenantIdentifier());
        tenantLiquibase.afterPropertiesSet();
        log.info("Upgrade for default database {} has finished", tenant.getTenantIdentifier());
    }

    private void upgradeIndividualTenants() throws LiquibaseException {
        log.info("Upgrading all tenants");
        var tenants = tenantDetailsService.findAllTenants();
        if (isNotEmpty(tenants)) {
            for (PlatformTenant tenant : tenants) {
                upgradeIndividualTenant(tenant);
            }
        }
        log.info("Tenant upgrades have finished");
    }

    private void applyInitialLiquibase(DataSource dataSource, ExtendedSpringLiquibase liquibase, String id,
                                       Function<DataSource, Boolean> isUpgradableFn) throws LiquibaseException {
        if (databaseStateVerifier.isFlywayPresent(dataSource)) {
            if (isUpgradableFn.apply(dataSource)) {
                log.error("Cannot proceed with upgrading database {}", id);
                log.error("It seems the database doesn't have the latest schema changes applied");
                throw new RuntimeException("Make sure to upgrade first and then to a newer version");
            }
            log.info("This is the first Liquibase migration for {}. We'll sync the changelog for you and then apply everything else", id);
            liquibase.changeLogSync();
            log.info("Liquibase changelog sync is complete");
        } else {
            liquibase.afterPropertiesSet();
        }
    }

    private void logTenantStoreDetails() {
        Properties.TenantProperties tenant = properties.getTenant();
        log.info("- dev4sep.tenant.username: {}", tenant.getUsername());
        log.info("- dev4sep.tenant.password: ****");
        log.info("- dev4sep.tenant.parameters: {}", tenant.getParameters());
        log.info("- dev4sep.tenant.timezone: {}", tenant.getTimezone());
        log.info("- dev4sep.tenant.description: {}", tenant.getDescription());
        log.info("- dev4sep.tenant.identifier: {}", tenant.getIdentifier());
        log.info("- dev4sep.tenant.name: {}", tenant.getName());

        String readOnlyUsername = tenant.getReadOnlyUsername();
        if (isNotBlank(readOnlyUsername)) {
            log.info("- dev4sep.tenant.readonly.username: {}", readOnlyUsername);
            log.info("- dev4sep.tenant.readonly.password: {}", isNotBlank(tenant.getReadOnlyPassword()) ? "****" : "");
            log.info("- dev4sep.tenant.readonly.parameters: {}", tenant.getReadOnlyParameters());
            log.info("- dev4sep.tenant.readonly.name: {}", tenant.getReadOnlyName());
        }

    }
}
