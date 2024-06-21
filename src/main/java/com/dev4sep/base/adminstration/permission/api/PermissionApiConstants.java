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
package com.dev4sep.base.adminstration.permission.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * @author YISivlay
 */
public class PermissionApiConstants {

    public static final String PERMISSIONS = "PERMISSION";
    public static final String RESOURCE = "permission";
    public static final String PATH = "/permissions";

    public static final String grouping = "grouping";
    public static final String code = "code";
    public static final String entityName = "entityName";
    public static final String actionName = "actionName";
    public static final String selected = "selected";
    public static final String isMakerChecker = "isMakerChecker";
    public static final String permissions = "permissions";

    public static final Set<String> RESPONSE_PARAMETERS = new HashSet<>(Arrays.asList(
            grouping,
            code,
            entityName,
            actionName,
            selected,
            isMakerChecker
    ));

    public static final Set<String> SUPPORTED_PARAMETERS = new HashSet<>(List.of(permissions));

}
