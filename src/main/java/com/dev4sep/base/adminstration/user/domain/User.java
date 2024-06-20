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
import com.dev4sep.base.adminstration.user.api.UserApiConstants;
import com.dev4sep.base.config.auditing.domain.AbstractPersistableCustom;
import com.dev4sep.base.config.command.domain.JsonCommand;
import com.dev4sep.base.config.security.domain.PlatformUser;
import com.dev4sep.base.config.security.exception.NoAuthorizationException;
import com.dev4sep.base.config.security.service.RandomPasswordGenerator;
import com.dev4sep.base.config.utils.DateUtils;
import com.dev4sep.base.organisation.office.domain.Office;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author YISivlay
 */
@Setter
@Getter
@Entity
@Table(name = "tbl_user", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"username"}, name = "username"),
        @UniqueConstraint(columnNames = {"email"}, name = "email")
})
public class User extends AbstractPersistableCustom implements PlatformUser {

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_user_role", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
    private Set<Role> roles;

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
    private final boolean isAccountNonLocked;

    @Column(name = "is_non_expired_credentials", nullable = false)
    private final boolean isCredentialsNonExpired;

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

    public User(final Office office,
                final org.springframework.security.core.userdetails.User user,
                final Set<Role> roles,
                final String email,
                final String firstname,
                final String lastname,
                final boolean isPasswordNeverExpires,
                final boolean isSelfServiceUser) {
        this.office = office;
        this.username = user.getUsername();
        this.email = email;
        this.firstname = firstname;
        this.lastname = lastname;
        this.password = user.getPassword();
        this.isAccountNonExpired = user.isAccountNonExpired();
        this.isAccountNonLocked = user.isAccountNonLocked();
        this.isCredentialsNonExpired = user.isCredentialsNonExpired();
        this.enabled = user.isEnabled();
        this.roles = roles;
        this.isFirstTimeLoginRemaining = true;
        this.lastTimePasswordUpdated = DateUtils.getLocalDateOfTenant();
        this.isPasswordNeverExpires = isPasswordNeverExpires;
        this.isSelfServiceUser = isSelfServiceUser;
        this.isCannotChangePassword = false;

    }

    public static User fromJson(final Office office, final Set<Role> roles, final JsonCommand command) {
        final var username = command.stringValueOfParameterNamed(UserApiConstants.username);
        var password = command.stringValueOfParameterNamed(UserApiConstants.password);
        final var isSendPasswordToEmail = command.booleanPrimitiveValueOfParameterNamed(UserApiConstants.isSendPasswordToEmail);
        if (isSendPasswordToEmail) {
            password = new RandomPasswordGenerator(13).generate();
        }
        final var isPasswordNeverExpires = command.booleanPrimitiveValueOfParameterNamed(UserApiConstants.isPasswordNeverExpires);
        final Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("DUMMY_ROLE_NOT_USED_OR_PERSISTED_TO_AVOID_EXCEPTION"));
        final var user = new org.springframework.security.core.userdetails.User(username, password, true, true, true, true, authorities);

        final var email = command.stringValueOfParameterNamedAllowingNull(UserApiConstants.email);
        final var firstname = command.stringValueOfParameterNamed(UserApiConstants.firstname);
        final var lastname = command.stringValueOfParameterNamed(UserApiConstants.lastname);
        final var isSelfServiceUser = command.booleanPrimitiveValueOfParameterNamed(UserApiConstants.isSelfServiceUser);

