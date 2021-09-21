/*

    Created by Sinatra Gunda
    At 9:46 AM on 9/19/2021

*/
package org.apache.fineract.wese.portfolio.depreciation.service;


import com.sun.org.slf4j.internal.Logger;
import com.sun.org.slf4j.internal.LoggerFactory;
import org.apache.fineract.accounting.producttoaccountmapping.service.ProductToGLAccountMappingWritePlatformService;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformDataIntegrityException;
import org.apache.fineract.infrastructure.entityaccess.domain.FineractEntityAccessType;
import org.apache.fineract.infrastructure.entityaccess.service.FineractEntityAccessUtil;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.spm.repository.DepreciationProductRepository;
import org.apache.fineract.wese.portfolio.depreciation.exceptions.DepreciationProductNotFoundException;
import org.apache.fineract.wese.portfolio.depreciation.domain.DepreciationProduct;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_ENTITY;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.apache.fineract.portfolio.common.service.BusinessEventNotifierService;
import org.apache.fineract.wese.portfolio.depreciation.serialization.DepreciationProductDataValidator;

import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class DepreciationProductWritePlatformServiceImpl implements DepreciationProductWritePlatformService{


    private DepreciationProductRepository depreciationProductRepository ;
    private final static Logger logger = LoggerFactory.getLogger(DepreciationProductWritePlatformServiceImpl.class);
    private final PlatformSecurityContext context;
    private final DepreciationProductDataValidator fromApiJsonDeserializer;
    private final ProductToGLAccountMappingWritePlatformService accountMappingWritePlatformService;
    private final FineractEntityAccessUtil fineractEntityAccessUtil;
    private final BusinessEventNotifierService businessEventNotifierService;

    @Autowired
    public DepreciationProductWritePlatformServiceImpl(final DepreciationProductRepository depreciationProductRepository , final PlatformSecurityContext context,
                                                            final ProductToGLAccountMappingWritePlatformService accountMappingWritePlatformService,
                                                            final FineractEntityAccessUtil fineractEntityAccessUtil,
                                                            final BusinessEventNotifierService businessEventNotifierService ,
                                                            final DepreciationProductDataValidator fromApiJsonDeserializer) {
        this.context = context;
        this.fromApiJsonDeserializer = fromApiJsonDeserializer;
        this.accountMappingWritePlatformService = accountMappingWritePlatformService;
        this.fineractEntityAccessUtil = fineractEntityAccessUtil;
        this.businessEventNotifierService = businessEventNotifierService;
    }



    @Transactional
    @Override
    public CommandProcessingResult createDepreciationProduct(final JsonCommand command) {

        try {

            this.context.authenticatedUser();

            this.fromApiJsonDeserializer.validateForCreate(command.json());
            //validateInputDates(command);

            final DepreciationProduct depreciationProduct = DepreciationProduct.assembleFromJson(command);
            this.depreciationProductRepository.save(depreciationProduct);

            // save accounting mappings
            this.accountMappingWritePlatformService.createDepreciationProductToGLAccountMapping(depreciationProduct.getId(), command);
            // check if the office specific products are enabled. If yes, then save this savings product against a specific office
            // i.e. this savings product is specific for this office.
            fineractEntityAccessUtil.checkConfigurationAndAddProductResrictionsForUserOffice(
                    FineractEntityAccessType.OFFICE_ACCESS_TO_LOAN_PRODUCTS,
                    depreciationProduct.getId());

            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.DEPRECIATION_PRODUCT_CREATE,
                    constructEntityMap(BUSINESS_ENTITY.DEPRECIATION_PRODUCT, depreciationProduct));

            this.businessEventNotifierService.notifyBusinessEventWasExecuted(BUSINESS_EVENTS.DEPRECIATION_PRODUCT_CREATE,
                    constructEntityMap(BUSINESS_ENTITY.DEPRECIATION_PRODUCT, depreciationProduct));

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(depreciationProduct.getId()) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return CommandProcessingResult.empty();
        }catch(final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause()) ;
            handleDataIntegrityIssues(command, throwable, dve);
            return CommandProcessingResult.empty();
        }

    }

    @Transactional
    @Override
    public CommandProcessingResult updateDepreciationProduct(final Long depreciationProductId, final JsonCommand command) {

        try {
            this.context.authenticatedUser();

            final DepreciationProduct product = this.depreciationProductRepository.findOne(depreciationProductId);
            if (product == null) { throw new DepreciationProductNotFoundException(depreciationProductId); }

            this.fromApiJsonDeserializer.validateForUpdate(command.json(), product);
            //validateInputDates(command);

            final Map<String, Object> changes = product.update(command);

            // accounting related changes
            final boolean accountingTypeChanged = changes.containsKey("accountingRule");
            final Map<String, Object> accountingMappingChanges = this.accountMappingWritePlatformService
                    .updateLoanProductToGLAccountMapping(product.getId(), command, accountingTypeChanged, product.getAccountingType());
            changes.putAll(accountingMappingChanges);

            if (!changes.isEmpty()) {
                this.depreciationProductRepository.saveAndFlush(product);
            }

            return new CommandProcessingResultBuilder() //
                    .withCommandId(command.commandId()) //
                    .withEntityId(depreciationProductId) //
                    .with(changes) //
                    .build();

        } catch (final DataIntegrityViolationException dve) {
            handleDataIntegrityIssues(command, dve.getMostSpecificCause(), dve);
            return new CommandProcessingResult(Long.valueOf(-1));
        }catch(final PersistenceException dve) {
            Throwable throwable = ExceptionUtils.getRootCause(dve.getCause()) ;
            handleDataIntegrityIssues(command, throwable, dve);
            return CommandProcessingResult.empty();
        }
    }

    private void handleDataIntegrityIssues(final JsonCommand command, final Throwable realCause, final Exception dve) {

        if (realCause.getMessage().contains("'external_id'")) {

            final String externalId = command.stringValueOfParameterNamed("externalId");
            throw new PlatformDataIntegrityException("Depreciation Product with externalId exists", "Depreciation Product with externalId `"
                    + externalId + "` already exists", "externalId", externalId);
        } else if (realCause.getMessage().contains("'unq_name'")) {

            final String name = command.stringValueOfParameterNamed("name");
            throw new PlatformDataIntegrityException("Depreciation product with name exists", "Depreciation product with name `" + name
                    + "` already exists", "name", name);
        } else if (realCause.getMessage().contains("'unq_short_name'")) {

            final String shortName = command.stringValueOfParameterNamed("shortName");
            throw new PlatformDataIntegrityException("Depreciation product with short name exists", "Depreciation product with short name `"
                    + shortName + "` already exists", "shortName", shortName);
        }
        logAsErrorUnexpectedDataIntegrityException(dve);
        throw new PlatformDataIntegrityException("Unknown data integrity issue with resource",
                "Unknown data integrity issue with resource.");
    }

    private void logAsErrorUnexpectedDataIntegrityException(final Exception dve) {
        logger.error(dve.getMessage(), dve);
    }


    private Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> constructEntityMap(final BusinessEventNotificationConstants.BUSINESS_ENTITY entityEvent, Object entity) {
        Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }

}
