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

import com.dev4sep.base.adminstration.user.domain.User;
import com.dev4sep.base.config.auditing.domain.AbstractPersistableCustom;
import com.dev4sep.base.config.utils.DateUtils;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.OffsetDateTime;

/**
 * @author YISivlay
 */
@Getter
@Setter
@Entity
@Table(name = "tbl_command_source")
public class CommandSource extends AbstractPersistableCustom {

    @Column(name = "office_id")
    private Long officeId;

    @Column(name = "resource_id")
    private Long resourceId;

    @Column(name = "subresource_id")
    private Long subResourceId;

    @Column(name = "action_name", length = 50)
    private String actionName;

    @Column(name = "entity_name", length = 50)
    private String entityName;

    @Column(name = "href", length = 100)
    private String href;

    @ManyToOne
    @JoinColumn(name = "maker_id", nullable = false)
    private User maker;

    @Column(name = "made_date", nullable = false)
    private OffsetDateTime madeDate;

    @ManyToOne
    @JoinColumn(name = "checker_id")
    private User checker;

    @Column(name = "checked_date")
    private OffsetDateTime checkedDate;

    @Column(name = "status", nullable = false)
    private Integer status;

    @Column(name = "result")
    private String result;

    @Column(name = "result_status_code")
    private Integer resultStatusCode;

    @Column(name = "command_as_json")
    private String commandAsJson;

    private CommandSource(final String actionName,
                          final String entityName,
                          final String href,
                          final Long resourceId,
                          final Long subResourceId,
                          final String commandSerializedAsJson,
                          final User maker,
                          final Integer status) {
        this.actionName = actionName;
        this.entityName = entityName;
        this.href = href;
        this.resourceId = resourceId;
        this.subResourceId = subResourceId;
        this.commandAsJson = commandSerializedAsJson;
        this.maker = maker;
        this.madeDate = DateUtils.getAuditOffsetDateTime();
        this.status = status;
    }

    public static CommandSource fullEntryFrom(final CommandWrapper request,
                                              final JsonCommand command,
                                              final User maker,
                                              Integer status) {
        CommandSource commandSource = new CommandSource(
                request.getActionName(),
                request.getEntityName(),
                request.getHref(),
                command.getResourceId(),
                command.getSubresourceId(),
                command.getJson(),
                maker,
                status
        );
        commandSource.officeId = request.getOfficeId();
        return commandSource;
    }
}
