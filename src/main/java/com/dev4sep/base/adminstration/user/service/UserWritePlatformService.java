package com.dev4sep.base.adminstration.user.service;

import com.dev4sep.base.config.command.domain.CommandProcessing;
import com.dev4sep.base.config.command.domain.JsonCommand;

/**
 * @author YISivlay
 */
public interface UserWritePlatformService {

    CommandProcessing create(final JsonCommand command);

    CommandProcessing update(final Long id, final JsonCommand command);

    CommandProcessing delete(final Long id);
}
