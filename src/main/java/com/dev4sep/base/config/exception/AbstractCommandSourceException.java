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
package com.dev4sep.base.config.exception;

import lombok.Getter;

/**
 * @author YISivlay
 */
public abstract class AbstractCommandSourceException extends AbstractPlatformException {

    public static final String CACHE_HEADER = "x-served-from-cache";

    @Getter
    private final String action;
    @Getter
    private final String entity;
    @Getter
    private final String response;

    protected AbstractCommandSourceException(String action, String entity, String response) {
        super(null, null);
        this.action = action;
        this.entity = entity;
        this.response = response;
    }
}
