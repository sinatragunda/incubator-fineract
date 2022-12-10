/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 December 2022 at 01:03
 */
package org.apache.fineract.portfolio.localref.data;

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
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.data.DataValidatorBuilder;
import org.apache.fineract.infrastructure.core.exception.InvalidJsonException;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.apache.fineract.portfolio.localref.enumerations.REF_VALUE_TYPE;
import org.apache.fineract.portfolio.localref.helper.LocalRefConstants;
import org.joda.time.MonthDay;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

@Component
public class LocalRefDataValidator {

    private final FromJsonHelper fromApiJsonHelper;

    @Autowired
    public LocalRefDataValidator(final FromJsonHelper fromApiJsonHelper) {
        this.fromApiJsonHelper = fromApiJsonHelper;
    }

    public void validateForCreate(final String json) {

        if (StringUtils.isBlank(json)) { throw new InvalidJsonException(); }

        final Type typeOfMap = new TypeToken<Map<String, Object>>() {}.getType();
        this.fromApiJsonHelper.checkForUnsupportedParameters(typeOfMap, json, LocalRefConstants.LOCAL_REF_REQUEST_DATA_PARAMETERS);

        final List<ApiParameterError> dataValidationErrors = new ArrayList<>();

        final DataValidatorBuilder baseDataValidator = new DataValidatorBuilder(dataValidationErrors)
                .resource(LocalRefConstants.LOCAL_REF_WRITE_PERMISSION);

        final JsonElement element = this.fromApiJsonHelper.parse(json);

        final String name = this.fromApiJsonHelper.extractStringNamed(LocalRefConstants.nameParam, element);
        baseDataValidator.reset().parameter(LocalRefConstants.nameParam).value(name).notBlank().notExceedingLengthOf(100);

        final String description = this.fromApiJsonHelper.extractStringNamed(LocalRefConstants.descriptionParam, element);
        baseDataValidator.reset().parameter(LocalRefConstants.descriptionParam).value(description).notBlank().notExceedingLengthOf(200);

        final Integer refTable = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LocalRefConstants.refTableParam, element);
        baseDataValidator.reset().parameter(LocalRefConstants.refTableParam).value(refTable).notNull().inMinMaxRange(0, REF_TABLE.values().length);

        final Integer refValueType = this.fromApiJsonHelper.extractIntegerSansLocaleNamed(LocalRefConstants.refValueTypeParam, element);
        baseDataValidator.reset().parameter(LocalRefConstants.refValueTypeParam).value(refValueType).notNull().inMinMaxRange(0, REF_VALUE_TYPE.values().length);

        throwExceptionIfValidationWarningsExist(dataValidationErrors);
    }

    private void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}