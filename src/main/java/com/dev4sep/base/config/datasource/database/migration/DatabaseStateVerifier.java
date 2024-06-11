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

import com.dev4sep.base.config.datasource.database.DatabaseIndependentQueryService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.Map;
import java.util.Objects;

/**
 * @author YISivlay
 */
@Component
@RequiredArgsConstructor
public class DatabaseStateVerifier {

    private static final int TENANT_STORE_LATEST_FLYWAY_VERSION = 1;
    private static final String TENANT_STORE_LATEST_FLYWAY_SCRIPT_NAME = "V1__initial_tenant.sql";
    private static final int TENANT_STORE_LATEST_FLYWAY_SCRIPT_CHECKSUM = -43094919;
    private static final int TENANT_LATEST_FLYWAY_VERSION = 1;
    private static final String TENANT_LATEST_FLYWAY_SCRIPT_NAME = "V1__initial_tenant.sql";
    private static final int TENANT_LATEST_FLYWAY_SCRIPT_CHECKSUM = 1102395052;

    private final LiquibaseProperties liquibaseProperties;
    private final DatabaseIndependentQueryService dbQueryService;

    public boolean isFirstLiquibaseMigration(DataSource dataSource) {
        boolean databaseChangelogTableExists = dbQueryService.isTablePresent(dataSource, "DATABASECHANGELOG");
        return !databaseChangelogTableExists;
    }

    public boolean isFlywayPresent(DataSource dataSource) {
        return dbQueryService.isTablePresent(dataSource, "schema_version");
    }

    public boolean isLiquibaseDisabled() {
        return !liquibaseProperties.isEnabled();
    }

    private boolean isOnLatestFlywayVersion(int version, String scriptName, int checksum, DataSource dataSource) {
        Map<String, Object> paramMap = Map.of(
                "latestFlywayVersion", version,
                "latestFlywayScriptName", scriptName,
                "latestFlywayScriptChecksum", checksum
        );

        var jdbcTemplate = new NamedParameterJdbcTemplate(dataSource);
        var result = jdbcTemplate.queryForObject(
                "SELECT COUNT(script) FROM schema_version " + "WHERE version = :latestFlywayVersion "
                        + "AND script = :latestFlywayScriptName " + "AND checksum = :latestFlywayScriptChecksum " + "AND success = 1",
                paramMap, Integer.class);
        return Objects.equals(result, 1);
    }

    public boolean isTenantOnLatestUpgradableVersion(DataSource dataSource) {
        return isOnLatestFlywayVersion(TENANT_LATEST_FLYWAY_VERSION, TENANT_LATEST_FLYWAY_SCRIPT_NAME, TENANT_LATEST_FLYWAY_SCRIPT_CHECKSUM,
                dataSource);
    }

    public boolean isTenantStoreOnLatestUpgradableVersion(DataSource dataSource) {

        return isOnLatestFlywayVersion(TENANT_STORE_LATEST_FLYWAY_VERSION, TENANT_STORE_LATEST_FLYWAY_SCRIPT_NAME,
                TENANT_STORE_LATEST_FLYWAY_SCRIPT_CHECKSUM, dataSource);
    }
}
