/**
 *    Copyright 2024 DEV4Sep
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */
package com.dev4sep.base.organisation.office.domain;

import com.dev4sep.base.config.auditing.domain.AbstractPersistableCustom;
import com.dev4sep.base.config.domain.ExternalId;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author YISivlay
 */
@Getter
@Setter
@Entity
@Table(name = "tbl_office", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"name"}, name = "name"),
        @UniqueConstraint(columnNames = {"external_id"}, name = "external_id"),
})
public class Office extends AbstractPersistableCustom implements Serializable {

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private List<Office> children = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Office parent;

    @Column(name = "name", nullable = false, length = 100, unique = true)
    private String name;

    @Column(name = "hierarchy", length = 100)
    private String hierarchy;

    @Temporal(TemporalType.DATE)
    @Column(name = "opening_date", nullable = false)
    private Date openingDate;

    @Column(name = "external_id", length = 100, unique = true)
    private String externalId;

    protected Office() {
        this.openingDate = null;
        this.parent = null;
        this.name = null;
        this.externalId = null;
    }

    private Office(final Office parent,
                   final String name,
                   final Date openingDate,
                   final String externalId) {
        this.parent = parent;
        this.openingDate = openingDate;
        if (parent != null) {
            this.parent.addChild(this);
        }

        if (StringUtils.isNotBlank(name)) {
            this.name = name.trim();
        } else {
            this.name = null;
        }
        this.externalId = externalId;
    }

    public static Office headOffice(final String name,
                                    final Date openingDate,
                                    final String externalId) {
        return new Office(null, name, openingDate, externalId);
    }

    private void addChild(final Office office) {
        this.children.add(office);
    }

    public void generateHierarchy() {

        if (this.parent != null) {
            this.hierarchy = this.parent.hierarchyOf(getId());
        } else {
            this.hierarchy = ".";
        }
    }

    private String hierarchyOf(final Long id) {
        return this.hierarchy + id.toString() + ".";
    }
}
