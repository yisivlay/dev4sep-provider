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
package com.dev4sep.base.config.hooks.event;

import com.dev4sep.base.adminstration.user.domain.User;
import com.dev4sep.base.config.domain.Context;
import lombok.Getter;

/**
 * @author YISivlay
 */
@Getter
public class HookEvent extends Event {

    private final String payload;
    private final User user;

    public HookEvent(final HookEventSource source, final String payload, final User user, Context context) {
        super(source, context);
        this.payload = payload;
        this.user = user;
    }
}
