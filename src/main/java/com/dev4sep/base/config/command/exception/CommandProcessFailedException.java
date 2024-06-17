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
package com.dev4sep.base.config.command.exception;

import com.dev4sep.base.config.command.domain.CommandSource;
import com.dev4sep.base.config.command.domain.CommandWrapper;
import com.dev4sep.base.config.exception.AbstractCommandSourceException;
import jakarta.validation.constraints.NotNull;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;

/**
 * @author YISivlay
 */
public class CommandProcessFailedException extends AbstractCommandSourceException {

    private final Integer status;

    public CommandProcessFailedException(CommandWrapper request, CommandSource command) {
        super(request.getActionName(), request.getEntityName(), command.getResult());
        this.status = command.getResultStatusCode();
    }

    @NotNull
    public Integer getStatus() {
        // If the database inconsistent we return http 500 instead of null pointer exception
        return status == null ? Integer.valueOf(SC_INTERNAL_SERVER_ERROR) : status;
    }
}
