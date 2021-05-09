
package org.apache.fineract.wese.helper ;
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
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.*;
import org.apache.fineract.infrastructure.core.service.*;
import org.apache.fineract.infrastructure.core.domain.FineractPlatformTenant;

import org.apache.fineract.wese.utility.JdbcTemplateInit;

public class NewTenantHelper{


	public static void createNewTenant(FineractPlatformTenant fineractPlatformTenant, JdbcTemplate jdbcTemplate , String tenantIdentifier ,String timezone){

		String sql = String.format("CREATE DATABASE IF NOT EXISTS %s",tenantIdentifier);
		
		ThreadLocalContextUtil.setTenant(fineractPlatformTenant);

		jdbcTemplate.execute(sql);

		String update = String.format("INSERT INTO tenant_server_connections(schema_name) VALUES(%s)",tenantIdentifier);
		
		//jdbcTemplate = JdbcTemplateInit.getJdbcTemplate("mifosplatform-tenants");
		jdbcTemplate.execute(sql);

		String queryReportId = String.format("SELECT id from tenant_server_connections where schema_name = %s",tenantIdentifier);

		///here we should get the id value we have created 
		SqlRowSet sqlRowSet = jdbcTemplate.queryForRowSet(queryReportId);

		int tenantId = sqlRowSet.getInt(1);

		String createTenantLink = String.format("INSERT into tenants(identifier ,name,timezone_id,oltp_id ,report_id) VALUES(%%s,%s,%s,%i ,%i)",tenantIdentifier ,tenantIdentifier ,timezone ,tenantId ,tenantId);
		jdbcTemplate.execute(createTenantLink);

	
	}


}