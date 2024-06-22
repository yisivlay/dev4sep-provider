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

import com.dev4sep.base.config.cache.api.CacheApiConstants;
import com.dev4sep.base.config.cache.domain.CacheType;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author YISivlay
 */
@Slf4j
@Component(value = "runtimeDelegatingCacheManager")
@RequiredArgsConstructor
public class RuntimeDelegatingCacheManager implements CacheManager, InitializingBean {

    @Qualifier("ehCacheManager")
    private final CacheManager ehCacheManager;
    @Qualifier("defaultCacheManager")
    private final CacheManager defaultCacheManager;
    private CacheManager currentCacheManager;

    @Override
    public void afterPropertiesSet() throws Exception {
        currentCacheManager = defaultCacheManager;
    }

    @Override
    public Cache getCache(String name) {
        return currentCacheManager.getCache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return currentCacheManager.getCacheNames();
    }

    public Map<String, Object> switchToCache(final boolean ehcacheEnabled, final CacheType cacheType) {
        final Map<String, Object> changes = new HashMap<>();
        final var noCacheEnabled = !ehcacheEnabled;
        switch (cacheType) {
            case INVALID -> {
                log.warn("Invalid cache type used");
            }
            case NO_CACHE -> {
                if (!noCacheEnabled) {
                    changes.put(CacheApiConstants.cacheType, cacheType.getValue());
                }
                currentCacheManager = defaultCacheManager;
            }
            case SINGLE_NODE -> {
                if (!ehcacheEnabled) {
                    changes.put(CacheApiConstants.cacheType, cacheType.getValue());
                    clearEhCache();
                }
                currentCacheManager = ehCacheManager;

                if (currentCacheManager.getCacheNames().isEmpty()) {
                    log.error("No caches configured for activated CacheManager {}", currentCacheManager);
                }
            }
            case MULTI_NODE -> throw new UnsupportedOperationException("Multi node cache is not supported");
        }

        return changes;
    }

    @SuppressFBWarnings(value = "DCN_NULLPOINTER_EXCEPTION", justification = "TODO: fix this!")
    private void clearEhCache() {
        var cacheNames = ehCacheManager.getCacheNames();
        for (var cacheName : cacheNames) {
            try {
                if (Objects.nonNull(ehCacheManager.getCache(cacheName))) {
                    Objects.requireNonNull(ehCacheManager.getCache(cacheName)).clear();
                }
            } catch (NullPointerException npe) {
                log.warn("NullPointerException occurred", npe);
            }
        }
    }
}
