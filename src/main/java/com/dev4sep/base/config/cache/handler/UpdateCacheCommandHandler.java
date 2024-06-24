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
package com.dev4sep.base.config.cache.handler;

import com.dev4sep.base.config.cache.api.CacheApiConstants;
import com.dev4sep.base.config.cache.domain.CacheType;
import com.dev4sep.base.config.cache.service.CacheWritePlatformService;
import com.dev4sep.base.config.command.annotation.CommandType;
import com.dev4sep.base.config.command.data.CommandProcessingBuilder;
import com.dev4sep.base.config.command.domain.CommandProcessing;
import com.dev4sep.base.config.command.domain.JsonCommand;
import com.dev4sep.base.config.command.handler.CommandSourceHandler;
import com.dev4sep.base.config.data.ApiParameterError;
import com.dev4sep.base.config.data.DataValidatorBuilder;
import com.dev4sep.base.config.exception.InvalidJsonException;
import com.dev4sep.base.config.exception.PlatformApiDataValidationException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author YISivlay
 */
@Service
@CommandType(entity = CacheApiConstants.PERMISSIONS, action = "UPDATE")
public class UpdateCacheCommandHandler implements CommandSourceHandler {

    private final CacheWritePlatformService cacheService;

    @Autowired
    public UpdateCacheCommandHandler(CacheWritePlatformService cacheService) {
        this.cacheService = cacheService;
    }

    @Override
    @Transactional
    public CommandProcessing processCommand(JsonCommand command) {

        final String json = command.getJson();

        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        command.checkForUnsupportedParameters(typeOfMap, json, CacheApiConstants.SUPPORT_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource(CacheApiConstants.RESOURCE.toLowerCase());

        final int cacheTypeEnum = command.integerValueSansLocaleOfParameterNamed(CacheApiConstants.CACHE_TYPE);
        baseDataValidator.reset().parameter(CacheApiConstants.CACHE_TYPE).value(cacheTypeEnum).notNull().isOneOfTheseValues(1, 2, 3);

        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(dataValidationErrors);
        }

        final CacheType cacheType = CacheType.fromInt(cacheTypeEnum);

        final Map<String, Object> changes = this.cacheService.switchToCache(cacheType);

        return new CommandProcessingBuilder()
                .withCommandId(command.commandId())
                .with(changes)
                .build();
    }
}
