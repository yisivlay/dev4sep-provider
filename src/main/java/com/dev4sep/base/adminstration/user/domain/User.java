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

import com.dev4sep.base.config.auditing.domain.AbstractAuditableCustom;
import com.dev4sep.base.organisation.office.domain.Office;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * @author YISivlay
 */
@Entity
@Table(name = "tbl_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}, name = "username"),
        @UniqueConstraint(columnNames = {"email"}, name = "email")
})
public class User extends AbstractAuditableCustom {

    @ManyToOne
    @JoinColumn(name = "office_id", nullable = false)
    private Office office;

    @Column(name = "username", nullable = false, length = 100)
    private String username;

    @Column(name = "email", nullable = false, length = 100)
    private String email;

    @Column(name = "firstname", nullable = false, length = 100)
    private String firstname;

    @Column(name = "lastname", nullable = false, length = 100)
    private String lastname;

    @Column(name = "password", nullable = false)
    private String password;

    @Column(name = "is_first_time_login_remaining", nullable = false)
    private boolean isFirstTimeLoginRemaining;

    @Column(name = "is_non_expired", nullable = false)
    private boolean isAccountNonExpired;

    @Column(name = "is_non_locked", nullable = false)
    private boolean isAccountNonLocked;

    @Column(name = "is_non_expired_credentials", nullable = false)
    private boolean isCredentialsNonExpired;

    @Column(name = "is_enabled", nullable = false)
    private boolean isEnabled;

    @Column(name = "last_time_password_updated")
    private LocalDate lastTimePasswordUpdated;

    @Column(name = "is_password_never_expires", nullable = false)
    private boolean isPasswordNeverExpires;

    @Column(name = "is_self_service_user", nullable = false)
    private boolean isSelfServiceUser;

    @Column(name = "is_cannot_change_password")
    private Boolean isCannotChangePassword;

    @Column(name = "is_deleted", nullable = false)
    private boolean isDeleted;

    protected User() {
    }
}
