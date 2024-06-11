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

import com.google.common.base.Joiner;
import org.springframework.boot.autoconfigure.liquibase.LiquibaseProperties;
import org.springframework.core.io.ResourceLoader;

import javax.sql.DataSource;
import java.io.File;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.isNotBlank;

/**
 * @author YISivlay
 */
public class ExtendedSpringLiquibaseBuilder {

    private final Set<String> contexts = new HashSet<>();
    private final Map<String, String> changeLogParameters = new HashMap<>();
    private final boolean clearCheckSums;
    private final String liquibaseSchema;
    private final String liquibaseTableSpace;
    private final String databaseChangeLogTable;
    private final String databaseChangeLogLockTable;
    private final boolean shouldRun;
    private final String labelFilter;
    private final File rollbackFile;
    private final boolean testRollbackOnUpdate;
    private final String tag;
    private final String changeLog;
    private ResourceLoader resourceLoader;
    private final String defaultSchema;
    private final boolean dropFirst;
    private DataSource dataSource;

    public ExtendedSpringLiquibaseBuilder(LiquibaseProperties liquibaseProperties) {
        this.defaultSchema = liquibaseProperties.getDefaultSchema();
        this.dropFirst = liquibaseProperties.isDropFirst();
        if (liquibaseProperties.getParameters() != null) {
            this.changeLogParameters.putAll(liquibaseProperties.getParameters());
        }
        this.changeLog = liquibaseProperties.getChangeLog();
        if (isNotBlank(liquibaseProperties.getContexts())) {
            this.contexts.add(liquibaseProperties.getContexts());
        }
        this.clearCheckSums = liquibaseProperties.isClearChecksums();
        this.liquibaseSchema = liquibaseProperties.getLiquibaseSchema();
        this.liquibaseTableSpace = liquibaseProperties.getLiquibaseTablespace();
        this.databaseChangeLogTable = liquibaseProperties.getDatabaseChangeLogTable();
        this.databaseChangeLogLockTable = liquibaseProperties.getDatabaseChangeLogLockTable();
        this.shouldRun = liquibaseProperties.isEnabled();
        this.labelFilter = liquibaseProperties.getLabelFilter();
        this.rollbackFile = liquibaseProperties.getRollbackFile();
        this.testRollbackOnUpdate = liquibaseProperties.isTestRollbackOnUpdate();
        this.tag = liquibaseProperties.getTag();
    }

    public ExtendedSpringLiquibaseBuilder withDataSource(DataSource dataSource) {
        this.dataSource = dataSource;
        return this;
    }

    public ExtendedSpringLiquibaseBuilder withContext(String context) {
        if (isNotBlank(context)) {
            this.contexts.add(context);
        }
        return this;
    }

    public ExtendedSpringLiquibaseBuilder withContexts(String... contexts) {
        return withContexts(Arrays.asList(contexts));
    }

    public ExtendedSpringLiquibaseBuilder withContexts(Collection<String> contexts) {
        this.contexts.addAll(contexts);
        return this;
    }

    public ExtendedSpringLiquibaseBuilder withResourceLoader(ResourceLoader resourceLoader) {
        this.resourceLoader = resourceLoader;
        return this;
    }

    public ExtendedSpringLiquibase build() {
        var result = new ExtendedSpringLiquibase();
        var liquibaseContexts = Joiner.on(",").join(contexts);
        result.setContexts(liquibaseContexts);
        result.setDataSource(dataSource);
        result.setChangeLog(changeLog);
        result.setResourceLoader(resourceLoader);
        result.setDefaultSchema(defaultSchema);
        result.setDropFirst(dropFirst);
        result.setChangeLogParameters(changeLogParameters);
        result.setClearCheckSums(clearCheckSums);
        result.setLiquibaseSchema(liquibaseSchema);
        result.setLiquibaseTablespace(liquibaseTableSpace);
        result.setDatabaseChangeLogTable(databaseChangeLogTable);
        result.setDatabaseChangeLogLockTable(databaseChangeLogLockTable);
        result.setShouldRun(shouldRun);
        result.setLabelFilter(labelFilter);
        result.setRollbackFile(rollbackFile);
        result.setTestRollbackOnUpdate(testRollbackOnUpdate);
        result.setTag(tag);
        return result;
    }

}
