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

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * @author YISivlay
 */
@Getter
@Setter
@ConfigurationProperties(prefix = "dev4sep")
public class Properties {

    private String nodeId;
    private TenantProperties tenant;
    private ModeProperties mode;
    private DatabaseProperties database;
    private SecurityProperties security;

    @Getter
    @Setter
    public static class TenantProperties {

        private String host;
        private Integer port;
        private String username;
        private String password;
        private String parameters;
        private String timezone;
        private String identifier;
        private String name;
        private String description;
        private String masterPassword;
        private String encryption;

        private String readOnlyHost;
        private Integer readOnlyPort;
        private String readOnlyUsername;
        private String readOnlyPassword;
        private String readOnlyParameters;
        private String readOnlyName;

        private ConfigProperties config;
    }

    /**
     * Configuration properties to override configurations stored in the tenants database
     */
    @Getter
    @Setter
    public static class ConfigProperties {

        private int minPoolSize;
        private int maxPoolSize;

        public boolean isMinPoolSizeSet() {
            return minPoolSize != -1;
        }

        public boolean isMaxPoolSizeSet() {
            return maxPoolSize != -1;
        }
    }

    @Getter
    @Setter
    public static class ModeProperties {

        private boolean readEnabled;
        private boolean writeEnabled;
        private boolean batchWorkerEnabled;
        private boolean batchManagerEnabled;

        public boolean isReadOnlyMode() {
            return readEnabled && !writeEnabled && !batchWorkerEnabled && !batchManagerEnabled;
        }
    }

    @Getter
    @Setter
    public static class DatabaseProperties {
        private String defaultMasterPassword;
    }

    @Getter
    @Setter
    public static class SecurityProperties {
        private SecurityBasicAuth basicauth;
    }

    @Getter
    @Setter
    public static class SecurityBasicAuth {

        private boolean enabled;
    }
}
