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

import jakarta.ws.rs.core.MultivaluedMap;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author YISivlay
 */
public final class ApiParameterHelper {

    public static Set<String> extractFieldsForResponseIfProvided(final MultivaluedMap<String, String> queryParams) {
        Set<String> fields = new HashSet<>();
        String commaSeparatedParameters = "";
        if (queryParams.getFirst("fields") != null) {
            commaSeparatedParameters = queryParams.getFirst("fields");
            if (StringUtils.isNotBlank(commaSeparatedParameters)) {
                fields = new HashSet<>(Arrays.asList(commaSeparatedParameters.split("\\s*,\\s*")));
            }
        }
        return fields;
    }

    public static boolean prettyPrint(final MultivaluedMap<String, String> queryParams) {
        boolean prettyPrint = false;
        if (queryParams.getFirst("pretty") != null) {
            final String prettyPrintValue = queryParams.getFirst("pretty");
            prettyPrint = "true".equalsIgnoreCase(prettyPrintValue);
        }
        return prettyPrint;
    }

    public static boolean template(final MultivaluedMap<String, String> queryParams) {
        boolean template = false;
        if (queryParams.getFirst("template") != null) {
            final String prettyPrintValue = queryParams.getFirst("template");
            template = "true".equalsIgnoreCase(prettyPrintValue);
        }
        return template;
    }

    public static boolean makerChecker(final MultivaluedMap<String, String> queryParams) {
        boolean makerCheckerable = false;
        if (queryParams.getFirst("makerChecker") != null) {
            final String prettyPrintValue = queryParams.getFirst("makerChecker");
            makerCheckerable = "true".equalsIgnoreCase(prettyPrintValue);
        }
        return makerCheckerable;
    }

    public static boolean includeJson(final MultivaluedMap<String, String> queryParams) {
        boolean includeJson = false;
        if (queryParams.getFirst("includeJson") != null) {
            final String includeJsonValue = queryParams.getFirst("includeJson");
            includeJson = "true".equalsIgnoreCase(includeJsonValue);
        }
        return includeJson;
    }

    public static boolean pagination(final MultivaluedMap<String, String> queryParams) {
        boolean pagination = false;
        if (queryParams.getFirst("pagination") != null) {
            final String prettyPrintValue = queryParams.getFirst("pagination");
            pagination = "true".equalsIgnoreCase(prettyPrintValue);
        }
        return pagination;
    }

}
