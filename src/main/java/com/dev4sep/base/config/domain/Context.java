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
package com.dev4sep.base.config.domain;

import com.dev4sep.base.config.datasource.database.domain.PlatformTenant;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.extern.jackson.Jacksonized;

import java.io.Serializable;

/**
 * @author YISivlay
 */
@AllArgsConstructor
@Jacksonized
@Builder
@Getter
public class Context implements Serializable {

    private final String contextHolder;
    private final PlatformTenant tenantContext;
    private final String authTokenContext;
    private final ActionContext actionContext;

}
