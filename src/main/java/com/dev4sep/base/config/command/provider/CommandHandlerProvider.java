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
package com.dev4sep.base.config.command.provider;

import com.dev4sep.base.config.command.annotation.CommandType;
import com.dev4sep.base.config.command.exception.UnsupportedCommandException;
import com.dev4sep.base.config.command.handler.CommandSourceHandler;
import com.google.common.base.Preconditions;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;

import java.util.HashMap;

/**
 * @author YISivlay
 */
@Slf4j
@Component
@NoArgsConstructor
public class CommandHandlerProvider implements ApplicationContextAware, InitializingBean {

    private final HashMap<String, String> registeredHandlers = new HashMap<>();
    private ApplicationContext applicationContext;

    @Override
    public void afterPropertiesSet() throws Exception {
        initializeHandlerRegistry();
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    private void initializeHandlerRegistry() {
        final String[] commandHandlerBeans = applicationContext.getBeanNamesForAnnotation(CommandType.class);
        if (ArrayUtils.isNotEmpty(commandHandlerBeans)) {
            for (final String commandHandlerName : commandHandlerBeans) {
                log.debug("Register command handler '{}' ...", commandHandlerName);
                final CommandType commandType = applicationContext.findAnnotationOnBean(commandHandlerName, CommandType.class);
                try {
                    if (commandType != null) {
                        registeredHandlers.put(commandType.entity() + "|" + commandType.action(), commandHandlerName);
                    } else {
                        log.error("Unable to register command handler '{}'!", commandHandlerName);
                    }
                } catch (final Throwable th) {
                    log.error("Unable to register command handler '{}'!", commandHandlerName, th);
                }
            }
        }
    }

    /**
     * Returns a handler for the given entity and action.<br>
     * <br>
     * Throws an {@link UnsupportedCommandException} if no handler for the given entity, action combination can be
     * found.
     *
     * @param entity
     *            the entity to lookup the handler, must be given.
     * @param action
     *            the action to lookup the handler, must be given.
     */
    public CommandSourceHandler getHandler(final String entity, final String action) {
        Preconditions.checkArgument(StringUtils.isNoneEmpty(entity), "An entity must be given!");
        Preconditions.checkArgument(StringUtils.isNoneEmpty(action), "An action must be given!");

        final String key = entity + "|" + action;
        if (!registeredHandlers.containsKey(key)) {
            throw new UnsupportedCommandException(key);
        }
        return (CommandSourceHandler) applicationContext.getBean(registeredHandlers.get(key));
    }
}
