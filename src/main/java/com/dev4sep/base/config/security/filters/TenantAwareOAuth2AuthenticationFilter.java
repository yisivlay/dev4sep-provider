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
import com.dev4sep.base.config.cache.domain.CacheType;
import com.dev4sep.base.config.cache.service.CacheWritePlatformService;
import com.dev4sep.base.config.configuration.domain.ConfigurationDomainService;
import com.dev4sep.base.config.datasource.database.domain.PlatformTenant;
import com.dev4sep.base.config.security.data.PlatformRequestLog;
import com.dev4sep.base.config.security.exception.InvalidTenantIdentifierException;
import com.dev4sep.base.config.security.service.BasicAuthTenantDetailsService;
import com.dev4sep.base.config.serialization.ToApiJsonSerializer;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.StopWatch;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author YISivlay
 */
@Slf4j
@RequiredArgsConstructor
public class TenantAwareOAuth2AuthenticationFilter extends GenericFilterBean {

    private static final AtomicBoolean FIRST_PROCESSED_REQUEST = new AtomicBoolean();

    private final BasicAuthTenantDetailsService basicAuthTenantDetailsService;
    private final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer;
    private final CacheWritePlatformService cacheWritePlatformService;
    private final ConfigurationDomainService configurationDomainService;

    @Override
    @SuppressFBWarnings("SLF4J_SIGN_ONLY_FORMAT")
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain) throws IOException, ServletException {

        final HttpServletRequest request = (HttpServletRequest) req;
        final HttpServletResponse response = (HttpServletResponse) res;

        final StopWatch task = new StopWatch();
        task.start();
        try {
            ThreadLocalContextUtil.reset();

            response.setHeader("Access-Control-Allow-Origin", "*");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
            final String reqHead = request.getHeader("Access-Control-Request-Headers");

            if (null != reqHead && !reqHead.isEmpty()) {
                response.setHeader("Access-Control-Allow-Headers", reqHead);
            }
            if (!"OPTIONS".equalsIgnoreCase(request.getMethod())) {
                String tenantIdentifier = request.getHeader("DEV4Sep-Platform-TenantId");
                if (StringUtils.isBlank(tenantIdentifier)) {
                    tenantIdentifier = request.getParameter("tenantIdentifier");
                }
                if (tenantIdentifier == null) {
                    throw new InvalidTenantIdentifierException("No tenant identifier found: Add request header of 'DEV4Sep-Platform-TenantId' or add the parameter 'tenantIdentifier' to query string of request URL.");
                }
                String pathInfo = request.getRequestURI();
                boolean isReportRequest = false;
                if (pathInfo != null && pathInfo.contains("report")) {
                    isReportRequest = true;
                }
                final PlatformTenant tenant = this.basicAuthTenantDetailsService.loadTenantById(tenantIdentifier, isReportRequest);
                ThreadLocalContextUtil.setTenant(tenant);
                String authToken = request.getHeader("Authorization");
                if (authToken != null && authToken.startsWith("bearer ")) {
                    ThreadLocalContextUtil.setAuthToken(authToken.replaceFirst("bearer ", ""));
                }
                if (!FIRST_PROCESSED_REQUEST.get()) {
                    final String baseUrl = request.getRequestURL().toString().replace(request.getRequestURI(),
                            request.getContextPath() + "/api/v1");
                    System.setProperty("baseUrl", baseUrl);

                    final boolean ehcacheEnabled = configurationDomainService.isEhcacheEnabled();
                    if (ehcacheEnabled) {
                        cacheWritePlatformService.switchToCache(CacheType.SINGLE_NODE);
                    } else {
                        cacheWritePlatformService.switchToCache(CacheType.NO_CACHE);
                    }
                    FIRST_PROCESSED_REQUEST.set(true);
                }
                chain.doFilter(request, response);
            }
        } catch (final InvalidTenantIdentifierException e) {
            SecurityContextHolder.getContext().setAuthentication(null);

            response.addHeader("WWW-Authenticate", "Basic realm=\"" + "DEV4Sep Platform API" + "\"");
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } finally {
            ThreadLocalContextUtil.reset();
            task.stop();
            final PlatformRequestLog logRequest = PlatformRequestLog.from(task, request);
            log.info("{}", toApiJsonSerializer.serialize(logRequest));
        }
    }
}
