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
package com.dev4sep.base.adminstration.permission.service;

import com.dev4sep.base.adminstration.permission.data.PermissionData;
import com.dev4sep.base.config.data.RequestParameters;
import com.dev4sep.base.config.service.Page;
import com.dev4sep.base.config.service.PaginationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * @author YISivlay
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionReadPlatformServiceImpl implements PermissionReadPlatformService {

    private final JdbcTemplate jdbcTemplate;
    private final PaginationHelper paginationHelper;
    private final PermissionMapper rm = new PermissionMapper();

    @Override
    public Page<PermissionData> getAllPermissions(final RequestParameters requestParameters) {

        var sql = "SELECT " + rm.schema();
        if (requestParameters != null) {
            if (requestParameters.hasOrderBy()) {
                sql += " ORDER BY " + requestParameters.getOrderBy();
                if (requestParameters.hasSortOrder()) {
                    sql += " ".concat(requestParameters.getSortOrder());
                }
                if (requestParameters.hasLimit()) {
                    sql += " LIMIT " + requestParameters.getLimit();
                    if (requestParameters.hasOffset()) {
                        sql += " OFFSET " + requestParameters.getOffset();
                    }
                }
            } else {
                sql += " ORDER BY p.grouping, COALESCE(entity_name, ''), p.code ";
            }
        }

        return this.paginationHelper.fetchPage(this.jdbcTemplate, sql, new Object[]{}, this.rm);
    }

    @Override
    public Page<PermissionData> getAllMakerCheckerPermissions(RequestParameters requestParameters) {
        var sql = "SELECT " + rm.makerCheckerSchema();
        if (requestParameters != null) {
            if (requestParameters.hasOrderBy()) {
                sql += " ORDER BY " + requestParameters.getOrderBy();
                if (requestParameters.hasSortOrder()) {
                    sql += " ".concat(requestParameters.getSortOrder());
                }
                if (requestParameters.hasLimit()) {
                    sql += " LIMIT " + requestParameters.getLimit();
                    if (requestParameters.hasOffset()) {
                        sql += " OFFSET " + requestParameters.getOffset();
                    }
                }
            } else {
                sql += " ORDER BY p.grouping, coalesce(entity_name, ''), p.code ";
            }
        }

        return this.paginationHelper.fetchPage(this.jdbcTemplate, sql, new Object[]{}, this.rm);
    }

    public static final class PermissionMapper implements RowMapper<PermissionData> {

        public String schema() {
            return " p.grouping, " +
                    "p.code, " +
                    "p.entity_name, " +
                    "p.action_name, " +
                    "true as selected " +
                    "FROM tbl_permission p " +
                    "WHERE p.code NOT LIKE '%\\_CHECKER' ";
        }

        public String makerCheckerSchema() {
            return " p.grouping, " +
                    "p.code, " +
                    "p.entity_name, " +
                    "p.action_name, " +
                    "p.can_maker_checker as selected " +
                    "FROM tbl_permission p " +
                    "WHERE p.grouping != 'special' AND p.code NOT LIKE 'READ_%' AND code NOT LIKE '%\\_CHECKER' ";
        }

        @Override
        public PermissionData mapRow(ResultSet rs, int rowNum) throws SQLException {
            final var grouping = rs.getString("grouping");
            final var code = rs.getString("code");
            final var entityName = rs.getString("entity_name");
            final var actionName = rs.getString("action_name");
            final var selected = rs.getBoolean("selected");

            return PermissionData.builder()
                    .grouping(grouping)
                    .code(code)
                    .entityName(entityName)
                    .actionName(actionName)
                    .selected(selected)
                    .build();
        }
    }
}
