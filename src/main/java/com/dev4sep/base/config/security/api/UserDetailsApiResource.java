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
package com.dev4sep.base.config.security.api;

import com.dev4sep.base.adminstration.role.data.RoleData;
import com.dev4sep.base.adminstration.role.domain.Role;
import com.dev4sep.base.adminstration.user.domain.User;
import com.dev4sep.base.config.security.data.AuthenticatedOauthUserData;
import com.dev4sep.base.config.security.data.CustomJwtAuthenticationToken;
import com.dev4sep.base.config.security.service.SpringSecurityPlatformSecurityContext;
import com.dev4sep.base.config.serialization.ToApiJsonSerializer;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

/**
 * @author YISivlay
 */
@Component
@ConditionalOnProperty("dev4sep.security.oauth.enabled")
@Path("/v1/userdetails")
@RequiredArgsConstructor
public class UserDetailsApiResource {

    private final SpringSecurityPlatformSecurityContext springSecurityPlatformSecurityContext;
    private final ToApiJsonSerializer<AuthenticatedOauthUserData> apiJsonSerializerService;

    @GET
    @Consumes({MediaType.APPLICATION_JSON})
    @Produces({MediaType.APPLICATION_JSON})
    public String fetchAuthenticatedUserData() {
        final SecurityContext context = SecurityContextHolder.getContext();
        if (context == null) {
            return null;
        }
        final CustomJwtAuthenticationToken authentication = (CustomJwtAuthenticationToken) context.getAuthentication();
        if (authentication == null) {
            return null;
        }
        final User principal = (User) authentication.getPrincipal();
        if (principal == null) {
            return null;
        }
        final Collection<String> permissions = new ArrayList<>();

        final Collection<GrantedAuthority> authorities = new ArrayList<>(authentication.getAuthorities());
        authorities.stream().map(GrantedAuthority::getAuthority).forEach(permissions::add);

        final Collection<RoleData> roles = new ArrayList<>();
        final Set<Role> userRoles = principal.getRoles();
        for (final Role role : userRoles) {
            roles.add(role.toData());
        }
        final Long officeId = principal.getOffice().getId();
        final String officeName = principal.getOffice().getName();

        AuthenticatedOauthUserData authenticatedUserData;
        if (this.springSecurityPlatformSecurityContext.doesPasswordHasToBeRenewed(principal)) {
            authenticatedUserData = new AuthenticatedOauthUserData()
                    .setUsername(principal.getUsername())
                    .setUserId(principal.getId())
                    .setAccessToken(authentication.getToken().getTokenValue())
                    .setAuthenticated(true)
                    .setShouldRenewPassword(true);
        } else {
            authenticatedUserData = new AuthenticatedOauthUserData()
                    .setUsername(principal.getUsername())
                    .setOfficeId(officeId)
                    .setOfficeName(officeName)
                    .setRoles(roles)
                    .setPermissions(permissions)
                    .setUserId(principal.getId())
                    .setAccessToken(authentication.getToken().getTokenValue())
                    .setAuthenticated(true);
        }
        return this.apiJsonSerializerService.serialize(authenticatedUserData);
    }
}
