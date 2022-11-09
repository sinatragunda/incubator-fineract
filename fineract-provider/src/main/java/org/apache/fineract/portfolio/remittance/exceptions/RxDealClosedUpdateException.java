/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 November 2022 at 09:09
 */
package org.apache.fineract.portfolio.remittance.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class RxDealClosedUpdateException extends AbstractPlatformResourceNotFoundException {

    public RxDealClosedUpdateException(){
        super("Deal has been closed and not possible to update it ","Deal has been closed and not possible to update it" ,1L);
    }

    public RxDealClosedUpdateException(String globalisationMessageCode, String defaultUserMessage, Object... defaultUserMessageArgs) {
        super(globalisationMessageCode, defaultUserMessage, defaultUserMessageArgs);
    }
}
