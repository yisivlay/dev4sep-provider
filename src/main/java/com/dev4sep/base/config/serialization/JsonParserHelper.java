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
package com.dev4sep.base.config.serialization;

import com.dev4sep.base.config.data.ApiParameterError;
import com.dev4sep.base.config.exception.PlatformApiDataValidationException;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.commons.lang3.StringUtils;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatterBuilder;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.time.temporal.ChronoField;
import java.util.*;

/**
 * @author YISivlay
 */
public class JsonParserHelper {

    public String extractDateFormatParameter(final JsonObject element) {
        String value = null;
        if (element.isJsonObject()) {
            final var object = element.getAsJsonObject();

            final var dateFormatParameter = "dateFormat";
            if (object.has(dateFormatParameter) && object.get(dateFormatParameter).isJsonPrimitive()) {
                final var primitive = object.get(dateFormatParameter).getAsJsonPrimitive();
                value = primitive.getAsString();
            }
        }
        return value;
    }

    public Long extractLongNamed(final String parameterName,
                                 final JsonElement element,
                                 final Set<String> parametersPassedInRequest) {
        Long longValue = null;
        if (element.isJsonObject()) {
            final var object = element.getAsJsonObject();
            if (object.has(parameterName) && object.get(parameterName).isJsonPrimitive()) {
                parametersPassedInRequest.add(parameterName);
                final var primitive = object.get(parameterName).getAsJsonPrimitive();
                final var stringValue = primitive.getAsString();
                if (StringUtils.isNotBlank(stringValue)) {
                    longValue = Long.valueOf(stringValue);
                }
            }
        }
        return longValue;
    }

    public String extractStringNamed(final String parameterName,
                                     final JsonElement element,
                                     final Set<String> parametersPassedInRequest) {
        String stringValue = null;
        if (element.isJsonObject()) {
            final var object = element.getAsJsonObject();
            if (object.has(parameterName) && object.get(parameterName).isJsonPrimitive()) {
                parametersPassedInRequest.add(parameterName);
                final var primitive = object.get(parameterName).getAsJsonPrimitive();
                final var valueAsString = primitive.getAsString();
                if (StringUtils.isNotBlank(valueAsString)) {
                    stringValue = valueAsString;
                }
            }
        }
        return stringValue;
    }

    public boolean parameterExists(final String parameterName, final JsonElement element) {
        if (element == null) {
            return false;
        }
        return element.getAsJsonObject().has(parameterName);
    }

    private static Locale localeFrom(final String languageCode, final String courntryCode, final String variantCode) {

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final var allowedLanguages = Arrays.asList(Locale.getISOLanguages());
        if (!allowedLanguages.contains(languageCode.toLowerCase())) {
            final var error = ApiParameterError.parameterError(
                    "validation.msg.invalid.locale.format",
                    "The parameter `locale` has an invalid language value " + languageCode + " .",
                    "locale",
                    languageCode
            );
            dataValidationErrors.add(error);
        }

        if (StringUtils.isNotBlank(courntryCode.toUpperCase())) {
            final var allowedCountries = Arrays.asList(Locale.getISOCountries());
            if (!allowedCountries.contains(courntryCode)) {
                final var error = ApiParameterError.parameterError(
                        "validation.msg.invalid.locale.format",
                        "The parameter `locale` has an invalid country value " + courntryCode + " .",
                        "locale",
                        courntryCode
                );
                dataValidationErrors.add(error);
            }
        }

        if (!dataValidationErrors.isEmpty()) {
            throw new PlatformApiDataValidationException(
                    "validation.msg.validation.errors.exist",
                    "Validation errors exist.",
                    dataValidationErrors
            );
        }

        return new Locale(languageCode.toLowerCase(), courntryCode.toUpperCase(), variantCode);
    }

    public static Locale localeFromString(final String localeAsString) {

        if (StringUtils.isBlank(localeAsString)) {
            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            final var error = ApiParameterError.parameterError(
                    "validation.msg.invalid.locale.format",
                    "The parameter `locale` is invalid. It cannot be blank.",
                    "locale");
            dataValidationErrors.add(error);

            throw new PlatformApiDataValidationException(
                    "validation.msg.validation.errors.exist",
                    "Validation errors exist.",
                    dataValidationErrors
            );
        }

        String languageCode = "";
        String countryCode = "";
        String variantCode = "";

        final var localeParts = localeAsString.split("_");

        if (localeParts.length == 1) {
            languageCode = localeParts[0];
        }

        if (localeParts.length == 2) {
            languageCode = localeParts[0];
            countryCode = localeParts[1];
        }

        if (localeParts.length == 3) {
            languageCode = localeParts[0];
            countryCode = localeParts[1];
            variantCode = localeParts[2];
        }

        return localeFrom(languageCode, countryCode, variantCode);
    }

