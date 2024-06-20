package com.dev4sep.base.config.security.domain;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.Accessors;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * @author YISivlay
 */
@Setter
@NoArgsConstructor
@Accessors(chain = true)
public class BasicPasswordEncodablePlatformUser implements PlatformUser {

    @Getter
    private Long id;
    @Getter(onMethod = @__(@Override))
    private String username;
    @Getter(onMethod = @__(@Override))
    private String password;

    @Override
    public Collection<GrantedAuthority> getAuthorities() {
        return null;
    }

    @Override
    public boolean isAccountNonExpired() {
        return false;
    }

    @Override
    public boolean isAccountNonLocked() {
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return false;
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
