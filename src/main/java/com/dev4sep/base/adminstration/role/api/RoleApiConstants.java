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
package com.dev4sep.base.adminstration.role.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author YISivlay
 */
public class RoleApiConstants {

    public static final String PERMISSIONS = "ROLE";
    public static final String RESOURCE = "role";
    public static final String PATH = "/roles";

    public static final String id = "id";
    public static final String name = "name";
    public static final String description = "description";
    public static final String availablePermissions = "availablePermissions";
    public static final String selectedPermissions = "selectedPermissions";
    public static final String permissionUsageData = "permissionUsageData";
    public static final String isDisable = "isDisable";
    public static final String isEnable = "isEnable";

    public static final String locale = "locale";

    public static final Set<String> RESPONSE_PARAMETERS = new HashSet<>(Arrays.asList(
            id,
            name,
            description,
            availablePermissions,
            selectedPermissions,
            permissionUsageData,
            isDisable,
            isEnable
    ));

    public static final Set<String> CREATE_UPDATE_PARAMETERS = new HashSet<>(Arrays.asList(
            name,
            description,
            availablePermissions,
            selectedPermissions,
            permissionUsageData,
            isDisable,
            isEnable,
            locale
    ));
}
