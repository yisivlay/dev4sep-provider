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
package com.dev4sep.base.config.exception;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YISivlay
 */
@Getter
public abstract class AbstractPlatformException extends RuntimeException {

    private static final Object[] NO_ARGS = new Object[0];

    private final String code;
    private final String message;
    private final String parameter;
    private final Object value;
    private final Object[] args;

    protected AbstractPlatformException(String code, String message) {
        super(message);
        this.code = code;
        this.message = message;
        this.parameter = null;
        this.value = null;
        this.args = NO_ARGS;
    }

    protected AbstractPlatformException(String code, String message, String parameter, Object value) {
        super(message);
        this.code = code;
        this.message = message;
        this.parameter = parameter;
        this.value = value;
        this.args = NO_ARGS;
    }

    protected AbstractPlatformException(String code, String message, Object[] args) {
        super(code, findThrowableCause(args));
        this.code = code;
        this.message = message;
        this.parameter = null;
        this.value = null;
        this.args = AbstractPlatformException.filterThrowableCause(args);
    }

    protected AbstractPlatformException(String code, String message, String parameter, Object value, Object[] args) {
        super(code, findThrowableCause(args));
        this.code = code;
        this.message = message;
        this.parameter = parameter;
        this.value = value;
        this.args = AbstractPlatformException.filterThrowableCause(args);
    }

    protected AbstractPlatformException(String code, String message, Throwable cause) {
        super(message, cause);
        this.code = code;
        this.message = message;
        this.parameter = null;
        this.value = null;
        this.args = NO_ARGS;
    }

    private static Throwable findThrowableCause(Object[] args) {
        if (args == null) {
            return null;
        }
        for (Object userMessageArg : args) {
            if (userMessageArg instanceof Throwable) {
                return (Throwable) userMessageArg;
            }
        }
        return null;
    }

    private static Object[] filterThrowableCause(Object[] args) {
        if (args == null) {
            return NO_ARGS;
        }
        List<Object> filteredDefaultUserMessageArgs = new ArrayList<>(args.length);
        for (Object userMessageArg : args) {
            if (!(userMessageArg instanceof Throwable)) {
                filteredDefaultUserMessageArgs.add(userMessageArg);
            }
        }
        return filteredDefaultUserMessageArgs.toArray();
    }
}
