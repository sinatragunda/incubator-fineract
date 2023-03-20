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
package org.apache.fineract.infrastructure.dataqueries.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.Iterator;

import javax.persistence.PersistenceException;

import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.fineract.infrastructure.configuration.domain.ConfigurationDomainService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.dataqueries.data.DatatableData;
import org.apache.fineract.infrastructure.dataqueries.data.EntityTables;
import org.apache.fineract.infrastructure.dataqueries.domain.*;
import org.apache.fineract.infrastructure.dataqueries.exception.*;
import org.apache.fineract.infrastructure.dataqueries.repo.XRegisteredTableRepositoryWrapper;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.loanproduct.service.LoanProductReadPlatformService;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.savings.service.SavingsProductReadPlatformService;
import org.apache.fineract.useradministration.domain.AppUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

@Service
public class EntityDatatableChecksWritePlatformServiceImpl implements EntityDatatableChecksWritePlatformService {

    private final static Logger logger = LoggerFactory.getLogger(EntityDatatableChecksWritePlatformServiceImpl.class);

    private final PlatformSecurityContext context;
    private final EntityDatatableChecksDataValidator fromApiJsonDeserializer;
    private final EntityDatatableChecksRepository entityDatatableChecksRepository;
    private final ReadWriteNonCoreDataService readWriteNonCoreDataService;
    private final LoanProductReadPlatformService loanProductReadPlatformService;
    private final SavingsProductReadPlatformService savingsProductReadPlatformService;
    private final FromJsonHelper fromApiJsonHelper;
    private final ConfigurationDomainService configurationDomainService;
    private final XRegisteredTableRepositoryWrapper xRegisteredTableRepositoryWrapper;
    private final ApplicationRecordRepositoryWrapper applicationRecordRepositoryWrapper;
    private final HybridTableEntityRepositoryWrapper hybridTableEntityRepositoryWrapper;

    @Autowired
    public EntityDatatableChecksWritePlatformServiceImpl(final PlatformSecurityContext context,
            final EntityDatatableChecksDataValidator fromApiJsonDeserializer,
            final EntityDatatableChecksRepository entityDatatableChecksRepository,
            final ReadWriteNonCoreDataService readWriteNonCoreDataService,
            final LoanProductReadPlatformService loanProductReadPlatformService,
            final SavingsProductReadPlatformService savingsProductReadPlatformService, final FromJsonHelper fromApiJsonHelper,
            final ConfigurationDomainService configurationDomainService ,final XRegisteredTableRepositoryWrapper xRegisteredTableRepositoryWrapper ,final ApplicationRecordRepositoryWrapper applicationRecordRepositoryWrapper ,final  HybridTableEntityRepositoryWrapper hybridTableEntityRepositoryWrapper) {
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.entityDatatableChecksRepository = entityDatatableChecksRepository;
        this.readWriteNonCoreDataService = readWriteNonCoreDataService;
        this.loanProductReadPlatformService = loanProductReadPlatformService;
        this.savingsProductReadPlatformService = savingsProductReadPlatformService;
        this.fromApiJsonHelper = fromApiJsonHelper;
        this.configurationDomainService = configurationDomainService;
        this.xRegisteredTableRepositoryWrapper = xRegisteredTableRepositoryWrapper ;
        this.hybridTableEntityRepositoryWrapper = hybridTableEntityRepositoryWrapper;
        this.applicationRecordRepositoryWrapper = applicationRecordRepositoryWrapper;
    }

