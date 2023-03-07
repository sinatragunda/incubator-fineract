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
package org.apache.fineract.infrastructure.dataqueries.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.utility.service.IEnum;

import java.util.*;

public enum EntityTables implements IEnum {

	CLIENT("m_client",
        new Integer[]{StatusEnum.CREATE.getCode(),
                StatusEnum.ACTIVATE.getCode(),
                StatusEnum.CLOSE.getCode()},
        "client_id" ,"Client"),
    LOAN("m_loan",
        new Integer[]{StatusEnum.CREATE.getCode(),
                StatusEnum.APPROVE.getCode(),
                StatusEnum.DISBURSE.getCode(),
                StatusEnum.WITHDRAWN.getCode(),
                StatusEnum.REJECTED.getCode(),
                StatusEnum.WRITE_OFF.getCode()},
        "loan_id" ,"Loan"),
    GROUP("m_group",
        new Integer[]{StatusEnum.CREATE.getCode(),
                StatusEnum.ACTIVATE.getCode(),
                StatusEnum.CLOSE.getCode(),},
        "group_id" ,"Group"),
    SAVING("m_savings_account",
        new Integer[]{StatusEnum.CREATE.getCode(),
                StatusEnum.APPROVE.getCode(),
                StatusEnum.ACTIVATE.getCode(),
                StatusEnum.WITHDRAWN.getCode(),
                StatusEnum.REJECTED.getCode(),
                StatusEnum.CLOSE.getCode()},
        "savings_account_id" ,"Account"),
	APPLICATION("m_application",
			new Integer[]{StatusEnum.CREATE.getCode(),
					StatusEnum.ACTIVATE.getCode()},
			"m_application_id" ,"Hybrid"),;

	private static final Map<String, EntityTables> lookup = new HashMap<String, EntityTables>();
	static {
		for (EntityTables d : EntityTables.values())
			lookup.put(d.getName(), d);
	}

	private String name;
	private String portfolioName;

	private Integer[] codes;

	private String foreignKeyColumnNameOnDatatable;

	private EntityTables(String name, Integer[] codes, String foreignKeyColumnNameOnDatatable ,String portfolioName) {
		this.name = name;
		this.codes = codes;
		this.foreignKeyColumnNameOnDatatable = foreignKeyColumnNameOnDatatable;
		this.portfolioName = portfolioName;
	}

	public static List<String> getEntitiesList() {

		List<String> data = new ArrayList<String>();

		for (EntityTables entity : EntityTables.values()){
			System.err.println("=================what are we pushing here ? "+entity.name);
			data.add(entity.name);
		}
		return data;

	}

	public static Integer[] getStatus(String name) {
		if (lookup.get(name) != null) {
			return lookup.get(name).getCodes();
		}
		return new Integer[]{};
	}

	public Integer[] getCodes() {
		return this.codes;
	}

	public String getName() {
		return name;
	}

	public String getForeignKeyColumnNameOnDatatable() {
		return this.foreignKeyColumnNameOnDatatable;
	}

	public static String getForeignKeyColumnNameOnDatatable(String name) {
		return lookup.get(name).foreignKeyColumnNameOnDatatable;
	}

	public static String portfolioName(String table){
		EntityTables entityTables = (EntityTables)EnumTemplateHelper.fromString(EntityTables.values() ,table);
		return Optional.ofNullable(entityTables.portfolioName).orElse("Undefined");
	}

	public static List<EnumOptionData> template(){
		List<EnumOptionData> enumOptionDataList = new ArrayList<>();
		EntityTables[] entityTables = EntityTables.values();
		for(EntityTables e : entityTables){
			System.err.println("-=================do we even get here son ? ");
			EnumOptionData enumOptionData = new EnumOptionData(Long.valueOf(e.ordinal()) ,e.name ,e.portfolioName);
			enumOptionDataList.add(enumOptionData);
		}
		return enumOptionDataList;
	}

	@Override
	public String getCode() {
		return getName();
	}


	public static boolean isApplicationTable(String applicationTableName){
		String entityTableName = EntityTables.APPLICATION.getName();
		boolean is = entityTableName.equalsIgnoreCase(applicationTableName);
		return is ;
	}
}
