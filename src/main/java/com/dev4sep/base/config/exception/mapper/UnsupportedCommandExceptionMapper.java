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
package com.dev4sep.base.config.exception.mapper;

import com.dev4sep.base.config.command.exception.UnsupportedCommandException;
import com.dev4sep.base.config.data.ApiGlobalErrorResponse;
import com.dev4sep.base.config.data.ApiParameterError;
import com.dev4sep.base.config.exception.ErrorHandler;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YISivlay
 */
@Slf4j
@Provider
@Component
@Scope("singleton")
public class UnsupportedCommandExceptionMapper implements ExceptionMapper<UnsupportedCommandException> {
    @Override
    public Response toResponse(UnsupportedCommandException exception) {
        final List<ApiParameterError> errors = new ArrayList<>();

        final StringBuilder validationErrorCode = new StringBuilder("error.msg.command.unsupported");
        String message = exception.getMessage();
        final StringBuilder defaultEnglishMessage = new StringBuilder("The command ")
                .append(exception.getUnsupportedCommandName())
                .append(" is not supported.");
        if (message != null) {
            defaultEnglishMessage.append(" ").append(message);
        }
        log.debug("Exception occurred", ErrorHandler.findMostSpecificException(exception));
        final ApiParameterError error = ApiParameterError.parameterError(validationErrorCode.toString(), defaultEnglishMessage.toString(),
                exception.getUnsupportedCommandName(), exception.getUnsupportedCommandName());

        errors.add(error);

        final ApiGlobalErrorResponse invalidParameterError = ApiGlobalErrorResponse
                .badClientRequest("validation.msg.validation.errors.exist", "Validation errors exist.", errors);

        return Response.status(Response.Status.BAD_REQUEST).entity(invalidParameterError).type(MediaType.APPLICATION_JSON).build();
    }
}