    public Locale extractLocaleParameter(final JsonObject element) {
        Locale clientApplicationLocale = null;
        if (element.isJsonObject()) {
            final var object = element.getAsJsonObject();

            String locale = null;
            final var localeParameter = "locale";
            if (object.has(localeParameter) && object.get(localeParameter).isJsonPrimitive()) {
                final var primitive = object.get(localeParameter).getAsJsonPrimitive();
                locale = primitive.getAsString();
                clientApplicationLocale = localeFromString(locale);
            }
        }
        return clientApplicationLocale;
    }

    private static void validateDateFormatAndLocale(final String parameterName,
                                                    final String dateFormat,
                                                    final Locale clientApplicationLocale) {
        if (StringUtils.isBlank(dateFormat) || clientApplicationLocale == null) {

            final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
            if (StringUtils.isBlank(dateFormat)) {
                final String defaultMessage = new StringBuilder(
                        "The parameter `" + parameterName + "` requires a `dateFormat` parameter to be passed with it.").toString();
                final ApiParameterError error = ApiParameterError.parameterError("validation.msg.missing.dateFormat.parameter",
                        defaultMessage, parameterName);
                dataValidationErrors.add(error);
            }
            if (clientApplicationLocale == null) {
                final String defaultMessage = new StringBuilder(
                        "The parameter `" + parameterName + "` requires a `locale` parameter to be passed with it.").toString();
                final ApiParameterError error = ApiParameterError.parameterError("validation.msg.missing.locale.parameter", defaultMessage,
                        parameterName);
                dataValidationErrors.add(error);
            }
            throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist", "Validation errors exist.",
                    dataValidationErrors);
        }

    }

    public static LocalDateTime convertDateTimeFrom(final String dateTimeAsString, final String parameterName, String dateTimeFormat,
                                                    final Locale clientApplicationLocale) {

        validateDateFormatAndLocale(parameterName, dateTimeFormat, clientApplicationLocale);
        LocalDateTime eventLocalDateTime = null;
        if (StringUtils.isNotBlank(dateTimeAsString)) {
            try {
                var strictResolveCompatibleDateTimeFormat = dateTimeFormat.replace("y", "u");
                var formatter = new DateTimeFormatterBuilder()
                        .parseCaseInsensitive()
                        .parseLenient()
                        .appendPattern(strictResolveCompatibleDateTimeFormat)
                        .optionalStart()
                        .appendPattern(" HH:mm:ss")
                        .optionalEnd()
                        .parseDefaulting(ChronoField.HOUR_OF_DAY, 0)
                        .parseDefaulting(ChronoField.MINUTE_OF_HOUR, 0)
                        .parseDefaulting(ChronoField.SECOND_OF_MINUTE, 0)
                        .toFormatter(clientApplicationLocale)
                        .withResolverStyle(ResolverStyle.STRICT);
                eventLocalDateTime = LocalDateTime.parse(dateTimeAsString, formatter);
            } catch (final IllegalArgumentException | DateTimeParseException e) {
                final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
                final var error = ApiParameterError.parameterError(
                        "validation.msg.invalid.dateFormat.format",
                        "The parameter `" + parameterName + "` is invalid based on the dateFormat: `" + dateTimeFormat
                                + "` and locale: `" + clientApplicationLocale + "` provided:",
                        parameterName,
                        eventLocalDateTime,
                        dateTimeFormat
                );
                dataValidationErrors.add(error);

                throw new PlatformApiDataValidationException(
                        "validation.msg.validation.errors.exist",
                        "Validation errors exist.",
                        dataValidationErrors,
                        e
                );
            }
        }

        return eventLocalDateTime;
    }

    public static LocalDate convertFrom(final String dateAsString,
                                        final String parameterName,
                                        final String dateFormat,
                                        final Locale clientApplicationLocale) {

        return convertDateTimeFrom(dateAsString, parameterName, dateFormat, clientApplicationLocale).toLocalDate();
    }

    public LocalDate extractLocalDateNamed(final String parameter,
                                           final JsonElement element,
                                           final String dateFormat,
                                           final Locale clientApplicationLocale,
                                           final Set<String> parametersPassedInCommand) {
        LocalDate value = null;
        if (element.isJsonObject()) {
            final var object = element.getAsJsonObject();

            if (object.has(parameter) && object.get(parameter).isJsonPrimitive()) {

                parametersPassedInCommand.add(parameter);

                final var primitive = object.get(parameter).getAsJsonPrimitive();
                final var valueAsString = primitive.getAsString();
                if (StringUtils.isNotBlank(valueAsString)) {
                    value = convertFrom(valueAsString, parameter, dateFormat, clientApplicationLocale);
                }
            }

        }
        return value;
    }

    public LocalDate extractLocalDateNamed(final String parameter,
                                           final JsonElement element,
                                           final Set<String> parametersPassedInCommand) {

        LocalDate value = null;

        if (element.isJsonObject()) {
            final var object = element.getAsJsonObject();

            final var dateFormat = extractDateFormatParameter(object);
            final var locale = extractLocaleParameter(object);
            value = extractLocalDateNamed(parameter, object, dateFormat, locale, parametersPassedInCommand);
        }
        return value;
    }

}
