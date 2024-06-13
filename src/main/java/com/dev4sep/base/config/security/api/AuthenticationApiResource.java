/**
 * Copyright 2024 DEV4Sep
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dev4sep.base.config.security.api;

import com.dev4sep.base.adminstration.role.domain.Role;
import com.dev4sep.base.adminstration.user.domain.User;
import com.dev4sep.base.config.security.data.AuthenticateRequest;
import com.dev4sep.base.config.security.data.AuthenticatedUserData;
import com.dev4sep.base.config.security.service.SpringSecurityPlatformSecurityContext;
import com.dev4sep.base.config.serialization.ToApiJsonSerializer;
import com.google.gson.Gson;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * @author YISivlay
 */
@Component
@ConditionalOnProperty("dev4sep.security.basicauth.enabled")
@Path("/v1/authentication")
public class AuthenticationApiResource {

    private final DaoAuthenticationProvider customAuthenticationProvider;
    private final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService;
    private final SpringSecurityPlatformSecurityContext springSecurityPlatformSecurityContext;

    @Autowired
    public AuthenticationApiResource(@Qualifier("customAuthenticationProvider") final DaoAuthenticationProvider customAuthenticationProvider,
                                     final ToApiJsonSerializer<AuthenticatedUserData> apiJsonSerializerService,
                                     final SpringSecurityPlatformSecurityContext springSecurityPlatformSecurityContext) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.apiJsonSerializerService = apiJsonSerializerService;
        this.springSecurityPlatformSecurityContext = springSecurityPlatformSecurityContext;
    }

    @POST
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String authenticate(final String apiRequestBodyAsJson) {
        var request = new Gson().fromJson(apiRequestBodyAsJson, AuthenticateRequest.class);
        if (request == null) {
            throw new IllegalStateException("Invalid JSON in BODY (no longer URL param) of POST to /authentication: " + apiRequestBodyAsJson);
        }
        if (request.username == null || request.password == null) {
            throw new IllegalArgumentException("Username or Password is null in JSON of POST to /authentication: "
                    + apiRequestBodyAsJson + "; username=" + request.username + ", password=" + request.password);
        }

        final var authentication = new UsernamePasswordAuthenticationToken(request.username, request.password);
        final var authenticationCheck = this.customAuthenticationProvider.authenticate(authentication);

        final Collection<String> permissions = new ArrayList<>();
        var authenticatedUserData = new AuthenticatedUserData().setUsername(request.username).setPermissions(permissions);
        if (authenticationCheck.isAuthenticated()) {
            final var authorities = new ArrayList<>(authenticationCheck.getAuthorities());
            authorities.stream().map(GrantedAuthority::getAuthority).forEach(permissions::add);

            final var base64EncodedAuthenticationKey = Base64.getEncoder().encode((request.username + ":" + request.password).getBytes(StandardCharsets.UTF_8));
            final var principal = (User) authenticationCheck.getPrincipal();
            final var userRoles = principal.getRoles();
            final var roles = userRoles.stream().map(Role::toData).collect(Collectors.toList());

            final var officeId = principal.getOffice().getId();
            final var officeName = principal.getOffice().getName();

            var userId = principal.getId();
            if (this.springSecurityPlatformSecurityContext.doesPasswordHasToBeRenewed(principal)) {
                authenticatedUserData = new AuthenticatedUserData()
                        .setUsername(request.username)
                        .setUserId(userId)
                        .setBase64EncodedAuthenticationKey(new String(base64EncodedAuthenticationKey, StandardCharsets.UTF_8))
                        .setAuthenticated(true)
                        .setShouldRenewPassword(true);
            } else {
                authenticatedUserData = new AuthenticatedUserData()
                        .setUsername(request.username)
                        .setOfficeId(officeId)
                        .setOfficeName(officeName)
                        .setRoles(roles)
                        .setPermissions(permissions)
                        .setUserId(userId)
                        .setAuthenticated(true)
                        .setBase64EncodedAuthenticationKey(new String(base64EncodedAuthenticationKey, StandardCharsets.UTF_8));
            }
        }
        return this.apiJsonSerializerService.serialize(authenticatedUserData);
    }
}
