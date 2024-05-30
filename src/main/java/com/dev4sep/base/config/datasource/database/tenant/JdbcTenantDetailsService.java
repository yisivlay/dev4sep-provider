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
package com.dev4sep.base.config.datasource.database.tenant;

import com.dev4sep.base.config.datasource.database.domain.PlatformTenant;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.util.List;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * @author YISivlay
 */
@Service
public class JdbcTenantDetailsService implements TenantDetailsService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JdbcTenantDetailsService(@Qualifier("hikariDataSource") final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public PlatformTenant loadTenantById(String tenantIdentifier) {
        if (isBlank(tenantIdentifier)) {
            throw new IllegalArgumentException("tenantIdentifier cannot be blank");
        }
        try {
            final var rm = new TenantMapper(false);
            final var sql = "SELECT " + rm.schema() + " where t.identifier = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, tenantIdentifier);
        } catch (final EmptyResultDataAccessException e) {
            throw new RuntimeException("The tenant identifier: " + tenantIdentifier + " is not valid.", e);
        }
    }

    @Override
    public List<PlatformTenant> findAllTenants() {
        final var rm = new TenantMapper(false);
        final var sql = "SELECT  " + rm.schema();
        return this.jdbcTemplate.query(sql, rm);
    }
}
