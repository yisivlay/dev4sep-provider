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

import com.dev4sep.base.config.data.ApiGlobalErrorResponse;
import com.dev4sep.base.config.exception.AbstractPlatformResourceNotFoundException;
import com.dev4sep.base.config.exception.ErrorHandler;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

/**
 * @author YISivlay
 */
@Slf4j
@Provider
@Component
@Scope("singleton")
public class PlatformResourceNotFoundExceptionMapper implements ExceptionMapper<AbstractPlatformResourceNotFoundException> {

    @Override
    public Response toResponse(AbstractPlatformResourceNotFoundException exception) {
        log.warn("Exception occurred", ErrorHandler.findMostSpecificException(exception));
        final ApiGlobalErrorResponse notFoundErrorResponse = ApiGlobalErrorResponse.notFound(exception.getMsgCode(), exception.getUserMsg(), exception.getUserMsgArgs());
        return Response.status(Response.Status.NOT_FOUND).entity(notFoundErrorResponse).type(MediaType.APPLICATION_JSON).build();
    }
}
