/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 19 June 2023 at 11:27
 */
package org.apache.fineract.portfolio.paymentrules.data;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.generic.exceptions.GenericErrorResourceNotFound;
import org.apache.fineract.infrastructure.generic.helper.GenericConstants;

import java.util.ArrayList;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;
import org.apache.fineract.utility.data.ValidationException;
import java.lang.reflect.Type;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.reflect.TypeToken;

import java.util.List ;
import java.util.Map;

@Component
public class PaymentRuleDataValidator extends ValidationException {

    private FromJsonHelper fromApiJsonHelper ;

    @Autowired
    public PaymentRuleDataValidator(FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(String json){

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        //this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, this.supportedParameters);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();
        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors).resource("paymentRule");

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed(GenericConstants.nameParam, element);
        baseDataValidator.reset().parameter(GenericConstants.nameParam).value(name).notBlank().notExceedingLengthOf(100);


        final Long officeId = this.fromApiJsonHelper.extractLongNamed("officeId", element);
        baseDataValidator.reset().parameter("officeId").value(officeId).notNull().integerGreaterThanZero();


        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }
}
