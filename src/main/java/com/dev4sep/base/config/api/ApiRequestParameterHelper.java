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
package com.dev4sep.base.config.api;

import com.dev4sep.base.config.serialization.ApiRequestJsonSerializationSettings;
import jakarta.ws.rs.core.MultivaluedMap;
import org.springframework.stereotype.Component;

import java.util.Set;

/**
 * @author YISivlay
 */
@Component
public class ApiRequestParameterHelper {

    public ApiRequestJsonSerializationSettings process(final MultivaluedMap<String, String> queryParameters,
                                                       final Set<String> mandatoryResponseParameters) {

        final Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(queryParameters);
        if (!responseParameters.isEmpty()) {
            responseParameters.addAll(mandatoryResponseParameters);
        }
        final boolean prettyPrint = ApiParameterHelper.prettyPrint(queryParameters);
        final boolean template = ApiParameterHelper.template(queryParameters);
        final boolean makerChecker = ApiParameterHelper.makerChecker(queryParameters);
        final boolean includeJson = ApiParameterHelper.includeJson(queryParameters);

        return ApiRequestJsonSerializationSettings.from(prettyPrint, responseParameters, template, makerChecker, includeJson);
    }

    public ApiRequestJsonSerializationSettings process(final MultivaluedMap<String, String> queryParameters) {

        final Set<String> responseParameters = ApiParameterHelper.extractFieldsForResponseIfProvided(queryParameters);
        final boolean prettyPrint = ApiParameterHelper.prettyPrint(queryParameters);
        final boolean template = ApiParameterHelper.template(queryParameters);
        final boolean makerChecker = ApiParameterHelper.makerChecker(queryParameters);
        final boolean includeJson = ApiParameterHelper.includeJson(queryParameters);

        return ApiRequestJsonSerializationSettings.from(prettyPrint, responseParameters, template, makerChecker, includeJson);
    }

}
