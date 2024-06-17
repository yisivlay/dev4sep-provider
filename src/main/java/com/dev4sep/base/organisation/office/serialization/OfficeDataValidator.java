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
package com.dev4sep.base.organisation.office.serialization;

import com.dev4sep.base.config.data.ApiParameterError;
import com.dev4sep.base.config.data.DataValidatorBuilder;
import com.dev4sep.base.config.exception.InvalidJsonException;
import com.dev4sep.base.config.exception.PlatformApiDataValidationException;
import com.dev4sep.base.config.serialization.FromJsonHelper;
import com.dev4sep.base.organisation.office.api.OfficesApiConstants;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author YISivlay
 */
@Component
public class OfficeDataValidator {

    private final FromJsonHelper fromJsonHelper;

    @Autowired
    public OfficeDataValidator(FromJsonHelper fromJsonHelper) {
        this.fromJsonHelper = fromJsonHelper;
    }

    public void validateForCreate(final String json) {
        if (StringUtils.isBlank(json)) throw new InvalidJsonException();

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();

        this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, json, OfficesApiConstants.CREATE_UPDATE_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder validatorBuilder = new DataValidatorBuilder(dataValidationErrors).resource(OfficesApiConstants.RESOURCE);

        final JsonElement element = this.fromJsonHelper.parse(json);

        final LocalDate openingDate = this.fromJsonHelper.extractLocalDateNamed(OfficesApiConstants.openingDate, element);
        validatorBuilder.reset().parameter(OfficesApiConstants.openingDate).value(openingDate).notNull();

        if (this.fromJsonHelper.parameterExists(OfficesApiConstants.externalId, element)) {
            final String externalId = this.fromJsonHelper.extractStringNamed(OfficesApiConstants.externalId, element);
            validatorBuilder.reset().parameter(OfficesApiConstants.externalId).value(externalId).notExceedingLengthOf(100);
        }

        if (this.fromJsonHelper.parameterExists(OfficesApiConstants.parentId, element)) {
            final Long parentId = this.fromJsonHelper.extractLongNamed(OfficesApiConstants.parentId, element);
            validatorBuilder.reset().parameter(OfficesApiConstants.parentId).value(parentId).notNull().integerGreaterThanZero();
        }

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(
                    "validation.msg.validation.errors.exist",
                    "Validation errors exist.",
                    dataValidationErrors
            );
        }
    }
}
