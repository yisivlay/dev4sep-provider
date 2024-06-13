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
import com.dev4sep.base.organisation.office.data.OfficeData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * @author YISivlay
 */
@Service
public class OfficeReadPlatformServiceImpl implements OfficeReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public OfficeReadPlatformServiceImpl(final PlatformSecurityContext context,
                                         final JdbcTemplate jdbcTemplate) {
        this.context = context;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<OfficeData> getAllOffices(boolean includeAllOffices, RequestParameters requestParameters) {

        final User login = this.context.authenticatedUser();
        final String loginHierarchy = login.getOffice().getHierarchy();
        final String hierarchySearchString = includeAllOffices ? "." + "%" : loginHierarchy + "%";

        String sql = "SELECT * FROM tbl_office o ";
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

        return this.jdbcTemplate.query(sql, (rs, rowNum) -> {

            final Long id = rs.getLong("id");
            final String hierarchy = rs.getString("hierarchy");
            final String externalId = rs.getString("external_id");
            final String name = rs.getString("name");
            final Date openingDate = rs.getDate("opening_date");

            return OfficeData.builder()
                    .id(id)
                    .name(name)
                    .externalId(externalId)
                    .openingDate(openingDate)
                    .hierarchy(hierarchy)
                    .build();
        }, hierarchySearchString);
    }

    @Override
    public OfficeData getOneOffices(Long id) {
        String sql = "SELECT * FROM tbl_office o WHERE o.id = ? ";

        return this.jdbcTemplate.queryForObject(sql, (rs, rowNum) -> {

            final String hierarchy = rs.getString("hierarchy");
            final String externalId = rs.getString("external_id");
            final String name = rs.getString("name");
            final Date openingDate = rs.getDate("opening_date");

            return OfficeData.builder()
                    .id(id)
                    .name(name)
                    .externalId(externalId)
                    .openingDate(openingDate)
                    .hierarchy(hierarchy)
                    .build();
        }, id);
    }
}
