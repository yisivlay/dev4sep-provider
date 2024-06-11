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
package com.dev4sep.base.config.jpa;

import com.dev4sep.base.config.auditing.JpaAuditingHandlerRegistrar;
import com.dev4sep.base.config.auditing.domain.AuditorAwareImpl;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration;
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.orm.jpa.EntityManagerFactoryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.persistenceunit.PersistenceManagedTypes;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;
import org.springframework.orm.jpa.vendor.EclipseLinkJpaVendorAdapter;
import org.springframework.transaction.jta.JtaTransactionManager;

import javax.sql.DataSource;
import java.util.*;

/**
 * @author YISivlay
 */
@Configuration
@EnableJpaAuditing
@EnableConfigurationProperties(JpaProperties.class)
@EnableJpaRepositories(basePackages = {"com.dev4sep.base.**"})
@Import(JpaAuditingHandlerRegistrar.class)
public class JPAConfig extends JpaBaseConfiguration {

    private final Collection<EntityManagerFactoryCustomizer> emFactoryCustomizers;

    @Autowired
    protected JPAConfig(DataSource dataSource,
                        JpaProperties properties,
                        ObjectProvider<JtaTransactionManager> jtaTransactionManager,
                        Collection<EntityManagerFactoryCustomizer> emFactoryCustomizers) {
        super(dataSource, properties, jtaTransactionManager);
        this.emFactoryCustomizers = emFactoryCustomizers;
    }

    @Bean
    public AuditorAware<Long> auditorAware() {
        return new AuditorAwareImpl();
    }

    @Override
    @Bean
    public LocalContainerEntityManagerFactoryBean entityManagerFactory(EntityManagerFactoryBuilder factoryBuilder, PersistenceManagedTypes persistenceManagedTypes) {
        var vendorProperties = getVendorProperties();
        var packagesToScan = getPackagesToScan();
        return factoryBuilder
                .dataSource(getDataSource())
                .properties(vendorProperties)
                .persistenceUnit("jpa-pu")
                .packages(packagesToScan)
                .jta(false).build();
    }

    protected String[] getPackagesToScan() {
        Set<String> packagesToScan = new HashSet<>();
        packagesToScan.add("com.dev4sep.base");
        emFactoryCustomizers.forEach(c -> packagesToScan.addAll(c.additionalPackagesToScan()));
        return packagesToScan.toArray(String[]::new);
    }

    @Override
    protected AbstractJpaVendorAdapter createJpaVendorAdapter() {
        return new EclipseLinkJpaVendorAdapter();
    }

    @Override
    protected Map<String, Object> getVendorProperties() {
        Map<String, Object> vendorProperties = new HashMap<>();
        vendorProperties.put(PersistenceUnitProperties.WEAVING, "static");
        vendorProperties.put(PersistenceUnitProperties.PERSISTENCE_CONTEXT_CLOSE_ON_COMMIT, "true");
        vendorProperties.put(PersistenceUnitProperties.CACHE_SHARED_DEFAULT, "false");
        emFactoryCustomizers.forEach(c -> vendorProperties.putAll(c.additionalVendorProperties()));
        return vendorProperties;
    }
}
