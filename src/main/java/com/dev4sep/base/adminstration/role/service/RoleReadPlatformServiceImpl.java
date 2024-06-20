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
package com.dev4sep.base.adminstration.role.service;

import com.dev4sep.base.adminstration.role.data.RoleData;
import com.dev4sep.base.adminstration.role.exception.RoleNotFoundException;
import com.dev4sep.base.config.data.RequestParameters;
import com.dev4sep.base.config.service.Page;
import com.dev4sep.base.config.service.PaginationHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

/**
 * @author YISivlay
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleReadPlatformServiceImpl implements RoleReadPlatformService {

    private final PaginationHelper paginationHelper;
    private final JdbcTemplate jdbcTemplate;
    private final RoleMapper roleRowMapper = new RoleMapper();

    @Override
    public Page<RoleData> getAllRoles(final RequestParameters requestParameters) {
        var sql = "SELECT " + roleRowMapper.schema();

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
                sql += " ORDER BY r.id ASC ";
            }
        }

        return this.paginationHelper.fetchPage(this.jdbcTemplate, sql, new Object[]{}, this.roleRowMapper);
    }

    @Override
    public RoleData getOneRole(final Long id) {
        try {
            var sql = "SELECT " + roleRowMapper.schema() + " WHERE r.id = ? ";

            return this.jdbcTemplate.queryForObject(sql, this.roleRowMapper, id);
        } catch (final EmptyResultDataAccessException dve) {
            log.warn("Role with id {} not found", id, dve);
            throw new RoleNotFoundException(id);
        }
    }

    @Override
    public List<RoleData> getUserRoles(final Long id) {
        try {
            final var sql = "SELECT " + roleRowMapper.schema() + " WHERE r.id = ? ";
            return jdbcTemplate.query(sql, this.roleRowMapper, id);
        } catch (final EmptyResultDataAccessException e) {
            throw new RoleNotFoundException(id, e);
        }
    }

    protected static final class RoleMapper implements RowMapper<RoleData> {

        @Override
        public RoleData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final var id = rs.getLong("id");
            final var name = rs.getString("name");
            final var description = rs.getString("description");
            final var isDisabled = rs.getBoolean("is_disabled");

            return RoleData.builder()
                    .id(id)
                    .name(name)
                    .description(description)
                    .isDisabled(isDisabled)
                    .build();
        }

        public String schema() {
            return " r.* FROM tbl_role r ";
        }
    }
}
