package com.dev4sep.base.config.security;

import com.dev4sep.base.config.Properties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.JdbcRegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.util.Set;
import java.util.UUID;

/**
 * @author YISivlay
 */
@Configuration
public class RegisteredClientConfig {

    @Bean
    JdbcTemplate secondDatasource() {
        DriverManagerDataSource dataSource = new DriverManagerDataSource();
        dataSource.setDriverClassName("org.mariadb.jdbc.Driver");
        dataSource.setUrl("jdbc:mariadb://localhost:3305/dev4sep-default");
        dataSource.setUsername("root");
        dataSource.setPassword("admin@2024!");

        return new JdbcTemplate(dataSource);
    }

    @Bean
    RegisteredClientRepository registeredClientRepository() {
        return new JdbcRegisteredClientRepository(secondDatasource());
    }

    @Bean
    public ApplicationRunner runner(RegisteredClientRepository repository) {
        return args -> {
            var clientId = "dev4sep-client";
            if (repository.findByClientId(clientId) == null) {
                repository.save(
                        RegisteredClient.withId(UUID.randomUUID().toString())
                                .clientId(clientId)
                                .clientSecret("{bcrypt}$2a$10$sbORCDSAhb.Dq99gSV5MNeHleBROnZXIa3S66YXK8o5ljhYzAGGAm")
                                .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
                                .authorizationGrantTypes(grantTypes -> grantTypes.addAll(Set.of(
                                        AuthorizationGrantType.CLIENT_CREDENTIALS,
                                        AuthorizationGrantType.AUTHORIZATION_CODE,
                                        AuthorizationGrantType.REFRESH_TOKEN)))
                                .redirectUri("https://127.0.0.1:8444/login/oauth2/code/dev4sep-client")
                                .scope(OidcScopes.OPENID)
                                .scope(OidcScopes.PROFILE)
                                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                                .build()
                );
            }
        };
    }
}
