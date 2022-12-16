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
package org.apache.fineract.portfolio.self.registration.service;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.portfolio.self.registration.domain.SelfServiceRegistration;
import org.apache.fineract.useradministration.domain.AppUser;

// Added 25/09/2021
import org.apache.fineract.portfolio.client.domain.Client ;

public interface SelfServiceRegistrationWritePlatformService {

    public SelfServiceRegistration createRegistrationRequest(String apiRequestBodyAsJson);

    public AppUser createUser(String apiRequestBodyAsJson);

    // Added 25/09/2021
    public SelfServiceRegistration createSelfServiceUserEx(Client client);

    /**
     * Added 16/12/2022 at 0007
     */
    public CommandProcessingResult massRegistration(Long officeId);
}
