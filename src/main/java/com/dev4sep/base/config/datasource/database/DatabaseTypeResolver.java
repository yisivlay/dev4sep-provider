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

import com.zaxxer.hikari.HikariConfig;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author YISivlay
 */
@Component
public class DatabaseTypeResolver implements InitializingBean {

    // TODO - Added multiple database drivers here
    private static final Map<String, DatabaseType> DRIVER_MAPPING = Map.of("com.mysql.cj.jdbc.Driver", DatabaseType.MYSQL, "org.mariadb.jdbc.Driver", DatabaseType.MYSQL);
    private static final AtomicReference<DatabaseType> currentDatabaseType = new AtomicReference<>();

    private final HikariConfig hikariConfig;

    @Autowired
    public DatabaseTypeResolver(HikariConfig hikariConfig) {
        this.hikariConfig = hikariConfig;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        currentDatabaseType.set(determineDatabaseType(hikariConfig.getDriverClassName()));
    }

    private DatabaseType determineDatabaseType(String driverClassName) {
        var databaseType = DRIVER_MAPPING.get(driverClassName);
        if (databaseType == null) {
            throw new IllegalArgumentException("The driver's class is not supported " + driverClassName);
        }
        return databaseType;
    }

    public DatabaseType databaseType() {
        return currentDatabaseType.get();
    }

    public boolean isMySQL() {
        return DatabaseType.MYSQL == currentDatabaseType.get();
    }
}
