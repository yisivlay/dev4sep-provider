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
package com.dev4sep.base.config.configuration.domain;

import com.dev4sep.base.adminstration.permission.domain.PermissionRepository;
import com.dev4sep.base.adminstration.permission.exception.PermissionNotFoundException;
import com.dev4sep.base.config.configuration.data.ConfigurationData;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

/**
 * @author YISivlay
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ConfigurationDomainServiceJpa implements ConfigurationDomainService {

    private final PermissionRepository permissionRepository;
    private final ConfigurationRepositoryWrapper configurationRepositoryWrapper;

    @Override
    public boolean isMakerCheckerEnabledForTask(String taskPermissionCode) {
        if (StringUtils.isBlank(taskPermissionCode)) {
            throw new PermissionNotFoundException(taskPermissionCode);
        }
        final String makerCheckerConfigurationProperty = "maker-checker";
        final var data = getConfigurationData(makerCheckerConfigurationProperty);
        if (data.isEnabled()) {
            final var thisTask = this.permissionRepository.findOneByCode(taskPermissionCode);
            if (thisTask == null) {
                throw new PermissionNotFoundException(taskPermissionCode);
            }

            return thisTask.hasMakerCheckerEnabled();
        }
        return false;
    }

    @NotNull
    private ConfigurationData getConfigurationData(final String name) {
        return configurationRepositoryWrapper.findOneByNameWithNotFoundDetection(name).toData();
    }
}
