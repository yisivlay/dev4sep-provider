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

import com.dev4sep.base.config.exception.PlatformApiDataValidationException;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YISivlay
 */
public class DataValidatorBuilder {

    public static final String VALID_INPUT_SEPERATOR = "_";
    private final List<ApiParameterError> dataValidationErrors;
    private String resource;
    private String parameter;
    private String arrayPart;
    private Integer arrayIndex;
    private Object value;
    private final boolean ignoreNullValue = false;

    public DataValidatorBuilder(final List<ApiParameterError> dataValidationErrors) {
        this.dataValidationErrors = dataValidationErrors;
    }

    public DataValidatorBuilder() {
        this(new ArrayList<>());
    }

    /**
     * Throws Exception if validation errors.
     *
     * @throws PlatformApiDataValidationException unchecked exception (RuntimeException) thrown if there are any validation error
     */
    public void throwValidationErrors() throws PlatformApiDataValidationException {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }
}
