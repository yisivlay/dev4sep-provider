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

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

import static org.apache.http.HttpStatus.*;

/**
 * @author YISivlay
 */
@Getter
@Setter(AccessLevel.PROTECTED)
public class ApiGlobalErrorResponse {

    private int status;
    private String message;
    private String code;
    private List<ApiParameterError> errors = new ArrayList<>();

    protected ApiGlobalErrorResponse() {
    }

    public static ApiGlobalErrorResponse create(int status,
                                                String code,
                                                String message,
                                                List<ApiParameterError> errors) {
        ApiGlobalErrorResponse response = new ApiGlobalErrorResponse();
        response.setStatus(status);
        response.setMessage(message);
        response.setCode(code);
        response.setErrors(errors);
        return response;
    }

    public static ApiGlobalErrorResponse create(int status,
                                                String code,
                                                String message) {
        return create(status, code, message, null);
    }

    public static ApiGlobalErrorResponse badClientRequest(final String code,
                                                          final String message,
                                                          final List<ApiParameterError> errors) {
        return create(SC_BAD_REQUEST, code, message, errors);
    }

    public static ApiGlobalErrorResponse serverSideError(final String code,
                                                         final String message,
                                                         final Object... args) {
        String msg = "Internal server error";
        final List<ApiParameterError> errors = new ArrayList<>();
        errors.add(ApiParameterError.generalError(code, message, args));

        return create(SC_INTERNAL_SERVER_ERROR, "error.msg.platform.server.side.error", msg, errors);
    }

    public static ApiGlobalErrorResponse notFound(final String code,
                                                  final String message,
                                                  final Object... args) {
        String msg = "Not found";
        final List<ApiParameterError> errors = new ArrayList<>();
        errors.add(ApiParameterError.resourceIdentifierNotFound(code, message, args));

        return create(SC_NOT_FOUND, "error.msg.resource.not.found", msg, errors);
    }

    public static ApiGlobalErrorResponse dataIntegrityError(final String code,
                                                            final String message,
                                                            final String parameterName,
                                                            final Object... args) {
        final List<ApiParameterError> errors = new ArrayList<>();
        errors.add(ApiParameterError.parameterError(code, message, parameterName, args));

        return create(SC_FORBIDDEN, code, message, errors);
    }

    public static ApiGlobalErrorResponse invalidTenantIdentifier() {
        return create(SC_UNAUTHORIZED, "error.msg.invalid.tenant.identifier", "Invalid tenant details were passed in api request.");
    }
}
