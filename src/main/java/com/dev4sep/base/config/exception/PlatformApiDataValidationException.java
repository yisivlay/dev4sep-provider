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

import com.dev4sep.base.config.data.ApiParameterError;
import com.dev4sep.base.config.data.DataValidatorBuilder;

import java.util.List;

/**
 * @author YISivlay
 */
public class PlatformApiDataValidationException extends AbstractPlatformException {
    private final List<ApiParameterError> errors;

    /**
     * Constructor. Consider simply using {@link DataValidatorBuilder#throwValidationErrors()} directly.
     *
     * @param errors list of {@link ApiParameterError} to throw
     */
    public PlatformApiDataValidationException(List<ApiParameterError> errors) {
        super("validation.msg.validation.errors.exist", "Validation errors exist.");
        this.errors = errors;
    }

    public PlatformApiDataValidationException(final List<ApiParameterError> errors, Throwable cause) {
        super("validation.msg.validation.errors.exist", "Validation errors exist.", cause);
        this.errors = errors;
    }

    public PlatformApiDataValidationException(String globalisationMessageCode, String defaultUserMessage, List<ApiParameterError> errors) {
        super(globalisationMessageCode, defaultUserMessage);
        this.errors = errors;
    }

    public PlatformApiDataValidationException(String globalisationMessageCode, String defaultUserMessage, List<ApiParameterError> errors,
                                              Throwable cause) {
        super(globalisationMessageCode, defaultUserMessage, cause);
        this.errors = errors;
    }

    public List<ApiParameterError> getErrors() {
        return this.errors;
    }

    @Override
    public String toString() {
        return "PlatformApiDataValidationException{" + "errors=" + errors + '}';
    }
}
