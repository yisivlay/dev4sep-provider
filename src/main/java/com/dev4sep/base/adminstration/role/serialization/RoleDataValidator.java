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
package com.dev4sep.base.adminstration.role.serialization;

import com.dev4sep.base.adminstration.role.api.RoleApiConstants;
import com.dev4sep.base.adminstration.user.api.UserApiConstants;
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
public class RoleDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, RoleApiConstants.CREATE_UPDATE_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder validatorBuilder = new DataValidatorBuilder(dataValidationErrors).resource(RoleApiConstants.RESOURCE);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed(RoleApiConstants.name, element);
        validatorBuilder.reset().parameter(UserApiConstants.username).value(name).notBlank().notExceedingLengthOf(100);

        final String description = this.fromApiJsonHelper.extractStringNamed(RoleApiConstants.description, element);
        validatorBuilder.reset().parameter(RoleApiConstants.description).value(description).notBlank().notExceedingLengthOf(255);

        if (this.fromApiJsonHelper.parameterExists(RoleApiConstants.isDisable, element)) {
            final Boolean isDisable = this.fromApiJsonHelper.extractBooleanNamed(RoleApiConstants.isDisable, element);
            validatorBuilder.reset().parameter(RoleApiConstants.isDisable).value(isDisable).validateForBooleanValue();
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    public void validateForUpdate(final String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, RoleApiConstants.CREATE_UPDATE_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder validatorBuilder = new DataValidatorBuilder(dataValidationErrors).resource(RoleApiConstants.RESOURCE);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        if (this.fromApiJsonHelper.parameterExists(RoleApiConstants.name, element)) {
            final String name = this.fromApiJsonHelper.extractStringNamed(RoleApiConstants.name, element);
            validatorBuilder.reset().parameter(RoleApiConstants.name).value(name).notBlank().notExceedingLengthOf(100);
        }
        if (this.fromApiJsonHelper.parameterExists(RoleApiConstants.description, element)) {
            final String description = this.fromApiJsonHelper.extractStringNamed(RoleApiConstants.description, element);
            validatorBuilder.reset().parameter(RoleApiConstants.description).value(description).notBlank().notExceedingLengthOf(255);
        }
        if (this.fromApiJsonHelper.parameterExists(RoleApiConstants.isDisable, element)) {
            final Boolean isDisable = this.fromApiJsonHelper.extractBooleanNamed(RoleApiConstants.isDisable, element);
            validatorBuilder.reset().parameter(RoleApiConstants.isDisable).value(isDisable).validateForBooleanValue();
        }
        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }
    }
}
