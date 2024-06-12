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
package com.dev4sep.base.organisation.office.data;

import com.dev4sep.base.config.domain.ExternalId;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.Collection;
import java.util.Date;

/**
 * @author YISivlay
 */
@Getter
@Builder
public class OfficeData implements Serializable {

    private final Long id;
    private final String name;
    private final String nameDecorated;
    private final String externalId;
    private final Date openingDate;
    private final String hierarchy;
    private final Long parentId;
    private final String parentName;
    private final Collection<OfficeData> allowedParents;

}
