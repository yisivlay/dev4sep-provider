package com.dev4sep.base.config.security;

import com.dev4sep.base.config.security.filters.TenantAwareBasicAuthenticationFilter;
import com.dev4sep.base.config.security.service.BasicAuthTenantDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationEntryPoint;
import org.springframework.security.web.context.SecurityContextHolderFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;

/**
 * @author YISivlay
 */
@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Autowired
    private BasicAuthTenantDetailsService basicAuthTenantDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .securityMatcher(antMatcher("/api/**")).authorizeHttpRequests((auth) -> {
                    auth.requestMatchers(antMatcher(HttpMethod.OPTIONS, "/api/**")).permitAll()
                            .requestMatchers(antMatcher(HttpMethod.GET, "/api/*/offices")).permitAll();
                }).httpBasic((httpBasic) -> httpBasic.authenticationEntryPoint(basicAuthenticationEntryPoint()))
                .cors(Customizer.withDefaults()).csrf(AbstractHttpConfigurer::disable)
                .sessionManagement((smc) -> smc.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(tenantAwareBasicAuthenticationFilter(), SecurityContextHolderFilter.class);
        return http.build();
    }

    @Bean
    public UserDetailsService userDetailsService() {
        var user = User.withDefaultPasswordEncoder()
                .username("user")
                .password("password")
                .roles("USER")
                .build();

        return new InMemoryUserDetailsManager(user);
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }

    @Bean
    public DaoAuthenticationProvider authProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService());
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        ProviderManager providerManager = new ProviderManager(authProvider());
        providerManager.setEraseCredentialsAfterAuthentication(false);
        return providerManager;
    }

    @Bean
    public BasicAuthenticationEntryPoint basicAuthenticationEntryPoint() {
        BasicAuthenticationEntryPoint basicAuthenticationEntryPoint = new BasicAuthenticationEntryPoint();
        basicAuthenticationEntryPoint.setRealmName("DEV4Sep Platform API");
        return basicAuthenticationEntryPoint;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of("*"));
        configuration.setAllowedMethods(List.of("*"));
        configuration.setAllowCredentials(true);
        configuration.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    public TenantAwareBasicAuthenticationFilter tenantAwareBasicAuthenticationFilter() throws Exception {
        return new TenantAwareBasicAuthenticationFilter(authenticationManagerBean(), basicAuthenticationEntryPoint(), basicAuthTenantDetailsService);
    }
}
