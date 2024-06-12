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

    private String developerMsg;
    private String httpStatusCode;
    private String userMsg;
    private String userMsgCode;
    private List<ApiParameterError> errors = new ArrayList<>();

    protected ApiGlobalErrorResponse() {
    }

    public static ApiGlobalErrorResponse create(int statusCode,
                                                String msgCode,
                                                String developerMessage,
                                                String defaultUserMessage,
                                                List<ApiParameterError> errors) {
        ApiGlobalErrorResponse response = new ApiGlobalErrorResponse();
        response.setHttpStatusCode(String.valueOf(statusCode));
        response.setUserMsgCode(msgCode);
        response.setDeveloperMsg(developerMessage);
        response.setUserMsg(defaultUserMessage);
        response.setErrors(errors);
        return response;
    }

    public static ApiGlobalErrorResponse create(int statusCode,
                                                String msgCode,
                                                String developerMsg,
                                                String userMsg) {
        return create(statusCode, msgCode, developerMsg, userMsg, null);
    }

    public static ApiGlobalErrorResponse badClientRequest(final String msgCode,
                                                          final String userMsg,
                                                          final List<ApiParameterError> errors) {
        return create(SC_BAD_REQUEST, msgCode, "The request was invalid. This typically will happen due to validation errors which are provided.", userMsg, errors);
    }

    public static ApiGlobalErrorResponse serverSideError(final String msgCode,
                                                         final String userMsg,
                                                         final Object... userMsgArgs) {
        String msg = "An unexpected error occurred on the platform server.";
        final List<ApiParameterError> errors = new ArrayList<>();
        errors.add(ApiParameterError.generalError(msgCode, userMsg, userMsgArgs));

        return create(SC_INTERNAL_SERVER_ERROR, "error.msg.platform.server.side.error", msg, msg, errors);
    }

    public static ApiGlobalErrorResponse notFound(final String msgCode,
                                                  final String userMsg,
                                                  final Object... userMsgArgs) {
        String msg = "The requested resource is not available.";
        final List<ApiParameterError> errors = new ArrayList<>();
        errors.add(ApiParameterError.resourceIdentifierNotFound(msgCode, userMsg, userMsgArgs));

        return create(SC_NOT_FOUND, "error.msg.resource.not.found", msg, msg, errors);
    }

    public static ApiGlobalErrorResponse dataIntegrityError(final String msgCode,
                                                            final String userMsg,
                                                            final String parameterName,
                                                            final Object... userMsgArgs) {
        final List<ApiParameterError> errors = new ArrayList<>();
        errors.add(ApiParameterError.parameterError(msgCode, userMsg, parameterName, userMsgArgs));

        return create(SC_FORBIDDEN, msgCode, "The request caused a data integrity issue to be fired by the database.", userMsg, errors);
    }

    public static ApiGlobalErrorResponse invalidTenantIdentifier() {
        return create(SC_UNAUTHORIZED, "error.msg.invalid.tenant.identifier", "Invalid tenant details were passed in api request.",
                "Invalid tenant identifier provided with request.");
    }
}
