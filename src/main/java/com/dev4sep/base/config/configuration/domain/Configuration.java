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
package com.dev4sep.base.config.configuration.domain;

import com.dev4sep.base.config.auditing.domain.AbstractPersistableCustom;
import com.dev4sep.base.config.configuration.data.ConfigurationData;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author YISivlay
 */
@Getter
@Setter
@Entity
@Accessors(chain = true)
@Table(name = "tbl_configuration")
public class Configuration extends AbstractPersistableCustom {

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "enabled", nullable = false)
    private boolean enabled;

    @Column(name = "value")
    private Long value;

    @Column(name = "date_value")
    private LocalDate dateValue;

    @Column(name = "string_value")
    private String stringValue;

    @Column(name = "description")
    private String description;

    @Column(name = "is_trap_door", nullable = false)
    private boolean isTrapDoor;

    protected Configuration() {
    }

    public ConfigurationData toData() {
        return new ConfigurationData()
                .setId(this.getId())
                .setName(getName())
                .setEnabled(isEnabled())
                .setValue(getValue())
                .setDateValue(getDateValue())
                .setStringValue(getStringValue())
                .setDescription(this.description)
                .setTrapDoor(this.isTrapDoor);
    }
}
