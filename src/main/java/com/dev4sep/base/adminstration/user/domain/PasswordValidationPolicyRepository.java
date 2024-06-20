package com.dev4sep.base.adminstration.user.domain;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

/**
 * @author YISivlay
 */
public interface PasswordValidationPolicyRepository extends JpaRepository<PasswordValidationPolicy, Long>, JpaSpecificationExecutor<PasswordValidationPolicy> {

    @Query(value = "SELECT * FROM tbl_password_validation_policy pwd WHERE pwd.is_active = true", nativeQuery = true)
    PasswordValidationPolicy findActivePasswordValidationPolicy();

}
