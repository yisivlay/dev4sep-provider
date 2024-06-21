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
package com.dev4sep.base.adminstration.permission.service;

import com.dev4sep.base.adminstration.permission.api.PermissionApiConstants;
import com.dev4sep.base.adminstration.permission.domain.Permission;
import com.dev4sep.base.adminstration.permission.domain.PermissionRepository;
import com.dev4sep.base.adminstration.permission.exception.PermissionNotFoundException;
import com.dev4sep.base.adminstration.permission.serialization.PermissionsCommandDataValidator;
import com.dev4sep.base.config.command.data.CommandProcessingBuilder;
import com.dev4sep.base.config.command.domain.CommandProcessing;
import com.dev4sep.base.config.command.domain.JsonCommand;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YISivlay
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class PermissionWritePlatformServiceImpl implements PermissionWritePlatformService {

    private final PermissionRepository permissionRepository;
    private final PermissionsCommandDataValidator validator;

    @Override
    public CommandProcessing update(final JsonCommand command) {
        final var permissions = this.permissionRepository.findAll();
        var permissionsCommand = this.validator.commandFromApiJson(command.getJson());

        final var commandPermissions = permissionsCommand.permissions();
        final Map<String, Object> changes = new HashMap<>();
        final Map<String, Boolean> changedPermissions = new HashMap<>();

        for (var entry : commandPermissions.entrySet()) {
            final var permission = findPermissionInCollectionByCode(permissions, entry.getKey());
            if (permission.getCode().endsWith("_CHECKER")
                    || permission.getCode().startsWith("READ_")
                    || permission.getGrouping().equalsIgnoreCase("special")) {
                throw new PermissionNotFoundException(entry.getKey());
            }
            final var isSelected = entry.getValue();
            final var changed = permission.enableMakerChecker(isSelected);
            if (changed) {
                changedPermissions.put(entry.getKey(), isSelected);
                this.permissionRepository.saveAndFlush(permission);
            }
        }
        if (!changedPermissions.isEmpty()) {
            changes.put(PermissionApiConstants.permissions, changedPermissions);
        }
        return new CommandProcessingBuilder()
                .withCommandId(command.commandId())
                .with(changes)
                .build();
    }

    private Permission findPermissionInCollectionByCode(final Collection<Permission> allPermissions, final String permissionCode) {
        if (allPermissions != null) {
            for (final var permission : allPermissions) {
                if (permission.hasCode(permissionCode)) {
                    return permission;
                }
            }
        }
        throw new PermissionNotFoundException(permissionCode);
    }
}
