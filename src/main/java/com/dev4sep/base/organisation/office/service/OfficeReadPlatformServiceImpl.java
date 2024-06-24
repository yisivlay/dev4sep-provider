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
package com.dev4sep.base.organisation.office.service;

import com.dev4sep.base.config.data.RequestParameters;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.config.service.Page;
import com.dev4sep.base.config.service.PaginationHelper;
import com.dev4sep.base.organisation.office.data.OfficeData;
import com.dev4sep.base.organisation.office.exception.OfficeNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;
import java.util.List;

/**
 * @author YISivlay
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OfficeReadPlatformServiceImpl implements OfficeReadPlatformService {

    private static final String nameDecoratedBaseOnHierarchy = "concat(substring('........................................', 1, ((LENGTH(o.hierarchy) - LENGTH(REPLACE(o.hierarchy, '.', '')) - 1) * 4)), o.name)";

    public final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final PaginationHelper paginationHelper;

    @Override
    @Cacheable(value = "offices", key = "T(com.dev4sep.base.config.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat(#root.target.context.authenticatedUser().getOffice().getHierarchy()+'of')")
    public Page<OfficeData> getAllOffices(boolean includeAllOffices, RequestParameters requestParameters) {

        final var login = this.context.authenticatedUser();
        final List<Object> params = new LinkedList<>();
        final var rm = new OfficeMapper();

        final var loginHierarchy = login.getOffice().getHierarchy();
        final var hierarchySearchString = includeAllOffices ? "." + "%" : loginHierarchy + "%";

        var sql = "SELECT " + rm.schema() + " WHERE o.hierarchy LIKE ? ";
        params.add(hierarchySearchString);
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
                sql += " ORDER BY o.hierarchy ";
            }
        }
        Object[] where = params.toArray();
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sql, where, rm);
    }

    @Override
    @Cacheable(value = "officesById", key = "T(com.dev4sep.base.config.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat(#id)")
    public OfficeData getOneOffices(Long id) {
        try {
            final var rm = new OfficeMapper();
            var sql = "SELECT " + rm.schema() + " WHERE o.id = ? ";
            return this.jdbcTemplate.queryForObject(sql, rm, id);
        } catch (EmptyResultDataAccessException e) {
            throw new OfficeNotFoundException(id, e);
        }
    }

    private static class OfficeMapper implements RowMapper<OfficeData> {

        private String schema() {
            return "o.*, " + nameDecoratedBaseOnHierarchy + " as nameDecorated FROM tbl_office o ";
        }

        @Override
        public OfficeData mapRow(ResultSet rs, int rowNum) throws SQLException {

            final var id = rs.getLong("id");
            final var hierarchy = rs.getString("hierarchy");
            final var externalId = rs.getString("external_id");
            final var name = rs.getString("name");
            final var nameDecorated = rs.getString("nameDecorated");
            final var openingDate = rs.getDate("opening_date");

            return OfficeData.builder()
                    .id(id)
                    .name(name)
                    .nameDecorated(nameDecorated)
                    .externalId(externalId)
                    .openingDate(openingDate)
                    .hierarchy(hierarchy)
                    .build();
        }
    }
}
