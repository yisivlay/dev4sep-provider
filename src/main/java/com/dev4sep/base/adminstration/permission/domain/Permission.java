package com.dev4sep.base.adminstration.permission.domain;

import com.dev4sep.base.config.auditing.domain.AbstractAuditableCustom;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.io.Serializable;

/**
 * @author YISivlay
 */
@Entity
@Table(name = "tbl_permission", uniqueConstraints = {@UniqueConstraint(columnNames = {"code"}, name = "code")})
public class Permission extends AbstractAuditableCustom implements Serializable {

    @Column(name = "grouping", nullable = false, length = 45)
    private String grouping;

    @Column(name = "code", nullable = false, unique = true, length = 100)
    private String code;

    @Column(name = "entity_name", length = 100)
    private String entityName;

    @Column(name = "action_name", length = 100)
    private String actionName;

    @Column(name = "can_maker_checker", nullable = false)
    private boolean canMakerChecker;

    protected Permission() {
    }
}
