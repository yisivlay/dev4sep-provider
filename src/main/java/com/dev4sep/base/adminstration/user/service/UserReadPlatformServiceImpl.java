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
package com.dev4sep.base.adminstration.user.service;

import com.dev4sep.base.adminstration.role.service.RoleReadPlatformService;
import com.dev4sep.base.adminstration.user.data.UserData;
import com.dev4sep.base.config.data.RequestParameters;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.config.service.Page;
import com.dev4sep.base.config.service.PaginationHelper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Service;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

/**
 * @author YISivlay
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserReadPlatformServiceImpl implements UserReadPlatformService {

    private final PlatformSecurityContext context;
    private final JdbcTemplate jdbcTemplate;
    private final PaginationHelper paginationHelper;
    private final RoleReadPlatformService roleReadPlatformService;

    @Override
    public Page<UserData> getAllUsers(RequestParameters requestParameters) {
        final var login = this.context.authenticatedUser();

        final var params = new LinkedList<>();
        final var hierarchy = login.getOffice().getHierarchy();
        final var hierarchySearch = hierarchy + "%";

        final var mapper = new UserMapper(this.roleReadPlatformService);
        var sql = "SELECT " + mapper.schema();

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
        params.add(hierarchySearch);
        var where = params.toArray();
        return this.paginationHelper.fetchPage(this.jdbcTemplate, sql, where, mapper);

    }

    private record UserMapper(RoleReadPlatformService roleReadPlatformService) implements RowMapper<UserData> {

        @Override
        public UserData mapRow(final ResultSet rs, @SuppressWarnings("unused") final int rowNum) throws SQLException {

            final var id = rs.getLong("id");
            final var username = rs.getString("username");
            final var firstname = rs.getString("firstname");
            final var lastname = rs.getString("lastname");
            final var email = rs.getString("email");
            final var officeId = rs.getLong("office_id");
            final var officeName = rs.getString("office_name");
            final var passwordNeverExpire = rs.getBoolean("password_never_expires");
            final var isSelfServiceUser = rs.getBoolean("is_self_service_user");
            final var selectedRoles = this.roleReadPlatformService.getUserRoles(id);

            return UserData.builder()
                    .id(id)
                    .username(username)
                    .email(email)
                    .officeId(officeId)
                    .officeName(officeName)
                    .firstname(firstname)
                    .lastname(lastname)
                    .selectedRoles(selectedRoles)
                    .passwordNeverExpires(passwordNeverExpire)
                    .isSelfServiceUser(isSelfServiceUser)
                    .build();
        }

        public String schema() {
            return " u.id, " +
                    "u.username, " +
                    "u.firstname, " +
                    "u.lastname, " +
                    "u.email, " +
                    "u.password_never_expires, " +
                    "u.office_id, " +
                    "o.name as office_name, " +
                    "u.is_self_service_user " +
                    "FROM tbl_user u " +
                    "JOIN tbl_office o ON o.id = u.office_id " +
                    "WHERE o.hierarchy LIKE ? AND u.is_deleted = false ORDER BY u.username";
        }

    }
}
