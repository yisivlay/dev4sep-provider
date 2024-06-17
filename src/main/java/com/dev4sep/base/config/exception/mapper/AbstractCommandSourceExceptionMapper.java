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

import com.dev4sep.base.config.command.exception.CommandProcessFailedException;
import com.dev4sep.base.config.command.exception.CommandSourceProcessSucceedException;
import com.dev4sep.base.config.command.exception.CommandSourceProcessUnderProcessingException;
import com.dev4sep.base.config.exception.AbstractCommandSourceException;
import com.dev4sep.base.config.exception.ErrorHandler;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import static com.dev4sep.base.config.exception.AbstractCommandSourceException.CACHE_HEADER;
import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;
import static org.apache.http.HttpStatus.SC_OK;

/**
 * @author YISivlay
 */
@Slf4j
@Provider
@Component
@Scope("singleton")
public class AbstractCommandSourceExceptionMapper implements ExceptionMapper<AbstractCommandSourceException> {
    @Override
    public Response toResponse(AbstractCommandSourceException exception) {
        log.debug("Exception occurred", ErrorHandler.findMostSpecificException(exception));
        Integer status = null;
        if (exception instanceof CommandSourceProcessSucceedException pse) {
            Integer statusCode = pse.getStatus();
            status = statusCode == null ? SC_OK : statusCode;
        }
        if (exception instanceof CommandSourceProcessUnderProcessingException) {
            status = 425;
        } else if (exception instanceof CommandProcessFailedException pfe) {
            status = pfe.getStatus();
        }
        if (status == null) {
            status = SC_INTERNAL_SERVER_ERROR;
        }
        return Response.status(status)
                .entity(exception.getResponse())
                .header(CACHE_HEADER, "true")
                .type(MediaType.APPLICATION_JSON)
                .build();
    }
}
