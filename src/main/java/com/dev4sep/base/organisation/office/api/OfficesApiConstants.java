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
package com.dev4sep.base.organisation.office.api;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @author YISivlay
 */
public class OfficesApiConstants {

    public static final String PERMISSIONS = "OFFICE";
    public static final String RESOURCE = "office";
    public static final String PATH = "/offices";

    public static final String id = "id";
    public static final String name = "name";
    public static final String nameDecorated = "nameDecorated";
    public static final String externalId = "externalId";
    public static final String openingDate = "openingDate";
    public static final String hierarchy = "hierarchy";
    public static final String parentId = "parentId";
    public static final String parentName = "parentName";
    public static final String allowedParents = "allowedParents";

    public static final String locale = "locale";
    public static final String dateFormat = "dateFormat";

    public static final Set<String> RESPONSE_PARAMETERS = new HashSet<>(Arrays.asList(
            id,
            name,
            nameDecorated,
            externalId,
            openingDate,
            hierarchy,
            parentId,
            parentName,
            allowedParents
    ));

    public static final Set<String> CREATE_UPDATE_PARAMETERS = new HashSet<>(Arrays.asList(
            name,
            externalId,
            openingDate,
            parentId,
            allowedParents,
            locale,
            dateFormat
    ));
}
