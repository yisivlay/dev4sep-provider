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
package com.dev4sep.base.adminstration.role.handler;

import com.dev4sep.base.adminstration.role.api.RoleApiConstants;
import com.dev4sep.base.config.command.domain.CommandWrapperBuilder;

/**
 * @author YISivlay
 */
public class RoleCommandWrapperBuilder extends CommandWrapperBuilder {

    public CommandWrapperBuilder create() {
        this.actionName("CREATE");
        this.entityName(RoleApiConstants.PERMISSIONS);
        this.resourceId(null);
        this.href(RoleApiConstants.PATH);
        return this;
    }

    public CommandWrapperBuilder update(final Long id) {
        this.actionName("UPDATE");
        this.entityName(RoleApiConstants.PERMISSIONS);
        this.resourceId(id);
        this.href(RoleApiConstants.PATH + "/" + id);
        return this;
    }

    public CommandWrapperBuilder disableRole(final Long id) {
        this.actionName("DISABLE");
        this.entityName(RoleApiConstants.PERMISSIONS);
        this.resourceId(id);
        this.href(RoleApiConstants.PATH + "/" + id + "/disable");
        this.json("{}");
        return this;
    }

    public CommandWrapperBuilder enableRole(final Long id) {
        this.actionName("ENABLE");
        this.entityName(RoleApiConstants.PERMISSIONS);
        this.resourceId(id);
        this.href(RoleApiConstants.PATH + "/" + id + "/enable");
        this.json("{}");
        return this;
    }

    public CommandWrapperBuilder updateRolePermission(final Long id) {
        this.actionName("PERMISSIONS");
        this.entityName(RoleApiConstants.PERMISSIONS);
        this.resourceId(id);
        this.href(RoleApiConstants.PATH + "/" + id + "/permissions");
        return this;
    }

    public CommandWrapperBuilder delete(final Long id) {
        this.actionName("DELETE");
        this.entityName(RoleApiConstants.PERMISSIONS);
        this.resourceId(id);
        this.href(RoleApiConstants.PATH + "/" + id);
        return this;
    }

}
