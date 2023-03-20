/**
 * Added 10/03/2023 at 0141
 * To be used by validation engine ,better way to get rid of errors since the errrors are eager in nature
 */
package org.apache.fineract.infrastructure.core.exception;

public class DataValidationException extends AbstractPlatformDomainRuleException {

    public DataValidationException(String messsage){
        super(messsage ,messsage ,messsage);
    }
}
