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

/**
 * @author YISivlay
 */
public class CommandWrapperBuilder {

    private Long officeId;
    private String actionName;
    private String entityName;
    private Long resourceId;
    private Long subResourceId;
    private String href;
    private String json = "{}";

    public CommandWrapper build() {
        return new CommandWrapper(
                null,
                this.officeId,
                this.actionName,
                this.entityName,
                this.resourceId,
                this.subResourceId,
                this.href,
                this.json
        );
    }

    public CommandWrapperBuilder actionName(final String actionName) {
        this.actionName = actionName;
        return this;
    }

    public CommandWrapperBuilder entityName(final String entityName) {
        this.entityName = entityName;
        return this;
    }

    public CommandWrapperBuilder resourceId(final Long resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public CommandWrapperBuilder subResourceId(final Long subResourceId) {
        this.subResourceId = subResourceId;
        return this;
    }

    public CommandWrapperBuilder href(final String href) {
        this.href = href;
        return this;
    }

    public CommandWrapperBuilder json(final String json) {
        this.json = json;
        return this;
    }
}
