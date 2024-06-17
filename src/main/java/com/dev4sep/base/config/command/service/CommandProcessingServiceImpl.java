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
import com.dev4sep.base.config.ThreadLocalContextUtil;
import com.dev4sep.base.config.command.domain.*;
import com.dev4sep.base.config.command.exception.CommandProcessFailedException;
import com.dev4sep.base.config.command.exception.CommandSourceProcessSucceedException;
import com.dev4sep.base.config.command.exception.CommandSourceProcessUnderProcessingException;
import com.dev4sep.base.config.command.exception.ErrorInfo;
import com.dev4sep.base.config.command.handler.CommandSourceHandler;
import com.dev4sep.base.config.command.provider.CommandHandlerProvider;
import com.dev4sep.base.config.configuration.domain.ConfigurationDomainService;
import com.dev4sep.base.config.domain.BatchRequestContextHolder;
import com.dev4sep.base.config.domain.CustomRequestContextHolder;
import com.dev4sep.base.config.exception.ErrorHandler;
import com.dev4sep.base.config.exception.PlatformApiDataValidationException;
import com.dev4sep.base.config.hooks.event.HookEvent;
import com.dev4sep.base.config.hooks.event.HookEventSource;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.config.serialization.GoogleGsonSerializerHelper;
import com.dev4sep.base.config.serialization.ToApiJsonSerializer;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.dev4sep.base.config.command.domain.CommandProcessingType.PROCESSED;
import static org.apache.http.HttpStatus.SC_OK;

