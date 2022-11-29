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
package org.apache.fineract.infrastructure.core.domain;

import org.apache.fineract.infrastructure.userinstancing.utility.UserSessionInstance;

import java.util.HashMap;
import java.util.Map;

public class FineractPlatformTenant {

    private final Long id;
    private final String tenantIdentifier;
    private final String name;
    private final String timezoneId;
    private final FineractPlatformTenantConnection connection;

    /**
     * Added 28/11/2022 at 0237
     */
    private static final Map<String , Map<Long ,UserSessionInstance>> userSessionInstanceMap = new HashMap<>();

    public FineractPlatformTenant(final Long id, final String tenantIdentifier, final String name,
            final String timezoneId, final FineractPlatformTenantConnection connection) {
        this.id = id;
        this.tenantIdentifier = tenantIdentifier;
        this.name = name;
        this.timezoneId = timezoneId;
        this.connection = connection;
        setTenantInstance(tenantIdentifier);

    }

    public Map<String ,Map<Long ,UserSessionInstance>> getUserSessionInstanceMap() {
        return userSessionInstanceMap;
    }

    public Long getId() {
        return this.id;
    }

    public String getTenantIdentifier() {
        return this.tenantIdentifier;
    }

    public String getName() {
        return this.name;
    }

    public String getTimezoneId() {
        return this.timezoneId;
    }

    public FineractPlatformTenantConnection getConnection() {
        return connection;
    }

    public void setTenantInstance(String tenant){
        boolean containsSession  = userSessionInstanceMap.containsKey(tenant);
        if(!containsSession){
            //System.err.println("--------setting instance for client "+tenant);
            userSessionInstanceMap.put(tenant ,new HashMap<>());
            //System.err.println("----------------tenant instance is "+userSessionInstanceMap.size());
        }

    }

}