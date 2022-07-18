/*

    Created by Sinatra Gunda
    At 6:54 AM on 7/18/2022

*/
package org.apache.fineract.notification.repo;

import org.apache.fineract.notification.domain.EventSubscription;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface EventSubscriptionRepository  extends JpaRepository<EventSubscription, Long>, JpaSpecificationExecutor<EventSubscription>{


}
