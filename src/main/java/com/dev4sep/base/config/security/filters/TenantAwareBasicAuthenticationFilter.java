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