    @Transactional
    @Override
    public CommandProcessingResult createCheck(final JsonCommand command) {

        try {

            System.err.println("===============create check son ");

            this.context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForCreate(command.json());

            // check if the datatable is linked to the entity

            String datatableName = command.stringValueOfParameterNamed("datatableName");

            DatatableData datatableData = this.readWriteNonCoreDataService.retrieveDatatable(datatableName);

            if (datatableData == null) { throw new DatatableNotFoundException(datatableName); }

            final String entity = command.stringValueOfParameterNamed("entity");
            final String foreignKeyColumnName = EntityTables.getForeignKeyColumnNameOnDatatable(entity);
            final boolean columnExist = datatableData.hasColumn(foreignKeyColumnName);

            String applicationTableName = datatableData.getApplicationTableName();


            System.err.println("===================entity is "+entity+"===========app table is "+applicationTableName);

            logger.info(datatableData.getRegisteredTableName() + "has column " + foreignKeyColumnName + " ? " + columnExist);

            System.err.println("===============we should edit this shit now ,force create either ways only when entity is already application ");

            //if (!columnExist) { throw new EntityDatatableCheckNotSupportedException(datatableData.getRegisteredTableName(), entity); }

            final Long productId = command.longValueOfParameterNamed("productId");
            final Long status = command.longValueOfParameterNamed("status");

            List<EntityDatatableChecks> entityDatatableCheck = null;
            if (productId == null) {
                entityDatatableCheck = this.entityDatatableChecksRepository.findByEntityStatusAndDatatableIdAndNoProduct(entity, status,
                        datatableName);
                if (!entityDatatableCheck.isEmpty()) { throw new EntityDatatableCheckAlreadyExistsException(entity, status, datatableName); }
            } else {
                if (entity.equals("m_loan")) {
                    // if invalid loan product id, throws exception
                    this.loanProductReadPlatformService.retrieveLoanProduct(productId);
                } else if (entity.equals("m_savings_account")) {
                    // if invalid savings product id, throws exception
                    this.savingsProductReadPlatformService.retrieveOne(productId);
                } else {
                    throw new EntityDatatableCheckNotSupportedException(entity, productId);
                }
                entityDatatableCheck = this.entityDatatableChecksRepository.findByEntityStatusAndDatatableIdAndProductId(entity, status,
                        datatableName, productId);
                if (!entityDatatableCheck.isEmpty()) { throw new EntityDatatableCheckAlreadyExistsException(entity, status, datatableName,
                        productId); }
            }

            final EntityDatatableChecks check = EntityDatatableChecks.fromJson(command);

            this.entityDatatableChecksRepository.saveAndFlush(check);

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(check.getId()) //
                    .build();
        } catch (final DataAccessException e) {
            handleReportDataIntegrityIssues(command, e.getMostSpecificCause(), e);
            return CommandProcessingResult.empty();
        } catch (final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause());
            handleReportDataIntegrityIssues(command, throwable, dve);
            return CommandProcessingResult.empty();
        }
    }

    @Override
    public void runTheCheck(final Long entityId, final String entityName, final Long statusCode, String foreignKeyColumn) {
        final List<EntityDatatableChecks> tableRequiredBeforeClientActivation = entityDatatableChecksRepository.findByEntityAndStatus(
                entityName, statusCode);

        if (tableRequiredBeforeClientActivation != null) {
            List<String> reqDatatables = new ArrayList<>();
            for (EntityDatatableChecks t : tableRequiredBeforeClientActivation) {

                final String datatableName = t.getDatatableName();
                final Long countEntries = readWriteNonCoreDataService.countDatatableEntries(datatableName, entityId, foreignKeyColumn);

                logger.info("The are " + countEntries + " entries in the table " + datatableName);
                if (countEntries.intValue() == 0) {
                    reqDatatables.add(datatableName);
                }
            }
            if (reqDatatables.size() > 0) { throw new DatatableEntryRequiredException(reqDatatables.toString()); }
        }

    }

    @Override
    public void runTheCheckForProduct(final Long entityId, final String entityName, final Long statusCode, String foreignKeyColumn,
            long productId) {
        List<EntityDatatableChecks> tableRequiredBeforAction = entityDatatableChecksRepository.findByEntityStatusAndProduct(entityName,
                statusCode, productId);

        if (tableRequiredBeforAction == null || tableRequiredBeforAction.size() < 1) {
            tableRequiredBeforAction = entityDatatableChecksRepository.findByEntityStatusAndNoProduct(entityName, statusCode);
        }
        if (tableRequiredBeforAction != null) {
            List<String> reqDatatables = new ArrayList<>();

            for (EntityDatatableChecks t : tableRequiredBeforAction) {

                final String datatableName = t.getDatatableName();

                Boolean isApplicationTable =  isApplicationTable(datatableName);

                System.err.println("===================is Application Table ? "+isApplicationTable+"=========if not skip");
                if(isApplicationTable){
                    continue;
                }

                final Long countEntries = readWriteNonCoreDataService.countDatatableEntries(datatableName, entityId, foreignKeyColumn);

                logger.info("The are " + countEntries + " entries in the table " + datatableName);
                if (countEntries.intValue() == 0) {
                    reqDatatables.add(datatableName);
                }
            }
            if (reqDatatables.size() > 0) { throw new DatatableEntryRequiredException(reqDatatables.toString()); }
        }

    }

    /**
     * Added 08/03/2023 at 0801 
     * Create for all hybrid entries .Hybrid is a mix of loan and custom tables 
     */ 
    @Transactional
    @Override
    public boolean saveHybridDataTables(final REF_TABLE refTable,final Long entityId, final AbstractPersistableCustom abstractPersistibleCustom
            ,final JsonElement jsonElement){
        
        final AppUser user = this.context.authenticatedUser();
        boolean isMakerCheckerEnabled = false;

        JsonObject jsonObject = jsonElement.getAsJsonObject();
        Set<String> keys = jsonObject.keySet();

        System.err.println("------------------keyset size is "+keys.size());

        for(String key : keys){

            System.err.println("============================key value is "+key);

            JsonArray jsonArray = jsonObject.getAsJsonArray(key);
            Iterator<JsonElement> it = jsonArray.iterator();
            
            while(it.hasNext()){
                JsonElement element = it.next();

                System.err.println("================jsonelement here is "+element);

                Long applicationId = this.fromApiJsonHelper.extractLongNamed("applicationId" ,element);

                System.err.println("===========================application id to insert here is "+applicationId);

                ApplicationRecord applicationRecord = applicationRecordRepositoryWrapper.findOneWithNotFoundDetection(applicationId);
                HybridTableEntity hybridTableEntity = new HybridTableEntity(refTable ,applicationRecord ,abstractPersistibleCustom);
                hybridTableEntityRepositoryWrapper.save(hybridTableEntity);
            }

        }
        return isMakerCheckerEnabled;
    }

    @Transactional
    @Override
    public boolean saveDatatables(final Long status, final String entity, final Long entityId, final Long productId,
            final JsonArray datatableDatas) {

        final AppUser user = this.context.authenticatedUser();
        boolean isMakerCheckerEnabled = false;
        
        if (datatableDatas != null && datatableDatas.size() > 0) {
            
            for (JsonElement element : datatableDatas) {
                final String datatableName = this.fromApiJsonHelper.extractStringNamed("registeredTableName", element);
                final JsonObject datatableData = this.fromApiJsonHelper.extractJsonObjectNamed("data", element);

                System.err.println("===================datatableName from array value is "+datatableName);

                boolean hasXRegisterTable = xRegisteredTableRepositoryWrapper.hasTable(datatableName);

                System.err.println("====================table with numbers should not come back here ,our table found "+hasXRegisterTable);
                
                if(!hasXRegisterTable){
                    System.err.println("------------------------continue");
                    continue;
                }

                boolean isApplicationTable = isApplicationTable(datatableName);

                if (datatableName == null || datatableData == null) {
                    final ApiParameterError error = ApiParameterError.generalError(
                            "registeredTableName.and.data.parameters.must.be.present.in.each.list.items.in.datatables",
                            "registeredTableName and data parameters must be present in each list items in datatables");
                    List<ApiParameterError> errors = new ArrayList<>();
                    errors.add(error);
                    throw new PlatformApiDataValidationException(errors);
                }
                
                try {
                    /**
                     * For tables of non hybrid type that is table who application table is m_application
                     */
                    if(!isApplicationTable){

                        final String taskPermissionName = "CREATE_" + datatableName;
                        user.validateHasPermissionTo(taskPermissionName);

                        if (this.configurationDomainService.isMakerCheckerEnabledForTask(taskPermissionName)) {
                            isMakerCheckerEnabled = true;
                        }

                        System.err.println("==============================create table after finding out its application table  ,entity id being "+entityId+"======= and main record being "+datatableData.toString());
                        System.err.println("===================create dataentry from loan function with data "+datatableData.toString());
                        this.readWriteNonCoreDataService.createNewDatatableEntry(datatableName, entityId, datatableData.toString());
                    }
                }
                catch (PlatformApiDataValidationException e) {
                    List<ApiParameterError> errors = e.getErrors();
                    for (ApiParameterError error : e.getErrors()) {
                        error.setParameterName("datatables." + datatableName + "." + error.getParameterName());
                    }
                    throw e;
                }
            }
        }
        return isMakerCheckerEnabled;
    }

    /**
     * Added 08/03/2023 at 0856
     * Use to filter if table is an application application table so as to skip
     */  
    private Boolean isApplicationTable(String datatableName){
        System.err.println("---------------------------does it run into any problems now ? ,throws error  ");
        XRegisteredTable xRegisteredTable = xRegisteredTableRepositoryWrapper.findByRegisteredTableNameWithNotFoundDetection(datatableName);
        System.err.println("-----------------------error caught here ");
        String applicationName = xRegisteredTable.getApplicationTableName();
        return applicationName.equalsIgnoreCase("m_application");
    }

    private List<String> getDatatableNames(Long status, String entity, Long productId) {
        List<String> ret = new ArrayList<>();
        List<EntityDatatableChecks> tableRequiredBeforeAction = null;
        if (productId != null) {
            tableRequiredBeforeAction = this.entityDatatableChecksRepository.findByEntityStatusAndProduct(entity, status, productId);
        }

        if (tableRequiredBeforeAction == null || tableRequiredBeforeAction.size() < 1) {
            tableRequiredBeforeAction = this.entityDatatableChecksRepository.findByEntityStatusAndNoProduct(entity, status);
        }
        if (tableRequiredBeforeAction != null && tableRequiredBeforeAction.size() > 0) {
            for (EntityDatatableChecks t : tableRequiredBeforeAction) {
                ret.add(t.getDatatableName());
            }
        }
        return ret;
    }

    @Transactional
    @Override
    public CommandProcessingResult deleteCheck(final Long entityDatatableCheckId) {

        final EntityDatatableChecks check = this.entityDatatableChecksRepository.findOne(entityDatatableCheckId);
        if (check == null) { throw new EntityDatatableChecksNotFoundException(entityDatatableCheckId); }

        this.entityDatatableChecksRepository.delete(check);

        return new CommandProcessingResultBuilder() //
                .withEntityId(entityDatatableCheckId) //
                .build();
    }

    /*
     * Guaranteed to throw an exception no matter what the data integrity issue
     * is.
     */
    private void handleReportDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dae) {

        if (realCause.getMessage().contains("FOREIGN KEY (`x_registered_table_name`)")) {
            final String datatableName = command.stringValueOfParameterNamed("datatableName");
            throw new PlatformDataIntegrityException("error.msg.entityDatatableCheck.foreign.key.constraint", "datatable with name '"
                    + datatableName + "' do not exist", "datatableName", datatableName);
        }

        if (realCause.getMessage().contains("unique_entity_check")) {
            final String datatableName = command.stringValueOfParameterNamed("datatableName");
            final long status = command.longValueOfParameterNamed("status");
            final String entity = command.stringValueOfParameterNamed("entity");
            final long productId = command.longValueOfParameterNamed("productId");
            throw new EntityDatatableCheckAlreadyExistsException(entity, status, datatableName, productId);
        }

        logger.error(dae.getMessage(), dae);
        throw new PlatformDataIntegrityException("error.msg.report.unknown.data.integrity.issue",
                "Unknown data integrity issue with resource: " + realCause.getMessage());
    }

}