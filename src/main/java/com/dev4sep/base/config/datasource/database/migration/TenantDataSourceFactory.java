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
package com.dev4sep.base.config.datasource.database.migration;

import com.dev4sep.base.config.datasource.database.DatabasePasswordEncryptor;
import com.dev4sep.base.config.datasource.database.domain.PlatformTenant;
import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;

import static com.dev4sep.base.config.datasource.database.domain.PlatformTenantConnection.toJdbcUrl;
import static com.dev4sep.base.config.datasource.database.domain.PlatformTenantConnection.toProtocol;

/**
 * @author YISivlay
 */
@Component
public class TenantDataSourceFactory {

    private static final Logger LOG = LoggerFactory.getLogger(TenantDataSourceFactory.class);

    private final HikariDataSource tenantDataSource;
    private final DatabasePasswordEncryptor databasePasswordEncryptor;

    @Autowired
    public TenantDataSourceFactory(@Qualifier("hikariDataSource") final HikariDataSource tenantDataSource,
                                   final DatabasePasswordEncryptor databasePasswordEncryptor) {
        this.tenantDataSource = tenantDataSource;
        this.databasePasswordEncryptor = databasePasswordEncryptor;
    }

    public DataSource create(PlatformTenant tenant) {
        var dataSource = new HikariDataSource();
        dataSource.setDriverClassName(tenantDataSource.getDriverClassName());
        dataSource.setDataSourceProperties(tenantDataSource.getDataSourceProperties());
        dataSource.setMinimumIdle(tenantDataSource.getMinimumIdle());
        dataSource.setMaximumPoolSize(tenantDataSource.getMaximumPoolSize());
        dataSource.setIdleTimeout(tenantDataSource.getIdleTimeout());
        dataSource.setConnectionTestQuery(tenantDataSource.getConnectionTestQuery());

        var tenantConnection = tenant.getConnection();
        if (!databasePasswordEncryptor.isMasterPasswordHashValid(tenantConnection.getMasterPasswordHash())) {
            throw new IllegalArgumentException("Invalid master password");
        }
        dataSource.setUsername(tenantConnection.getSchemaUsername());
        dataSource.setPassword(databasePasswordEncryptor.decrypt(tenantConnection.getSchemaPassword()));
        var protocol = toProtocol(tenantDataSource);
        var tenantJdbcUrl = toJdbcUrl(protocol, tenantConnection.getSchemaServer(), tenantConnection.getSchemaServerPort(), tenantConnection.getSchemaName(), tenantConnection.getSchemaConnectionParameters());
        LOG.debug("JDBC URL for tenant {} is {}", tenant.getTenantIdentifier(), tenantJdbcUrl);
        dataSource.setJdbcUrl(tenantJdbcUrl);
        return dataSource;
    }
}
