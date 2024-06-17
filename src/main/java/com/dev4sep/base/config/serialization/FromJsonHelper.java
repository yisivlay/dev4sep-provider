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
package com.dev4sep.base.config.serialization;

import com.dev4sep.base.config.exception.InvalidJsonException;
import com.dev4sep.base.config.exception.UnsupportedParameterException;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.*;

/**
 * @author YISivlay
 */
@Slf4j
@Primary
@Component
public class FromJsonHelper {

    private final Gson gsonConverter;
    private final JsonParserHelper helperDelegator;

    public FromJsonHelper() {
        this.gsonConverter = new Gson();
        this.helperDelegator = new JsonParserHelper();
    }

    public JsonElement parse(final String json) {

        JsonElement parsedElement = null;
        if (StringUtils.isNotBlank(json)) {
            parsedElement = JsonParser.parseString(json);
        }
        return parsedElement;
    }

    public Long extractLongNamed(final String parameterName, final JsonElement element) {
        return this.helperDelegator.extractLongNamed(parameterName, element, new HashSet<>());
    }

    public String extractStringNamed(final String parameterName, final JsonElement element) {
        return this.helperDelegator.extractStringNamed(parameterName, element, new HashSet<>());
    }

    public boolean parameterExists(final String parameterName, final JsonElement element) {
        return this.helperDelegator.parameterExists(parameterName, element);
    }

    public void checkForUnsupportedParameters(final Type typeOfMap, final String json, final Collection<String> supportedParams) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Map<String, Object> requestMap = this.gsonConverter.fromJson(json, typeOfMap);
        final List<String> unsupportedParameterList = new ArrayList<>();
        for (final var providedParameter : requestMap.keySet()) {
            if (!supportedParams.contains(providedParameter)) {
                unsupportedParameterList.add(providedParameter);
            }
        }

        if (!unsupportedParameterList.isEmpty()) {
            throw new UnsupportedParameterException(unsupportedParameterList);
        }
    }

    public LocalDate extractLocalDateNamed(final String parameterName, final JsonElement element) {
        return this.helperDelegator.extractLocalDateNamed(parameterName, element, new HashSet<>());
    }

}
