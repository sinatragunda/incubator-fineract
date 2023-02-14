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

import java.util.List;

/**
 * Immutable data object representing datatable data.
 */
public class DatatableData {

    @SuppressWarnings("unused")
    private final String applicationTableName;
    @SuppressWarnings("unused")
    private final String registeredTableName;
    @SuppressWarnings("unused")
    private final List<ResultsetColumnHeaderData> columnHeaderData;

    /**
     * Added 02/02/2023 at 0944
     */
    private final String portfolioName ;
    private final String prettyName;

    private List<EnumOptionData> applicationOptions ;
    private GenericResultsetData genericResultsetData;

    public static DatatableData template(){
        return new DatatableData();
    }


    public static DatatableData create(final String applicationTableName, final String registeredTableName,
            final List<ResultsetColumnHeaderData> columnHeaderData) {
        return new DatatableData(applicationTableName, registeredTableName, columnHeaderData);
    }


    public static DatatableData create(final String applicationTableName, final String registeredTableName,final String prettyName ,
            final List<ResultsetColumnHeaderData> columnHeaderData) {
        return new DatatableData(applicationTableName, registeredTableName,prettyName, columnHeaderData);
    }

    private DatatableData(final String applicationTableName, final String registeredTableName,
            final List<ResultsetColumnHeaderData> columnHeaderData) {
        this.applicationTableName = applicationTableName;
        this.registeredTableName = registeredTableName;
        this.columnHeaderData = columnHeaderData;
        this.portfolioName = EntityTables.portfolioName(applicationTableName);
        this.prettyName = null ;
    }


    private DatatableData(final String applicationTableName, final String registeredTableName,String prettyName ,
            final List<ResultsetColumnHeaderData> columnHeaderData) {
        this.applicationTableName = applicationTableName;
        this.registeredTableName = registeredTableName;
        this.columnHeaderData = columnHeaderData;
        this.prettyName = prettyName;
        this.portfolioName = EntityTables.portfolioName(applicationTableName);
    }



    private DatatableData() {
        this.applicationTableName = null;
        this.registeredTableName = null;
        this.columnHeaderData = null;
        this.portfolioName = null;
        this.prettyName = null ;
        this.applicationOptions = EntityTables.template();
    }

    public boolean hasColumn(final String columnName){
        for(ResultsetColumnHeaderData c : this.columnHeaderData){
            if(c.getColumnName().equals(columnName)) return true;
        }
        return false;
    }

    public void setGenericResultsetData(GenericResultsetData genericResultsetData) {
        this.genericResultsetData = genericResultsetData;
    }

    public String getRegisteredTableName(){
        return registeredTableName;
    }
    
}