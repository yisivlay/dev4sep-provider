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
package com.dev4sep.base.config.utils;

import com.dev4sep.base.config.ThreadLocalContextUtil;
import com.dev4sep.base.config.datasource.database.domain.PlatformTenant;

import java.time.*;
import java.time.temporal.ChronoUnit;

/**
 * @author YISivlay
 */
public final class DateUtils {

    public static LocalDateTime getLocalDateTimeOfSystem(ChronoUnit truncate) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        return truncate == null ? now : now.truncatedTo(truncate);
    }

    public static ZoneId getDateTimeZoneOfTenant() {
        final PlatformTenant tenant = ThreadLocalContextUtil.getTenant();
        return ZoneId.of(tenant.getTimezoneId());
    }

    public static LocalDate getLocalDateOfTenant() {
        return LocalDate.now(getDateTimeZoneOfTenant());
    }

    public static LocalDateTime getLocalDateTimeOfSystem() {
        return getLocalDateTimeOfSystem(null);
    }

    public static OffsetDateTime getAuditOffsetDateTime() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

    public static ZoneId getSystemZoneId() {
        return ZoneId.systemDefault();
    }
}
