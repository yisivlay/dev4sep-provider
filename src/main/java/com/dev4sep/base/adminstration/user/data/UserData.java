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
package com.dev4sep.base.adminstration.user.data;

import com.dev4sep.base.adminstration.role.data.RoleData;
import com.dev4sep.base.organisation.office.data.OfficeData;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * @author YISivlay
 */
@Data
@Builder
public class UserData {

    private final Long id;
    private final String username;
    private final OfficeData officeData;
    private final String firstname;
    private final String lastname;
    private final String email;
    private final Boolean isPasswordNeverExpire;
    private List<Long> roles;
    private Boolean sendPasswordToEmail;
    private final List<OfficeData> allowedOffices;
    private final List<RoleData> availableRoles;
    private final List<RoleData> selfServiceRoles;
    private final List<RoleData> selectedRoles;
    private final Boolean isSelfServiceUser;

}
