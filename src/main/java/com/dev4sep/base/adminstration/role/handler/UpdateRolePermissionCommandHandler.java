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
package com.dev4sep.base.adminstration.role.handler;

import com.dev4sep.base.adminstration.role.api.RoleApiConstants;
import com.dev4sep.base.adminstration.role.service.RoleWritePlatformService;
import com.dev4sep.base.config.command.annotation.CommandType;
import com.dev4sep.base.config.command.domain.CommandProcessing;
import com.dev4sep.base.config.command.domain.JsonCommand;
import com.dev4sep.base.config.command.handler.CommandSourceHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author YISivlay
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CommandType(entity = RoleApiConstants.PERMISSIONS, action = "PERMISSIONS")
public class UpdateRolePermissionCommandHandler implements CommandSourceHandler {

    private final RoleWritePlatformService writePlatformService;

    @Override
    public CommandProcessing processCommand(final JsonCommand command) {
        return this.writePlatformService.updateRolePermissions(command.getResourceId(), command);
    }
}