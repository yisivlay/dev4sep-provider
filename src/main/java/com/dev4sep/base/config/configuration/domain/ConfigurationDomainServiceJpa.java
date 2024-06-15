package com.dev4sep.base.config.configuration.domain;

import com.dev4sep.base.adminstration.permission.domain.Permission;
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

    @Override
    public boolean isMakerCheckerEnabledForTask(String taskPermissionCode) {
        if (StringUtils.isBlank(taskPermissionCode)) {
            throw new PermissionNotFoundException(taskPermissionCode);
        }
        final String makerCheckerConfigurationProperty = "maker-checker";
        final ConfigurationData data = getGlobalConfigurationPropertyData(makerCheckerConfigurationProperty);
        if (data.isEnabled()) {
            final Permission thisTask = this.permissionRepository.findOneByCode(taskPermissionCode);
            if (thisTask == null) {
                throw new PermissionNotFoundException(taskPermissionCode);
            }

            return thisTask.hasMakerCheckerEnabled();
        }
        return false;
    }

    @NotNull
    private ConfigurationData getConfigurationData(final String name) {
        return globalConfigurationRepository.findOneByNameWithNotFoundDetection(name).toData();
    }
}
