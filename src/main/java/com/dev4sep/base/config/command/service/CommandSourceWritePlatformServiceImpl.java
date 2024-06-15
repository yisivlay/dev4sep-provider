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
package com.dev4sep.base.config.command.service;

import com.dev4sep.base.config.command.domain.CommandProcessing;
import com.dev4sep.base.config.command.domain.CommandSourceRepository;
import com.dev4sep.base.config.command.domain.CommandWrapper;
import com.dev4sep.base.config.command.domain.JsonCommand;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.config.serialization.FromJsonHelper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author YISivlay
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommandSourceWritePlatformServiceImpl implements CommandSourceWritePlatformService {

    private final PlatformSecurityContext context;
    private final FromJsonHelper fromApiJsonHelper;
    private final CommandSourceRepository commandSourceRepository;

    @Override
    public CommandProcessing logCommandSource(CommandWrapper request) {
        boolean isApprovedByChecker = false;
        // check if is update of own account details
        if (request.isUpdateOfOwnUserDetails(this.context.authenticatedUser(request).getId())) {
            // then allow this operation to proceed.
            // maker checker doesnt mean anything here.
            isApprovedByChecker = true; // set to true in case permissions have
            // been maker-checker enabled by
            // accident.
        } else {
            // if not user changing their own details - check user has
            // permission to perform specific task.
            this.context.authenticatedUser(request).validateHasPermissionTo(request.getTaskPermissionName());
        }
        //TODO - Here we will validate job update allow

        final var json = request.getJson();
        final var parsedCommand = this.fromApiJsonHelper.parse(json);
        JsonCommand command = JsonCommand.builder()
                .json(json)
                .parsedCommand(parsedCommand)
                .fromApiJsonHelper(this.fromApiJsonHelper)
                .resourceId(request.getResourceId())
                .subresourceId(request.getSubResourceId())
                .href(request.getHref())
                .entityName(request.getEntityName())
                .build();

        return this.;
    }
}
