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
package com.dev4sep.base.config.security;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * @author YISivlay
 */
@Configuration
public class SecurityProfileConfig {

    @Value("${dev4sep.security.basicauth.enabled}")
    private Boolean basicAuthEnabled;

    @PostConstruct
    public void validate() {
        if (Boolean.FALSE.equals(basicAuthEnabled)) {
            throw new IllegalArgumentException(
                    "Please enable 'dev4sep.security.basicauth.enabled=true' to use basic authentication.");
        }
    }
}
