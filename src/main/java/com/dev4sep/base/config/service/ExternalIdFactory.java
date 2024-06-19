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
package com.dev4sep.base.config.service;

import com.dev4sep.base.config.command.domain.JsonCommand;
import com.dev4sep.base.config.configuration.domain.ConfigurationDomainService;
import com.dev4sep.base.config.domain.ExternalId;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

/**
 * @author YISivlay
 */
@Component
@RequiredArgsConstructor
public class ExternalIdFactory {

    private final ConfigurationDomainService configurationDomainService;

    public static ExternalId produce(String value) {
        return StringUtils.isBlank(value) ? ExternalId.empty() : new ExternalId(value);
    }

    public ExternalId createFromCommand(JsonCommand command, final String externalIdKey) {
        String externalIdStr = null;
        if (command.parsedJson() != null) {
            externalIdStr = command.stringValueOfParameterNamedAllowingNull(externalIdKey);
        }
        return create(externalIdStr);
    }

    public ExternalId create(String externalIdStr) {
        if (StringUtils.isBlank(externalIdStr)) {
            if (configurationDomainService.isExternalIdAutoGenerationEnabled()) {
                return ExternalId.generate();
            } else {
                return ExternalId.empty();
            }
        }
        return new ExternalId(externalIdStr);
    }

    public ExternalId create() {
        return create(null);
    }
}
