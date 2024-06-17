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

import lombok.Getter;
import lombok.ToString;

import java.io.Serializable;
import java.util.Map;

/**
 * @author YISivlay
 */
@ToString
@Getter
public class CommandProcessing implements Serializable {

    private Long commandId;
    private Long officeId;
    private final Long resourceId;
    private final Long subResourceId;
    private final Map<String, Object> changes;
    private Boolean rollbackTransaction;

    public CommandProcessing() {
        this.commandId = null;
        this.officeId = null;
        this.resourceId = null;
        this.subResourceId = null;
        this.changes = null;
        this.rollbackTransaction = null;
    }

    public CommandProcessing(final Long commandId,
                             final Long officeId,
                             final Long resourceId,
                             final Long subResourceId,
                             final Map<String, Object> changes,
                             final Boolean rollbackTransaction) {
        this.commandId = commandId;
        this.officeId = officeId;
        this.resourceId = resourceId;
        this.subResourceId = subResourceId;
        this.changes = changes;
        this.rollbackTransaction = rollbackTransaction;
    }

    public void setOfficeId(final Long officeId) {
        this.officeId = officeId;
    }

    public static CommandProcessing fromDetails(Long commandId, Long officeId, Long resourceId, Long subResourceId, Map<String, Object> changes, boolean rollbackTransaction) {
        return new CommandProcessing(commandId, officeId, resourceId, subResourceId, changes, rollbackTransaction);
    }

    public static CommandProcessing fromCommandProcessing(CommandProcessing result) {
        return fromCommandProcessing(result, result.getResourceId());
    }

    public static CommandProcessing fromCommandProcessing(CommandProcessing result, final Long resourceId) {
        return new CommandProcessing(
                result.commandId,
                result.officeId,
                resourceId,
                result.subResourceId,
                result.changes,
                result.rollbackTransaction
        );
    }

    public static CommandProcessing empty() {
        return new CommandProcessing();
    }

    public void setRollbackTransaction(Boolean rollbackTransaction) {
        this.rollbackTransaction = rollbackTransaction;
    }

    public boolean isRollbackTransaction() {
        return this.rollbackTransaction != null && this.rollbackTransaction;
    }
}
