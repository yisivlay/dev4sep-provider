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
package com.dev4sep.base.config.command.data;

import com.dev4sep.base.config.command.domain.CommandProcessing;

import java.util.Map;

/**
 * @author YISivlay
 */
public class CommandProcessingBuilder {

    private Long commandId;
    private Long officeId;
    private Long resourceId;
    private Long subResourceId;
    private Map<String, Object> changes;
    private boolean rollbackTransaction = false;

    public CommandProcessing build() {
        return CommandProcessing.fromDetails(this.commandId, this.officeId, this.resourceId, this.subResourceId, this.changes, this.rollbackTransaction);
    }

    public CommandProcessingBuilder withResourceId(final Long resourceId) {
        this.resourceId = resourceId;
        return this;
    }

    public CommandProcessingBuilder withSubResourceId(final Long subResourceId) {
        this.subResourceId = subResourceId;
        return this;
    }

    public CommandProcessingBuilder withOfficeId(final Long withOfficeId) {
        this.officeId = withOfficeId;
        return this;
    }

    public CommandProcessingBuilder setRollbackTransaction(final boolean rollbackTransaction) {
        this.rollbackTransaction |= rollbackTransaction;
        return this;
    }

    public CommandProcessingBuilder with(final Map<String, Object> changes) {
        this.changes = changes;
        return this;
    }

    public CommandProcessingBuilder withCommandId(final Long commandId) {
        this.commandId = commandId;
        return this;
    }
}
