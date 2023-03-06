/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 06 March 2023 at 08:24
 */
package org.apache.fineract.infrastructure.security.helper;

import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;

public class OverrideHelper {

    public static boolean override(){
        RequestState requestState  = ThreadLocalContextUtil.getRequestState().get();
        boolean isRepeat = requestState.isRepeat();
        return isRepeat;
    }

    /**
     * Added 06/03/2023 at 0930
     *
     */
    public static Long timestamp(){
        return ThreadLocalContextUtil.getRequestState().get().getTimestamp();
    }
}
