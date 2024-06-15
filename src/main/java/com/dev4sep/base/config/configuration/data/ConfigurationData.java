package com.dev4sep.base.config.configuration.data;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.time.LocalDate;

/**
 * @author YISivlay
 */
@Data
@NoArgsConstructor
@Accessors(chain = true)
public class ConfigurationData {

    @SuppressWarnings("unused")
    private String name;
    @SuppressWarnings("unused")
    private boolean enabled;
    @SuppressWarnings("unused")
    private Long value;
    @SuppressWarnings("unused")
    private LocalDate dateValue;
    private String stringValue;
    @SuppressWarnings("unused")
    private Long id;
    @SuppressWarnings("unused")
    private String description;
    @SuppressWarnings("unused")
    private boolean trapDoor;

}
