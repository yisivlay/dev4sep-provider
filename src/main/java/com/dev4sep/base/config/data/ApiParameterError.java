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

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.List;


@Getter
@Setter
public final class ApiParameterError {

    private final String code;
    private final String message;
    private String parameterName;
    private final Object value;
    private final List<ApiErrorMessageArg> args;

    public ApiParameterError(final String code,
                             final String message,
                             final Object[] args,
                             String parameterName,
                             Object value) {
        this.message = message;
        this.code = code;
        this.parameterName = parameterName;
        this.value = value;

        final List<ApiErrorMessageArg> msgArgs = new ArrayList<>();
        if (args != null) {
            for (final var object : args) {
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

    public static ApiParameterError parameterError(final String code,
                                                   final String message,
                                                   final String parameterName,
                                                   final Object... args) {
        return new ApiParameterError(code, message, args, parameterName, null);
    }

    public static ApiParameterError generalError(final String code,
                                                 final String message,
                                                 final Object... args) {
        return new ApiParameterError(code, message, args, "id", null);
    }

    public static ApiParameterError resourceIdentifierNotFound(final String code,
                                                               final String message,
                                                               final Object... args) {
        return new ApiParameterError(code, message, args, "id", null);
    }

    public static ApiParameterError resourceIdentifierNotFound(final String code,
                                                               final String message,
                                                               final String parameter,
                                                               final Object value,
                                                               final Object... args) {
        String parameterName = parameter != null ? parameter : "id";
        return new ApiParameterError(code, message, args, parameterName, value);
    }

    public static ApiParameterError parameterErrorWithValue(final String code,
                                                            final String message,
                                                            final String parameterName,
                                                            final String value,
                                                            final Object... args) {
        return new ApiParameterError(code, message, args, parameterName, value);
    }

    public String getMessage() {
        return this.message;
    }

    public String getCode() {
        return this.code;
    }

    public String getParameterName() {
        return this.parameterName;
    }

    public Object getValue() {
        return this.value;
    }

    public List<ApiErrorMessageArg> getArgs() {
        return this.args;
    }

    @Override
    public String toString() {
        return message;
    }
}
