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

import com.dev4sep.base.config.security.data.PlatformRequestLog;
import com.dev4sep.base.config.security.filters.TenantAwareBasicAuthenticationFilter;
import com.dev4sep.base.config.security.service.BasicAuthTenantDetailsService;
import com.dev4sep.base.config.security.service.TenantAwareJpaPlatformUserDetailsService;
import com.dev4sep.base.config.serialization.ToApiJsonSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.authorization.AuthenticatedAuthorizationManager.fullyAuthenticated;
import static org.springframework.security.authorization.AuthorizationManagers.allOf;
import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * @author YISivlay
 */
@Configuration
@EnableMethodSecurity
@ConditionalOnProperty("dev4sep.security.basicauth.enabled")
public class SecurityConfig {

    private final ServerProperties serverProperties;
    private final BasicAuthTenantDetailsService basicAuthTenantDetailsService;
    private final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer;
    private final TenantAwareJpaPlatformUserDetailsService userDetailsService;

    @Autowired
    public SecurityConfig(final ServerProperties serverProperties,
                          final BasicAuthTenantDetailsService basicAuthTenantDetailsService,
                          final ToApiJsonSerializer<PlatformRequestLog> toApiJsonSerializer,
                          final TenantAwareJpaPlatformUserDetailsService userDetailsService) {
        this.serverProperties = serverProperties;
        this.basicAuthTenantDetailsService = basicAuthTenantDetailsService;
        this.toApiJsonSerializer = toApiJsonSerializer;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(antMatcher("/api/**")).authorizeHttpRequests((auth) -> {
                    auth.requestMatchers(antMatcher(HttpMethod.OPTIONS, "/api/**")).permitAll()
                            .requestMatchers(antMatcher(HttpMethod.POST, "/api/*/authentication")).permitAll()
                            .requestMatchers(antMatcher("/api/**"))
                            .access(allOf(fullyAuthenticated()));
                }).httpBasic((httpBasic) -> httpBasic.authenticationEntryPoint(basicAuthenticationEntryPoint()))
                .cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((smc) -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tenantAwareBasicAuthenticationFilter(), SecurityContextHolderFilter.class);
        if (serverProperties.getSsl().isEnabled()) {
            http.requiresChannel(channel -> channel.requestMatchers(antMatcher("/api/**")).requiresSecure());
        }
        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean(name = "customAuthenticationProvider")
    public DaoAuthenticationProvider authProvider() {
        var authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() {
        var providerManager = new ProviderManager(authProvider());
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean
    public BasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
        var basicAuthenticationEntryPoint = new BasicAuthenticationEntryPoint();
        basicAuthenticationEntryPoint.setRealmName("DEV4Sep Platform API");
        return basicAuthenticationEntryPoint;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public TenantAwareBasicAuthenticationFilter tenantAwareBasicAuthenticationFilter() throws Exception {
        var filter = new TenantAwareBasicAuthenticationFilter(
                authenticationManagerBean(),
                basicAuthenticationEntryPoint(),
                basicAuthTenantDetailsService,
                toApiJsonSerializer
        );
        filter.setRequestMatcher(antMatcher("/api/**"));
        return filter;
    }
}
