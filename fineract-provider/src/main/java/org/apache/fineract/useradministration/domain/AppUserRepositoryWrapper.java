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
package org.apache.fineract.useradministration.domain;

import org.apache.fineract.useradministration.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AppUserRepositoryWrapper {

    private final AppUserRepository appUserRepository ;
    
    @Autowired
    public AppUserRepositoryWrapper(final AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository ;
    }
    
    public AppUser fetchSystemUser() {
        String userName = "system" ;
        AppUser user = this.appUserRepository.findAppUserByName(userName);
        if(user == null) {
            throw new UserNotFoundException(userName) ;
        }
        return user ;
    }

    public AppUser fetchByEmail(String emailAddress){
        AppUser appUser = this.appUserRepository.findAppUserByEmail(emailAddress);
        return appUser ;
    }
}
