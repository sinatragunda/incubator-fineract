/*

    Created by Sinatra Gunda
    At 9:46 AM on 8/4/2021

*/
package org.apache.fineract.infrastructure.dataqueries.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.ManyToOne;
import javax.persistence.JoinColumn;
import javax.persistence.Transient;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobDetail;
import org.apache.fineract.portfolio.client.domain.MailRecipientsKey;


@Table(name ="m_scheduled_report")
@Entity
public class ScheduledReport extends AbstractPersistableCustom<Long> {

    @Column(name="report_name")
    private String reportName ;

    @Column(name="parameters")
    private String parameters ;

    @Column(name="job_id")
    private Long jobId;

    @Column(name="enique_report" ,nullable=true)
    private Boolean eniqueReport = true;

    @ManyToOne
    @JoinColumn(name="mail_recipients_key_id")
    private MailRecipientsKey mailRecipientsKey;

    // Added 02/09/2021
    @Transient
    private ScheduledJobDetail scheduledJobDetail ;

    public ScheduledReport(){}

    public ScheduledReport(String reportName, String parameters, Long jobId) {
        this.reportName = reportName;
        this.parameters = parameters;
        this.jobId = jobId;
    }

    public Long getJobId() {
        return jobId;
    }

    public void setJobId(Long jobId) {
        this.jobId = jobId;
    }

    public String getReportName() {
        return reportName;
    }

    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

    public String getParameters() {
        return parameters;
    }

    public void setParameters(String parameters) {
        this.parameters = parameters;
    }

    public MailRecipientsKey getMailRecipientsKey() {
        return mailRecipientsKey;
    }

    public void setMailRecipientsKey(MailRecipientsKey mailRecipientsKey) {
        this.mailRecipientsKey = mailRecipientsKey;
    }

    public Boolean getEniqueReport() {
        return eniqueReport;
    }

    public void setEniqueReport(Boolean eniqueReport) {
        this.eniqueReport = eniqueReport;
    }

    public ScheduledJobDetail getScheduledJobDetail() {
        return scheduledJobDetail;
    }

    public void setScheduledJobDetail(ScheduledJobDetail scheduledJobDetail) {
        this.scheduledJobDetail = scheduledJobDetail;
    }
}
