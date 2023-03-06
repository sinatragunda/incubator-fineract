/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 18 February 2023 at 13:05
 */
package org.apache.fineract.portfolio.localref.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class LocalRefValueMandatoryException extends AbstractPlatformResourceNotFoundException {

    public LocalRefValueMandatoryException(String defaultUserMessage, Object... defaultUserMessageArgs) {
        super(defaultUserMessage, defaultUserMessage, defaultUserMessageArgs);
    }

    public LocalRefValueMandatoryException(String key){
        super("Local Ref marked with requirement for mandatory value is missing a value ",String.format("Local Reference %s is marked mandatory ,is missing a value",key) ,key);
    }
}
