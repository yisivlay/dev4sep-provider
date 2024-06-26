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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.util.List;

import static java.lang.String.format;

/**
 * @author YISivlay
 */
@Component
public class MySQLQueryService implements DatabaseQueryService {

    private final DatabaseTypeResolver databaseTypeResolver;

    @Autowired
    public MySQLQueryService(DatabaseTypeResolver databaseTypeResolver) {
        this.databaseTypeResolver = databaseTypeResolver;
    }

    @Override
    public boolean isSupported() {
        return databaseTypeResolver.isMySQL();
    }

    @Override
    public boolean isTablePresent(DataSource dataSource, String tableName) {
        JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        SqlRowSet rs = jdbcTemplate.queryForRowSet(format("SHOW TABLES LIKE '%s'", tableName));
        return rs.next();
    }

    @Override
    public SqlRowSet getTableColumns(DataSource dataSource, String tableName) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final String sql = "SELECT c.COLUMN_NAME, c.IS_NULLABLE, c.DATA_TYPE, c.CHARACTER_MAXIMUM_LENGTH, c.COLUMN_KEY FROM INFORMATION_SCHEMA.COLUMNS c WHERE TABLE_SCHEMA = schema() AND TABLE_NAME = ? ORDER BY ORDINAL_POSITION";

        final SqlRowSet columnDefinitions = jdbcTemplate.queryForRowSet(sql, tableName);
        if (columnDefinitions.next()) {
            return columnDefinitions;
        } else {
            throw new IllegalArgumentException("Table " + tableName + " is not found");
        }
    }

    @Override
    public List<IndexDetail> getTableIndexes(DataSource dataSource, String tableName) {
        final JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
        final String sql = "SELECT i.INDEX_NAME FROM INFORMATION_SCHEMA.STATISTICS i WHERE TABLE_SCHEMA = schema() AND TABLE_NAME = ?";
        final SqlRowSet indexDefinitions = jdbcTemplate.queryForRowSet(sql, tableName);
        if (indexDefinitions.next()) {
            return DatabaseIndexMapper.getIndexDetails(indexDefinitions);
        } else {
            throw new IllegalArgumentException("Table " + tableName + " is not found");
        }
    }
}
