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

import com.dev4sep.base.adminstration.user.domain.User;
import com.dev4sep.base.config.command.domain.*;
import com.dev4sep.base.config.command.exception.CommandProcessFailedException;
import com.dev4sep.base.config.command.exception.CommandSourceProcessSucceedException;
import com.dev4sep.base.config.command.exception.CommandSourceProcessUnderProcessingException;
import com.dev4sep.base.config.domain.CustomRequestContextHolder;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * @author YISivlay
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommandProcessingServiceImpl implements CommandProcessingService {

    public static final String COMMAND_SOURCE_ID = "commandSourceId";

    private final PlatformSecurityContext context;
    private final CustomRequestContextHolder customRequestContextHolder;
    private final CommandSourceService commandSourceService;

    @Override
    public CommandProcessing executeCommand(CommandWrapper request, JsonCommand command, boolean isApprovedByChecker) {

        Long commandId = (Long) customRequestContextHolder.getAttribute(COMMAND_SOURCE_ID, null);
        boolean isRetry = commandId != null;
        CommandSource commandSource = null;
        if (isRetry) {
            commandSource = commandSourceService.getCommandSource(commandId);
        } else if ((commandId = command.getCommandId()) != null) {
            commandSource = commandSourceService.getCommandSource(commandId);
        }
        exceptionWhenTheRequestAlreadyProcessed(request, isRetry);

        User user = this.context.authenticatedUser(request);
        if (commandSource == null) {
            commandSource = commandSourceService.saveInitialNewTransaction(request, command, user);
            commandId = commandSource.getId();
        }
        if (commandId != null) {
            storeCommandIdInContext(commandSource); // Store command id as a request attribute
        }
        boolean isMakerChecker = configurationDomainService.isMakerCheckerEnabledForTask(wrapper.taskPermissionName());
        if (isApprovedByChecker || (isMakerChecker && user.isCheckerSuperUser())) {
            commandSource.markAsChecked(user);
        }

        return null;
    }

    private void storeCommandIdInContext(CommandSource savedCommandSource) {
        if (savedCommandSource.getId() == null) {
            throw new IllegalStateException("Command source not saved");
        }
        // Idempotency filters and retry need this
        customRequestContextHolder.setAttribute(COMMAND_SOURCE_ID, savedCommandSource.getId());
    }

    private void exceptionWhenTheRequestAlreadyProcessed(CommandWrapper request, boolean retry) {
        CommandSource command = commandSourceService.findCommandSource(request);
        if (command == null) {
            return;
        }
        CommandProcessingType status = CommandProcessingType.fromInt(command.getStatus());
        switch (status) {
            case UNDER_PROCESSING -> throw new CommandSourceProcessUnderProcessingException(request);
            case PROCESSED -> throw new CommandSourceProcessSucceedException(request, command);
            case ERROR -> {
                if (!retry) {
                    throw new CommandProcessFailedException(request, command);
                }
            }
            default -> {
            }
        }
    }
}
