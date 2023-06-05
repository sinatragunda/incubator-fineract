/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 June 2023 at 00:55
 */
package org.apache.fineract.portfolio.ssbpayments.exception;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformResourceNotFoundException;

public class SsbFileDuplicityException extends AbstractPlatformResourceNotFoundException {

    public SsbFileDuplicityException(String filename) {
        super(String.format("SSb File with filename %s is a duplicate " ,filename) ,String.format("SSb File with filename %s is a duplicate " ,filename) ,filename);
    }
}
