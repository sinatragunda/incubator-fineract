/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 04 November 2022 at 04:53
 */
package org.apache.fineract.portfolio.remittance.data;

import com.google.gson.JsonArray;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.bulkimport.importhandler.helper.SanitizeExcelValues;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.portfolio.client.data.ClientApiCollectionConstants;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.domain.Client;
import org.joda.time.LocalDate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.remittance.constants.RxDealConstants;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Component
public class RxDealValidator {
    private FromJsonHelper fromJsonHelper;

    @Autowired
    public RxDealValidator(FromJsonHelper fromJsonHelper) {
        this.fromJsonHelper = fromJsonHelper;
    }

    public void validateForCreate(String jsonDeal){

        if (StringUtils.isBlank(jsonDeal)) {
            throw new InvalidJsonException();
        }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromJsonHelper.checkForUnsupportedParameters(typeOfMap, jsonDeal, RxDealConstants.RX_DEAL_PARAMETERS);
        final JsonElement element = this.fromJsonHelper.parse(jsonDeal);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(RxDealConstants.RESOURCE_NAME);

        boolean hasClient = false ;
        if (this.fromJsonHelper.parameterExists(ClientApiConstants.clientIdParamName, element)){
            hasClient = true ;
            final Long clientId = this.fromJsonHelper.extractLongNamed(ClientApiConstants.clientIdParamName, element);
            baseDataValidator.reset().parameter(ClientApiConstants.groupIdParamName).value(clientId).notNull().integerGreaterThanZero();
        }

        if (this.fromJsonHelper.parameterExists(RxDealConstants.createNewClientParam, element)) {
            final boolean isCreateNewClient = this.fromJsonHelper.extractBooleanNamed(RxDealConstants.createNewClientParam,element);
            baseDataValidator.validateForBooleanValue().mustBeFalseParameterProvidedIs(ClientApiConstants.clientIdParamName ,hasClient);
        }



        //final Integer providerInt = this.fromJsonHelper.extractIntegerNamed();

    }
}
