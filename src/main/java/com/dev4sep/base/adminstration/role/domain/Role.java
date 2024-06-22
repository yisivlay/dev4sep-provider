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
package com.dev4sep.base.adminstration.role.domain;

import com.dev4sep.base.adminstration.permission.domain.Permission;
import com.dev4sep.base.adminstration.role.api.RoleApiConstants;
import com.dev4sep.base.adminstration.role.data.RoleData;
import com.dev4sep.base.config.auditing.domain.AbstractPersistableCustom;
import com.dev4sep.base.config.command.domain.JsonCommand;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.*;

/**
 * @author YISivlay
 */
@Getter
@Setter
@Entity
@Table(name = "tbl_role", uniqueConstraints = {@UniqueConstraint(columnNames = {"name"}, name = "name")})
public class Role extends AbstractPersistableCustom implements Serializable {

    @Column(name = "name", unique = true, nullable = false, length = 100)
    private String name;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_disabled", nullable = false)
    private Boolean disabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "tbl_role_permission", joinColumns = @JoinColumn(name = "role_id"), inverseJoinColumns = @JoinColumn(name = "permission_id"))
    private Set<Permission> permissions = new HashSet<>();

    protected Role() {
    }

    public Role(final String name, final String description, final Boolean isDisable) {
        this.name = name;
        this.description = description;
        this.disabled = isDisable != null ? isDisable : false;
    }

    public static Role fromJson(JsonCommand command) {

        final String name = command.stringValueOfParameterNamed(RoleApiConstants.name);
        final String description = command.stringValueOfParameterNamed(RoleApiConstants.description);
        final Boolean isDisable = command.booleanObjectValueOfParameterNamed(RoleApiConstants.isDisable);

        return new Role(name, description, isDisable);
    }

    public Map<String, Object> update(JsonCommand command) {
        final Map<String, Object> actualChanges = new LinkedHashMap<>(7);

        final String name = RoleApiConstants.name;
        if (command.isChangeInStringParameterNamed(name, this.name)) {
            final String newValue = command.stringValueOfParameterNamed(name);
            actualChanges.put(name, newValue);
            this.name = newValue;
        }

        final String description = RoleApiConstants.description;
        if (command.isChangeInStringParameterNamed(description, this.description)) {
            final String newValue = command.stringValueOfParameterNamed(description);
            actualChanges.put(description, newValue);
            this.description = newValue;
        }

        return actualChanges;
    }

    public boolean updatePermission(final Permission permission, final boolean isSelected) {
        var changed = false;
        if (isSelected) {
            changed = addPermission(permission);
        } else {
            changed = removePermission(permission);
        }

        return changed;
    }

    private boolean addPermission(final Permission permission) {
        return this.permissions.add(permission);
    }

    private boolean removePermission(final Permission permission) {
        return this.permissions.remove(permission);
    }

    public Collection<Permission> getPermissions() {
        return this.permissions;
    }

    public RoleData toData() {
        return RoleData.builder()
                .id(getId())
                .name(this.name)
                .description(this.description)
                .isDisabled(this.disabled)
                .build();
    }

    public boolean hasPermissionTo(final String permissionCode) {
        return this.permissions.stream().anyMatch(permission -> permission.hasCode(permissionCode));
    }
}
