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
package com.dev4sep.base.organisation.office.exception;

import com.dev4sep.base.config.domain.ExternalId;
import com.dev4sep.base.config.exception.AbstractPlatformResourceNotFoundException;

/**
 * @author YISivlay
 */
public class OfficeNotFoundException extends AbstractPlatformResourceNotFoundException {

    public OfficeNotFoundException(final Long id) {
        super("error.msg.office.id.invalid", "Office with identifier " + id + " does not exist", id);
    }

    public OfficeNotFoundException(ExternalId externalId) {
        super("error.msg.office.external.id.invalid", "Office with external identifier " + externalId + " does not exist", externalId);
    }
}
