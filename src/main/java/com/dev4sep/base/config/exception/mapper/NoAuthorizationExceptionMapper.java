package com.dev4sep.base.config.exception.mapper;

import com.dev4sep.base.config.data.ApiGlobalErrorResponse;
import com.dev4sep.base.config.exception.ErrorHandler;
import com.dev4sep.base.config.security.exception.NoAuthorizationException;
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
public class NoAuthorizationExceptionMapper implements ExceptionMapper<NoAuthorizationException> {
    @Override
    public Response toResponse(NoAuthorizationException exception) {
        log.debug("Exception occurred", ErrorHandler.findMostSpecificException(exception));
        return Response
                .status(Response.Status.FORBIDDEN)
                .entity(ApiGlobalErrorResponse.unAuthorized(exception.getMessage()))
                .type(MediaType.APPLICATION_JSON).build();
    }
}
