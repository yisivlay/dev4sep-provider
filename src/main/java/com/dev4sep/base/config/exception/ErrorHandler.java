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
package com.dev4sep.base.config.exception;

import com.dev4sep.base.config.command.domain.Header;
import com.dev4sep.base.config.command.exception.ErrorInfo;
import com.dev4sep.base.config.data.ApiParameterError;
import com.dev4sep.base.config.exception.mapper.CustomExceptionMapper;
import com.dev4sep.base.config.exception.mapper.DefaultExceptionMapper;
import com.dev4sep.base.config.serialization.GoogleGsonSerializerHelper;
import com.google.gson.Gson;
import jakarta.persistence.PersistenceException;
import jakarta.validation.constraints.NotNull;
import jakarta.ws.rs.ext.ExceptionMapper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.validate.ValidationException;
import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.eclipse.persistence.exceptions.OptimisticLockException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.NestedRuntimeException;
import org.springframework.dao.NonTransientDataAccessException;
import org.springframework.dao.PessimisticLockingFailureException;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;

import javax.naming.AuthenticationException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.springframework.core.ResolvableType.forClassWithGenerics;

/**
 * @author YISivlay
 */
@Slf4j
@Component
@AllArgsConstructor(onConstructor = @__(@Autowired))
public final class ErrorHandler {

    private static final Gson JSON_HELPER = GoogleGsonSerializerHelper.createGsonBuilder(true).create();

    private final ApplicationContext ctx;
    private final DefaultExceptionMapper defaultExceptionMapper;

    public static RuntimeException getMappable(@NotNull Throwable thr) {
        return getMappable(thr, null, null, null);
    }

    public static RuntimeException getMappable(@NotNull Throwable thr, String msgCode, String defaultMsg) {
        return getMappable(thr, msgCode, defaultMsg, null);
    }

    public static RuntimeException getMappable(@NotNull Throwable t, String msgCode, String defaultMsg, String param,
                                               final Object... defaultMsgArgs) {
        var msg = defaultMsg == null ? t.getMessage() : defaultMsg;
        var codePfx = "error.msg" + (param == null ? "" : ("." + param));
        var args = defaultMsgArgs == null ? new Object[]{t} : defaultMsgArgs;

        Throwable cause;
        if ((cause = PessimisticLockingFailureCode.match(t)) != null) {
            return new PessimisticLockingFailureException(msg, cause); // deadlock
        }
        if (t instanceof NestedRuntimeException nre) {
            cause = nre.getMostSpecificCause();
            msg = defaultMsg == null ? cause.getMessage() : defaultMsg;
            if (nre instanceof NonTransientDataAccessException) {
                msgCode = msgCode == null ? codePfx + ".data.integrity.issue" : msgCode;
                return new PlatformDataIntegrityException(msgCode, msg, param, args);
            } else if (cause instanceof OptimisticLockException) {
                return (RuntimeException) cause;
            }
        }
        if (t instanceof ValidationException) {
            msgCode = msgCode == null ? codePfx + ".validation.error" : msgCode;
            return new PlatformApiDataValidationException(List.of(ApiParameterError.parameterError(msgCode, msg, param, defaultMsgArgs)));
        }
        if (t instanceof jakarta.persistence.OptimisticLockException) {
            return (RuntimeException) t;
        }
        if (t instanceof PersistenceException) {
            msgCode = msgCode == null ? codePfx + ".persistence.error" : msgCode;
            return new PlatformDataIntegrityException(msgCode, msg, param, args);
        }
        if (t instanceof AuthenticationException) {
            msgCode = msgCode == null ? codePfx + ".authentication.error" : msgCode;
            return new PlatformDataIntegrityException(msgCode, msg, param, args);
        }
        if (t instanceof ParseException) {
            msgCode = msgCode == null ? codePfx + ".parse.error" : msgCode;
            return new PlatformDataIntegrityException(msgCode, msg, param, args);
        }
        if (t instanceof RuntimeException re) {
            return re;
        }
        return new RuntimeException(msg, t);
    }

    private static <T> Set<T> createSet(T[] array) {
        if (array == null) {
            return Set.of();
        } else {
            return Set.of(array);
        }
    }

    public static Throwable findMostSpecificException(Exception exception) {
        Throwable mostSpecificException = exception;
        while (mostSpecificException.getCause() != null) {
            mostSpecificException = mostSpecificException.getCause();
        }
        return mostSpecificException;
    }

    @NotNull
    public <T extends RuntimeException> ExceptionMapper<T> findMostSpecificExceptionHandler(T exception) {
        Class<?> clazz = exception.getClass();
        do {
            var exceptionMappers = createSet(ctx.getBeanNamesForType(forClassWithGenerics(ExceptionMapper.class, clazz)));
            var errorMappers = createSet(ctx.getBeanNamesForType(CustomExceptionMapper.class));
            var intersection = SetUtils.intersection(exceptionMappers, errorMappers);
            if (!intersection.isEmpty()) {
                // noinspection unchecked
                return (ExceptionMapper<T>) ctx.getBean(intersection.iterator().next());
            }
            if (!exceptionMappers.isEmpty()) {
                // noinspection unchecked
                return (ExceptionMapper<T>) ctx.getBean(exceptionMappers.iterator().next());
            }
            clazz = clazz.getSuperclass();
        } while (!clazz.equals(Exception.class));
        // noinspection unchecked
        return (ExceptionMapper<T>) defaultExceptionMapper;
    }

    private enum PessimisticLockingFailureCode {

        ROLLBACK("40"), // Transaction rollback
        DEADLOCK("60"), // Oracle: deadlock
        HY00("HY", "Lock wait timeout exceeded"), // MySql deadlock HY00
        ;

        private final String code;
        private final String msg;

        PessimisticLockingFailureCode(String code, String msg) {
            this.code = code;
            this.msg = msg;
        }

        PessimisticLockingFailureCode(String code) {
            this(code, null);
        }

        private static Throwable match(Throwable t) {
            Throwable rootCause = ExceptionUtils.getRootCause(t);
            return rootCause instanceof SQLException sqle && Arrays.stream(values()).anyMatch(e -> e.matches(sqle)) ? rootCause : null;
        }

        @Nullable
        private static String getSqlClassCode(SQLException ex) {
            var sqlState = ex.getSQLState();
            if (sqlState == null) {
                var nestedEx = ex.getNextException();
                if (nestedEx != null) {
                    sqlState = nestedEx.getSQLState();
                }
            }
            return sqlState != null && sqlState.length() > 2 ? sqlState.substring(0, 2) : sqlState;
        }

        private boolean matches(SQLException ex) {
            return code.equals(getSqlClassCode(ex)) && (msg == null || ex.getMessage().contains(msg));
        }
    }

    /**
     * Returns an object of ErrorInfo type containing the information regarding the raised error.
     * @return ErrorInfo
     */
    public ErrorInfo handle(@NotNull RuntimeException exception) {
        ExceptionMapper<RuntimeException> exceptionMapper = findMostSpecificExceptionHandler(exception);
        var response = exceptionMapper.toResponse(exception);
        var headers = response.getHeaders();
        var batchHeaders = headers == null ? null
                : headers.keySet().stream().map(e -> new Header(e, response.getHeaderString(e))).collect(Collectors.toSet());
        var errorCode = exceptionMapper instanceof CustomExceptionMapper ? ((CustomExceptionMapper) exceptionMapper).errorCode() : null;
        var msg = response.getEntity();
        return new ErrorInfo(response.getStatus(), errorCode, msg instanceof String ? (String) msg : JSON_HELPER.toJson(msg), batchHeaders);
    }
}
