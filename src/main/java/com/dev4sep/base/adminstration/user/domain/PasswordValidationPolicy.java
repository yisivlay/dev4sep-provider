package com.dev4sep.base.adminstration.user.domain;

import com.dev4sep.base.config.auditing.domain.AbstractPersistableCustom;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

/**
 * @author YISivlay
 */
@Getter
@Entity
@Table(name = "tbl_password_validation_policy")
public class PasswordValidationPolicy extends AbstractPersistableCustom {

    @Column(name = "regex", nullable = false)
    private String regex;

    @Column(name = "description", nullable = false)
    private String description;

    @Column(name = "is_active", nullable = false)
    private boolean isActive;

    @Column(name = "key")
    private String key;

    protected PasswordValidationPolicy() {
    }

    public boolean isActive() {
        return this.isActive;
    }

    public void deActivate() {
        this.isActive = false;
    }
}
