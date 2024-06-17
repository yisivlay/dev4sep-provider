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
package com.dev4sep.base.config.security.service;

import com.dev4sep.base.adminstration.user.domain.User;
import com.dev4sep.base.adminstration.user.exception.UnAuthenticatedUserException;
import com.dev4sep.base.adminstration.user.handler.UserCommandWrapperBuilder;
import com.dev4sep.base.config.command.domain.CommandWrapper;
import com.dev4sep.base.config.security.exception.ResetPasswordException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * @author YISivlay
 */
@Service
public class SpringSecurityPlatformSecurityContext implements PlatformSecurityContext {

    protected static final List<CommandWrapper> EXEMPT_FROM_PASSWORD_RESET_CHECK = new ArrayList<>(
            List.of(new UserCommandWrapperBuilder().update(null).build()));

    @Override
    public User authenticatedUser() {
        User user = null;
        final var context = SecurityContextHolder.getContext();
        if (context != null) {
            final var auth = context.getAuthentication();
            if (auth != null) {
                user = (User) auth.getPrincipal();
            }
        }
        if (user == null) {
            throw new UnAuthenticatedUserException();
        }
        if (this.doesPasswordHasToBeRenewed(user)) {
            throw new ResetPasswordException(user.getId());
        }
        return user;
    }

    @Override
    public User authenticatedUser(CommandWrapper request) {
        User login = null;
        final var context = SecurityContextHolder.getContext();
        if (context != null) {
            final var auth = context.getAuthentication();
            if (auth != null) {
                login = (User) auth.getPrincipal();
            }
        }

        if (login == null) {
            throw new UnAuthenticatedUserException();
        }

        if (this.shouldCheckForPasswordForceReset(request) && this.doesPasswordHasToBeRenewed(login)) {
            throw new ResetPasswordException(login.getId());
        }

        return login;
    }

    @Override
    public boolean doesPasswordHasToBeRenewed(User currentUser) {
        //TODO We will add configuration handler here from database for forcing user to change password
        return false;
    }

    private boolean shouldCheckForPasswordForceReset(CommandWrapper request) {
        for (CommandWrapper commandWrapper : EXEMPT_FROM_PASSWORD_RESET_CHECK) {
            if (commandWrapper.getActionName().equals(request.getActionName())
                    && commandWrapper.getEntityName().equals(request.getEntityName())) {
                return false;
            }
        }
        return true;
    }

    @Override
    public void isAuthenticated() {
        authenticatedUser();
    }
}
