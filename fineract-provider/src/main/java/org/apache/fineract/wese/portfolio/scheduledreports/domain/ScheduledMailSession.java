/*

    Created by Sinatra Gunda
    At 1:59 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.service.ThreadLocalContextUtil;
import org.apache.fineract.infrastructure.dataqueries.domain.ScheduledReport;
import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobDetail;
import org.apache.fineract.wese.enumerations.EMAIL_MESSAGE_TYPE;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.fineract.wese.helper.TimeHelper;
import org.apache.fineract.wese.portfolio.scheduledreports.enumerations.ACTIVE_MAIL_SESSION_STATUS;

import java.util.ArrayList;
import java.util.List;

// Added 06/09/2021

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;

@Entity
@Table(name="m_scheduled_mail_session")
public class ScheduledMailSession extends AbstractPersistableCustom<Long>{

    @ManyToOne
    @JoinColumn(name="scheduled_report_id" ,nullable = false)
    private ScheduledReport scheduledReport;

    @Transient
    private List<EmailSendStatus> activeEmailSendStatusList = new ArrayList<>();


    @Transient
    private List<EmailSendStatus> previousEmailSendStatusList = new ArrayList<>();


    @Column(name="start_time" ,nullable = false)
    private Long startTime ;

    @Column(name="end_time" ,nullable = false)
    private Long endTime ;

    @Column(name="count",nullable = false)
    private int count = 0;

    @Column(name="success_count",nullable = false)
    private int successCount = 0 ;

    @Column(name="fail_count" ,nullable = false)
    private int failCount = 0 ;

    @Transient
    private boolean isActive = false;

    @Transient
    private ACTIVE_MAIL_SESSION_STATUS activeMailSessionStatus;

    public ScheduledMailSession(){}

    public ScheduledMailSession(ScheduledReport scheduledReport){
        this.scheduledReport = scheduledReport ;
        //init();
    }

    public void init(){
        startTime = TimeHelper.dateNow().getTime();
        //tenant = ThreadLocalContextUtil.getTenant().getTenantIdentifier();
        isActive = true ;
    }

    public ScheduledReport getScheduledReport() {
        return scheduledReport;
    }

    public void setScheduledReport(ScheduledReport scheduledReport) {
        this.scheduledReport = scheduledReport;
    }

    public List<EmailSendStatus> getActiveEmailSendStatusList() {
        return activeEmailSendStatusList;
    }

    public void setActiveEmailSendStatusList(List<EmailSendStatus> activeEmailSendStatusList) {
        this.activeEmailSendStatusList = activeEmailSendStatusList;
    }

    public List<EmailSendStatus> getPreviousEmailSendStatusList() {
        return previousEmailSendStatusList;
    }

    public void setPreviousEmailSendStatusList(List<EmailSendStatus> previousEmailSendStatusList) {
        this.previousEmailSendStatusList = previousEmailSendStatusList;
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

    public ACTIVE_MAIL_SESSION_STATUS getActiveMailSessionStatus() {
        return activeMailSessionStatus;
    }

    public void setActiveMailSessionStatus(ACTIVE_MAIL_SESSION_STATUS activeMailSessionStatus) {
        this.activeMailSessionStatus = activeMailSessionStatus;
    }

    public void updateActiveEmailSendList(EmailSendStatus emailSendStatus){

        System.err.println("--------------update mail session here son-----------------");

        synchronized (activeEmailSendStatusList){

            activeEmailSendStatusList.add(emailSendStatus);
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

        System.err.println("-------------count is ---------------------"+activeEmailSendStatusList.size());
    }

    public void closeSession(){
        activeMailSessionStatus = ACTIVE_MAIL_SESSION_STATUS.CLOSED;
        endTime = TimeHelper.dateNow().getTime();
    }

    @Override
    public String toString() {
        return "ScheduledMailSession{" +
                "scheduledReport=" + scheduledReport +
                ", activeEmailSendStatusList=" + activeEmailSendStatusList +
                ", previousEmailSendStatusList=" + previousEmailSendStatusList +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", count=" + count +
                ", successCount=" + successCount +
                ", failCount=" + failCount +
                ", isActive=" + isActive +
                '}';
    }
}
