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
