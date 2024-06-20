package com.dev4sep.base.adminstration.user.domain;

import com.dev4sep.base.config.auditing.domain.AbstractPersistableCustom;
import com.dev4sep.base.config.utils.DateUtils;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;

import java.time.LocalDate;

/**
 * @author YISivlay
 */
@Getter
@Entity
@Table(name = "tbl_user_previous_password")
public class UserPreviousPassword extends AbstractPersistableCustom {

    @Column(name = "user_id", nullable = false)
    private Long userId;

    @Column(name = "removal_date", nullable = false)
    private LocalDate removalDate;

    @Column(name = "password", nullable = false)
    private String password;

    protected UserPreviousPassword() {
    }

    public UserPreviousPassword(final User user) {
        this.userId = user.getId();
        this.password = user.getPassword().trim();
        this.removalDate = DateUtils.getLocalDateOfTenant();
    }
}
