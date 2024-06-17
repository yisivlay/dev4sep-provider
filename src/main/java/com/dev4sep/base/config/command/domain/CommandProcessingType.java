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
package com.dev4sep.base.config.command.domain;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @author YISivlay
 */
@Getter
@RequiredArgsConstructor
public enum CommandProcessingType {

    INVALID(0, "commandProcessingResultType.invalid"),
    PROCESSED(1, "commandProcessingResultType.processed"),
    AWAITING_APPROVAL(2, "commandProcessingResultType.awaiting.approval"),
    REJECTED(3, "commandProcessingResultType.rejected"),
    UNDER_PROCESSING(4, "commandProcessingResultType.underProcessing"),
    ERROR(5, "commandProcessingResultType.error");

    private static final Map<Integer, CommandProcessingType> BY_ID = Arrays.stream(values()).collect(Collectors.toMap(CommandProcessingType::getValue, v -> v));

    private final Integer value;
    private final String code;

    public static CommandProcessingType fromInt(final Integer value) {
        CommandProcessingType transactionType = BY_ID.get(value);
        return transactionType == null ? INVALID : transactionType;
    }
}
