package com.dev4sep.base.config.configuration.domain;

import com.dev4sep.base.config.auditing.domain.AbstractPersistableCustom;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

/**
 * @author YISivlay
 */
@Entity
@Table(name = "tbl_configuration")
public class Configuration extends AbstractPersistableCustom {
}
