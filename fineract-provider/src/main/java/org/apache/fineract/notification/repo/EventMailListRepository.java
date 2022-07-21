/*

    Created by Sinatra Gunda
    At 6:57 AM on 7/18/2022

*/
package org.apache.fineract.notification.repo;

import org.apache.fineract.notification.domain.EventMailList;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface EventMailListRepository extends JpaRepository<EventMailList ,Long> ,JpaSpecificationExecutor<EventMailList>{

    List<EventMailList> findByEventSubscriptionId(Long id);

}
