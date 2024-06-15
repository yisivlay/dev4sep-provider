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
import com.dev4sep.base.config.command.domain.CommandSource;
import com.dev4sep.base.config.command.domain.CommandSourceRepository;
import com.dev4sep.base.config.command.domain.CommandWrapper;
import com.dev4sep.base.config.command.domain.JsonCommand;
import com.dev4sep.base.config.command.exception.CommandSourceNotFoundException;
import com.dev4sep.base.config.exception.ErrorHandler;
import lombok.RequiredArgsConstructor;
import org.jetbrains.annotations.NotNull;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import static com.dev4sep.base.config.command.domain.CommandProcessingType.UNDER_PROCESSING;

/**
 * @author YISivlay
 */
@Component
@RequiredArgsConstructor
public class CommandSourceService {

    private final CommandSourceRepository commandSourceRepository;
    private final ErrorHandler errorHandler;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CommandSource getCommandSource(Long commandSourceId) {
        return commandSourceRepository.findById(commandSourceId).orElseThrow(() -> new CommandSourceNotFoundException(commandSourceId));
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public CommandSource findCommandSource(CommandWrapper request) {
        return commandSourceRepository.findByActionNameAndEntityName(request.getActionName(), request.getEntityName());
    }

    public CommandSource getInitialCommandSource(CommandWrapper wrapper, JsonCommand jsonCommand, User maker) {
        CommandSource commandSourceResult = CommandSource.fullEntryFrom(wrapper, jsonCommand, maker, UNDER_PROCESSING.getValue());
        if (commandSourceResult.getCommandAsJson() == null) {
            commandSourceResult.setCommandAsJson("{}");
        }
        return commandSourceResult;
    }

    @NotNull
    private CommandSource saveInitial(CommandWrapper request, JsonCommand jsonCommand, User maker) {
        CommandSource initialCommandSource = getInitialCommandSource(request, jsonCommand, maker);
        return commandSourceRepository.saveAndFlush(initialCommandSource);
    }

    @NotNull
    @Transactional(propagation = Propagation.REQUIRES_NEW, isolation = Isolation.REPEATABLE_READ)
    public CommandSource saveInitialNewTransaction(CommandWrapper wrapper, JsonCommand jsonCommand, AppUser maker, String idempotencyKey) {
        return saveInitial(wrapper, jsonCommand, maker, idempotencyKey);
    }
}
