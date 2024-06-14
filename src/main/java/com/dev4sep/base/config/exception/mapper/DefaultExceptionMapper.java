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

import com.dev4sep.base.config.exception.ErrorHandler;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import java.util.Map;

import static org.apache.http.HttpStatus.SC_INTERNAL_SERVER_ERROR;

/**
 * @author YISivlay
 */
@Slf4j
@Component
@Scope("singleton")
public class DefaultExceptionMapper implements ExceptionMapper<RuntimeException> {

    @Override
    public Response toResponse(RuntimeException exception) {
        log.debug("Exception occurred", ErrorHandler.findMostSpecificException(exception));
        return Response.status(SC_INTERNAL_SERVER_ERROR)
                .entity(Map.of("Exception", ObjectUtils.defaultIfNull(exception.getMessage(), "No error message available")))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
