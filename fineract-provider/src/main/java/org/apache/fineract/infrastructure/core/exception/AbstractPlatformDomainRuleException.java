/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.core.exception;

import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;

/**
 * A {@link RuntimeException} thrown when valid api request end up violating
 * some domain rule.
 */
public abstract class AbstractPlatformDomainRuleException extends RuntimeException {

    private final String globalisationMessageCode;
    private final String defaultUserMessage; // also used as override message 
    private final Object[] defaultUserMessageArgs;
    private final Long timestamp ;
    private final Boolean repeatableTransaction;
    private final String overrideMessage ;

    public AbstractPlatformDomainRuleException(final String globalisationMessageCode, final String defaultUserMessage,
            final Object... defaultUserMessageArgs) {
        this.globalisationMessageCode = globalisationMessageCode;
        this.defaultUserMessage = defaultUserMessage;
        this.defaultUserMessageArgs = defaultUserMessageArgs;
        this.timestamp = ThreadLocalContextUtil.getRequestState().get().getTimestamp();
        this.repeatableTransaction = false ;
        this.overrideMessage= null ;
    }
    public AbstractPlatformDomainRuleException(final String globalisationMessageCode, final String defaultUserMessage,final String overrideMessage, final Boolean repeatableTransaction ,
                                               final Object... defaultUserMessageArgs) {
        this.globalisationMessageCode = globalisationMessageCode;
        this.defaultUserMessage = defaultUserMessage;
        this.defaultUserMessageArgs = defaultUserMessageArgs;
        this.timestamp = ThreadLocalContextUtil.getRequestState().get().getTimestamp();
        this.repeatableTransaction = repeatableTransaction;
        this.overrideMessage = overrideMessage;
    }

    public String getOverrideMessage() {
        return overrideMessage;
    }

    public Boolean isRepeatableTransaction() {
        return this.repeatableTransaction;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }

    public String getGlobalisationMessageCode() {
        return this.globalisationMessageCode;
    }

    public String getDefaultUserMessage() {
        return this.defaultUserMessage;
    }

    public Object[] getDefaultUserMessageArgs() {
        return this.defaultUserMessageArgs;
    }
}