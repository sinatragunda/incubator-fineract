/*

    Created by Sinatra Gunda
    At 9:54 AM on 8/25/2022

*/
package org.apache.fineract.notification.service;

import org.apache.fineract.infrastructure.core.domain.EmailDetail;
import org.apache.fineract.notification.data.EventMailListData;
import org.apache.fineract.notification.data.NotificationData;
import org.apache.fineract.notification.enumerations.NOTIFICATION_BROADCAST_TYPE;
import org.apache.fineract.notification.helper.EventSubscriptionHelper;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.MailRecipients;
import org.apache.fineract.notification.helper.MailRecipientsHelper;
import org.apache.fineract.portfolio.client.repo.MailRecipientsKeyRepository;
import org.apache.fineract.portfolio.client.repo.MailRecipientsRepository;
import org.apache.fineract.portfolio.client.service.ClientReadPlatformService;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;

import org.apache.fineract.portfolio.loanaccount.service.LoanReadPlatformService;
import org.apache.fineract.portfolio.savings.service.SavingsAccountReadPlatformService;
import org.apache.fineract.wese.enumerations.SEND_MAIL_MESSAGE_STATUS;
import org.apache.fineract.wese.service.WeseEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Queue;


@Service
public class NotificationEventServiceExImpl implements NotificationEventServiceEx {

    /**
     * Class is used to send event based notifications to corresponding recipients
     * Class uses WeseMailService to send emails
     * Class should also find a way to handle exceptions now since no mechanism for exceptions exists
     */

    private final ClientReadPlatformService clientReadPlatformService;
    private final EventMailListReadPlatformService eventMailListReadPlatformService;
    private final LoanReadPlatformService loanReadPlatformService ;
    private final SavingsAccountReadPlatformService savingsAccountReadPlatformService;
    private final MailRecipientsRepository mailRecipientsRepository ;
    private final MailRecipientsKeyRepository mailRecipientsKeyRepository;
    private final WeseEmailService weseEmailService;


    @Autowired
    public NotificationEventServiceExImpl(ClientReadPlatformService clientReadPlatformService, EventMailListReadPlatformService eventMailListReadPlatformService, LoanReadPlatformService loanReadPlatformService, SavingsAccountReadPlatformService savingsAccountReadPlatformService, MailRecipientsRepository mailRecipientsRepository, MailRecipientsKeyRepository mailRecipientsKeyRepository, WeseEmailService weseEmailService) {
        this.clientReadPlatformService = clientReadPlatformService;
        this.eventMailListReadPlatformService = eventMailListReadPlatformService;
        this.loanReadPlatformService = loanReadPlatformService;
        this.savingsAccountReadPlatformService = savingsAccountReadPlatformService;
        this.mailRecipientsRepository = mailRecipientsRepository;
        this.mailRecipientsKeyRepository = mailRecipientsKeyRepository;
        this.weseEmailService = weseEmailService;
    }

    @Override
    public void trigger(BUSINESS_EVENTS businessEvents , NotificationData notificationData){

        System.err.println("-------------------business event is "+businessEvents+"-----------participant id "+notificationData.getActor());

        Long actorId = notificationData.getObjectIdentfier();
        Long officeId = notificationData.getOfficeId();

        // get client record from participant id ?
        // we need a way to get message templates now son

        List<EventMailListData> eventMailListDataList = eventMailListReadPlatformService.retrieveWhereEvent(officeId ,businessEvents);

        System.err.println("---------------event mail list is -------------"+eventMailListDataList.size());

        eventMailListDataList.stream().forEach(e->{

            // first find type of broadcast type
            NOTIFICATION_BROADCAST_TYPE notificationBroadcastType = e.getEventSubscriptionData().getNotificationType();
            // if its topic then find if client is subscribed

            System.err.println("---------------------broadcast type is ---------------"+notificationBroadcastType);

            switch (notificationBroadcastType){

                case TOPIC:
                    // send to one person only
                    ClientData clientData = getClientRecordFromActorEvent(businessEvents ,actorId);
                    Long clientId = clientData.getId();
                    boolean isSubscribed = EventSubscriptionHelper.isClientSubscribedForEvent(mailRecipientsRepository ,e ,clientId);
                    topicNotification(notificationData,clientData, businessEvents,isSubscribed);
                    break;

                case BROADCAST:
                    // send to all recipients
                    Long keyId = e.getMailRecipientsKeyData().getId();

                    System.err.println("----------------------key id is ------------"+keyId);

                    Queue<MailRecipients> mailRecipientsQueue = MailRecipientsHelper.emailRecipients(mailRecipientsKeyRepository ,mailRecipientsRepository ,clientReadPlatformService ,keyId);

                    mailRecipientsQueue.stream().forEach(mailRecipient->{
                        broadcastNotification(notificationData ,mailRecipient ,businessEvents);
                    });
                    break;
            }
        });

    }

