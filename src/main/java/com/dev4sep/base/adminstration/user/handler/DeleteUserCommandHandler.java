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
package com.dev4sep.base.adminstration.user.handler;

import com.dev4sep.base.adminstration.user.api.UserApiConstants;
import com.dev4sep.base.adminstration.user.service.UserWritePlatformService;
import com.dev4sep.base.config.command.annotation.CommandType;
import com.dev4sep.base.config.command.domain.CommandProcessing;
import com.dev4sep.base.config.command.domain.JsonCommand;
import com.dev4sep.base.config.command.handler.CommandSourceHandler;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author YISivlay
 */
@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@CommandType(entity = UserApiConstants.PERMISSIONS, action = "DELETE")
public class DeleteUserCommandHandler implements CommandSourceHandler {

    private final UserWritePlatformService writePlatformService;

    @Override
    @Transactional
    public CommandProcessing processCommand(final JsonCommand command) {
        return this.writePlatformService.delete(command.getResourceId());
    }
}
