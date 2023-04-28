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
package org.apache.fineract.infrastructure.core.data;

/**
 * <p>
 * Immutable data object representing generic enumeration value.
 * </p>
 */
public class EnumOptionData {

    private Long id;
    private String code;
    private String value;

    public EnumOptionData(){}

    public EnumOptionData(final Long id, final String code, final String value) {
        this.id = id;
        this.code = code;
        this.value = value;
    }

    /**
     * Added 26/04/2023 at 0942 
     */ 
    
    public EnumOptionData(final Integer id, final String code) {
        this.id = new Long(id);
        this.code = code;
        this.value = code;
    }


    public EnumOptionData(final Long id, final String code) {
        this.id = new Long(id);
        this.code = code;
        this.value = code;
    }


    public Long getId() {
        return this.id;
    }

    public String getCode() {
        return this.code;
    }
    
    public String getValue() {
        return this.value;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void setValue(String value) {
        this.value = value;
    }
}