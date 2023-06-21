/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 19 June 2023 at 11:32
 */
package org.apache.fineract.utility.data;

import org.apache.fineract.infrastructure.core.data.ApiParameterError;
import org.apache.fineract.infrastructure.core.exception.PlatformApiDataValidationException;

import java.util.List ;
public abstract class ValidationException {


    public void throwExceptionIfValidationWarningsExist(final List<ApiParameterError> dataValidationErrors) {
        if (!dataValidationErrors.isEmpty()) { throw new PlatformApiDataValidationException(dataValidationErrors); }
    }
}
