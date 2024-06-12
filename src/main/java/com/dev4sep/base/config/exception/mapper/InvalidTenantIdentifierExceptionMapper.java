package com.dev4sep.base.config.exception.mapper;

import com.dev4sep.base.config.data.ApiGlobalErrorResponse;
import com.dev4sep.base.config.exception.ErrorHandler;
import com.dev4sep.base.config.security.exception.InvalidTenantIdentifierException;
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
public class InvalidTenantIdentifierExceptionMapper implements ExceptionMapper<InvalidTenantIdentifierException> {

    @Override
    public Response toResponse(InvalidTenantIdentifierException exception) {
        log.warn("Exception occurred", ErrorHandler.findMostSpecificException(exception));
        return Response.status(Response.Status.UNAUTHORIZED).entity(ApiGlobalErrorResponse.invalidTenantIdentifier())
                .type(MediaType.APPLICATION_JSON).build();
    }
}
