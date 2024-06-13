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
import com.dev4sep.base.config.security.data.PlatformRequestLog;
import com.dev4sep.base.config.security.exception.InvalidTenantIdentifierException;
import com.dev4sep.base.config.security.service.BasicAuthTenantDetailsService;
import com.dev4sep.base.config.serialization.ToApiJsonSerializer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;
import org.springframework.security.web.util.matcher.AnyRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import java.io.IOException;

/**
 * @author YISivlay
 */
@Slf4j
public class TenantAwareBasicAuthenticationFilter extends BasicAuthenticationFilter {

    private static boolean FIRST_REQUEST_PROCESSED = false;

    private final BasicAuthTenantDetailsService basicAuthTenantDetailsService;
    private final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer;

    @Setter
    private RequestMatcher requestMatcher = AnyRequestMatcher.INSTANCE;

    public TenantAwareBasicAuthenticationFilter(final AuthenticationManager authenticationManager,
                                                final AuthenticationEntryPoint authenticationEntryPoint,
                                                final BasicAuthTenantDetailsService basicAuthTenantDetailsService,
                                                final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer) {
        super(authenticationManager, authenticationEntryPoint);
        this.basicAuthTenantDetailsService = basicAuthTenantDetailsService;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws IOException, ServletException {

        final var task = new StopWatch();
        task.start();

        try {
            ThreadLocalContextUtil.reset();
            if ("OPTIONS".equalsIgnoreCase(request.getMethod())) {
                chain.doFilter(request, response);
            } else {
                if (requestMatcher.matches(request)) {
                    var tenantIdentifier = request.getHeader("DEV4Sep-Platform-TenantId");
                    if (StringUtils.isBlank(tenantIdentifier)) {
                        tenantIdentifier = request.getParameter("tenantIdentifier");
                    }
                    if (tenantIdentifier == null) {
                        throw new InvalidTenantIdentifierException("No tenant identifier found: Add request header of 'DEV4Sep-Platform-TenantId' or add the parameter 'tenantIdentifier' to query string of request URL.");
                    }
                    var pathInfo = request.getRequestURI();
                    var isReportRequest = pathInfo != null && pathInfo.contains("report");

                    final var tenant = basicAuthTenantDetailsService.loadTenantById(tenantIdentifier, isReportRequest);
                    ThreadLocalContextUtil.setTenant(tenant);
                    var authToken = request.getHeader("Authorization");
                    if (authToken != null && authToken.startsWith("Basic ")) {
                        ThreadLocalContextUtil.setAuthToken(authToken.replaceFirst("Basic ", ""));
                    }
                    if (!FIRST_REQUEST_PROCESSED) {
                        final String baseUrl = request.getRequestURL().toString().replace(request.getPathInfo(), "/");
                        System.setProperty("baseUrl", baseUrl);
                        TenantAwareBasicAuthenticationFilter.FIRST_REQUEST_PROCESSED = true;
                    }
                }
                super.doFilterInternal(request, response, chain);
            }
        } catch (final InvalidTenantIdentifierException e) {
            SecurityContextHolder.getContext().setAuthentication(null);

            response.addHeader("WWW-Authenticate", "Basic realm=\"" + "DEV4Sep Platform API" + "\"");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            ThreadLocalContextUtil.reset();
            task.stop();
            final var msg = PlatformRequestLog.from(task, request);
            log.info("{}", toApiJsonSerializer.serialize(msg));
        }

    }
}
