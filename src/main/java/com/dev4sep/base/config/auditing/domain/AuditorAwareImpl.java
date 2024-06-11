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
package com.dev4sep.base.config.auditing.domain;

import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

/**
 * @author YISivlay
 */
public class AuditorAwareImpl implements AuditorAware<Long> {
    @Override
    public Optional<Long> getCurrentAuditor() {
        var currentUserId = Optional.of(1L);
        final var securityContext = SecurityContextHolder.getContext();
        if (securityContext != null) {
            final var authentication = securityContext.getAuthentication();
            if (authentication != null) {
                //currentUserId = Optional.ofNullable(((AppUser) authentication.getPrincipal()).getId());
            } else {
                currentUserId = retrieveSuperUser();
            }
        } else {
            currentUserId = retrieveSuperUser();
        }
        return currentUserId;
    }

    private Optional<Long> retrieveSuperUser() {
        return Optional.of(1L); // TODO change to SYSTEM_USER_ID and add rights
    }
}
