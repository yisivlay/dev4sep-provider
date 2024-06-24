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
package com.dev4sep.base.config.security;

import com.dev4sep.base.config.cache.service.CacheWritePlatformService;
import com.dev4sep.base.config.configuration.domain.ConfigurationDomainService;
import com.dev4sep.base.config.exception.mapper.OAuth2ExceptionEntryPoint;
import com.dev4sep.base.config.security.data.CustomJwtAuthenticationToken;
import com.dev4sep.base.config.security.data.PlatformRequestLog;
import com.dev4sep.base.config.security.filters.TenantAwareOAuth2AuthenticationFilter;
import com.dev4sep.base.config.security.service.BasicAuthTenantDetailsService;
import com.dev4sep.base.config.security.service.TenantAwareJpaPlatformUserDetailsService;
import com.dev4sep.base.config.serialization.ToApiJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.OAuth2ErrorCodes;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.context.SecurityContextHolderFilter;

import java.util.Collection;

import static org.springframework.security.authorization.AuthenticatedAuthorizationManager.fullyAuthenticated;
import static org.springframework.security.authorization.AuthorizationManagers.allOf;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * @author YISivlay
 */
@Configuration
@EnableMethodSecurity
@ConditionalOnProperty("dev4sep.security.oauth.enabled")
public class OAuth2SecurityConfig {
    private static final JwtGrantedAuthoritiesConverter jwtGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final ServerProperties serverProperties;
    private final BasicAuthTenantDetailsService basicAuthTenantDetailsService;
    private final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer;
    private final CacheWritePlatformService cacheWritePlatformService;
    private final TenantAwareJpaPlatformUserDetailsService userDetailsService;
    private final ConfigurationDomainService configurationDomainService;

    @Autowired
    public OAuth2SecurityConfig(final ServerProperties serverProperties,
                                final BasicAuthTenantDetailsService basicAuthTenantDetailsService,
                                final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer,
                                final CacheWritePlatformService cacheWritePlatformService,
                                final TenantAwareJpaPlatformUserDetailsService userDetailsService,
                                final ConfigurationDomainService configurationDomainService) {
        this.serverProperties = serverProperties;
        this.basicAuthTenantDetailsService = basicAuthTenantDetailsService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.cacheWritePlatformService = cacheWritePlatformService;
        this.userDetailsService = userDetailsService;
        this.configurationDomainService = configurationDomainService;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(antMatcher("/api/**")).authorizeHttpRequests((auth) -> {
                    auth.requestMatchers(antMatcher(HttpMethod.OPTIONS, "/api/**")).permitAll()
                            .requestMatchers(antMatcher(HttpMethod.POST, "/api/*/authentication")).permitAll()
                            .requestMatchers(antMatcher(HttpMethod.POST, "/api/*/self/authentication")).permitAll()
                            .requestMatchers(antMatcher("/api/**"))
                            .access(allOf(fullyAuthenticated()));
                }).csrf(AbstractHttpConfigurer::disable)
                .exceptionHandling((e) -> e.authenticationEntryPoint(new OAuth2ExceptionEntryPoint()))
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(jwt -> jwt.jwtAuthenticationConverter(authenticationConverter()))
                        .authenticationEntryPoint(new OAuth2ExceptionEntryPoint()))
                .sessionManagement((smc) -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterAfter(tenantAwareOAuth2AuthenticationFilter(), SecurityContextHolderFilter.class);
        if (serverProperties.getSsl().isEnabled()) {
            http.requiresChannel(channel -> channel.requestMatchers(antMatcher("/api/**")).requiresSecure());
        }

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public BasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
        var basicAuthenticationEntryPoint = new BasicAuthenticationEntryPoint();
        basicAuthenticationEntryPoint.setRealmName("DEV4Sep Platform API");
        return basicAuthenticationEntryPoint;
    }

    public TenantAwareOAuth2AuthenticationFilter tenantAwareOAuth2AuthenticationFilter() {
        return new TenantAwareOAuth2AuthenticationFilter(
                basicAuthTenantDetailsService,
                toApiJsonSerializer,
                cacheWritePlatformService,
                configurationDomainService
        );
    }

    private Converter<Jwt, CustomJwtAuthenticationToken> authenticationConverter() {
        return jwt -> {
            try {
                UserDetails user = userDetailsService.loadUserByUsername(jwt.getSubject());
                jwtGrantedAuthoritiesConverter.setAuthorityPrefix("");
                Collection<GrantedAuthority> authorities = jwtGrantedAuthoritiesConverter.convert(jwt);
                return new CustomJwtAuthenticationToken(jwt, authorities, user);
            } catch (UsernameNotFoundException ex) {
                throw new OAuth2AuthenticationException(new OAuth2Error(OAuth2ErrorCodes.INVALID_TOKEN), ex);
            }
        };
    }
}
