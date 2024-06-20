package com.dev4sep.base.adminstration.user.domain;

import com.dev4sep.base.config.security.domain.PlatformUser;

/**
 * @author YISivlay
 */
public interface PlatformPasswordEncoder {

    String encode(PlatformUser user);

}
