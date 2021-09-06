/*

    Created by Sinatra Gunda
    At 1:59 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.domain;

import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobDetail;
import org.apache.fineract.wese.enumerations.EMAIL_MESSAGE_TYPE;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.fineract.wese.helper.TimeHelper;

import java.util.ArrayList;
import java.util.List;

public class ScheduledMailSession {

    private String tenant ;
    private ScheduledReport scheduledReport;
    private List<EmailSendStatus> emailSendStatusList = new ArrayList<>();
    private Long startTime ;
    private Long endTime ;
    private int count  = 0;
    private int successCount = 0 ;
    private int failCount = 0 ;
    private boolean isActive = false;

    public ScheduledMailSession(){
        init();
    }

    public ScheduledMailSession(ScheduledReport scheduledReport){
        this.scheduledReport = scheduledReport ;
        init();
    }

    public void init(){
        startTime = TimeHelper.dateNow().getTime();
        tenant = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        isActive = true ;
    }

    public ScheduledReport getScheduledReport() {
        return scheduledReport;
    }

    public void setScheduledReport(ScheduledReport scheduledReport) {
        this.scheduledReport = scheduledReport;
    }

    public List<EmailSendStatus> getEmailSendStatusList() {
        return emailSendStatusList;
    }

    public void setEmailSendStatusList(List<EmailSendStatus> emailSendStatusList) {
        this.emailSendStatusList = emailSendStatusList;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(Long startTime) {
        this.startTime = startTime;
    }

    public Long getEndTime() {
        return endTime;
    }

    public void setEndTime(Long endTime) {
        this.endTime = endTime;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public int getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(int successCount) {
        this.successCount = successCount;
    }

    public int getFailCount() {
        return failCount;
    }

    public void setFailCount(int failCount) {
        this.failCount = failCount;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public synchronized void updateCount(){
        ++count ;
    }

    public void updateEmailSendList(EmailSendStatus emailSendStatus){

        synchronized (emailSendStatusList){
            emailSendStatusList.add(emailSendStatus);
            updateCount();
            SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus = emailSendStatus.getSendMailMessageStatus();
            switch (sendMailMessageStatus){
                case SUCCESS:
                    ++successCount;
                    break;
                case ERROR:
                case INVALID_ADDRESS:
                case BOUNCE_BACK:
                    ++failCount;
                    break;
            }
        }
    }

    public void closeSession(){
        endTime = TimeHelper.dateNow().getTime();
    }

}
