/**
 * Copyright 2024 DEV4Sep
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.dev4sep.base.config.datasource.database;

import com.dev4sep.base.config.Properties;
import com.dev4sep.base.config.security.service.PasswordEncryptor;
import com.dev4sep.base.config.utils.EncryptionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.Optional;

/**
 * @author YISivlay
 */
@Component
public class DatabasePasswordEncryptor implements PasswordEncryptor {

    public static final String DEFAULT_ENCRYPTION = "AES/CBC/PKCS5Padding";

    private final Properties properties;

    @Autowired
    public DatabasePasswordEncryptor(Properties properties) {
        this.properties = properties;
    }

    private static String getPasswordHash(String masterPassword) {
        return BCrypt.hashpw(masterPassword.getBytes(StandardCharsets.UTF_8), BCrypt.gensalt());
    }

    public String getMasterPasswordHash() {
        var masterPassword = Optional.ofNullable(properties)
                .map(Properties::getTenant)
                .map(Properties.TenantProperties::getMasterPassword)
                .orElse(properties.getDatabase().getDefaultMasterPassword());
        return getPasswordHash(masterPassword);
    }

    public boolean isMasterPasswordHashValid(String hashed) {
        var masterPassword = Optional.ofNullable(properties)
                .map(Properties::getTenant)
                .map(Properties.TenantProperties::getMasterPassword)
                .orElse(properties.getDatabase().getDefaultMasterPassword());
        return BCrypt.checkpw(masterPassword, hashed);
    }

    @Override
    public String encrypt(String plainPassword) {
        var masterPassword = Optional.ofNullable(properties.getTenant())
                .map(Properties.TenantProperties::getMasterPassword)
                .orElse(properties.getDatabase().getDefaultMasterPassword());
        var encryption = Optional.ofNullable(properties.getTenant())
                .map(Properties.TenantProperties::getEncryption).orElse(DEFAULT_ENCRYPTION);
        return EncryptionUtil.encryptToBase64(encryption, masterPassword, plainPassword);
    }

    @Override
    public String decrypt(String encryptedPassword) {
        var masterPassword = Optional.ofNullable(properties.getTenant())
                .map(Properties.TenantProperties::getMasterPassword)
                .orElse(properties.getDatabase().getDefaultMasterPassword());
        var encryption = Optional.ofNullable(properties.getTenant())
                .map(Properties.TenantProperties::getEncryption).orElse(DEFAULT_ENCRYPTION);
        return EncryptionUtil.decryptFromBase64(encryption, masterPassword, encryptedPassword);
    }
}
