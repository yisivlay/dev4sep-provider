/**
 * Copyright 2024 DEV4Sep
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dev4sep.base.config.security.service;

import com.dev4sep.base.config.security.domain.PlatformUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * @author YISivlay
 */
@Service("userDetailsService")
public class TenantAwareJpaPlatformUserDetailsService implements PlatformUserDetailsService {

    private final PlatformUserRepository platformUserRepository;

    @Autowired
    public TenantAwareJpaPlatformUserDetailsService(final PlatformUserRepository platformUserRepository) {
        this.platformUserRepository = platformUserRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        final var deleted = false;
        final var enabled = true;

        final var appUser = this.platformUserRepository.findByUsernameAndDeletedAndEnabled(username, deleted, enabled);

        if (appUser == null) {
            throw new UsernameNotFoundException(username + ": not found");
        }

        return appUser;
    }
}