    // in future would need custom message ,to be taken from database and sanitized but for now nope
    private EmailDetail getMailDetails(NotificationData notificationData ,ClientData clientData ,BUSINESS_EVENTS businessEvents){

        String subject = businessEvents.getValue();
        String contact = clientData.displayName();
        String email = clientData.getEmailAddress();
        String body = String.format("%s with record id %d created at %s",notificationData.getContent() ,notificationData.getObjectIdentfier() ,notificationData.getCreatedAt());

        return new EmailDetail(subject ,body ,email ,contact);
    }

    // get mail details for broadcast events
    private EmailDetail getMailDetails(NotificationData notificationData ,MailRecipients mailRecipients ,BUSINESS_EVENTS businessEvents){

        String subject = businessEvents.getValue();
        String contact = mailRecipients.getName();
        String email = mailRecipients.getEmailAddress();
        String body = String.format("%s with record id %d created at %s",notificationData.getContent() ,notificationData.getObjectIdentfier() ,notificationData.getCreatedAt());

        return new EmailDetail(subject ,body ,email ,contact);
    }

    private SEND_MAIL_MESSAGE_STATUS topicNotification(NotificationData notificationData , ClientData clientData, BUSINESS_EVENTS businessEvents, boolean isSubscribed){

        System.err.println("-----------------------------send specific message to whoever this is son -------------");
        // only send if client is subscribed
        if(isSubscribed){
            EmailDetail emailDetail = getMailDetails(notificationData ,clientData ,businessEvents);
            return sendMessage(emailDetail);
        }

        return null ;

    }

    private SEND_MAIL_MESSAGE_STATUS broadcastNotification(NotificationData notificationData ,MailRecipients mailRecipients ,BUSINESS_EVENTS businessEvents){

        System.err.println("------------------broadcast notification son -----------------------");
        // only send if client is subscribed
        EmailDetail emailDetail = getMailDetails(notificationData ,mailRecipients ,businessEvents);

        return sendMessage(emailDetail);

    }

    private SEND_MAIL_MESSAGE_STATUS sendMessage(EmailDetail emailDetail){
        SEND_MAIL_MESSAGE_STATUS sendMailMessageStatus = null;
        sendMailMessageStatus = weseEmailService.send(emailDetail);
        System.err.println("-------------------send mail status -----------------"+sendMailMessageStatus);

        return sendMailMessageStatus;
    }

    private enum ACTOR_EVENT{
        LOAN,
        SAVING,
        SHARE,
        CLIENT,
        GROUP
    }

    /**
     * ClientData record is essential when doing topic based notifications as it has the client information including client email address ,name etc
     * @param businessEvents
     * @param id
     * @return
     */
    private ClientData getClientRecordFromActorEvent(BUSINESS_EVENTS businessEvents,Long id){

        final ClientData clientData ;
        Long clientId = null ;
        ACTOR_EVENT actorEvent = null ;

        switch (businessEvents){
            case LOAN_CLOSE:
            case LOAN_CREATE:
            case LOAN_REFUND:
            case LOAN_APPROVED:
            case LOAN_REJECTED:
            case LOAN_DISBURSAL:
            case LOAN_ADD_CHARGE:
                actorEvent = ACTOR_EVENT.LOAN;
                break;

        }

        switch (actorEvent){
            case LOAN:
                clientId = loanReadPlatformService.retrieveOne(id).getClientId();
                break;
            case SAVING:
                clientId = savingsAccountReadPlatformService.retrieveOne(id).getClientId();
                break;
        }

        clientData = clientReadPlatformService.retrieveOne(clientId);
        return clientData;
    }
}
