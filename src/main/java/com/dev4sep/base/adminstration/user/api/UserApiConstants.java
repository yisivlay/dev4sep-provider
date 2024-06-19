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
package com.dev4sep.base.adminstration.user.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author YISivlay
 */
public class UserApiConstants {

    public static final String PERMISSIONS = "USER";
    public static final String PATH = "/users";

    public static String id = "id";
    public static String officeId = "officeId";
    public static String officeName = "officeName";
    public static String username = "username";
    public static String firstname = "firstname";
    public static String lastname = "lastname";
    public static String email = "email";
    public static String allowedOffices = "allowedOffices";
    public static String availableRoles = "availableRoles";
    public static String selectedRoles = "selectedRoles";

    public static final Set<String> RESPONSE_PARAMETERS = new HashSet<>(Arrays.asList(
            id,
            officeName,
            username,
            firstname,
            lastname,
            email,
            allowedOffices,
            availableRoles,
            selectedRoles
    ));

}
