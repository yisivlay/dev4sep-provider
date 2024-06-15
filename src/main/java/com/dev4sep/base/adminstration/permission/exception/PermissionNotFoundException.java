package com.dev4sep.base.adminstration.permission.exception;

import com.dev4sep.base.config.exception.AbstractPlatformResourceNotFoundException;

/**
 * @author YISivlay
 */
public class PermissionNotFoundException extends AbstractPlatformResourceNotFoundException {
    public PermissionNotFoundException(final String code) {
        super("error.msg.permission.code.invalid", "Permission with Code " + code + " does not exist", code);
    }
}
