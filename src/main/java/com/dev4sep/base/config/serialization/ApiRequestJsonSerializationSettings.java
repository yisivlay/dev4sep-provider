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

import java.util.Set;

/**
 * @author YISivlay
 */
public class ApiRequestJsonSerializationSettings {

    private final boolean prettyPrint;
    private final Set<String> parametersForPartialResponse;
    private final boolean template;
    private final boolean makerChecker;
    private final boolean includeJson;
    private final boolean pagination;

    public ApiRequestJsonSerializationSettings(boolean prettyPrint,
                                               Set<String> parametersForPartialResponse,
                                               boolean template,
                                               boolean makerChecker,
                                               boolean includeJson,
                                               boolean pagination) {
        this.prettyPrint = prettyPrint;
        this.parametersForPartialResponse = parametersForPartialResponse;
        this.template = template;
        this.makerChecker = makerChecker;
        this.includeJson = includeJson;
        this.pagination = pagination;
    }

    public static ApiRequestJsonSerializationSettings from(final boolean prettyPrint,
                                                           final Set<String> parametersForPartialResponse,
                                                           final boolean template,
                                                           final boolean makerChecker,
                                                           final boolean includeJson,
                                                           final boolean pagination) {

        // just send by common ones like, prettyprint=false, empty response parameters
        return new ApiRequestJsonSerializationSettings(prettyPrint, parametersForPartialResponse, template, makerChecker, includeJson, pagination);
    }

    public boolean isPartialResponseRequired() {
        return !this.parametersForPartialResponse.isEmpty();
    }

    public boolean isPrettyPrint() {
        return this.prettyPrint;
    }

    public Set<String> getParametersForPartialResponse() {
        return this.parametersForPartialResponse;
    }

    public boolean isPagination() {
        return this.pagination;
    }
}
