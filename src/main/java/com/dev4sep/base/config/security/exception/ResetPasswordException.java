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
package com.dev4sep.base.config.security.exception;

import com.dev4sep.base.config.data.ApiParameterError;
import com.dev4sep.base.config.exception.PlatformApiDataValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YISivlay
 */
public class ResetPasswordException extends PlatformApiDataValidationException {
    public ResetPasswordException(final Long userId) {
        super("error.msg.password.outdated", "The password of the user with id " + userId + " has expired, please reset it",
                new ArrayList<>(List.of(ApiParameterError.parameterError(
                        "error.msg.password.outdated",
                        "The password of the user with id " + userId + " has expired, please reset it",
                        "userId", userId)))

        );
    }
}
