/*

    Created by Sinatra Gunda
    At 9:14 AM on 9/2/2021

*/
package org.apache.fineract.portfolio.mailserver.domain;



import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.wese.helper.JsonHelper;

import java.util.ArrayList;
import java.util.List;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Transient ;

@Entity
@Table(name="m_mail_server_settings")
public class MailServerSettings extends AbstractPersistableCustom<Long>{

    @Column(name="is_timed_server",nullable=false)
    private boolean isTimedServer;

    @Column(name="timer_type",nullable=false)
    private int timerType;
    /// uses duration enum here

    @Column(name="quota_duration",nullable=false)
    private int quotaDuration;

    @Column(name="quota_limit",nullable=false)
    private int limit;

    public MailServerSettings(){}

    public boolean isTimedServer() {
        return isTimedServer;
    }

    public void setIsTimedServer(boolean timedServer) {
        isTimedServer = timedServer;
    }

    public int getTimerType() {
        return timerType;
    }

    public void setTimerType(int timerType) {
        this.timerType = timerType;
    }

    public int getQuotaDuration() {
        return quotaDuration;
    }

    public void setQuotaDuration(int quotaDuration) {
        this.quotaDuration = quotaDuration;
    }

    public static MailServerSettings fromHttpResponse(String arg){
        return JsonHelper.serializeFromHttpResponse(new MailServerSettings(),arg);
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    @Override
    public String toString() {
        return "MailServerSettings{" +
                "isTimedServer=" + isTimedServer +
                ", timerType=" + timerType +
                ", quotaDuration=" + quotaDuration +
                ", limit=" + limit +
                '}';
    }
}
