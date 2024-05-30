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
package com.dev4sep.base.config.auditing;

import com.dev4sep.base.config.auditing.domain.AbstractAuditableWithUTCDateTimeCustom;
import org.springframework.data.auditing.AuditableBeanWrapper;
import org.springframework.data.auditing.AuditingHandler;
import org.springframework.data.auditing.DateTimeProvider;
import org.springframework.data.mapping.context.PersistentEntities;
import org.springframework.util.Assert;

/**
 * @author YISivlay
 */
public class CustomAuditingHandler extends AuditingHandler {
    /**
     * Creates a new {@link AuditableBeanWrapper} using the given {@link PersistentEntities} when looking up auditing
     * metadata via reflection.
     *
     * @param entities must not be {@literal null}.
     * @since 1.10
     */
    public CustomAuditingHandler(PersistentEntities entities) {
        super(entities);
    }

    private DateTimeProvider fetchDateTimeProvider(Object bean) {
        if (bean instanceof AbstractAuditableWithUTCDateTimeCustom) {
            return CustomDateTimeProvider.UTC;
        } else {
            return CustomDateTimeProvider.INSTANCE;
        }
    }

    @Override
    public <T> T markCreated(T source) {
        Assert.notNull(source, "Source entity must not be null");
        setDateTimeProvider(fetchDateTimeProvider(source));
        return super.markCreated(source);
    }

    @Override
    public <T> T markModified(T source) {
        Assert.notNull(source, "Source entity must not be null");
        setDateTimeProvider(fetchDateTimeProvider(source));
        return super.markModified(source);
    }
}
