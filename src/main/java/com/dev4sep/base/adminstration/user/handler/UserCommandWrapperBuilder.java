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
package com.dev4sep.base.adminstration.user.handler;

import com.dev4sep.base.adminstration.user.api.UserApiConstants;
import com.dev4sep.base.config.command.domain.CommandWrapperBuilder;

/**
 * @author YISivlay
 */
public class UserCommandWrapperBuilder extends CommandWrapperBuilder {

    public CommandWrapperBuilder create() {
        this.actionName("CREATE");
        this.entityName(UserApiConstants.PERMISSIONS);
        this.resourceId(null);
        this.href(UserApiConstants.PATH);
        return this;
    }

    public CommandWrapperBuilder update(final Long id) {
        this.actionName("UPDATE");
        this.entityName(UserApiConstants.PERMISSIONS);
        this.resourceId(id);
        this.href(UserApiConstants.PATH + "/" + id);
        return this;
    }

    public CommandWrapperBuilder delete(final Long id) {
        this.actionName("DELETE");
        this.entityName(UserApiConstants.PERMISSIONS);
        this.resourceId(id);
        this.href(UserApiConstants.PATH + "/" + id);
        return this;
    }

}
