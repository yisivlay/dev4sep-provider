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

import com.dev4sep.base.config.serialization.FromJsonHelper;
import com.google.gson.JsonElement;
import lombok.Builder;
import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
import java.util.Objects;

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
}
