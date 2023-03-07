/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 06 March 2023 at 08:06
 */
package org.apache.fineract.infrastructure.security.helper;


import javax.servlet.http.HttpServletRequest;
public class RequestState {

    private Long timestamp ;
    private Boolean repeat ;

    public RequestState(Long timestamp, Boolean repeat) {
        this.timestamp = timestamp;
        this.repeat = repeat;
    }

    public RequestState(HttpServletRequest httpServletRequest){

        String timestampL = httpServletRequest.getHeader("timestamp");
        String repeatStr = httpServletRequest.getHeader("repeat");

        this.timestamp = Long.valueOf(timestampL);
        this.repeat = Boolean.valueOf(repeatStr);

    }

    public Boolean isRepeat() {
        return this.repeat;
    }

    public Long getTimestamp() {
        return this.timestamp;
    }
}
