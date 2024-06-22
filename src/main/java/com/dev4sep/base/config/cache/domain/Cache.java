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
package com.dev4sep.base.config.cache.domain;

import com.dev4sep.base.config.auditing.domain.AbstractPersistableCustom;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Setter;

/**
 * @author YISivlay
 */
@Setter
@Entity
@Table(name = "tbl_cache")
public class Cache extends AbstractPersistableCustom {

    @Column(name = "cache_type")
    private Integer cacheType;

    public boolean isNoCachedEnabled() {
        return CacheType.fromInt(this.cacheType).isNoCache();
    }

    public boolean isEhcacheEnabled() {
        return CacheType.fromInt(this.cacheType).isEhcache();
    }

    public boolean isDistributedCacheEnabled() {
        return CacheType.fromInt(this.cacheType).isDistributedCache();
    }
}
