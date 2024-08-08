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

import com.dev4sep.base.adminstration.user.domain.PlatformPasswordEncoder;
import com.dev4sep.base.adminstration.user.domain.User;
import com.dev4sep.base.adminstration.user.domain.UserRepository;
import com.dev4sep.base.config.keycloak.service.KeycloakService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author YISivlay
 */
@Service
public class JpaUserDomainService implements UserDomainService {

    private final UserRepository userRepository;
    private final PlatformPasswordEncoder platformPasswordEncoder;
    private final KeycloakService keycloakService;

    @Autowired
    public JpaUserDomainService(final UserRepository userRepository,
                                @Qualifier("applicationPasswordEncoder") final PlatformPasswordEncoder platformPasswordEncoder,
                                final KeycloakService keycloakService) {
        this.userRepository = userRepository;
        this.platformPasswordEncoder = platformPasswordEncoder;
        this.keycloakService = keycloakService;
    }

    @Override
    public void create(final User user, final String rawPassword) {
        this.userRepository.save(user);
        final String encodePassword = this.platformPasswordEncoder.encode(user);
        user.updatePassword(encodePassword);

        this.userRepository.saveAndFlush(user);
        this.keycloakService.createUser(user, rawPassword);
    }
}
