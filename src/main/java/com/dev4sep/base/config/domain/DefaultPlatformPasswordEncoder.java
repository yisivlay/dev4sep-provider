package com.dev4sep.base.config.domain;

import com.dev4sep.base.adminstration.user.domain.PlatformPasswordEncoder;
import com.dev4sep.base.config.security.domain.PlatformUser;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

/**
 * @author YISivlay
 */
@Scope("singleton")
@Service(value = "applicationPasswordEncoder")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
public class DefaultPlatformPasswordEncoder implements PlatformPasswordEncoder {

    private final PasswordEncoder passwordEncoder;

    @Override
    public String encode(PlatformUser user) {
        return this.passwordEncoder.encode(user.getPassword());
    }
}
