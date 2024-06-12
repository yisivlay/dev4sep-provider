package com.dev4sep.base.config.security.exception;

import org.springframework.dao.EmptyResultDataAccessException;

/**
 * @author YISivlay
 */
public class InvalidTenantIdentifierException extends RuntimeException {

    public InvalidTenantIdentifierException(final String message) {
        super(message);
    }

    public InvalidTenantIdentifierException(String message, EmptyResultDataAccessException e) {
        super(message, e);
    }

}
