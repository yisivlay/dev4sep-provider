package com.dev4sep.base.adminstration.user.domain;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

/**
 * @author YISivlay
 */
public interface UserPreviousPasswordRepository extends JpaRepository<UserPreviousPassword, Long>, JpaSpecificationExecutor<UserPreviousPassword> {
    List<UserPreviousPassword> findByUserId(Long userId, Pageable pageable);
}
