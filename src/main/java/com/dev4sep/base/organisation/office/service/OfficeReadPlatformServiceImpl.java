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

import com.dev4sep.base.adminstration.user.domain.User;
import com.dev4sep.base.config.data.RequestParameters;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.config.service.Page;
import com.dev4sep.base.config.service.PaginationHelper;
import com.dev4sep.base.organisation.office.data.OfficeData;
import com.dev4sep.base.organisation.office.exception.OfficeNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

/**
 * @author YISivlay
 */
@Service
public class OfficeReadPlatformServiceImpl implements OfficeReadPlatformService {

    private static final String nameDecoratedBaseOnHierarchy = "concat(substring('........................................', 1, ((LENGTH(o.hierarchy) - LENGTH(REPLACE(o.hierarchy, '.', '')) - 1) * 4)), o.name)";

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final PaginationHelper paginationHelper;

    @Autowired
    public OfficeReadPlatformServiceImpl(final PlatformSecurityContext context,
                                         final JdbcTemplate jdbcTemplate,
                                         final PaginationHelper paginationHelper) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
        this.paginationHelper = paginationHelper;
    }

    @Override
    public Page<OfficeData> getAllOffices(boolean includeAllOffices, RequestParameters requestParameters) {

        final User login = this.context.authenticatedUser();
        final List<Object> params = new LinkedList<>();
        final OfficeMapper rm = new OfficeMapper();

        final String loginHierarchy = login.getOffice().getHierarchy();
        final String hierarchySearchString = includeAllOffices ? "." + "%" : loginHierarchy + "%";

        String sql = "SELECT " + rm.schema() + " WHERE o.hierarchy LIKE ? ";
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
    public OfficeData getOneOffices(Long id) {
        try {
            final OfficeMapper rm = new OfficeMapper();
            String sql = "SELECT " + rm.schema() + " WHERE o.id = ? ";
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

            final Long id = rs.getLong("id");
            final String hierarchy = rs.getString("hierarchy");
            final String externalId = rs.getString("external_id");
            final String name = rs.getString("name");
            final String nameDecorated = rs.getString("nameDecorated");
            final Date openingDate = rs.getDate("opening_date");

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
