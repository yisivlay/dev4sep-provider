package com.dev4sep.base.adminstration.user.service;

import com.dev4sep.base.adminstration.user.domain.PlatformPasswordEncoder;
import com.dev4sep.base.adminstration.user.domain.User;
import com.dev4sep.base.adminstration.user.domain.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

/**
 * @author YISivlay
 */
@Service
public class JpaUserDomainService implements UserDomainService {

    private final UserRepository userRepository;
    private final PlatformPasswordEncoder platformPasswordEncoder;

    @Autowired
    public JpaUserDomainService(final UserRepository userRepository,
                                @Qualifier("applicationPasswordEncoder") final PlatformPasswordEncoder platformPasswordEncoder) {
        this.userRepository = userRepository;
        this.platformPasswordEncoder = platformPasswordEncoder;
    }

    @Override
    public void create(final User user) {
        this.userRepository.save(user);
        final String encodePassword = this.platformPasswordEncoder.encode(user);
        user.updatePassword(encodePassword);

        this.userRepository.saveAndFlush(user);
    }
}
