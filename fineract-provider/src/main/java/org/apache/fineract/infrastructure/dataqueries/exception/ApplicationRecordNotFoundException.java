/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 March 2023 at 00:20
 */
package org.apache.fineract.infrastructure.dataqueries.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class ApplicationRecordNotFoundException extends AbstractPlatformResourceNotFoundException {

    public ApplicationRecordNotFoundException(Object... defaultUserMessageArgs) {
        super("Linked referenced record not found", "Linked reference record not found in system", defaultUserMessageArgs);
    }
}
