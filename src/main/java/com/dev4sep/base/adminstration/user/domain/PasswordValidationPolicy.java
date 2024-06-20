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
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * @author YISivlay
 */
@Getter
@Entity
@Table(name = "tbl_password_validation_policy")
public class PasswordValidationPolicy extends AbstractPersistableCustom {

    @Column(name = "regex", nullable = false)
    private String regex;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "key")
    private String key;

    protected PasswordValidationPolicy() {
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void deActivate() {
        this.isActive = false;
    }
}
