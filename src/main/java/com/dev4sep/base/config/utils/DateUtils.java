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
package com.dev4sep.base.config.utils;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;

/**
 * @author YISivlay
 */
public final class DateUtils {

    public static LocalDateTime getLocalDateTimeOfSystem(ChronoUnit truncate) {
        LocalDateTime now = LocalDateTime.now(ZoneId.systemDefault());
        return truncate == null ? now : now.truncatedTo(truncate);
    }

    public static LocalDateTime getLocalDateTimeOfSystem() {
        return getLocalDateTimeOfSystem(null);
    }

    public static OffsetDateTime getAuditOffsetDateTime() {
        return OffsetDateTime.now(ZoneOffset.UTC);
    }

}