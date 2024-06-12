package com.dev4sep.base.config.security.service;

import com.dev4sep.base.config.datasource.database.domain.PlatformTenant;
import com.dev4sep.base.config.datasource.database.tenant.TenantMapper;
import com.dev4sep.base.config.security.exception.InvalidTenantIdentifierException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;

/**
 * @author YISivlay
 */
@Service
public class BasicAuthTenantDetailsServiceJdbc implements BasicAuthTenantDetailsService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BasicAuthTenantDetailsServiceJdbc(@Qualifier("hikariDataSource") final DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public PlatformTenant loadTenantById(String tenantIdentifier, boolean isReport) {
        try {
            final TenantMapper rm = new TenantMapper(isReport);
            final String sql = "select  " + rm.schema() + " where t.identifier = ?";

            return this.jdbcTemplate.queryForObject(sql, rm, new Object[] { tenantIdentifier });
        } catch (final EmptyResultDataAccessException e) {
            throw new InvalidTenantIdentifierException("The tenant identifier: " + tenantIdentifier + " is not valid.", e);
        }
    }
}
