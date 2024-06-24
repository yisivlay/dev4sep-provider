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
package com.dev4sep.base.config.security.data;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import java.util.Collection;
import java.util.Objects;

/**
 * @author YISivlay
 */
public class CustomJwtAuthenticationToken extends JwtAuthenticationToken {

    private final UserDetails user;

    public CustomJwtAuthenticationToken(Jwt jwt, Collection<GrantedAuthority> authorities, UserDetails user) {
        super(jwt, authorities, user.getUsername());
        this.user = Objects.requireNonNull(user, "user");
    }

    @Override
    public UserDetails getPrincipal() {
        return user;
    }
}