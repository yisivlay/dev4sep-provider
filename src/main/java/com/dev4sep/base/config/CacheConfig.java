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
package com.dev4sep.base.config;

import com.dev4sep.base.config.cache.SpecifiedCacheSupportingCacheManager;
import com.dev4sep.base.config.cache.TransactionBoundCacheManager;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.springframework.cache.jcache.JCacheCacheManager;
import org.springframework.cache.support.NoOpCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.cache.CacheManager;
import javax.cache.Caching;
import javax.cache.spi.CachingProvider;

/**
 * @author YISivlay
 */
@Configuration
public class CacheConfig {

    public static final String CONFIG_BY_NAME_CACHE_NAME = "configByName";

    @Bean
    public TransactionBoundCacheManager defaultCacheManager(JCacheCacheManager ehCacheManager) {
        SpecifiedCacheSupportingCacheManager cacheManager = new SpecifiedCacheSupportingCacheManager();
        cacheManager.setNoOpCacheManager(new NoOpCacheManager());
        cacheManager.setDelegateCacheManager(ehCacheManager);
        cacheManager.setSupportedCaches(CONFIG_BY_NAME_CACHE_NAME);
        return new TransactionBoundCacheManager(cacheManager);
    }

    @Bean
    public JCacheCacheManager ehCacheManager() {
        JCacheCacheManager jCacheCacheManager = new JCacheCacheManager();
        jCacheCacheManager.setCacheManager(getInternalEhCacheManager());
        return jCacheCacheManager;
    }

    private CacheManager getInternalEhCacheManager() {
        CachingProvider provider = Caching.getCachingProvider();
        CacheManager cacheManager = provider.getCacheManager();

        javax.cache.configuration.Configuration<Object, Object> defaultTemplate = Eh107Configuration
                .fromEhcacheCacheConfiguration(CacheConfigurationBuilder
                        .newCacheConfigurationBuilder(Object.class, Object.class, ResourcePoolsBuilder.heap(10000))
                        .withExpiry(ExpiryPolicyBuilder.noExpiration()).build());

        cacheManager.createCache("users", defaultTemplate);
        cacheManager.createCache("userById", defaultTemplate);
        cacheManager.createCache("usersByUsername", defaultTemplate);
        cacheManager.createCache("tenantsById", defaultTemplate);
        cacheManager.createCache("offices", defaultTemplate);
        cacheManager.createCache("officesForDropdown", defaultTemplate);
        cacheManager.createCache("officesById", defaultTemplate);
        cacheManager.createCache(CONFIG_BY_NAME_CACHE_NAME, defaultTemplate);

        //TODO - We will cache user access token here later

        return cacheManager;
    }
}
