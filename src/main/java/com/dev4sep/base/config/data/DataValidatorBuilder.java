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
import com.google.common.base.Splitter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ObjectUtils;

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

    public DataValidatorBuilder resource(final String resource) {
        this.resource = resource;
        return this;
    }

    public DataValidatorBuilder value(final Object value) {
        this.value = value;
        return this;
    }

    public DataValidatorBuilder parameter(final String parameter) {
        this.parameter = parameter;
        return this;
    }

    public DataValidatorBuilder reset() {
        return new DataValidatorBuilder(this.dataValidationErrors).resource(this.resource);
    }

    public DataValidatorBuilder() {
        this(new ArrayList<>());
    }

    public DataValidatorBuilder integerGreaterThanZero() {
        if (this.value == null && this.ignoreNullValue) {
            return this;
        }

        if (this.value != null) {
            final var number = Integer.parseInt(this.value.toString());
            if (number < 1) {
                var validationErrorCode = "validation.msg." + this.resource + "." + this.parameter + ".not.greater.than.zero";
                var defaultEnglishMessage = "The parameter `" + this.parameter + "` must be greater than 0.";
                final var error = ApiParameterError.parameterError(
                        validationErrorCode,
                        defaultEnglishMessage,
                        this.parameter,
                        number,
                        0
                );
                this.dataValidationErrors.add(error);
            }
        }
        return this;
    }

    public DataValidatorBuilder notNull() {
        if (this.value == null && !this.ignoreNullValue) {

            var parameter = this.parameter;
            final var errorCode = new StringBuilder("validation.msg.").append(this.resource).append(".").append(this.parameter);
            if (this.arrayIndex != null && StringUtils.isNotBlank(this.arrayPart)) {
                errorCode.append(".").append(this.arrayPart);
                parameter = this.parameter + '[' + this.arrayIndex + "][" + this.arrayPart + ']';
            }

            errorCode.append(".cannot.be.blank");
            var message = "The parameter `" + parameter + "` is mandatory.";
            final ApiParameterError error = ApiParameterError.parameterError(errorCode.toString(), message, parameter, this.arrayIndex);
            this.dataValidationErrors.add(error);
        }
        return this;
    }

    public DataValidatorBuilder notBlank() {
        if (this.value == null && this.ignoreNullValue) {
            return this;
        }

        if (this.value == null || StringUtils.isBlank(this.value.toString())) {
            String realParameterName = this.parameter;
            final var validationErrorCode = new StringBuilder("validation.msg.").append(this.resource).append(".").append(this.parameter);
            if (this.arrayIndex != null && StringUtils.isNotBlank(this.arrayPart)) {
                validationErrorCode.append(".").append(this.arrayPart);
                realParameterName = this.parameter + '[' + this.arrayIndex + "][" + this.arrayPart + ']';
            }

            validationErrorCode.append(".cannot.be.blank");
            var defaultEnglishMessage = "The parameter `" + realParameterName + "` is mandatory.";
            final var error = ApiParameterError.parameterError(validationErrorCode.toString(), defaultEnglishMessage, realParameterName, this.arrayIndex);
            this.dataValidationErrors.add(error);
        }
        return this;
    }

    public DataValidatorBuilder notExceedingLengthOf(final Integer maxLength) {
        if (this.value == null && this.ignoreNullValue) {
            return this;
        }

        if (this.value != null && this.value.toString().trim().length() > maxLength) {
            var validationErrorCode = "validation.msg." + this.resource + "." + this.parameter + ".exceeds.max.length";
            var defaultEnglishMessage = "The parameter `" + this.parameter + "` exceeds max length of " + maxLength + ".";
            final var error = ApiParameterError.parameterError(
                    validationErrorCode,
                    defaultEnglishMessage,
                    this.parameter,
                    maxLength,
                    this.value.toString()
            );
            this.dataValidationErrors.add(error);
        }
        return this;
    }

    public DataValidatorBuilder equalToParameter(final String parameterName, final Object linkedValue) {
        if (this.value == null && linkedValue == null && this.ignoreNullValue) {
            return this;
        }
        if (this.value != null && !this.value.equals(linkedValue)) {
            var errorCode = "validation.msg." + this.resource + "." + parameterName + ".not.equal.to." + this.parameter;
            var message = "The parameter `" + parameterName + "` is not equal to " + this.parameter + ".";
            final var error = ApiParameterError.parameterError(errorCode, message, parameterName, linkedValue, this.value);
            this.dataValidationErrors.add(error);
        }
        return this;
    }

    public DataValidatorBuilder matchesRegularExpression(final String expression, final String message) {
        if (this.value == null && this.ignoreNullValue) {
            return this;
        }
        if (this.value != null && !this.value.toString().matches(expression)) {
            var errorCode = "validation.msg." + this.resource + "." + this.parameter + ".does.not.match.regexp";
            final var error = ApiParameterError.parameterError(errorCode, message, this.parameter, this.value, expression);
            this.dataValidationErrors.add(error);
        }
        return this;
    }

    private DataValidatorBuilder validateStringFor(final String validInputs) {
        if (this.value == null && this.ignoreNullValue) {
            return this;
        }
        final Iterable<String> inputs = Splitter.onPattern(VALID_INPUT_SEPERATOR).split(validInputs);
        var validationErr = true;
        for (final var input : inputs) {
            if (input.equalsIgnoreCase(this.value.toString().trim())) {
                validationErr = false;
                break;
            }
        }
        if (validationErr) {
            var errorCode = "validation.msg." + this.resource + "." + this.parameter + ".value.should.true.or.false";
            var message = "The parameter `" + this.parameter + "` value should true or false ";
            final var error = ApiParameterError.parameterError(errorCode, message, this.parameter, this.value);
            this.dataValidationErrors.add(error);
        }
        return this;
    }

    public DataValidatorBuilder validateForBooleanValue() {
        return validateStringFor("TRUE" + VALID_INPUT_SEPERATOR + "FALSE");
    }

    public DataValidatorBuilder trueOrFalseRequired(final Object trueOfFalseField) {
        if (trueOfFalseField != null && !trueOfFalseField.toString().equalsIgnoreCase("true") && !trueOfFalseField.toString().equalsIgnoreCase("false")) {
            var errorCode = "validation.msg." + this.resource + "." + this.parameter + ".must.be.true.or.false";
            var message = "The parameter `" + this.parameter + "` must be set as true or false.";
            final var error = ApiParameterError.parameterError(errorCode, message, this.parameter);
            this.dataValidationErrors.add(error);
        }
        return this;
    }

    public DataValidatorBuilder arrayNotEmpty() {
        if (this.value == null && this.ignoreNullValue) {
            return this;
        }
        final var array = (Object[]) this.value;
        if (ObjectUtils.isEmpty(array)) {
            var errorCode = "validation.msg." + this.resource + "." + this.parameter + ".cannot.be.empty";
            var message = "The parameter `" + this.parameter + "` cannot be empty. You must select at least one.";
            final var error = ApiParameterError.parameterError(errorCode, message, this.parameter);
            this.dataValidationErrors.add(error);
        }
        return this;
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
