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
package com.dev4sep.base.adminstration.user.domain;

import com.dev4sep.base.config.auditing.domain.AbstractPersistableCustom;
import com.dev4sep.base.config.utils.DateUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDate;

/**
 * @author YISivlay
 */
@Getter
@Entity
@Table(name = "tbl_user_previous_password")
public class UserPreviousPassword extends AbstractPersistableCustom {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "removal_date", nullable = false)
    private LocalDate removalDate;

    @Column(name = "password", nullable = false)
    private String password;

    protected UserPreviousPassword() {
    }

    public UserPreviousPassword(final User user) {
        this.userId = user.getId();
        this.password = user.getPassword().trim();
        this.removalDate = DateUtils.getLocalDateOfTenant();
    }
}
