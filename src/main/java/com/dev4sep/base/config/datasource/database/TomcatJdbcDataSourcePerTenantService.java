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
package com.dev4sep.base.config.datasource.database;

import com.dev4sep.base.config.ThreadLocalContextUtil;
import com.dev4sep.base.config.datasource.RoutingDataSourceService;
import com.dev4sep.base.config.datasource.database.domain.PlatformTenant;
import com.dev4sep.base.config.datasource.database.tenant.TenantDetailsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author YISivlay
 */
@Slf4j
@Service(value = "tomcatJdbcDataSourcePerTenantService")
public class TomcatJdbcDataSourcePerTenantService implements RoutingDataSourceService, ApplicationListener<ContextRefreshedEvent> {

    private static final Map<Long, DataSource> TENANT_TO_DATA_SOURCE_MAP = new ConcurrentHashMap<>();
    private final DataSource dataSource;
    private final TenantDetailsService tenantDetailsService;
    private final DataSourcePerTenantServiceFactory dataSourcePerTenantServiceFactory;

    @Autowired
    public TomcatJdbcDataSourcePerTenantService(@Qualifier("hikariDataSource") final DataSource dataSource,
                                                final TenantDetailsService tenantDetailsService,
                                                final DataSourcePerTenantServiceFactory dataSourcePerTenantServiceFactory) {
        this.dataSource = dataSource;
        this.tenantDetailsService = tenantDetailsService;
        this.dataSourcePerTenantServiceFactory = dataSourcePerTenantServiceFactory;
    }

    @Override
    public DataSource retrieveDataSource() {
        var ds = this.dataSource;
        final var tenant = ThreadLocalContextUtil.getTenant();
        if (tenant != null) {
            final var cn = tenant.getConnection();
            var cnk = cn.getConnectionId();
            ds = TENANT_TO_DATA_SOURCE_MAP.computeIfAbsent(cnk, (k) -> {
                return dataSourcePerTenantServiceFactory.createNewDataSourceFor(cn);
            });
        }
        return ds;
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        final var allTenants = tenantDetailsService.findAllTenants();
        allTenants.forEach(this::initializeDataSourceConnection);
    }

    private void initializeDataSourceConnection(PlatformTenant tenant) {
        log.debug("Initializing database connection for {}", tenant.getName());
        final var cn = tenant.getConnection();
        var cnk = cn.getConnectionId();
        TENANT_TO_DATA_SOURCE_MAP.computeIfAbsent(cnk, (k) -> {
            var tenantSpecificDataSource = dataSourcePerTenantServiceFactory.createNewDataSourceFor(cn);
            try (var connection = tenantSpecificDataSource.getConnection()) {
                var url = connection.getMetaData().getURL();
                log.debug("Established database connection with URL {}", url);
            } catch (SQLException e) {
                log.error("Error while initializing database connection for {}", tenant.getName(), e);
            }
            return tenantSpecificDataSource;
        });
        log.debug("Database connection for {} initialized", tenant.getName());

    }
}
