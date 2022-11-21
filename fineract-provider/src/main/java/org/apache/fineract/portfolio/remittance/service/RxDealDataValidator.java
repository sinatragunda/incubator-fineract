/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 November 2022 at 10:25
 */
package org.apache.fineract.portfolio.remittance.service;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.remittance.constants.RxDealConstants;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.apache.fineract.adhocquery.domain.ReportRunFrequency;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import java.lang.reflect.Type;
import java.util.Optional;

@Component
public class RxDealDataValidator {

    private FromJsonHelper fromApiJsonHelper ;

    @Autowired
    public RxDealDataValidator(FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        //this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource(RxDealConstants.RESOURCE_NAME);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final Long officeId  = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.officeIdParamName, element);
        baseDataValidator.reset().parameter(ClientApiConstants.officeIdParamName).value(officeId).notNull().longGreaterThanNumber(0L);

        final Long payinAccountId  = this.fromApiJsonHelper.extractLongNamed(RxDealConstants.payinAccountParam, element);
        baseDataValidator.reset().parameter(RxDealConstants.payinAccountParam).value(payinAccountId).notNull().longGreaterThanNumber(0L);

        final String currencyCode = this.fromApiJsonHelper.extractStringNamed(RxDealConstants.currencyParam, element);
        baseDataValidator.reset().parameter(RxDealConstants.currencyParam).value(currencyCode).notBlank().notExceedingLengthOf(4);

        final Boolean isCreateNewClientTemp = this.fromApiJsonHelper.extractBooleanNamed(RxDealConstants.createNewClientParam ,element);

        System.err.println("---------------------value here is ? "+isCreateNewClientTemp);

        boolean isCreateNewClient = Optional.ofNullable(isCreateNewClientTemp).orElse(false);


        System.err.println("----------isCreateClient -----------------"+isCreateNewClient);

        final Long clientId  = this.fromApiJsonHelper.extractLongNamed(ClientApiConstants.clientIdParamName, element);
        if(!isCreateNewClient){
            baseDataValidator.reset().parameter(ClientApiConstants.clientIdParamName).value(clientId).notNull().longGreaterThanNumber(0L);
        }
        final String receiverName = this.fromApiJsonHelper.extractStringNamed(RxDealConstants.receiverNameParam, element);
        baseDataValidator.reset().parameter(RxDealConstants.receiverNameParam).value(receiverName).notBlank().notExceedingLengthOf(2000);

        final String receiverPhoneNumber = this.fromApiJsonHelper.extractStringNamed(RxDealConstants.receiverPhoneNumberParam, element);
        baseDataValidator.reset().parameter(RxDealConstants.receiverPhoneNumberParam).value(receiverPhoneNumber).notBlank().notExceedingLengthOf(20);

        final BigDecimal amount = this.fromApiJsonHelper.extractBigDecimalWithLocaleNamed(RxDealConstants.amountParam, element);
        baseDataValidator.reset().parameter(RxDealConstants.amountParam).value(amount).notNull().positiveAmount();


        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException("validation.msg.validation.errors.exist",
                "Validation errors exist.", dataValidationErrors); }
    }

}
