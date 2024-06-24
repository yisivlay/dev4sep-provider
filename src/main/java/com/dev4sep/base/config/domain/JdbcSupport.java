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
package com.dev4sep.base.config.domain;

import com.dev4sep.base.config.utils.DateUtils;
import org.springframework.jdbc.support.JdbcUtils;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.time.ZonedDateTime;

/**
 * @author YISivlay
 */
public final class JdbcSupport {

    private JdbcSupport() {}

    public static ZonedDateTime getDateTime(final ResultSet rs, final String columnName) throws SQLException {
        ZonedDateTime dateTime = null;
        final var dateValue = rs.getTimestamp(columnName);
        if (dateValue != null) {
            dateTime = ZonedDateTime.of(dateValue.toLocalDateTime(), DateUtils.getDateTimeZoneOfTenant());
        }
        return dateTime;
    }

    public static LocalDate getLocalDate(final ResultSet rs, final String columnName) throws SQLException {
        LocalDate localDate = null;
        final var dateValue = rs.getDate(columnName);
        if (dateValue != null) {
            localDate = dateValue.toLocalDate();
        }
        return localDate;
    }

    public static LocalTime getLocalTime(final ResultSet rs, final String columnName) throws SQLException {
        LocalTime localTime = null;
        final var timeValue = rs.getTimestamp(columnName);
        if (timeValue != null) {
            localTime = timeValue.toLocalDateTime().toLocalTime();
        }
        return localTime;
    }

    public static Long getLong(final ResultSet rs, final String columnName) throws SQLException {
        return (Long) JdbcUtils.getResultSetValue(rs, rs.findColumn(columnName), Long.class);
    }

    public static Integer getInteger(final ResultSet rs, final String columnName) throws SQLException {
        return (Integer) JdbcUtils.getResultSetValue(rs, rs.findColumn(columnName), Integer.class);
    }

    public static Integer getIntegerDefaultToNullIfZero(final ResultSet rs, final String columnName) throws SQLException {
        final var value = (Integer) JdbcUtils.getResultSetValue(rs, rs.findColumn(columnName), Integer.class);
        return defaultToNullIfZero(value);
    }

    public static Long getLongDefaultToNullIfZero(final ResultSet rs, final String columnName) throws SQLException {
        final var value = (Long) JdbcUtils.getResultSetValue(rs, rs.findColumn(columnName), Long.class);
        return defaultToNullIfZero(value);
    }

    private static Integer defaultToNullIfZero(final Integer value) {
        var result = value;
        if (result != null && Integer.valueOf(0).equals(value)) {
            result = null;
        }
        return result;
    }

    private static Long defaultToNullIfZero(final Long value) {
        var result = value;
        if (result != null && Long.valueOf(0).equals(value)) {
            result = null;
        }
        return result;
    }

    public static BigDecimal getBigDecimalDefaultToZeroIfNull(final ResultSet rs, final String columnName) throws SQLException {
        final BigDecimal value = rs.getBigDecimal(columnName);
        return defaultToZeroIfNull(value);
    }

    private static BigDecimal defaultToZeroIfNull(final BigDecimal value) {
        var result = BigDecimal.ZERO;
        if (value != null) {
            result = value;
        }
        return result;
    }

    public static BigDecimal getBigDecimalDefaultToNullIfZero(final ResultSet rs, final String columnName) throws SQLException {
        final var value = rs.getBigDecimal(columnName);
        return defaultToNullIfZero(value);
    }

    private static BigDecimal defaultToNullIfZero(final BigDecimal value) {
        var result = value;
        if (value != null && BigDecimal.ZERO.compareTo(value) == 0) {
            result = null;
        }
        return result;
    }

    public static OffsetDateTime getOffsetDateTime(ResultSet rs, String columnName) throws SQLException {
        final var timestamp = rs.getTimestamp(columnName);
        if (timestamp != null) {
            var offsetDateTimeAtUTC = OffsetDateTime.of(timestamp.toLocalDateTime(),DateUtils.getSystemZoneId().getRules().getOffset(timestamp.toLocalDateTime()));
            return offsetDateTimeAtUTC.withOffsetSameInstant(DateUtils.getDateTimeZoneOfTenant().getRules().getOffset(offsetDateTimeAtUTC.toInstant()));
        }
        return null;
    }

}
