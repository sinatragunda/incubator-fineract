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
package org.apache.fineract.notification.eventandlistener;

import org.apache.fineract.notification.data.NotificationData;
import org.apache.fineract.notification.service.NotificationEventServiceEx;
import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants.BUSINESS_EVENTS;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.stereotype.Service;

import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.Session;

@Service
public class NotificationEventService {

    private final JmsTemplate jmsTemplate;
    private final NotificationEventServiceEx notificationEventServiceEx;


    @Autowired
    public NotificationEventService(JmsTemplate jmsTemplate, NotificationEventServiceEx notificationEventServiceEx) {
        this.jmsTemplate = jmsTemplate;
        this.notificationEventServiceEx = notificationEventServiceEx;
    }



    // edited 25/08/2022
    public void broadcastNotification(final Destination destination, final NotificationData notificationData , final BUSINESS_EVENTS businessEvents) {
        this.jmsTemplate.send(destination, new MessageCreator() {
            @Override
            public Message createMessage(Session session) throws JMSException {
                return session.createObjectMessage(notificationData);
            }
        });
    }
}
