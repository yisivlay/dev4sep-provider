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
package com.dev4sep.base.config.datasource.database.migration;

import com.dev4sep.base.config.datasource.database.DatabaseType;
import com.dev4sep.base.config.datasource.database.DatabaseTypeResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * @author YISivlay
 */
@Component
public class DatabaseAwareMigrationContextProvider {

    //TODO - Add multiple migration database connections here
    private static final Map<DatabaseType, String> CONTEXT_MAPPING = Map.of(DatabaseType.MYSQL, "mysql");

    private final DatabaseTypeResolver databaseTypeResolver;

    @Autowired
    public DatabaseAwareMigrationContextProvider(DatabaseTypeResolver databaseTypeResolver) {
        this.databaseTypeResolver = databaseTypeResolver;
    }

    public String provide() {
        var databaseType = databaseTypeResolver.databaseType();
        var context = CONTEXT_MAPPING.get(databaseType);
        if (context == null) {
            throw new IllegalStateException("Database is not supported");
        }
        return context;
    }
}
