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
    public static final String RESOURCE = "user";
    public static final String PATH = "/users";

    public static final String id = "id";
    public static final String officeId = "officeId";
    public static final String officeName = "officeName";
    public static final String username = "username";
    public static final String password = "password";
    public static final String passwordEncoded = "passwordEncoded";
    public static final String repeatPassword = "repeatPassword";
    public static final String firstname = "firstname";
    public static final String lastname = "lastname";
    public static final String email = "email";
    public static final String roles = "roles";
    public static final String isSendPasswordToEmail = "isSendPasswordToEmail";
    public static final String isPasswordNeverExpires = "isPasswordNeverExpires";
    public static final String isSelfServiceUser = "isSelfServiceUser";
    public static final String allowedOffices = "allowedOffices";
    public static final String availableRoles = "availableRoles";
    public static final String selectedRoles = "selectedRoles";
    public static final String notSelectedRoles = "notSelectedRoles";

    public static final String systemUserName = "system";
    public static final String removalDate = "removalDate";
    public static final int numberOfPreviousPasswords = 3;

    public static final String locale = "locale";

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

    public static final Set<String> CREATE_UPDATE_PARAMETERS = new HashSet<>(Arrays.asList(
            officeId,
            username,
            password,
            passwordEncoded,
            repeatPassword,
            firstname,
            lastname,
            email,
            notSelectedRoles,
            roles,
            isSendPasswordToEmail,
            isPasswordNeverExpires,
            isSelfServiceUser,
            locale
    ));

}
