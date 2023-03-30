/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 01:03
 */
package org.apache.fineract.utility.helper;

import java.time.Instant;

public class TimeHelperEx {

    /**
     *  Added on 30/03/2023 at 0104
     *  Get Epoch second instead of Milli ?
     */
    public static Long now(){
        return Instant.now().getEpochSecond();
    }
}
