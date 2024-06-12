package com.dev4sep.base.config.security.filters;

import com.dev4sep.base.config.ThreadLocalContextUtil;
import com.dev4sep.base.config.datasource.database.domain.PlatformTenant;
import com.dev4sep.base.config.security.service.BasicAuthTenantDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import java.io.IOException;

/**
 * @author YISivlay
 */
public class TenantAwareBasicAuthenticationFilter extends BasicAuthenticationFilter {

    private final BasicAuthTenantDetailsService basicAuthTenantDetailsService;

    public TenantAwareBasicAuthenticationFilter(AuthenticationManager authenticationManager,
                                                AuthenticationEntryPoint authenticationEntryPoint,
                                                BasicAuthTenantDetailsService basicAuthTenantDetailsService) {
        super(authenticationManager, authenticationEntryPoint);
        this.basicAuthTenantDetailsService = basicAuthTenantDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {
        final PlatformTenant tenant = basicAuthTenantDetailsService.loadTenantById("default", false);
        ThreadLocalContextUtil.setTenant(tenant);
        super.doFilterInternal(request, response, chain);
    }
}
