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

import com.dev4sep.base.config.service.Page;
import com.google.gson.Gson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.Set;

/**
 * @author YISivlay
 */
@Component
public class DefaultToApiJsonSerializer<T> implements ToApiJsonSerializer<T> {

    private final GoogleGsonSerializerHelper helper;
    private final ExcludeNothingWithPrettyPrintingOffJsonSerializerGoogleGson excludeNothingWithPrettyPrintingOff;
    private final ExcludeNothingWithPrettyPrintingOnJsonSerializerGoogleGson excludeNothingWithPrettyPrintingOn;

    @Autowired
    public DefaultToApiJsonSerializer(final GoogleGsonSerializerHelper helper,
                                      final ExcludeNothingWithPrettyPrintingOffJsonSerializerGoogleGson excludeNothingWithPrettyPrintingOff,
                                      final ExcludeNothingWithPrettyPrintingOnJsonSerializerGoogleGson excludeNothingWithPrettyPrintingOn) {
        this.helper = helper;
        this.excludeNothingWithPrettyPrintingOff = excludeNothingWithPrettyPrintingOff;
        this.excludeNothingWithPrettyPrintingOn = excludeNothingWithPrettyPrintingOn;
    }

    private Gson findAppropriateSerializer(final ApiRequestJsonSerializationSettings settings,
                                           final Set<String> supportedResponseParameters) {
        Gson gson = null;
        if (settings.isPartialResponseRequired()) {
            gson = this.helper.createGsonBuilderWithParameterExclusionSerializationStrategy(supportedResponseParameters,
                    settings.isPrettyPrint(), settings.getParametersForPartialResponse());
        }
        return gson;
    }

    private String serializeWithSettings(final Gson gson, final ApiRequestJsonSerializationSettings settings, final Object[] dataObject) {
        String json = null;
        if (gson != null) {
            json = this.helper.serializedJsonFrom(gson, dataObject);
        } else {
            if (settings.isPrettyPrint()) {
                json = this.excludeNothingWithPrettyPrintingOn.serialize(dataObject);
            } else {
                json = serialize(dataObject);
            }
        }
        return json;
    }

    private String serializeWithSettings(final Gson gson, final ApiRequestJsonSerializationSettings settings, final Object dataObject) {
        String json = null;
        if (gson != null) {
            json = this.helper.serializedJsonFrom(gson, dataObject);
        } else {
            if (settings.isPrettyPrint()) {
                json = this.excludeNothingWithPrettyPrintingOn.serialize(dataObject);
            } else {
                json = serialize(dataObject);
            }
        }
        return json;
    }

    @Override
    public String serialize(final ApiRequestJsonSerializationSettings settings,
                            final T object,
                            final Set<String> supportedResponseParameters) {
        final var delegatedSerializer = findAppropriateSerializer(settings, supportedResponseParameters);
        return serializeWithSettings(delegatedSerializer, settings, object);
    }

    @Override
    public String serialize(Object object) {
        return this.excludeNothingWithPrettyPrintingOff.serialize(object);
    }

    @Override
    public String serialize(final ApiRequestJsonSerializationSettings settings,
                            final Collection<T> collection,
                            final Set<String> supportedResponseParameters) {
        final var delegatedSerializer = findAppropriateSerializer(settings, supportedResponseParameters);
        return serializeWithSettings(delegatedSerializer, settings, collection.toArray());
    }

    @Override
    public String serialize(ApiRequestJsonSerializationSettings settings, Page<T> page, Set<String> supportedResponseParameters) {
        final Gson delegatedSerializer = findAppropriateSerializer(settings, supportedResponseParameters);
        return serializeWithSettings(delegatedSerializer, settings, page);
    }
}
