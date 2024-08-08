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
package com.dev4sep.base.config;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author YISivlay
 */
@Slf4j
@Configuration
public class KeycloakConfig {

    @Value("${dev4sep.keycloak.server-url}")
    private String serverUrl;

    @Getter
    @Value("${dev4sep.keycloak.realm}")
    private String realm;

    @Value("${dev4sep.keycloak.client-id}")
    private String clientId;

    @Value("${dev4sep.keycloak.client-secret}")
    private String clientSecret;

    @Value("${dev4sep.keycloak.grant-type}")
    private String grantType;

    @Value("${dev4sep.keycloak.username}")
    private String username;

    @Value("${dev4sep.keycloak.password}")
    private String password;

    @Bean
    Keycloak keycloak() {
        Keycloak builder = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realm)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(grantType)
                .username(username)
                .password(password)
                .build();
        log.info("Building Keycloak instance successfully");
        return builder;
    }

}
