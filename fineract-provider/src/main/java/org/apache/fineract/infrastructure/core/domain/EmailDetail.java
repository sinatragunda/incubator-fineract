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

public class EmailDetail {

    private final String subject;
    private final String body;
    private final String address;
    private final String contactName;

    public EmailDetail(EmailDetail emailDetail){
        this.subject = emailDetail.getSubject();
        this.body = emailDetail.getBody();
        this.address = emailDetail.getAddress();
        this.contactName = emailDetail.getContactName();
    }

    public EmailDetail(final String subject, final String body, final String address, final String contactName) {
        this.subject = subject;
        this.body = body;
        this.address = address;
        this.contactName = contactName;
    }

    public String getSubject() {
        return subject;
    }

    public String getBody() {
        return body;
    }

    public String getContactName() {
        return this.contactName;
    }

    public String getAddress() {
        return this.address;
    }

    @Override
    public String toString() {
        return "EmailDetail{" +
                "subject='" + subject + '\'' +
                ", body='" + body + '\'' +
                ", address='" + address + '\'' +
                ", contactName='" + contactName + '\'' +
                '}';
    }
}