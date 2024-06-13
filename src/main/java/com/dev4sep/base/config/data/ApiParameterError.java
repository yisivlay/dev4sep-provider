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
package com.dev4sep.base.config.data;

import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;

/**
 * @author YISivlay
 */
public final class ApiParameterError {

    private final String developerMsg;
    private final String userMsg;
    private final String userMsgCode;
    private final Object value;
    private final List<ApiErrorMessageArg> args;
    private String parameterName;

    public ApiParameterError(final String msgCode,
                             final String userMsg,
                             final Object[] userMsgArgs,
                             String parameterName,
                             String value) {
        this.developerMsg = userMsg;
        this.userMsg = userMsg;
        this.userMsgCode = msgCode;
        this.parameterName = parameterName;
        this.value = value;

        final List<ApiErrorMessageArg> msgArgs = new ArrayList<>();
        if (userMsgArgs != null) {
            for (final var object : userMsgArgs) {
                if (object instanceof LocalDate) {
                    final var dateFormatter = new DateTimeFormatterBuilder().appendPattern("yyyy-MM-dd").toFormatter();
                    final var formattedDate = dateFormatter.format((LocalDate) object);
                    msgArgs.add(ApiErrorMessageArg.from(formattedDate));
                } else {
                    msgArgs.add(ApiErrorMessageArg.from(object));
                }
            }
        }
        this.args = msgArgs;
    }

    public static ApiParameterError parameterError(final String msgCode,
                                                   final String userMsg,
                                                   final String parameterName,
                                                   final Object... userMsgArgs) {
        return new ApiParameterError(msgCode, userMsg, userMsgArgs, parameterName, null);
    }

    public static ApiParameterError generalError(final String msgCode,
                                                 final String userMsg,
                                                 final Object... userMsgArgs) {
        return new ApiParameterError(msgCode, userMsg, userMsgArgs, "id", null);
    }

    public static ApiParameterError resourceIdentifierNotFound(final String msgCode,
                                                               final String userMsg,
                                                               final Object... userMsgArgs) {
        return new ApiParameterError(msgCode, userMsg, userMsgArgs, "id", null);
    }

    public static ApiParameterError parameterErrorWithValue(final String msgCode,
                                                            final String userMsg,
                                                            final String parameterName,
                                                            final String value,
                                                            final Object... userMsgArgs) {
        return new ApiParameterError(msgCode, userMsg, userMsgArgs, parameterName, value);
    }

    public String getDeveloperMsg() {
        return this.developerMsg;
    }

    public String getUserMsg() {
        return this.userMsg;
    }

    public String getUserMsgCode() {
        return this.userMsgCode;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    public void setParameterName(final String parameterName) {
        this.parameterName = parameterName;
    }

    public Object getValue() {
        return this.value;
    }

    public List<ApiErrorMessageArg> getArgs() {
        return this.args;
    }

    @Override
    public String toString() {
        return developerMsg;
    }
}
