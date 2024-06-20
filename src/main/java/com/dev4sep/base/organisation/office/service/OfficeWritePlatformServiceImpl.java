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
package com.dev4sep.base.organisation.office.service;

import com.dev4sep.base.adminstration.user.domain.User;
import com.dev4sep.base.config.command.data.CommandProcessingBuilder;
import com.dev4sep.base.config.command.domain.CommandProcessing;
import com.dev4sep.base.config.command.domain.JsonCommand;
import com.dev4sep.base.config.exception.ErrorHandler;
import com.dev4sep.base.config.exception.PlatformDataIntegrityException;
import com.dev4sep.base.config.security.exception.NoAuthorizationException;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.organisation.office.api.OfficesApiConstants;
import com.dev4sep.base.organisation.office.domain.Office;
import com.dev4sep.base.organisation.office.domain.OfficeRepositoryWrapper;
import com.dev4sep.base.organisation.office.serialization.OfficeDataValidator;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * @author YISivlay
 */
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class OfficeWritePlatformServiceImpl implements OfficeWritePlatformService {

    private final PlatformSecurityContext context;
    private final OfficeDataValidator validator;
    private final OfficeRepositoryWrapper officeRepositoryWrapper;

    @Override
    public CommandProcessing create(JsonCommand command) {
        try {
            var login = this.context.authenticatedUser();
            this.validator.validateForCreate(command.getJson());

            final var parentId = command.longValueOfParameterNamed(OfficesApiConstants.parentId);
            final var parent = validateUserPrivilegeOnOfficeAndRetrieve(login, parentId);

            final var office = Office.fromJson(parent, command);

            this.officeRepositoryWrapper.saveAndFlush(office);

            office.generateHierarchy();
            office.generateExternalId();

            this.officeRepositoryWrapper.save(office);

            return new CommandProcessingBuilder()
                    .withCommandId(command.commandId())
                    .withResourceId(office.getId())
                    .withOfficeId(office.getId())
                    .build();
        } catch (JpaSystemException | DataIntegrityViolationException dve) {
            handleOfficeDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessing.empty();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleOfficeDataIntegrityIssues(command, throwable, dve);
            return CommandProcessing.empty();
        }
    }

    @Override
    public CommandProcessing update(final Long id, final JsonCommand command) {
        try {
            final var login = this.context.authenticatedUser();
            this.validator.validateForUpdate(command.getJson());

            final var parentId = command.longValueOfParameterNamed(OfficesApiConstants.parentId);

            final var office = validateUserPrivilegeOnOfficeAndRetrieve(login, id);

            final var changes = office.update(command);
            if (!changes.isEmpty()) {
                if (changes.containsKey(OfficesApiConstants.parentId)) {
                    final var parent = validateUserPrivilegeOnOfficeAndRetrieve(login, parentId);
                    office.setParent(parent);
                }
                this.officeRepositoryWrapper.save(office);
            }
            return new CommandProcessingBuilder()
                    .withCommandId(command.commandId())
                    .withResourceId(office.getId())
                    .withOfficeId(office.getId())
                    .with(changes)
                    .build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleOfficeDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessing.empty();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleOfficeDataIntegrityIssues(command, throwable, dve);
            return CommandProcessing.empty();
        }
    }

    @Override
    public CommandProcessing delete(final Long id) {
        try {
            this.context.authenticatedUser();

            final var office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(id);
            this.officeRepositoryWrapper.delete(office);

            return new CommandProcessingBuilder()
                    .withResourceId(id)
                    .withOfficeId(id)
                    .build();
        } catch (final JpaSystemException | DataIntegrityViolationException dve) {
            handleOfficeDataIntegrityIssues(null, dve.getMostSpecificCause(), dve);
            return CommandProcessing.empty();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleOfficeDataIntegrityIssues(null, throwable, dve);
            return CommandProcessing.empty();
        }

    }

    private Office validateUserPrivilegeOnOfficeAndRetrieve(final User login, final Long officeId) {

        final var userOfficeId = login.getOffice().getId();
        final var userOffice = this.officeRepositoryWrapper.findOfficeHierarchy(userOfficeId);
        if (userOffice.doesNotHaveAnOfficeInHierarchyWithId(officeId)) {
            throw new NoAuthorizationException("User does not have sufficient privileges to act on the provided office.");
        }

        var officeToReturn = userOffice;
        if (!userOffice.identifiedBy(officeId)) {
            officeToReturn = this.officeRepositoryWrapper.findOfficeHierarchy(officeId);
        }

        return officeToReturn;
    }

    private void handleOfficeDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {
        if (realCause.getMessage().contains("external_id")) {
            final var externalId = command.stringValueOfParameterNamed(OfficesApiConstants.externalId);
            throw new PlatformDataIntegrityException(
                    "error.msg.office.duplicate.externalId",
                    "Office already exists.",
                    "externalId",
                    externalId
            );
        } else if (realCause.getMessage().contains("name")) {
            final var name = command.stringValueOfParameterNamed(OfficesApiConstants.name);
            throw new PlatformDataIntegrityException(
                    "error.msg.office.duplicate.name",
                    "Office already exists",
                    "name",
                    name
            );
        }

        throw ErrorHandler.getMappable(dve, "error.msg.office.unknown.data.integrity.issue", "Unknown data integrity issue with resource.");
    }
}
