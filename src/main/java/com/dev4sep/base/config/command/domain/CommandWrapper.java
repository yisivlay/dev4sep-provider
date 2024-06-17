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
package com.dev4sep.base.config.command.domain;

import com.dev4sep.base.adminstration.user.api.PasswordPreferencesApiConstants;
import lombok.Getter;

/**
 * @author YISivlay
 */
@Getter
public class CommandWrapper {

    private final Long commandId;
    private final Long officeId;
    private final String actionName;
    private final String entityName;
    private final String taskPermissionName;
    private final Long resourceId;
    private final Long subResourceId;
    private final String href;
    private final String json;

    public CommandWrapper(final Long commandId,
                          final Long officeId,
                          final String actionName,
                          final String entityName,
                          final Long resourceId,
                          final Long subResourceId,
                          final String href,
                          final String json) {
        this.commandId = commandId;
        this.officeId = officeId;
        this.actionName = actionName;
        this.entityName = entityName;
        this.taskPermissionName = actionName + "_" + entityName;
        this.resourceId = resourceId;
        this.subResourceId = subResourceId;
        this.href = href;
        this.json = json;
    }

    private CommandWrapper(final String actionName, final String entityName) {
        this.commandId = null;
        this.officeId = null;
        this.actionName = actionName;
        this.entityName = entityName;
        this.taskPermissionName = actionName + "_" + entityName;
        this.resourceId = null;
        this.subResourceId = null;
        this.href = null;
        this.json = null;
    }

    private CommandWrapper(final String actionName,
                           final String entityName,
                           final Long resourceId,
                           final Long subResourceId) {
        this.commandId = null;
        this.officeId = null;
        this.actionName = actionName;
        this.entityName = entityName;
        this.taskPermissionName = actionName + "_" + entityName;
        this.resourceId = resourceId;
        this.subResourceId = subResourceId;
        this.href = null;
        this.json = null;
    }

    private CommandWrapper(final Long commandId,
                           final String actionName,
                           final String entityName,
                           final Long resourceId,
                           final Long subResourceId,
                           final String href) {
        this.commandId = commandId;
        this.officeId = null;
        this.actionName = actionName;
        this.entityName = entityName;
        this.taskPermissionName = actionName + "_" + entityName;
        this.resourceId = resourceId;
        this.subResourceId = subResourceId;
        this.href = href;
        this.json = null;
    }

    public static CommandWrapper wrap(final String actionName, final String entityName, final Long resourceId, final Long subresourceId) {
        return new CommandWrapper(actionName, entityName, resourceId, subresourceId);
    }

    public static CommandWrapper wrap(final String actionName, final String entityName) {
        return new CommandWrapper(actionName, entityName);
    }

    public boolean isPermissionResource() {
        return this.entityName.equalsIgnoreCase("PERMISSION");
    }

    public boolean isUpdateOperation() {
        return this.actionName.equalsIgnoreCase("UPDATE");
    }

    public boolean isCurrencyResource() {
        return this.entityName.equalsIgnoreCase("CURRENCY");
    }

    public boolean isPasswordPreferencesResource() {
        return this.entityName.equalsIgnoreCase(PasswordPreferencesApiConstants.ENTITY_NAME);
    }

    public boolean isUserResource() {
        return this.entityName.equalsIgnoreCase("USER");
    }

    public boolean isUpdate() {
        // permissions resource has special update which involves no resource.
        return (isPermissionResource() && isUpdateOperation()) || (isCurrencyResource() && isUpdateOperation())
                || (isPasswordPreferencesResource() && isUpdateOperation()) || (isUpdateOperation() && (this.resourceId != null));
    }

    public boolean isUpdateOfOwnUserDetails(final Long loggedInUserId) {
        return isUserResource() && isUpdate() && loggedInUserId.equals(this.resourceId);
    }

    public String taskPermissionName() {
        return this.actionName + "_" + this.entityName;
    }
}
