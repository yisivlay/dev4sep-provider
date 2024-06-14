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
package com.dev4sep.base.config.service;

import com.dev4sep.base.config.datasource.database.DatabaseSpecificSQLGenerator;
import com.dev4sep.base.config.datasource.database.DatabaseTypeResolver;
import org.apache.commons.lang3.ObjectUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * @author YISivlay
 */
@Component
public class PaginationHelper {

    private final DatabaseSpecificSQLGenerator sqlGenerator;
    private final DatabaseTypeResolver databaseTypeResolver;

    @Autowired
    public PaginationHelper(final DatabaseSpecificSQLGenerator sqlGenerator,
                            final DatabaseTypeResolver databaseTypeResolver) {
        this.sqlGenerator = sqlGenerator;
        this.databaseTypeResolver = databaseTypeResolver;
    }

    public <E> Page<E> fetchPage(final JdbcTemplate jt, final String sqlFetchRows, final Object[] args, final RowMapper<E> rowMapper) {

        final List<E> items = jt.query(sqlFetchRows, rowMapper, args);

        // determine how many rows are available
        final String sqlCountRows = sqlGenerator.countLastExecutedQueryResult(sqlFetchRows);
        final Integer totalFilteredRecords;
        if (databaseTypeResolver.isMySQL()) {
            totalFilteredRecords = jt.queryForObject(sqlCountRows, Integer.class);
        } else {
            totalFilteredRecords = jt.queryForObject(sqlCountRows, Integer.class, args);
        }

        return new Page<>(items, totalFilteredRecords);
    }

    public <E> Page<Long> fetchPage(JdbcTemplate jdbcTemplate, String sql, Class<Long> type) {
        final List<Long> items = jdbcTemplate.queryForList(sql, type);

        String sqlCountRows = sqlGenerator.countLastExecutedQueryResult(sql);
        Integer totalFilteredRecords = jdbcTemplate.queryForObject(sqlCountRows, Integer.class);

        return new Page<>(items, ObjectUtils.defaultIfNull(totalFilteredRecords, 0));
    }

}
