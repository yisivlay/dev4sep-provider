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
package com.dev4sep.base.config.cache;

import com.dev4sep.base.config.persistence.TransactionLifecycleCallback;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;

import java.util.Collection;


/**
 * @author YISivlay
 */
@RequiredArgsConstructor
public class TransactionBoundCacheManager implements TransactionLifecycleCallback, CacheManager {

    private final CacheManager delegate;

    private void resetCaches() {
        var cacheNames = delegate.getCacheNames();
        cacheNames.forEach(c -> {
            var cache = delegate.getCache(c);
            if (cache != null) {
                cache.clear();
            }
        });
    }

    @Override
    public void afterBegin() {
        resetCaches();
    }

    @Override
    public void afterCompletion() {
        resetCaches();
    }

    @Override
    public Cache getCache(String name) {
        return this.delegate.getCache(name);
    }

    @Override
    public Collection<String> getCacheNames() {
        return this.delegate.getCacheNames();
    }
}
