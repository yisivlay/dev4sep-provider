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

import com.dev4sep.base.config.exception.AbstractPlatformDomainRuleException;

/**
 * @author YISivlay
 */
public class RootOfficeParentCannotBeUpdated extends AbstractPlatformDomainRuleException {
    public RootOfficeParentCannotBeUpdated() {
        super("error.msg.office.cannot.update.parent.office.of.root.office", "The root office must not be set with a parent office.");
    }
}
