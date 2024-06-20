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
package com.dev4sep.base.adminstration.user.serialization;

import com.dev4sep.base.adminstration.user.api.UserApiConstants;
import com.dev4sep.base.adminstration.user.domain.PasswordValidationPolicy;
import com.dev4sep.base.adminstration.user.domain.PasswordValidationPolicyRepository;
import com.dev4sep.base.config.data.ApiParameterError;
import com.dev4sep.base.config.data.DataValidatorBuilder;
import com.dev4sep.base.config.exception.InvalidJsonException;
import com.dev4sep.base.config.exception.PlatformApiDataValidationException;
import com.dev4sep.base.config.serialization.FromJsonHelper;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author YISivlay
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserDataValidator {

    private final FromJsonHelper fromApiJsonHelper;
    private final PasswordValidationPolicyRepository passwordValidationPolicy;

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, UserApiConstants.CREATE_UPDATE_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder validatorBuilder = new DataValidatorBuilder(dataValidationErrors).resource(UserApiConstants.RESOURCE);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String username = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.username, element);
        validatorBuilder.reset().parameter(UserApiConstants.username).value(username).notBlank().notExceedingLengthOf(100);

        final String firstname = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.firstname, element);
        validatorBuilder.reset().parameter(UserApiConstants.firstname).value(firstname).notBlank().notExceedingLengthOf(100);

        final String lastname = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.lastname, element);
        validatorBuilder.reset().parameter(UserApiConstants.lastname).value(lastname).notBlank().notExceedingLengthOf(100);

        final Boolean isSendPasswordToEmail = this.fromApiJsonHelper.extractBooleanNamed(UserApiConstants.isSendPasswordToEmail, element);
        if (isSendPasswordToEmail != null && isSendPasswordToEmail) {
            final String email = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.email, element);
            validatorBuilder.reset().parameter(UserApiConstants.email).value(email).notBlank().notExceedingLengthOf(100);
        } else {
            final String password = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.password, element);
            final String repeatPassword = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.repeatPassword, element);
            final PasswordValidationPolicy validationPolicy = this.passwordValidationPolicy.findActivePasswordValidationPolicy();
            final String regex = validationPolicy.getRegex();
            final String description = validationPolicy.getDescription();
            validatorBuilder.reset().parameter(UserApiConstants.password).value(password).matchesRegularExpression(regex, description);
            if (StringUtils.isNotBlank(password)) {
                validatorBuilder.reset().parameter(UserApiConstants.password).value(password).equalToParameter(UserApiConstants.repeatPassword, repeatPassword);
            }
        }
        final Long officeId = this.fromApiJsonHelper.extractLongNamed(UserApiConstants.officeId, element);
        validatorBuilder.reset().parameter(UserApiConstants.officeId).value(officeId).notNull().integerGreaterThanZero();

        if (this.fromApiJsonHelper.parameterExists(UserApiConstants.isPasswordNeverExpires, element)) {
            final boolean passwordNeverExpire = this.fromApiJsonHelper.extractBooleanNamed(UserApiConstants.isPasswordNeverExpires, element);
            validatorBuilder.reset().parameter(UserApiConstants.isPasswordNeverExpires).value(passwordNeverExpire).validateForBooleanValue();
        }

        Boolean isSelfServiceUser = null;
        if (this.fromApiJsonHelper.parameterExists(UserApiConstants.isSelfServiceUser, element)) {
            isSelfServiceUser = this.fromApiJsonHelper.extractBooleanNamed(UserApiConstants.isSelfServiceUser, element);
            if (isSelfServiceUser == null) {
                validatorBuilder.reset().parameter(UserApiConstants.isSelfServiceUser).trueOrFalseRequired(false);
            }
        }

        final String[] roles = this.fromApiJsonHelper.extractArrayNamed(UserApiConstants.roles, element);
        validatorBuilder.reset().parameter(UserApiConstants.roles).value(roles).arrayNotEmpty();

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, UserApiConstants.CREATE_UPDATE_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder validatorBuilder = new DataValidatorBuilder(dataValidationErrors).resource(UserApiConstants.RESOURCE);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists(UserApiConstants.officeId, element)) {
            final Long officeId = this.fromApiJsonHelper.extractLongNamed(UserApiConstants.officeId, element);
            validatorBuilder.reset().parameter(UserApiConstants.officeId).value(officeId).notNull().integerGreaterThanZero();
        }
        if (this.fromApiJsonHelper.parameterExists(UserApiConstants.username, element)) {
            final String username = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.username, element);
            validatorBuilder.reset().parameter(UserApiConstants.username).value(username).notBlank().notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(UserApiConstants.firstname, element)) {
            final String firstname = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.firstname, element);
            validatorBuilder.reset().parameter(UserApiConstants.firstname).value(firstname).notBlank().notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(UserApiConstants.lastname, element)) {
            final String lastname = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.lastname, element);
            validatorBuilder.reset().parameter(UserApiConstants.lastname).value(lastname).notBlank().notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(UserApiConstants.email, element)) {
            final String email = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.email, element);
            validatorBuilder.reset().parameter(UserApiConstants.email).value(email).notBlank().notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(UserApiConstants.roles, element)) {
            final String[] roles = this.fromApiJsonHelper.extractArrayNamed(UserApiConstants.roles, element);
            validatorBuilder.reset().parameter(UserApiConstants.roles).value(roles).arrayNotEmpty();
        }
        if (this.fromApiJsonHelper.parameterExists(UserApiConstants.password, element)) {
            final String password = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.password, element);
            final String repeatPassword = this.fromApiJsonHelper.extractStringNamed(UserApiConstants.repeatPassword, element);

            final PasswordValidationPolicy validationPolicy = this.passwordValidationPolicy.findActivePasswordValidationPolicy();
            final String regex = validationPolicy.getRegex();
            final String description = validationPolicy.getDescription();
            validatorBuilder.reset().parameter(UserApiConstants.password).value(password).matchesRegularExpression(regex, description);

            if (StringUtils.isNotBlank(password)) {
                validatorBuilder.reset().parameter(UserApiConstants.password).value(password).equalToParameter(UserApiConstants.repeatPassword, repeatPassword);
            }
        }
        if (this.fromApiJsonHelper.parameterExists(UserApiConstants.isPasswordNeverExpires, element)) {
            final boolean passwordNeverExpire = this.fromApiJsonHelper.extractBooleanNamed(UserApiConstants.isPasswordNeverExpires, element);
            validatorBuilder.reset().parameter(UserApiConstants.isPasswordNeverExpires).value(passwordNeverExpire).validateForBooleanValue();
        }
        Boolean isSelfServiceUser = null;
        if (this.fromApiJsonHelper.parameterExists(UserApiConstants.isSelfServiceUser, element)) {
            isSelfServiceUser = this.fromApiJsonHelper.extractBooleanNamed(UserApiConstants.isSelfServiceUser, element);
            if (isSelfServiceUser == null) {
                validatorBuilder.reset().parameter(UserApiConstants.isSelfServiceUser).trueOrFalseRequired(false);
            }
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }
}
