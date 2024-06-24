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
package com.dev4sep.base.config.command.domain;

import com.dev4sep.base.adminstration.user.domain.PlatformPasswordEncoder;
import com.dev4sep.base.config.security.domain.BasicPasswordEncodablePlatformUser;
import com.dev4sep.base.config.security.domain.PlatformUser;
import com.dev4sep.base.config.serialization.FromJsonHelper;
import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.Arrays;
import java.util.Objects;
import java.util.Set;

/**
 * @author YISivlay
 */
@Getter
@Builder
public class JsonCommand {

    private final String json;
    private final JsonElement parsedCommand;
    private final FromJsonHelper fromApiJsonHelper;
    private final Long commandId;
    private final Long resourceId;
    private final Long subresourceId;
    private final String entityName;
    private final String href;

    public String dateFormat() {
        return stringValueOfParameterNamed("dateFormat");
    }

    public String locale() {
        return stringValueOfParameterNamed("locale");
    }

    public Long commandId() {
        return this.commandId;
    }

    public Long longValueOfParameterNamed(final String parameterName) {
        return this.fromApiJsonHelper.extractLongNamed(parameterName, this.parsedCommand);
    }

    public LocalDate localDateValueOfParameterNamed(final String parameterName) {
        return this.fromApiJsonHelper.extractLocalDateNamed(parameterName, this.parsedCommand);
    }

    public String stringValueOfParameterNamed(final String parameterName) {
        final var value = this.fromApiJsonHelper.extractStringNamed(parameterName, this.parsedCommand);
        return StringUtils.defaultIfEmpty(value, "");
    }

    public String stringValueOfParameterNamedAllowingNull(final String parameterName) {
        return this.fromApiJsonHelper.extractStringNamed(parameterName, this.parsedCommand);
    }

    public boolean booleanPrimitiveValueOfParameterNamed(final String parameterName) {
        final var value = this.fromApiJsonHelper.extractBooleanNamed(parameterName, this.parsedCommand);
        return ObjectUtils.defaultIfNull(value, Boolean.FALSE);
    }

    public Boolean booleanObjectValueOfParameterNamed(final String parameterName) {
        return this.fromApiJsonHelper.extractBooleanNamed(parameterName, this.parsedCommand);
    }

    public String[] arrayValueOfParameterNamed(final String parameterName) {
        return this.fromApiJsonHelper.extractArrayNamed(parameterName, this.parsedCommand);
    }

    public boolean hasParameter(final String parameterName) {
        return parameterExists(parameterName);
    }

    public JsonElement parsedJson() {
        return this.parsedCommand;
    }

    public boolean parameterExists(final String parameterName) {
        return this.fromApiJsonHelper.parameterExists(parameterName, this.parsedCommand);
    }

    private boolean differenceExists(final Number baseValue, final Number workingCopyValue) {
        return !Objects.equals(baseValue, workingCopyValue);
    }

    private boolean differenceExists(final TemporalAccessor baseValue, final TemporalAccessor workingCopyValue) {
        return !Objects.equals(baseValue, workingCopyValue);
    }

    private boolean differenceExists(final String baseValue, final String workingCopyValue) {
        return !Objects.equals(baseValue, workingCopyValue);
    }

    private boolean differenceExists(final String[] baseValue, final String[] workingCopyValue) {
        Arrays.sort(baseValue);
        Arrays.sort(workingCopyValue);
        return !Arrays.equals(baseValue, workingCopyValue);
    }

    private boolean differenceExists(final Boolean baseValue, final Boolean workingCopyValue) {
        return !Objects.equals(baseValue, workingCopyValue);
    }

    public String passwordValueOfParameterNamed(final String parameterName,
                                                final PlatformPasswordEncoder platformPasswordEncoder,
                                                final Long saltValue) {
        final String passwordPlainText = stringValueOfParameterNamed(parameterName);

        final PlatformUser dummyPlatformUser = new BasicPasswordEncodablePlatformUser()
                .setId(saltValue).setUsername("")
                .setPassword(passwordPlainText);
        return platformPasswordEncoder.encode(dummyPlatformUser);
    }

    public boolean isChangeInPasswordParameterNamed(final String parameterName,
                                                    final String existingValue,
                                                    final PlatformPasswordEncoder platformPasswordEncoder,
                                                    final Long saltValue) {
        boolean isChanged = false;
        if (parameterExists(parameterName)) {
            final String workingValue = passwordValueOfParameterNamed(parameterName, platformPasswordEncoder, saltValue);
            isChanged = differenceExists(existingValue, workingValue);
        }
        return isChanged;
    }

    public boolean isChangeInLongParameterNamed(final String parameterName, final Long existingValue) {
        var isChanged = false;
        if (parameterExists(parameterName)) {
            final var workingValue = longValueOfParameterNamed(parameterName);
            isChanged = differenceExists(existingValue, workingValue);
        }
        return isChanged;
    }

    public boolean isChangeInLocalDateParameterNamed(final String parameterName, final LocalDate existingValue) {
        var isChanged = false;
        if (parameterExists(parameterName)) {
            final var workingValue = localDateValueOfParameterNamed(parameterName);
            isChanged = differenceExists(existingValue, workingValue);
        }
        return isChanged;
    }

    public boolean isChangeInStringParameterNamed(final String parameterName, final String existingValue) {
        var isChanged = false;
        if (parameterExists(parameterName)) {
            final var workingValue = stringValueOfParameterNamed(parameterName);
            isChanged = differenceExists(existingValue, workingValue);
        }
        return isChanged;
    }

    public boolean isChangeInArrayParameterNamed(final String parameterName, final String[] existingValue) {
        var isChanged = false;
        if (parameterExists(parameterName)) {
            final var workingValue = arrayValueOfParameterNamed(parameterName);
            isChanged = differenceExists(existingValue, workingValue);
        }
        return isChanged;
    }

    public boolean isChangeInBooleanParameterNamed(final String parameterName, final Boolean existingValue) {
        var isChanged = false;
        if (parameterExists(parameterName)) {
            final var workingValue = booleanObjectValueOfParameterNamed(parameterName);
            isChanged = differenceExists(existingValue, workingValue);
        }
        return isChanged;
    }

    public void checkForUnsupportedParameters(final Type typeOfMap, final String json, final Set<String> requestDataParameters) {
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, requestDataParameters);
    }

    public Integer integerValueSansLocaleOfParameterNamed(final String parameterName) {
        return this.fromApiJsonHelper.extractIntegerSansLocaleNamed(parameterName, this.parsedCommand);
    }
}
