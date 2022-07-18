/*

    Created by Sinatra Gunda
    At 3:33 AM on 9/5/2021

*/
package org.apache.fineract.wese.portfolio.scheduledreports.domain;

import org.apache.fineract.portfolio.client.domain.MailRecipients;
import org.apache.fineract.wese.helper.SessionIDGenerator;

import java.util.Queue;

public class SendableReport {

    private String sessionId;
    private PentahoReportGenerator pentahoReportGenerator;
    private Queue<MailRecipients> mailRecipientsQueue;

    public SendableReport(PentahoReportGenerator pentahoReportGenerator, Queue<MailRecipients> mailRecipientsQueue) {
        // generate unique session upon init of this function
        this.sessionId = SessionIDGenerator.sessionId();
        this.pentahoReportGenerator = pentahoReportGenerator;
        this.mailRecipientsQueue = mailRecipientsQueue;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public PentahoReportGenerator getPentahoReportGenerator() {
        return pentahoReportGenerator;
    }

    public void setPentahoReportGenerator(PentahoReportGenerator pentahoReportGenerator) {
        this.pentahoReportGenerator = pentahoReportGenerator;
    }

    public Queue<MailRecipients> getMailRecipientsQueue() {
        return mailRecipientsQueue;
    }

    public void setMailRecipientsQueue(Queue<MailRecipients> mailRecipientsQueue) {
        this.mailRecipientsQueue = mailRecipientsQueue;
    }
}
