/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.infrastructure.jobs.service;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResult;
import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.dataqueries.helper.ScheduledReportHelper;
import org.apache.fineract.infrastructure.dataqueries.service.ScheduledReportRepositoryWrapper;
import org.apache.fineract.infrastructure.jobs.annotation.CronTarget;
import org.apache.fineract.infrastructure.jobs.data.JobDetailDataValidator;
import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobDetail;
import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobDetailRepository;
import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobRunHistory;
import org.apache.fineract.infrastructure.jobs.domain.ScheduledJobRunHistoryRepository;
import org.apache.fineract.infrastructure.jobs.domain.SchedulerDetail;
import org.apache.fineract.infrastructure.jobs.domain.SchedulerDetailRepository;
import org.apache.fineract.infrastructure.jobs.exception.JobNotFoundException;
import org.apache.fineract.infrastructure.jobs.helper.ScheduledJobsHelper;
import org.apache.fineract.portfolio.client.repo.MailRecipientsKeyRepository;
import org.apache.fineract.portfolio.client.repo.MailRecipientsRepository;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.spm.repository.EmailSendStatusRepository;
import org.apache.fineract.spm.repository.MailServerSettingsRepository;
import org.apache.fineract.spm.repository.ScheduledMailSessionRepository;
import org.apache.fineract.wese.service.WeseEmailService;
import org.mifosplatform.infrastructure.report.service.PentahoReportingProcessServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class SchedularWritePlatformServiceJpaRepositoryImpl implements SchedularWritePlatformService {

    private final ScheduledJobDetailRepository scheduledJobDetailsRepository;
    private final ScheduledJobRunHistoryRepository scheduledJobRunHistoryRepository;
    private final SchedulerDetailRepository schedulerDetailRepository;
    private final JobDetailDataValidator dataValidator;
    private final FromJsonHelper fromJsonHelper;
    private final ScheduledReportRepositoryWrapper scheduledReportRepositoryWrapper;
    private final PentahoReportingProcessServiceImpl pentahoReportingProcessService;
    private final WeseEmailService weseEmailService ;
    private final ClientReadPlatformService clientReadPlatformService ;
    private final MailRecipientsKeyRepository mailRecipientsKeyRepository;
    private final MailRecipientsRepository mailRecipientsRepository;
    private final MailServerSettingsRepository mailServerSettingsRepository ;

    // Added 06/09/2021
    private final ScheduledMailSessionRepository scheduledMailSessionRepository;
    private final EmailSendStatusRepository emailSendStatusRepository;


    @Autowired
    public SchedularWritePlatformServiceJpaRepositoryImpl(final ScheduledJobDetailRepository scheduledJobDetailsRepository,
                                                          final ScheduledJobRunHistoryRepository scheduledJobRunHistoryRepository, final JobDetailDataValidator dataValidator,
                                                          final SchedulerDetailRepository schedulerDetailRepository , final FromJsonHelper fromJsonHelper , final  ScheduledReportRepositoryWrapper scheduledReportRepositoryWrapper , final WeseEmailService weseEmailService , final PentahoReportingProcessServiceImpl pentahoReportingProcessService , final MailRecipientsKeyRepository mailRecipientsKeyRepository, final ClientReadPlatformService clientReadPlatformService , final MailRecipientsRepository mailRecipientsRepository, final MailServerSettingsRepository mailServerSettingsRepository , final ScheduledMailSessionRepository scheduledMailSessionRepository , final EmailSendStatusRepository emailSendStatusRepository) {
        this.scheduledJobDetailsRepository = scheduledJobDetailsRepository;
        this.scheduledJobRunHistoryRepository = scheduledJobRunHistoryRepository;
        this.schedulerDetailRepository = schedulerDetailRepository;
        this.dataValidator = dataValidator;
        this.fromJsonHelper = fromJsonHelper ;
        this.scheduledReportRepositoryWrapper = scheduledReportRepositoryWrapper;
        this.pentahoReportingProcessService = pentahoReportingProcessService ;
        this.weseEmailService = weseEmailService;
        this.mailRecipientsKeyRepository = mailRecipientsKeyRepository;
        this.clientReadPlatformService = clientReadPlatformService;
        this.mailRecipientsRepository = mailRecipientsRepository;
        this.mailServerSettingsRepository = mailServerSettingsRepository ;
        this.emailSendStatusRepository = emailSendStatusRepository ;
        this.scheduledMailSessionRepository = scheduledMailSessionRepository;
    }

    @Override
    public List<ScheduledJobDetail> retrieveAllJobs() {
        return this.scheduledJobDetailsRepository.findAll();
    }

    @Override
    public ScheduledJobDetail findByJobKey(final String jobKey) {
        return this.scheduledJobDetailsRepository.findByJobKey(jobKey);
    }

    @Transactional
    @Override
    public void saveOrUpdate(final ScheduledJobDetail scheduledJobDetails) {
        this.scheduledJobDetailsRepository.saveAndFlush(scheduledJobDetails);
    }

    @Transactional
    @Override
    public void saveOrUpdate(final ScheduledJobDetail scheduledJobDetails, final ScheduledJobRunHistory scheduledJobRunHistory) {
        this.scheduledJobDetailsRepository.save(scheduledJobDetails);
        this.scheduledJobRunHistoryRepository.save(scheduledJobRunHistory);
    }

    @Override
    public Long fetchMaxVersionBy(final String jobKey) {
        Long version = 0L;
        final Long versionFromDB = this.scheduledJobRunHistoryRepository.findMaxVersionByJobKey(jobKey);
        if (versionFromDB != null) {
            version = versionFromDB;
        }
        return version;
    }

    @Override
    public ScheduledJobDetail findByJobId(final Long jobId) {
        return this.scheduledJobDetailsRepository.findByJobId(jobId);
    }

    @Override
    @Transactional
    public void updateSchedulerDetail(final SchedulerDetail schedulerDetail) {
        this.schedulerDetailRepository.save(schedulerDetail);
    }

    @Override
    public SchedulerDetail retriveSchedulerDetail() {
        SchedulerDetail schedulerDetail = null;
        final List<SchedulerDetail> schedulerDetailList = this.schedulerDetailRepository.findAll();
        if (schedulerDetailList != null) {
            schedulerDetail = schedulerDetailList.get(0);
        }
        return schedulerDetail;
    }

    @Transactional
    @Override
    public CommandProcessingResult updateJobDetail(final Long jobId, final JsonCommand command) {
        this.dataValidator.validateForUpdate(command.json());
        final ScheduledJobDetail scheduledJobDetail = findByJobId(jobId);
        if (scheduledJobDetail == null) { throw new JobNotFoundException(String.valueOf(jobId)); }
        final Map<String, Object> changes = scheduledJobDetail.update(command);
        if (!changes.isEmpty()) {
            this.scheduledJobDetailsRepository.saveAndFlush(scheduledJobDetail);
        }
        return new CommandProcessingResultBuilder() //
                .withCommandId(command.commandId()) //
                .withEntityId(jobId) //
                .with(changes) //
                .build();

    }

    @Transactional
    @Override
    public boolean processJobDetailForExecution(final String jobKey, final String triggerType) {
        boolean isStopExecution = false;
        final ScheduledJobDetail scheduledJobDetail = this.scheduledJobDetailsRepository.findByJobKeyWithLock(jobKey);
        if (scheduledJobDetail.isCurrentlyRunning()
                || (triggerType.equals(SchedulerServiceConstants.TRIGGER_TYPE_CRON) && (scheduledJobDetail.getNextRunTime().after(new Date())))) {
            isStopExecution = true;
        }
        final SchedulerDetail schedulerDetail = retriveSchedulerDetail();
        if (triggerType.equals(SchedulerServiceConstants.TRIGGER_TYPE_CRON) && schedulerDetail.isSuspended()) {
            scheduledJobDetail.updateTriggerMisfired(true);
            isStopExecution = true;
        } else if (!isStopExecution) {
            scheduledJobDetail.updateCurrentlyRunningStatus(true);
        }
        this.scheduledJobDetailsRepository.save(scheduledJobDetail);
        return isStopExecution;
    }


    // Added 11/08/2021 for scheduling pdf reports sent to mails

    @Override
    public Long createScheduledReport(String apiBody){
        return ScheduledReportHelper.createScheduledReport(scheduledReportRepositoryWrapper ,fromJsonHelper,this ,apiBody);
    }

    @Override
    @CronTarget(jobName = JobName.SCHEDULED_EMAIL_CLIENT_REPORTS)
    public void executeScheduledClientReportMail(){

        Long jobId = ScheduledJobsHelper.activeJobId;
        ScheduledJobDetail scheduledJobDetail = findByJobId(jobId);

        if (jobId !=null){
            ScheduledReportHelper.runScheduledMailReport(pentahoReportingProcessService ,weseEmailService ,scheduledReportRepositoryWrapper , mailRecipientsKeyRepository, mailRecipientsRepository,mailServerSettingsRepository,scheduledMailSessionRepository ,emailSendStatusRepository , clientReadPlatformService ,scheduledJobDetail);
        }

    }
}
