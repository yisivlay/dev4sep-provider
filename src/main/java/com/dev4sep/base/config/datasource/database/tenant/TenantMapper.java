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
package com.dev4sep.base.config.datasource.database.tenant;

import com.dev4sep.base.config.datasource.database.domain.PlatformTenant;
import com.dev4sep.base.config.datasource.database.domain.PlatformTenantConnection;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author YISivlay
 */
public final class TenantMapper implements RowMapper<PlatformTenant> {

    private static final String TENANT_SERVER_CONNECTION_BUILDER = " t.id, ts.id as connectionId , "
            + " t.timezone_id as timezoneId , t.name,t.identifier, ts.schema_name as schemaName, ts.schema_server as schemaServer,"
            + " ts.schema_server_port as schemaServerPort, ts.schema_connection_parameters as schemaConnectionParameters, ts.auto_update as autoUpdate,"
            + " ts.schema_username as schemaUsername, ts.schema_password as schemaPassword , ts.pool_initial_size as initialSize,"
            + " ts.pool_validation_interval as validationInterval, ts.pool_remove_abandoned as removeAbandoned, ts.pool_remove_abandoned_timeout as removeAbandonedTimeout,"
            + " ts.pool_log_abandoned as logAbandoned, ts.pool_abandon_when_percentage_full as abandonedWhenPercentageFull, ts.pool_test_on_borrow as testOnBorrow,"
            + " ts.pool_max_active as poolMaxActive, ts.pool_min_idle as poolMinIdle, ts.pool_max_idle as poolMaxIdle,"
            + " ts.pool_suspect_timeout as poolSuspectTimeout, ts.pool_time_between_eviction_runs_millis as poolTimeBetweenEvictionRunsMillis,"
            + " ts.pool_min_evictable_idle_time_millis as poolMinEvictableIdleTimeMillis,"
            + " ts.readonly_schema_server as readOnlySchemaServer, " + " ts.readonly_schema_server_port as readOnlySchemaServerPort, "
            + " ts.readonly_schema_name as readOnlySchemaName, " + " ts.readonly_schema_username as readOnlySchemaUsername, "
            + " ts.readonly_schema_password as readOnlySchemaPassword, "
            + " ts.readonly_schema_connection_parameters as readOnlySchemaConnectionParameters, "
            + " ts.master_password_hash as masterPasswordHash " + " from tenants t left join tenant_server_connections ts ";
    private final boolean isReport;
    private final StringBuilder sqlBuilder = new StringBuilder(TENANT_SERVER_CONNECTION_BUILDER);

    public TenantMapper(boolean isReport) {
        this.isReport = isReport;
    }

    public String schema() {
        if (this.isReport) {
            this.sqlBuilder.append(" on t.report_Id = ts.id");
        } else {
            this.sqlBuilder.append(" on t.oltp_Id = ts.id");
        }
        return this.sqlBuilder.toString();
    }

    @Override
    public PlatformTenant mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {
        final var id = rs.getLong("id");
        final var tenantIdentifier = rs.getString("identifier");
        final var name = rs.getString("name");
        final var timezoneId = rs.getString("timezoneId");
        final var connection = getDBConnection(rs);
        return new PlatformTenant(id, tenantIdentifier, name, timezoneId, connection);
    }

    // gets the DB connection
    private PlatformTenantConnection getDBConnection(ResultSet rs) throws SQLException {

        final var connectionId = rs.getLong("connectionId");
        final var schemaName = rs.getString("schemaName");
        final var schemaServer = rs.getString("schemaServer");
        final var schemaServerPort = rs.getString("schemaServerPort");
        final var schemaConnectionParameters = rs.getString("schemaConnectionParameters");
        final var schemaUsername = rs.getString("schemaUsername");
        final var schemaPassword = rs.getString("schemaPassword");
        final var readOnlySchemaName = rs.getString("readOnlySchemaName");
        final var readOnlySchemaServer = rs.getString("readOnlySchemaServer");
        final var readOnlySchemaServerPort = rs.getString("readOnlySchemaServerPort");
        final var readOnlySchemaUsername = rs.getString("readOnlySchemaUsername");
        final var readOnlySchemaPassword = rs.getString("readOnlySchemaPassword");
        final var readOnlySchemaConnectionParameters = rs.getString("readOnlySchemaConnectionParameters");

        final var autoUpdateEnabled = rs.getBoolean("autoUpdate");
        final var initialSize = rs.getInt("initialSize");
        final var testOnBorrow = rs.getBoolean("testOnBorrow");
        final var validationInterval = rs.getLong("validationInterval");
        final var removeAbandoned = rs.getBoolean("removeAbandoned");
        final var removeAbandonedTimeout = rs.getInt("removeAbandonedTimeout");
        final var logAbandoned = rs.getBoolean("logAbandoned");
        final var abandonWhenPercentageFull = rs.getInt("abandonedWhenPercentageFull");
        final var maxActive = rs.getInt("poolMaxActive");
        final var minIdle = rs.getInt("poolMinIdle");
        final var maxIdle = rs.getInt("poolMaxIdle");
        final var suspectTimeout = rs.getInt("poolSuspectTimeout");
        final var timeBetweenEvictionRunsMillis = rs.getInt("poolTimeBetweenEvictionRunsMillis");
        final var minEvictableIdleTimeMillis = rs.getInt("poolMinEvictableIdleTimeMillis");
        final var masterPasswordHash = rs.getString("masterPasswordHash");

        return new PlatformTenantConnection(connectionId, schemaName, schemaServer, schemaServerPort, schemaConnectionParameters,
                schemaUsername, schemaPassword, autoUpdateEnabled, initialSize, validationInterval, removeAbandoned, removeAbandonedTimeout,
                logAbandoned, abandonWhenPercentageFull, maxActive, minIdle, maxIdle, suspectTimeout, timeBetweenEvictionRunsMillis,
                minEvictableIdleTimeMillis, testOnBorrow, readOnlySchemaServer, readOnlySchemaServerPort, readOnlySchemaName,
                readOnlySchemaUsername, readOnlySchemaPassword, readOnlySchemaConnectionParameters, masterPasswordHash);
    }
}
