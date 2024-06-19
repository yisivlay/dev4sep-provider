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

import com.dev4sep.base.config.ApplicationContextConfig;
import com.dev4sep.base.config.datasource.database.DatabasePasswordEncryptor;
import liquibase.change.custom.CustomTaskChange;
import liquibase.database.Database;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.CustomChangeException;
import liquibase.exception.SetupException;
import liquibase.exception.ValidationErrors;
import liquibase.resource.ResourceAccessor;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author YISivlay
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
public class TenantPasswordEncryptionTask implements CustomTaskChange {

    private final Map<String, Boolean> done = new ConcurrentHashMap<>();

    @Override
    public void execute(Database database) throws CustomChangeException {
        var databasePasswordEncryptor = ApplicationContextConfig.getBean("databasePasswordEncryptor", DatabasePasswordEncryptor.class);

        var dbConn = (JdbcConnection) database.getConnection(); // autocommit is false
        try (var selectStatement = dbConn.createStatement(); var updateStatement = dbConn.createStatement()) {

            try (var rs = selectStatement.executeQuery("SELECT id, schema_password FROM tenant_server_connections")) {
                while (rs.next()) {
                    var id = rs.getString("id");
                    if (!Boolean.TRUE.equals(done.get(id))) {
                        var schemaPassword = rs.getString("schema_password");
                        var encryptedPassword = databasePasswordEncryptor.encrypt(schemaPassword);

                        var updateSql = String.format(
                                "UPDATE tenant_server_connections SET schema_password = '%s', master_password_hash = '%s' WHERE id = %s",
                                encryptedPassword, databasePasswordEncryptor.getMasterPasswordHash(), id);
                        updateStatement.execute(updateSql);
                        done.put(id, true);
                    }
                }
            }
        } catch (Exception e) {
            throw new CustomChangeException(e);
        }
    }

    @Override
    public String getConfirmationMessage() {
        return null;
    }

    @Override
    public void setUp() throws SetupException {

    }

    @Override
    public void setFileOpener(ResourceAccessor resourceAccessor) {

    }

    @Override
    public ValidationErrors validate(Database database) {
        return null;
    }
}