/**
 * @author YISivlay
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class CommandProcessingServiceImpl implements CommandProcessingService {

    public static final String COMMAND_SOURCE_ID = "commandSourceId";

    private final PlatformSecurityContext context;
    private final ApplicationContext applicationContext;
    private final CommandHandlerProvider commandHandlerProvider;
    private final CustomRequestContextHolder customRequestContextHolder;
    private final CommandSourceService commandSourceService;
    private final ConfigurationDomainService configurationDomainService;
    private final ToApiJsonSerializer<CommandProcessing> toApiResultJsonSerializer;
    private final ToApiJsonSerializer<Map<String, Object>> toApiJsonSerializer;
    private final Gson gson = GoogleGsonSerializerHelper.createSimpleGson();

    @Override
    @Retry(name = "executeCommand", fallbackMethod = "fallbackExecuteCommand")
    public CommandProcessing executeCommand(CommandWrapper request, JsonCommand command, boolean isApprovedByChecker) {

        Long commandId = (Long) customRequestContextHolder.getAttribute(COMMAND_SOURCE_ID, null);
        boolean isRetry = commandId != null;
        boolean isEnclosingTransaction = BatchRequestContextHolder.isEnclosingTransaction();

        CommandSource commandSource = null;
        if (isRetry) {
            commandSource = commandSourceService.getCommandSource(commandId);
        } else if ((commandId = command.getCommandId()) != null) {
            commandSource = commandSourceService.getCommandSource(commandId);
        }
        //exceptionWhenTheRequestAlreadyProcessed(request, isRetry);

        User user = this.context.authenticatedUser(request);
        if (commandSource == null) {
            if (isEnclosingTransaction) {
                commandSource = commandSourceService.getInitialCommandSource(request, command, user);
            } else {
                commandSource = commandSourceService.saveInitialNewTransaction(request, command, user);
                commandId = commandSource.getId();
            }
        }
        if (commandId != null) {
            storeCommandIdInContext(commandSource); // Store command id as a request attribute
        }
        boolean isMakerChecker = configurationDomainService.isMakerCheckerEnabledForTask(request.taskPermissionName());
        if (isApprovedByChecker || (isMakerChecker && user.isCheckerSuperUser())) {
            commandSource.markAsChecked(user);
        }

        final CommandProcessing result;
        try {
            result = commandSourceService.processCommand(findCommandHandler(request), command, commandSource, user, isApprovedByChecker, isMakerChecker);
        } catch (Throwable t) {
            RuntimeException mappable = ErrorHandler.getMappable(t);
            ErrorInfo errorInfo = commandSourceService.generateErrorInfo(mappable);
            Integer statusCode = errorInfo.getStatusCode();
            commandSource.setResultStatusCode(statusCode);
            commandSource.setResult(errorInfo.getMessage());
            if (statusCode != SC_OK) {
                commandSource.setStatus(CommandProcessingType.ERROR.getValue());
            }
            if (!isEnclosingTransaction) { // TODO: temporary solution
                commandSource = commandSourceService.saveResultNewTransaction(commandSource);
            }
            // must not throw any exception; must persist in new transaction as the current transaction was already
            // marked as rollback
            publishHookErrorEvent(request, command, errorInfo);
            throw mappable;
        }

        commandSource.setResultStatusCode(SC_OK);
        commandSource.updateForAudit(result);
        commandSource.setResult(toApiJsonSerializer.serializeResult(result));
        commandSource.setStatus(PROCESSED.getValue());
        commandSource = commandSourceService.saveResultSameTransaction(commandSource);
        storeCommandIdInContext(commandSource);

        result.setRollbackTransaction(null);
        publishHookEvent(request.getEntityName(), request.getActionName(), command, result);

        return result;
    }

    @SuppressWarnings("unused")
    public CommandProcessing fallbackExecuteCommand(Exception e) {
        throw ErrorHandler.getMappable(e);
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

    private CommandSourceHandler findCommandHandler(final CommandWrapper request) {
        return commandHandlerProvider.getHandler(request.getEntityName(), request.getActionName());
    }

    private void publishHookErrorEvent(CommandWrapper wrapper, JsonCommand command, ErrorInfo errorInfo) {
        publishHookEvent(wrapper.getEntityName(), wrapper.getActionName(), command, gson.toJson(errorInfo));
    }

    protected void publishHookEvent(final String entityName, final String actionName, JsonCommand command, final Object result) {

        final User user = context.authenticatedUser(CommandWrapper.wrap(actionName, entityName));

        final HookEventSource hookEventSource = new HookEventSource(entityName, actionName);

        // TODO: Add support for publishing array events
        if (command.getJson() != null) {
            Type type = new TypeToken<Map<String, Object>>() {
            }.getType();

            Map<String, Object> myMap;
            try {
                myMap = gson.fromJson(command.getJson(), type);
            } catch (Exception e) {
                throw new PlatformApiDataValidationException("error.msg.invalid.json", "The provided JSON is invalid.", new ArrayList<>(), e);
            }

            Map<String, Object> reqmap = new HashMap<>();
            reqmap.put("entityName", entityName);
            reqmap.put("actionName", actionName);
            reqmap.put("createdBy", context.authenticatedUser().getId());
            reqmap.put("createdByName", context.authenticatedUser().getUsername());
            reqmap.put("createdByFullName", context.authenticatedUser().getDisplayName());

            reqmap.put("request", myMap);
            if (result instanceof CommandProcessing) {
                CommandProcessing resultCopy = CommandProcessing.fromCommandProcessing((CommandProcessing) result);

                reqmap.put("officeId", resultCopy.getOfficeId());
                resultCopy.setOfficeId(null);
                reqmap.put("response", resultCopy);
            } else if (result instanceof ErrorInfo ex) {
                reqmap.put("status", "Exception");

                Map<String, Object> errorMap = new HashMap<>();

                try {
                    errorMap = gson.fromJson(ex.getMessage(), type);
                } catch (Exception e) {
                    errorMap.put("errorMessage", ex.getMessage());
                }

                errorMap.put("errorCode", ex.getErrorCode());
                errorMap.put("statusCode", ex.getStatusCode());

                reqmap.put("response", errorMap);
            }

            reqmap.put("timestamp", Instant.now().toString());

            final String serializedResult = toApiResultJsonSerializer.serialize(reqmap);

            final HookEvent applicationEvent = new HookEvent(hookEventSource, serializedResult, user, ThreadLocalContextUtil.getContext());

            applicationContext.publishEvent(applicationEvent);
        }
    }
}
