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
package com.dev4sep.base.adminstration.user.service;

import com.dev4sep.base.adminstration.role.domain.Role;
import com.dev4sep.base.adminstration.role.domain.RoleRepository;
import com.dev4sep.base.adminstration.role.exception.RoleNotFoundException;
import com.dev4sep.base.adminstration.user.api.UserApiConstants;
import com.dev4sep.base.adminstration.user.domain.*;
import com.dev4sep.base.adminstration.user.exception.UserNotFoundException;
import com.dev4sep.base.adminstration.user.serialization.UserDataValidator;
import com.dev4sep.base.config.command.data.CommandProcessingBuilder;
import com.dev4sep.base.config.command.domain.CommandProcessing;
import com.dev4sep.base.config.command.domain.JsonCommand;
import com.dev4sep.base.config.exception.ErrorHandler;
import com.dev4sep.base.config.exception.PlatformDataIntegrityException;
import com.dev4sep.base.config.security.exception.PasswordPreviouslyUsedException;
import com.dev4sep.base.config.security.service.PlatformSecurityContext;
import com.dev4sep.base.organisation.office.domain.OfficeRepositoryWrapper;
import jakarta.persistence.PersistenceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Caching;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * @author YISivlay
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class UserWritePlatformServiceImpl implements UserWritePlatformService {

    private final PlatformSecurityContext context;
    private final UserDataValidator validator;
    private final UserDomainService userDomainService;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final OfficeRepositoryWrapper officeRepositoryWrapper;
    private final PlatformPasswordEncoder platformPasswordEncoder;
    private final UserPreviousPasswordRepository userPreviewPasswordRepository;

    @Override
    @Caching(evict = {@CacheEvict(value = "users", allEntries = true), @CacheEvict(value = "usersByUsername", allEntries = true)})
    public CommandProcessing create(JsonCommand command) {
        try {
            this.validator.validateForCreate(command.getJson());

            final var officeId = command.longValueOfParameterNamed(UserApiConstants.officeId);
            final var office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(officeId);

            final var roles = command.arrayValueOfParameterNamed(UserApiConstants.roles);
            final var allRoles = assembleSetOfRoles(roles);

            var user = User.fromJson(office, allRoles, command);

            this.userDomainService.create(user);

            return new CommandProcessingBuilder()
                    .withCommandId(command.commandId())
                    .withResourceId(user.getId())
                    .withOfficeId(office.getId())
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            throw handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
        } catch (final JpaSystemException | PersistenceException | AuthenticationServiceException dve) {
            log.error("createUser: JpaSystemException | PersistenceException | AuthenticationServiceException", dve);
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            throw handleDataIntegrityIssues(command, throwable, dve);
        }
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "users", allEntries = true), @CacheEvict(value = "usersByUsername", allEntries = true),
            @CacheEvict(value = "userById", key = "T(com.dev4sep.base.config.ThreadLocalContextUtil).getTenant().getTenantIdentifier().concat(#id)")})
    public CommandProcessing update(Long id, JsonCommand command) {
        try {
            this.validator.validateForUpdate(command.getJson());
            final var user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
            final var currentPasswordToSaveAsPreview = getCurrentPasswordToSaveAsPreview(user, command);

            final Map<String, Object> changes = user.update(command, this.platformPasswordEncoder);
            if (!changes.isEmpty()) {
                if (changes.containsKey(UserApiConstants.officeId)) {
                    final var officeId = (Long) changes.get(UserApiConstants.officeId);
                    final var office = this.officeRepositoryWrapper.findOneWithNotFoundDetection(officeId);
                    user.setOffice(office);
                }
                if (changes.containsKey(UserApiConstants.roles)) {
                    final var roleIds = (String[]) changes.get(UserApiConstants.roles);
                    final var roles = assembleSetOfRoles(roleIds);
                    user.setRoles(roles);
                }
                this.userRepository.saveAndFlush(user);

                if (currentPasswordToSaveAsPreview != null) {
                    this.userPreviewPasswordRepository.save(currentPasswordToSaveAsPreview);
                }
            }
            return new CommandProcessingBuilder()
                    .withResourceId(id)
                    .withOfficeId(user.getOffice().getId())
                    .with(changes)
                    .build();
        } catch (final DataIntegrityViolationException dve) {
            throw handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
        } catch (final JpaSystemException | PersistenceException | AuthenticationServiceException dve) {
            log.error("updateUser: JpaSystemException | PersistenceException | AuthenticationServiceException", dve);
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            throw handleDataIntegrityIssues(command, throwable, dve);
        }
    }

    @Override
    @Caching(evict = { @CacheEvict(value = "users", allEntries = true), @CacheEvict(value = "usersByUsername", allEntries = true) })
    public CommandProcessing delete(final Long id) {
        final var user = this.userRepository.findById(id).orElseThrow(() -> new UserNotFoundException(id));
        if (user.isDeleted()) {
            throw new UserNotFoundException(id);
        }
        user.delete();
        this.userRepository.save(user);
        return new CommandProcessingBuilder()
                .withResourceId(id)
                .withOfficeId(user.getOffice().getId())
                .build();
    }

    private UserPreviousPassword getCurrentPasswordToSaveAsPreview(final User user, final JsonCommand command) {
        final var passWordEncodedValue = user.getEncodedPassword(command, this.platformPasswordEncoder);
        UserPreviousPassword currentPasswordToSaveAsPreview = null;
        if (passWordEncodedValue != null) {
            var pageRequest = PageRequest.of(0, UserApiConstants.numberOfPreviousPasswords, Sort.Direction.DESC, UserApiConstants.removalDate);
            final var nLastUsedPasswords = this.userPreviewPasswordRepository.findByUserId(user.getId(), pageRequest);
            for (var aPreviewPassword : nLastUsedPasswords) {
                if (aPreviewPassword.getPassword().equals(passWordEncodedValue)) {
                    throw new PasswordPreviouslyUsedException();
                }
            }
            currentPasswordToSaveAsPreview = new UserPreviousPassword(user);
        }
        return currentPasswordToSaveAsPreview;
    }

    private Set<Role> assembleSetOfRoles(final String[] rolesArray) {
        final Set<Role> allRoles = new HashSet<>();
        if (!ObjectUtils.isEmpty(rolesArray)) {
            Arrays.stream(rolesArray)
                    .map(Long::valueOf).map(id -> this.roleRepository.findById(id).orElseThrow(() -> new RoleNotFoundException(id)))
                    .forEach(allRoles::add);
        }
        return allRoles;
    }

    private RuntimeException handleDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {
        if (realCause.getMessage().contains("username")) {
            final String username = command.stringValueOfParameterNamed(UserApiConstants.username);
            final String message = "User with username " + username + " already exists.";
            return new PlatformDataIntegrityException("error.msg.user.duplicate.username", message, "username", username);
        }

        log.error("handleDataIntegrityIssues: Neither duplicate username nor existing user; unknown error occurred", dve);
        return ErrorHandler.getMappable(dve, "error.msg.unknown.data.integrity.issue", "Unknown data integrity issue with resource.");
    }
}
