/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 May 2023 at 00:31
 */
package org.apache.fineract.infrastructure.generic.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class GenericErrorResourceNotFound extends AbstractPlatformResourceNotFoundException {

    public GenericErrorResourceNotFound(String globalisationMessageCode) {
        super(globalisationMessageCode, globalisationMessageCode, globalisationMessageCode);
    }

    public GenericErrorResourceNotFound(String globalisationMessageCode ,Object ...arg) {
        super(globalisationMessageCode, globalisationMessageCode, arg);
    }
}
