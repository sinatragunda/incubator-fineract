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

import org.apache.fineract.infrastructure.core.service.RoutingDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

@Service
public class SelfServiceReadPlatformServiceImpl implements SelfServiceReadPlatformService {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public SelfServiceReadPlatformServiceImpl(final RoutingDataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public Long selfServiceUserLinkedClientId(Long userId) {

        System.err.println("-----------------------app user id is son ------------"+userId);

        String sql = "select client_id from m_selfservice_user_client_mapping where appuser_id = ?";
        Object[] params = new Object[] { userId };
        Long clientId = this.jdbcTemplate.queryForObject(sql, params, Long.class);
        return clientId;
    }

}
