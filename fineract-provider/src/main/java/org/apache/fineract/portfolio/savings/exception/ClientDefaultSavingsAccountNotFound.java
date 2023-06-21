/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 19 June 2023 at 10:26
 */
package org.apache.fineract.portfolio.savings.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ClientDefaultSavingsAccountNotFound extends AbstractPlatformResourceNotFoundException {

    public ClientDefaultSavingsAccountNotFound(){
        super("Client default settlement acccount not found ,please set one" ,"Client default savings acccount not found ,please set one");
    }
}
