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
package com.dev4sep.base.config.auditing.domain;

import com.dev4sep.base.config.utils.DateUtils;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.domain.Auditable;

import java.time.OffsetDateTime;
import java.util.Optional;

/**
 * @author YISivlay
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class AbstractAuditableWithUTCDateTimeCustom extends AbstractPersistableCustom implements Auditable<Long, Long, OffsetDateTime> {

    private static final long serialVersionUID = 141481953116476081L;

    @Column(name = "created_by", nullable = false)
    @Setter(onMethod = @__(@Override))
    private Long createdBy;

    @Column(name = "created_on_utc", nullable = false)
    @Setter(onMethod = @__(@Override))
    private OffsetDateTime createdDate;

    @Column(name = "last_modified_by", nullable = false)
    @Setter(onMethod = @__(@Override))
    private Long lastModifiedBy;

    @Column(name = "last_modified_on_utc", nullable = false)
    @Setter(onMethod = @__(@Override))
    private OffsetDateTime lastModifiedDate;

    @Override
    @NotNull
    public Optional<Long> getCreatedBy() {
        return Optional.ofNullable(this.createdBy);
    }

    @Override
    @NotNull
    public Optional<OffsetDateTime> getCreatedDate() {
        return Optional.ofNullable(createdDate);
    }

    @NotNull
    public OffsetDateTime getCreatedDateTime() {
        return getCreatedDate().orElseGet(DateUtils::getAuditOffsetDateTime);
    }

    @Override
    @NotNull
    public Optional<Long> getLastModifiedBy() {
        return Optional.ofNullable(this.lastModifiedBy);
    }

    @Override
    @NotNull
    public Optional<OffsetDateTime> getLastModifiedDate() {
        return Optional.ofNullable(lastModifiedDate);
    }

    @NotNull
    public OffsetDateTime getLastModifiedDateTime() {
        return getLastModifiedDate().orElseGet(DateUtils::getAuditOffsetDateTime);
    }

}
