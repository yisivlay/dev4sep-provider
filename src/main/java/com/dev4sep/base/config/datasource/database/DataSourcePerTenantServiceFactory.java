/**
 * Copyright 2024 DEV4Sep
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dev4sep.base.config.datasource.database;

import com.dev4sep.base.config.Properties;
import com.dev4sep.base.config.datasource.database.domain.HikariDataSourceFactory;
import com.dev4sep.base.config.datasource.database.domain.PlatformTenantConnection;
import com.zaxxer.hikari.HikariConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.dev4sep.base.config.datasource.database.domain.PlatformTenantConnection.toJdbcUrl;
import static com.dev4sep.base.config.datasource.database.domain.PlatformTenantConnection.toProtocol;

/**
 * @author YISivlay
 */
@Slf4j
@Component
public class DataSourcePerTenantServiceFactory {

    private final DataSource dataSource;
    private final HikariConfig hikariConfig;
    private final Properties properties;
    private final HikariDataSourceFactory hikariDataSourceFactory;
    private final DatabasePasswordEncryptor databasePasswordEncryptor;

    @Autowired
    public DataSourcePerTenantServiceFactory(@Qualifier("hikariDataSource") final DataSource dataSource,
                                             final HikariConfig hikariConfig,
                                             final Properties properties,
                                             final HikariDataSourceFactory hikariDataSourceFactory,
                                             final DatabasePasswordEncryptor databasePasswordEncryptor) {
        this.dataSource = dataSource;
        this.hikariConfig = hikariConfig;
        this.properties = properties;
        this.hikariDataSourceFactory = hikariDataSourceFactory;
        this.databasePasswordEncryptor = databasePasswordEncryptor;
    }

    public DataSource createNewDataSourceFor(final PlatformTenantConnection cn) {
        if (!databasePasswordEncryptor.isMasterPasswordHashValid(cn.getMasterPasswordHash())) {
            throw new IllegalArgumentException("Invalid master password on tenant connection %d.".formatted(cn.getConnectionId()));
        }
        var protocol = toProtocol(dataSource);
        // Default properties for Writing
        var schemaServer = cn.getSchemaServer();
        var schemaPort = cn.getSchemaServerPort();
        var schemaName = cn.getSchemaName();
        var schemaUsername = cn.getSchemaUsername();
        var schemaPassword = cn.getSchemaPassword();
        var schemaConnectionParameters = cn.getSchemaConnectionParameters();
        // Properties to ReadOnly case
        if (properties.getMode().isReadOnlyMode()) {
            schemaServer = StringUtils.defaultIfBlank(cn.getReadOnlySchemaServer(), schemaServer);
            schemaPort = StringUtils.defaultIfBlank(cn.getReadOnlySchemaServerPort(), schemaPort);
            schemaName = StringUtils.defaultIfBlank(cn.getReadOnlySchemaName(), schemaName);
            schemaUsername = StringUtils.defaultIfBlank(cn.getReadOnlySchemaUsername(), schemaUsername);
            schemaPassword = StringUtils.defaultIfBlank(cn.getReadOnlySchemaPassword(), schemaPassword);
            schemaConnectionParameters = StringUtils.defaultIfBlank(cn.getReadOnlySchemaConnectionParameters(),
                    schemaConnectionParameters);
        }
        var jdbcUrl = toJdbcUrl(protocol, schemaServer, schemaPort, schemaName, schemaConnectionParameters);
        log.debug("{}", jdbcUrl);

        var config = new HikariConfig();
        config.setReadOnly(properties.getMode().isReadOnlyMode());
        config.setJdbcUrl(jdbcUrl);
        config.setPoolName(schemaName + "_pool");
        config.setUsername(schemaUsername);
        config.setPassword(databasePasswordEncryptor.decrypt(schemaPassword));
        config.setMinimumIdle(getMinPoolSize(cn));
        config.setMaximumPoolSize(getMaxPoolSize(cn));
        config.setValidationTimeout(cn.getValidationInterval());
        config.setDriverClassName(hikariConfig.getDriverClassName());
        config.setConnectionTestQuery(hikariConfig.getConnectionTestQuery());
        config.setAutoCommit(hikariConfig.isAutoCommit());

        // https://github.com/brettwooldridge/HikariCP/wiki/MBean-(JMX)-Monitoring-and-Management
        config.setRegisterMbeans(true);

        // https://github.com/brettwooldridge/HikariCP/wiki/MySQL-Configuration
        // These are the properties for each Tenant DB; the same configuration
        // is also in src/main/resources/META-INF/spring/hikariDataSource.xml
        // for the all Tenants DB -->
        config.setDataSourceProperties(hikariConfig.getDataSourceProperties());

        return hikariDataSourceFactory.create(config);
    }

    private int getMaxPoolSize(PlatformTenantConnection cn) {
        var configOverride = properties.getTenant().getConfig();
        if (configOverride.isMaxPoolSizeSet()) {
            var maxPoolSize = configOverride.getMaxPoolSize();
            log.info("Overriding tenant datasource maximum pool size configuration to {}", maxPoolSize);
            return maxPoolSize;
        } else {
            return cn.getMaxActive();
        }
    }

    private int getMinPoolSize(PlatformTenantConnection cn) {
        var configOverride = properties.getTenant().getConfig();
        if (configOverride.isMinPoolSizeSet()) {
            var minPoolSize = configOverride.getMinPoolSize();
            log.info("Overriding tenant datasource minimum pool size configuration to {}", minPoolSize);
            return minPoolSize;
        } else {
            return cn.getInitialSize();
        }
    }
}
