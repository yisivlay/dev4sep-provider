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
package com.dev4sep.base.config.datasource.database.domain;

import lombok.Builder;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * @author YISivlay
 */
@Jacksonized
@Builder
public class PlatformTenant implements Serializable {

    private final Long id;
    private final String tenantIdentifier;
    private final String name;
    private final String timezoneId;
    private final PlatformTenantConnection connection;

    public PlatformTenant(final Long id,
                          final String tenantIdentifier,
                          final String name,
                          final String timezoneId,
                          final PlatformTenantConnection connection) {
        this.id = id;
        this.tenantIdentifier = tenantIdentifier;
        this.name = name;
        this.timezoneId = timezoneId;
        this.connection = connection;
    }

    public Long getId() {
        return this.id;
    }

    public String getTenantIdentifier() {
        return this.tenantIdentifier;
    }

    public String getName() {
        return this.name;
    }

    public String getTimezoneId() {
        return this.timezoneId;
    }

    public PlatformTenantConnection getConnection() {
        return connection;
    }

}
