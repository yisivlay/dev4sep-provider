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
package com.dev4sep.base.config.keycloak.service;

import com.dev4sep.base.adminstration.role.domain.Role;
import com.dev4sep.base.adminstration.user.domain.User;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RolesResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * @author YISivlay
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class KeycloakServiceImpl implements KeycloakService {

    private final Keycloak keycloak;

    @Value("${dev4sep.keycloak.realm}")
    private String realm;

    @Override
    public void createUser(User user, String rawPassword) {

        UserRepresentation userRepresentation = getUserRepresentation(user, rawPassword);
        UsersResource usersResource = getUsersResource();

        String userId = getUserId(user);
        if (userId != null) {
            usersResource.delete(userId);
        }

        Response response = usersResource.create(userRepresentation);
        if (response.getStatus() != 201) {
            log.info("Status code {} ", response.getStatus());
            throw new RuntimeException("Status code " + response.getStatus());
        }
        log.info("New user has been created");

        String roleName = String.valueOf(user.getRoles().stream().map(Role::getName).findFirst());
        assignRole(user, roleName);
        log.info("New user has been assigned to role {}", roleName);
    }

    private static @NotNull UserRepresentation getUserRepresentation(User user, String rawPassword) {
        UserRepresentation userRepresentation = new UserRepresentation();
        userRepresentation.setEnabled(true);
        userRepresentation.setFirstName(user.getFirstname());
        userRepresentation.setLastName(user.getLastname());
        userRepresentation.setUsername(user.getUsername());
        userRepresentation.setEmail(user.getEmail());
        userRepresentation.setEmailVerified(true);

        CredentialRepresentation credentialRepresentation = new CredentialRepresentation();
        credentialRepresentation.setValue(rawPassword);
        credentialRepresentation.setType(CredentialRepresentation.PASSWORD);

        userRepresentation.setCredentials(List.of(credentialRepresentation));
        return userRepresentation;
    }

    @Override
    public void sendVerificationEmail(String userId) {
        UsersResource usersResource = getUsersResource();
        usersResource.get(userId).sendVerifyEmail();

    }

    @Override
    public void deleteUser(User user) {
        UsersResource usersResource = getUsersResource();
        String userId = getUserId(user);
        usersResource.delete(userId);
    }

    @Override
    public void forgotPassword(User user) {
        UsersResource usersResource = getUsersResource();
        String userId = getUserId(user);
        UserResource userResource = usersResource.get(userId);
        userResource.executeActionsEmail(List.of("UPDATE_PASSWORD"));

    }

    @Override
    public UserResource getUser(User user) {
        UsersResource usersResource = getUsersResource();
        String userId = getUserId(user);
        return usersResource.get(userId);
    }

    @Override
    public void assignRole(User user, String roleName) {
        UserResource userResource = getUser(user);
        RolesResource rolesResource = getRolesResource();
        RoleRepresentation representation = rolesResource.get(roleName).toRepresentation();
        userResource.roles().realmLevel().add(Collections.singletonList(representation));
    }

    @Override
    public void deleteRoleFromUser(User user, String roleName) {
        UserResource userResource = getUser(user);
        RolesResource rolesResource = getRolesResource();
        RoleRepresentation representation = rolesResource.get(roleName).toRepresentation();
        userResource.roles().realmLevel().remove(Collections.singletonList(representation));
    }

    @Override
    public List<RoleRepresentation> getUserRoles(User user) {
        return getUser(user).roles().realmLevel().listAll();
    }

    @Override
    public List<GroupRepresentation> getUserGroups(User user) {
        return getUser(user).groups();
    }

    @Override
    public void assignGroup(User user, String groupId) {
        UserResource userResource = getUser(user);
        userResource.joinGroup(groupId);
    }

    @Override
    public void deleteGroupFromUser(User user, String groupId) {
        UserResource userResource = getUser(user);
        userResource.leaveGroup(groupId);

    }

    private UsersResource getUsersResource() {
        return keycloak.realm(realm).users();
    }

    private RolesResource getRolesResource() {
        return keycloak.realm(realm).roles();
    }


    private String getUserId(User user) {
        UsersResource usersResource = getUsersResource();
        List<UserRepresentation> userRepresentations = usersResource.searchByUsername(user.getUsername(), true);
        String id = null;
        if (!userRepresentations.isEmpty()) {
            UserRepresentation userRepresentation = userRepresentations.getFirst();
            id = userRepresentation.getId();
        }
        return id;
    }
}
