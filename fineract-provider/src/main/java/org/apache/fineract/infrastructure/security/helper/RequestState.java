/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 06 March 2023 at 08:06
 */
package org.apache.fineract.infrastructure.security.helper;


import org.apache.fineract.utility.helper.TimeHelperEx;
import org.apache.fineract.wese.helper.TimeHelper;

import javax.servlet.http.HttpServletRequest;
public class RequestState {

    private Long timestamp = TimeHelperEx.now();
    private Boolean repeat = false;

    public RequestState(Long timestamp, Boolean repeat) {
        this.timestamp = timestamp;
        this.repeat = repeat;
    }

    public RequestState(HttpServletRequest httpServletRequest){

        String timestampL = httpServletRequest.getHeader("timestamp");
        String repeatStr = httpServletRequest.getHeader("repeat");

        boolean hasHeader = HeaderHelper.hasHeader(httpServletRequest ,"timestamp");
        if(hasHeader) {
            this.timestamp = Long.valueOf(timestampL);
            this.repeat = Boolean.valueOf(repeatStr);
            return ;
        }
    }

    public Boolean isRepeat() {
        return this.repeat;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }
}
