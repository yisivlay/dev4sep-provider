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

import jakarta.servlet.http.HttpServletRequest;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import java.util.Optional;

/**
 * @author YISivlay
 */
@Slf4j
@Component
@NoArgsConstructor
public final class CustomRequestContextHolder {

    public Object getAttribute(String key, HttpServletRequest request) {
        if (request != null) {
            return request.getAttribute(key);
        } else if (RequestContextHolder.getRequestAttributes() != null) {
            return Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                    .map(r -> r.getAttribute(key, RequestAttributes.SCOPE_REQUEST))
                    .orElse(null);
        }
        //TODO - We will handle Batch Request later
        return null;
    }

    public void setAttribute(String key, Object value, HttpServletRequest request) {
        if (request != null) {
            request.setAttribute(key, value);
        } else if (RequestContextHolder.getRequestAttributes() != null) {
            Optional.ofNullable(RequestContextHolder.getRequestAttributes())
                    .ifPresent(requestAttributes -> requestAttributes.setAttribute(key, value, RequestAttributes.SCOPE_REQUEST));
        }
        //TODO - We will handle Batch Request later
    }

    public void setAttribute(String key, Object value) {
        setAttribute(key, value, null);
    }

}
