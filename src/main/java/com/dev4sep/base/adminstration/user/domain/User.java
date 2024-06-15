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

import com.dev4sep.base.adminstration.role.domain.Role;
import com.dev4sep.base.config.auditing.domain.AbstractPersistableCustom;
import com.dev4sep.base.config.security.domain.PlatformUser;
import com.dev4sep.base.config.security.exception.NoAuthorizationException;
import com.dev4sep.base.organisation.office.domain.Office;
import jakarta.persistence.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * @author YISivlay
 */
@Entity
@Table(name = "tbl_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}, name = "username"),
        @UniqueConstraint(columnNames = {"email"}, name = "email")
})
public class User extends AbstractPersistableCustom implements PlatformUser {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private final Set<Role> roles;

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
    private boolean enabled;

    @Column(name = "last_time_password_updated")
    private LocalDate lastTimePasswordUpdated;

    @Column(name = "is_password_never_expires", nullable = false)
    private boolean isPasswordNeverExpires;

    @Column(name = "is_self_service_user", nullable = false)
    private boolean isSelfServiceUser;

    @Column(name = "is_cannot_change_password")
    private Boolean isCannotChangePassword;

    @Column(name = "is_deleted", nullable = false)
    private boolean deleted;

    protected User() {
        this.isAccountNonLocked = false;
        this.isCredentialsNonExpired = false;
        this.roles = new HashSet<>();
    }

    private boolean hasAllFunctionsPermission() {
        return this.roles.stream().anyMatch(role -> role.hasPermissionTo("ALL_FUNCTIONS"));
    }

    private boolean hasPermissionTo(final String permissionCode) {
        var hasPermission = hasAllFunctionsPermission();
        if (!hasPermission) {
            if (this.roles.stream().anyMatch(role -> role.hasPermissionTo(permissionCode))) {
                hasPermission = true;
            }
        }
        return hasPermission;
    }

    public boolean hasNotPermissionForAnyOf(final String... permissionCodes) {
        var hasNotPermission = true;
        for (final var permissionCode : permissionCodes) {
            final var checkPermission = hasPermissionTo(permissionCode);
            if (checkPermission) {
                hasNotPermission = false;
                break;
            }
        }
        return hasNotPermission;
    }

    private void validateHasPermission(final String prefix, final String resourceType) {
        final var authorizationMessage = "User has no authority to " + prefix + " " + resourceType.toLowerCase() + "s";
        final var matchPermission = prefix + "_" + resourceType.toUpperCase();

        if (!hasNotPermissionForAnyOf("ALL_FUNCTIONS", "ALL_FUNCTIONS_READ", matchPermission)) {
            return;
        }

        throw new NoAuthorizationException(authorizationMessage);
    }

    public void validateHasReadPermission(final String resourceType) {
        validateHasPermission("READ", resourceType);
    }

    public Set<Role> getRoles() {
        return this.roles;
    }

    private List<GrantedAuthority> populateGrantedAuthorities() {
        return this.roles.stream()
                .map(Role::getPermissions)
                .flatMap(Collection::stream)
                .map(permission -> new SimpleGrantedAuthority(permission.getCode()))
                .collect(Collectors.toList());
    }

    private boolean hasNotPermissionTo(final String permissionCode) {
        return !hasPermissionTo(permissionCode);
    }

    public void validateHasPermissionTo(final String function) {
        if (hasNotPermissionTo(function)) {
            final String authorizationMessage = "User has no authority to: " + function;
            throw new NoAuthorizationException(authorizationMessage);
        }
    }

    public Office getOffice() {
        return office;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return populateGrantedAuthorities();
    }

    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return this.isAccountNonExpired;
    }

    @Override
    public boolean isAccountNonLocked() {
        return this.isAccountNonLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return this.isCredentialsNonExpired;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }
}
