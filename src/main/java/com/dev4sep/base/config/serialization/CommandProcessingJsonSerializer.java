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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.stereotype.Component;

/**
 * @author YISivlay
 */
@Component
public final class CommandProcessingJsonSerializer {
    private final Gson gson;

    public CommandProcessingJsonSerializer() {
        final GsonBuilder builder = new GsonBuilder();
        GoogleGsonSerializerHelper.registerTypeAdapters(builder);
        builder.serializeNulls();
        this.gson = builder.create();
    }

    public String serialize(final Object result) {
        String returnedResult = null;
        final var serializedResult = this.gson.toJson(result);
        if (!"null".equalsIgnoreCase(serializedResult)) {
            returnedResult = serializedResult;
        }
        return returnedResult;
    }
}
