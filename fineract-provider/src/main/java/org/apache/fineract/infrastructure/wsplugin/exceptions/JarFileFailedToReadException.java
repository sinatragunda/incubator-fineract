/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 25 April 2023 at 09:07
 */
package org.apache.fineract.infrastructure.wsplugin.exceptions;

import org.apache.fineract.infrastructure.core.exception.AbstractPlatformDomainRuleException;
import org.apache.fineract.infrastructure.wsplugin.api.WsScriptConstants;

public class JarFileFailedToReadException extends AbstractPlatformDomainRuleException {

    public JarFileFailedToReadException() {
        super(WsScriptConstants.jarErrorMessage ,WsScriptConstants.jarErrorMessage ,WsScriptConstants.jarErrorMessage);
    }
}
