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

import com.dev4sep.base.config.security.data.EnumOptionData;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

/**
 * @author YISivlay
 */
@Getter
public enum CacheType {
    INVALID(0, "cache.type.invalid"),
    NO_CACHE(1, "cache.type.no.cache"),
    SINGLE_NODE(2, "cache.type.single.node"),
    MULTI_NODE(3, "cache.type.multi.node");

    private final Integer value;
    private final String code;

    private static final Map<Integer, CacheType> intToEnumMap = new HashMap<>();

    static {
        for (final CacheType type : CacheType.values()) {
            intToEnumMap.put(type.value, type);
        }
    }

    public static EnumOptionData cacheEnumType(final int id) {
        return cacheType(CacheType.fromInt(id));
    }

    public static CacheType fromInt(final Integer value) {
        CacheType type = intToEnumMap.get(value);
        if (type == null) {
            type = INVALID;
        }
        return type;
    }

    CacheType(final Integer value, final String code) {
        this.value = value;
        this.code = code;
    }

    public static EnumOptionData cacheType(final CacheType cacheType) {
        new EnumOptionData(CacheType.INVALID.getValue().longValue(), CacheType.INVALID.getCode(), "Invalid");
        return switch (cacheType) {
            case INVALID ->
                    new EnumOptionData(CacheType.INVALID.getValue().longValue(), CacheType.INVALID.getCode(), "Invalid");
            case NO_CACHE ->
                    new EnumOptionData(CacheType.NO_CACHE.getValue().longValue(), CacheType.NO_CACHE.getCode(), "No cache");
            case SINGLE_NODE ->
                    new EnumOptionData(CacheType.SINGLE_NODE.getValue().longValue(), CacheType.SINGLE_NODE.getCode(),
                            "Single node");
            case MULTI_NODE ->
                    new EnumOptionData(CacheType.MULTI_NODE.getValue().longValue(), CacheType.MULTI_NODE.getCode(), "Multi node");
        };
    }

    @Override
    public String toString() {
        return name().toString().replaceAll("_", " ");
    }

    public boolean isNoCache() {
        return NO_CACHE.getValue().equals(this.value);
    }

    public boolean isEhcache() {
        return SINGLE_NODE.getValue().equals(this.value);
    }

    public boolean isDistributedCache() {
        return MULTI_NODE.getValue().equals(this.value);
    }
}
