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
package com.dev4sep.base.config.cache.service;

import com.dev4sep.base.config.cache.domain.CacheRepository;
import com.dev4sep.base.config.cache.domain.CacheType;
import com.dev4sep.base.config.configuration.domain.ConfigurationDomainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

/**
 * @author YISivlay
 */
@Service
public class CacheWritePlatformServiceImpl implements CacheWritePlatformService {

    private final ConfigurationDomainService configurationDomainService;
    private final RuntimeDelegatingCacheManager cacheService;
    private final CacheRepository cacheRepository;

    @Autowired
    public CacheWritePlatformServiceImpl(final ConfigurationDomainService configurationDomainService,
                                         @Qualifier("runtimeDelegatingCacheManager") final RuntimeDelegatingCacheManager cacheService,
                                         final CacheRepository cacheRepository) {
        this.configurationDomainService = configurationDomainService;
        this.cacheService = cacheService;
        this.cacheRepository = cacheRepository;
    }

    @Override
    public Map<String, Object> switchToCache(CacheType cacheType) {
        final boolean ehCacheEnabled = this.configurationDomainService.isEhcacheEnabled();

        final Map<String, Object> changes = this.cacheService.switchToCache(ehCacheEnabled, cacheType);

        if (!changes.isEmpty()) {
            this.cacheRepository.findById(1L).ifPresent(cache -> {
                cache.setCacheType(cacheType.getValue());
                this.cacheRepository.save(cache);
            });
        }

        return changes;
    }
}
