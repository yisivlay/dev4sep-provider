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
package com.dev4sep.base.config.datasource.database;

import jakarta.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import static java.lang.String.format;

/**
 * @author YISivlay
 */
@Component
public class DatabaseSpecificSQLGenerator {

    private final DatabaseTypeResolver databaseTypeResolver;

    @Autowired
    public DatabaseSpecificSQLGenerator(DatabaseTypeResolver databaseTypeResolver) {
        this.databaseTypeResolver = databaseTypeResolver;
    }

    public String countLastExecutedQueryResult(@NotNull String sql) {
        if (databaseTypeResolver.isMySQL()) {
            return "SELECT FOUND_ROWS()";
        } else {
            return countQueryResult(sql);
        }
    }

    public String countQueryResult(@NotNull String sql) {
        // Needs to remove the limit and offset
        sql = sql.replaceAll("LIMIT \\d+", "").replaceAll("OFFSET \\d+", "").trim();
        return format("SELECT COUNT(*) FROM (%s) AS temp", sql);
    }
}
