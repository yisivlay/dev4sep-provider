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
package com.dev4sep.base.adminstration.permission.serialization;

import com.dev4sep.base.adminstration.permission.api.PermissionApiConstants;
import com.dev4sep.base.adminstration.permission.data.PermissionsCommand;
import com.dev4sep.base.config.exception.InvalidJsonException;
import com.dev4sep.base.config.serialization.FromJsonHelper;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Type;
import java.util.Map;

/**
 * @author YISivlay
 */
@Component
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public final class PermissionsCommandDataValidator<T> extends AbstractFromApiJsonDeserializer<PermissionsCommand> {

    private final FromJsonHelper fromApiJsonHelper;

    @Override
    public PermissionsCommand commandFromApiJson(String json) {
        if (StringUtils.isBlank(json)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {
        }.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, PermissionApiConstants.SUPPORTED_PARAMETERS);

        return this.fromApiJsonHelper.fromJson(json, PermissionsCommand.class);
    }
}
