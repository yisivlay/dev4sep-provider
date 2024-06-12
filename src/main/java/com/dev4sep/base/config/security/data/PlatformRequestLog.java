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
package com.dev4sep.base.config.security.data;

import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import org.apache.commons.lang3.time.StopWatch;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author YISivlay
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class PlatformRequestLog {

    @SuppressWarnings("unused")
    private long startTime;
    @SuppressWarnings("unused")
    private long totalTime;
    @SuppressWarnings("unused")
    private String method;
    @SuppressWarnings("unused")
    private String url;
    @SuppressWarnings("unused")
    private Map<String, String[]> parameters;

    public static PlatformRequestLog from(final StopWatch task, final HttpServletRequest request) throws IOException {
        final var requestUrl = request.getRequestURL().toString();
        final var parameters = new HashMap<>(request.getParameterMap());
        parameters.remove("password");
        parameters.remove("_");

        return new PlatformRequestLog()
                .setStartTime(task.getStartTime())
                .setTotalTime(task.getTime())
                .setMethod(request.getMethod())
                .setUrl(requestUrl)
                .setParameters(parameters);
    }
}
