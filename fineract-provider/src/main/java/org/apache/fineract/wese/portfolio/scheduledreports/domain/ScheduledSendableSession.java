/*

    Created by Sinatra Gunda
    At 10:47 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.domain;

public class ScheduledSendableSession {

    private ScheduledMailSession scheduledMailSession;
    private SendableReport sendableReport;

    public ScheduledSendableSession(){}

    public ScheduledSendableSession(ScheduledMailSession scheduledMailSession, SendableReport sendableReport) {
        this.scheduledMailSession = scheduledMailSession;
        this.sendableReport = sendableReport;
    }

    public void updateResults(EmailSendStatus emailSendStatus){
        //scheduledMailSession.updateCount();
        scheduledMailSession.updateEmailSendList(emailSendStatus);
    }

    public void closeSession(){
        scheduledMailSession.closeSession();
    }

    public ScheduledMailSession getScheduledMailSession() {
        return scheduledMailSession;
    }

    public void setScheduledMailSession(ScheduledMailSession scheduledMailSession) {
        this.scheduledMailSession = scheduledMailSession;
    }

    public SendableReport getSendableReport() {
        return sendableReport;
    }

    public void setSendableReport(SendableReport sendableReport) {
        this.sendableReport = sendableReport;
    }
}