        return new User(
                office,
                user,
                roles,
                email,
                firstname,
                lastname,
                isPasswordNeverExpires,
                isSelfServiceUser
        );
    }

    public Map<String, Object> update(final JsonCommand command, final PlatformPasswordEncoder platformPasswordEncoder) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final var password = UserApiConstants.password;
        if (command.hasParameter(password)) {
            if (command.isChangeInPasswordParameterNamed(password, this.password, platformPasswordEncoder, getId())) {
                final var passwordEncodedValue = command.passwordValueOfParameterNamed(password, platformPasswordEncoder, getId());
                actualChanges.put(password, passwordEncodedValue);
                updatePassword(passwordEncodedValue);
            }
        }

        final var passwordEncoded = UserApiConstants.passwordEncoded;
        if (command.hasParameter(passwordEncoded)) {
            if (command.isChangeInStringParameterNamed(passwordEncoded, this.password)) {
                final var newValue = command.stringValueOfParameterNamed(passwordEncoded);
                actualChanges.put(passwordEncoded, newValue);
                updatePassword(newValue);
            }
        }
        final var officeId = UserApiConstants.officeId;
        if (command.isChangeInLongParameterNamed(officeId, this.office.getId())) {
            final var newValue = command.longValueOfParameterNamed(officeId);
            actualChanges.put(officeId, newValue);
        }
        final var roles = UserApiConstants.roles;
        if (command.isChangeInArrayParameterNamed(roles, getRolesAsIdStringArray())) {
            final var newValue = command.arrayValueOfParameterNamed(roles);
            actualChanges.put(roles, newValue);
        }

        final var username = UserApiConstants.username;
        if (command.isChangeInStringParameterNamed(username, this.username)) {
            if (isSystemUser()) {
                throw new NoAuthorizationException("User name of current system user may not be modified");
            }
            final var newValue = command.stringValueOfParameterNamed(username);
            actualChanges.put(username, newValue);
            this.username = newValue;
        }
        final var firstname = UserApiConstants.firstname;
        if (command.isChangeInStringParameterNamed(firstname, this.firstname)) {
            final var newValue = command.stringValueOfParameterNamed(firstname);
            actualChanges.put(firstname, newValue);
            this.firstname = newValue;
        }

        final var lastname = UserApiConstants.lastname;
        if (command.isChangeInStringParameterNamed(lastname, this.lastname)) {
            final var newValue = command.stringValueOfParameterNamed(lastname);
            actualChanges.put(lastname, newValue);
            this.lastname = newValue;
        }

        final var email = UserApiConstants.email;
        if (command.isChangeInStringParameterNamed(email, this.email)) {
            final var newValue = command.stringValueOfParameterNamed(email);
            actualChanges.put(email, newValue);
            this.email = newValue;
        }

        final var isPasswordNeverExpires = UserApiConstants.isPasswordNeverExpires;
        if (command.hasParameter(isPasswordNeverExpires)) {
            if (command.isChangeInBooleanParameterNamed(isPasswordNeverExpires, this.isPasswordNeverExpires)) {
                final var newValue = command.booleanPrimitiveValueOfParameterNamed(isPasswordNeverExpires);
                actualChanges.put(isPasswordNeverExpires, newValue);
                this.isPasswordNeverExpires = newValue;
            }
        }

        final var isSelfServiceUser = UserApiConstants.isSelfServiceUser;
        if (command.hasParameter(isSelfServiceUser)) {
            if (command.isChangeInBooleanParameterNamed(isSelfServiceUser, this.isSelfServiceUser)) {
                final var newValue = command.booleanPrimitiveValueOfParameterNamed(isSelfServiceUser);
                actualChanges.put(isSelfServiceUser, newValue);
                this.isSelfServiceUser = newValue;
            }
        }

        return actualChanges;
    }

    public boolean isSystemUser() {
        if (this.username.equals(UserApiConstants.systemUserName)) {
            return true;
        }
        return false;
    }

    private String[] getRolesAsIdStringArray() {
        return this.roles.stream().map(role -> role.getId().toString()).toArray(String[]::new);
    }

    public void updatePassword(final String encodePassword) {
        if (isCannotChangePassword != null && isCannotChangePassword) {
            throw new NoAuthorizationException("Password of this user may not be modified");
        }

        this.password = encodePassword;
        this.isFirstTimeLoginRemaining = false;
        this.lastTimePasswordUpdated = DateUtils.getLocalDateOfTenant();

    }

    public String getEncodedPassword(final JsonCommand command, final PlatformPasswordEncoder platformPasswordEncoder) {
        String passwordEncodedValue = null;
        if (command.hasParameter(UserApiConstants.password)) {
            if (command.isChangeInPasswordParameterNamed(UserApiConstants.password, this.password, platformPasswordEncoder, getId())) {

                passwordEncodedValue = command.passwordValueOfParameterNamed(UserApiConstants.password, platformPasswordEncoder, getId());

            }
        } else if (command.hasParameter(UserApiConstants.passwordEncoded)) {
            if (command.isChangeInStringParameterNamed(UserApiConstants.passwordEncoded, this.password)) {

                passwordEncodedValue = command.stringValueOfParameterNamed(UserApiConstants.passwordEncoded);

            }
        }

        return passwordEncodedValue;
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
        final var matchPermission = prefix.toUpperCase() + "_" + resourceType.toUpperCase();

        if (!hasNotPermissionForAnyOf("ALL_FUNCTIONS", "ALL_FUNCTIONS_READ", matchPermission)) {
            return;
        }

        throw new NoAuthorizationException(authorizationMessage);
    }

    public void validateHasReadPermission(final String resourceType) {
        validateHasPermission("read", resourceType);
    }

    public void validateHasCreatePermission(final String resourceType) {
        validateHasPermission("create", resourceType);
    }

    public void validateHasUpdatePermission(final String resourceType) {
        validateHasPermission("update", resourceType);
    }

    public void validateHasDeletePermission(final String resourceType) {
        validateHasPermission("delete", resourceType);
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

    public boolean isCheckerSuperUser() {
        return hasPermissionTo("CHECKER_SUPER_USER");
    }

    public String getDisplayName() {
        String firstName = StringUtils.isNotBlank(this.firstname) ? this.firstname : "";
        if (StringUtils.isNotBlank(this.lastname)) {
            return firstName + " " + this.lastname;
        }
        return firstName;
    }

    public boolean isSelfServiceUser() {
        return isSelfServiceUser;
    }

    public void setRoles(final Set<Role> roles) {
        if (!roles.isEmpty()) {
            this.roles.clear();
            this.roles = roles;
        }
    }

    public void delete() {
        if (isSystemUser()) {
            throw new NoAuthorizationException("User configured as the system user cannot be deleted");
        }

        this.deleted = true;
        this.enabled = false;
        this.isAccountNonExpired = false;
        this.isFirstTimeLoginRemaining = true;
        this.username = getId() + "_DELETED_" + this.username;
        this.email = getId() + "_DELETED_" + this.email;
        this.roles.clear();
    }
}
