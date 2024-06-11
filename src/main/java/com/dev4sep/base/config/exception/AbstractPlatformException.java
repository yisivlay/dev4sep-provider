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

import java.util.ArrayList;
import java.util.List;

/**
 * @author YISivlay
 */
public abstract class AbstractPlatformException extends RuntimeException {

    private static final Object[] NO_ARGS = new Object[0];

    private final String msgCode;
    private final String userMsg;
    private final Object[] userMsgArgs;

    protected AbstractPlatformException(String msgCode, String userMsg) {
        super(userMsg);
        this.msgCode = msgCode;
        this.userMsg = userMsg;
        this.userMsgArgs = NO_ARGS;
    }

    protected AbstractPlatformException(String msgCode, String userMsg, Object[] userMsgArgs) {
        super(msgCode, findThrowableCause(userMsgArgs));
        this.msgCode = msgCode;
        this.userMsg = userMsg;
        this.userMsgArgs = AbstractPlatformException.filterThrowableCause(userMsgArgs);
    }

    protected AbstractPlatformException(String msgCode, String userMsg, Throwable cause) {
        super(userMsg, cause);
        this.msgCode = msgCode;
        this.userMsg = userMsg;
        this.userMsgArgs = NO_ARGS;
    }

    private static Throwable findThrowableCause(Object[] userMsgArgs) {
        if (userMsgArgs == null) {
            return null;
        }
        for (Object userMessageArg : userMsgArgs) {
            if (userMessageArg instanceof Throwable) {
                return (Throwable) userMessageArg;
            }
        }
        return null;
    }

    private static Object[] filterThrowableCause(Object[] userMsgArgs) {
        if (userMsgArgs == null) {
            return NO_ARGS;
        }
        List<Object> filteredDefaultUserMessageArgs = new ArrayList<>(userMsgArgs.length);
        for (Object userMessageArg : userMsgArgs) {
            if (!(userMessageArg instanceof Throwable)) {
                filteredDefaultUserMessageArgs.add(userMessageArg);
            }
        }
        return filteredDefaultUserMessageArgs.toArray();
    }

    public final String getMsgCode() {
        return this.msgCode;
    }

    public String getUserMsg() {
        return userMsg;
    }

    public Object[] getUserMsgArgs() {
        return userMsgArgs;
    }
}
