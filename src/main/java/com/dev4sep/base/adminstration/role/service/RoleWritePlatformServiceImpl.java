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
package com.dev4sep.base.adminstration.role.service;

import com.dev4sep.base.adminstration.role.api.RoleApiConstants;
import com.dev4sep.base.adminstration.role.domain.Role;
import com.dev4sep.base.adminstration.role.domain.RoleRepository;
import com.dev4sep.base.adminstration.role.exception.RoleDeleteAssociatedException;
import com.dev4sep.base.adminstration.role.exception.RoleNotFoundException;
import com.dev4sep.base.adminstration.role.serialization.RoleDataValidator;
import com.dev4sep.base.config.command.data.CommandProcessingBuilder;
import com.dev4sep.base.config.command.domain.CommandProcessing;
import com.dev4sep.base.config.command.domain.JsonCommand;
import com.dev4sep.base.config.exception.ErrorHandler;
import com.dev4sep.base.config.exception.PlatformDataIntegrityException;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author YISivlay
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class RoleWritePlatformServiceImpl implements RoleWritePlatformService {

    private final PlatformSecurityContext context;
    private final RoleRepository roleRepository;
    private final RoleDataValidator validator;

    @Override
    public CommandProcessing create(final JsonCommand command) {
        try {
            final var login = this.context.authenticatedUser();
            this.validator.validateForCreate(command.getJson());

            var role = Role.fromJson(command);

            this.roleRepository.saveAndFlush(role);

            return new CommandProcessingBuilder()
                    .withCommandId(command.commandId())
                    .withResourceId(role.getId())
                    .withOfficeId(login.getOffice().getId())
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            throw handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
        } catch (final JpaSystemException | PersistenceException | AuthenticationServiceException dve) {
            log.error("createRole: JpaSystemException | PersistenceException | AuthenticationServiceException", dve);
            var throwable = ExceptionUtils.getRootCause(dve.getCause());
            throw handleDataIntegrityIssues(command, throwable, dve);
        }
    }

    @Override
    public CommandProcessing update(final Long id, final JsonCommand command) {
        try {
            final var login = this.context.authenticatedUser();
            var role = this.roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(id));

            this.validator.validateForUpdate(command.getJson());
            final var changes = role.update(command);
            if (!changes.isEmpty()) {
                this.roleRepository.saveAndFlush(role);
            }

            return new CommandProcessingBuilder()
                    .withCommandId(command.commandId())
                    .withResourceId(role.getId())
                    .withOfficeId(login.getOffice().getId())
                    .with(changes)
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            throw handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
        } catch (final JpaSystemException | PersistenceException | AuthenticationServiceException dve) {
            log.error("updateRole: JpaSystemException | PersistenceException | AuthenticationServiceException", dve);
            var throwable = ExceptionUtils.getRootCause(dve.getCause());
            throw handleDataIntegrityIssues(command, throwable, dve);
        }
    }

    @Override
    public CommandProcessing delete(final Long id) {
        try {
            final var login = this.context.authenticatedUser();
            var role = this.roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(id));
            final var count = this.roleRepository.getCountOfRolesAssociatedWithUsers(id);
            if (count > 0) {
                throw new RoleDeleteAssociatedException(id);
            }
            this.roleRepository.delete(role);
            return new CommandProcessingBuilder()
                    .withResourceId(id)
                    .withOfficeId(login.getOffice().getId())
                    .build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            throw new PlatformDataIntegrityException(
                    "error.msg.unknown.data.integrity.issue",
                    "Unknown data integrity issue with resource: " + dve.getMostSpecificCause(), dve);
        }
    }

    private RuntimeException handleDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {
        if (realCause.getMessage().contains("name")) {
            final String name = command.stringValueOfParameterNamed(RoleApiConstants.name);
            final String message = "Role " + name + " already exists.";
            return new PlatformDataIntegrityException("error.msg.role.duplicate.name", message, RoleApiConstants.name, name);
        }

        log.error("handleDataIntegrityIssues: Neither duplicate name nor existing role; unknown error occurred", dve);
        return ErrorHandler.getMappable(dve, "error.msg.unknown.data.integrity.issue", "Unknown data integrity issue with resource.");
    }
}
