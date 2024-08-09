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

import com.dev4sep.base.adminstration.user.domain.User;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.GroupRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;

import java.util.List;

/**
 * @author YISivlay
 */
public interface KeycloakService {

    void createUser(User user, String rawPassword);

    void sendVerificationEmail(String userId);

    void deleteUser(User user);

    void updatePassword(User user);

    void forgotPassword(User user);

    UserResource getUser(User user);

    void assignRole(User user, String roleName);

    void deleteRoleFromUser(User user, String roleName);

    List<RoleRepresentation> getUserRoles(User user);

    List<GroupRepresentation> getUserGroups(User user);

    void assignGroup(User user, String groupId);

    void deleteGroupFromUser(User user, String groupId);

}
