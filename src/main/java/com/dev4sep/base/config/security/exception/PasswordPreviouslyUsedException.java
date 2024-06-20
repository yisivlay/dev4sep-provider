package com.dev4sep.base.config.security.exception;

import com.dev4sep.base.config.exception.PlatformApiDataValidationException;

/**
 * @author YISivlay
 */
public class PasswordPreviouslyUsedException extends PlatformApiDataValidationException {

    public PasswordPreviouslyUsedException() {
        super("error.msg.password.already.used", "The submitted password has already been used in the past", null);
    }

}
