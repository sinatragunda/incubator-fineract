/*

    Created by Sinatra Gunda
    At 12:18 AM on 8/12/2022

*/
package org.apache.fineract.portfolio.savings.repo;

import org.apache.fineract.portfolio.savings.domain.SavingsTransactionTrigger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface SavingsTransactionTriggerRepository  extends JpaRepository<SavingsTransactionTrigger, Long>,
        JpaSpecificationExecutor<SavingsTransactionTrigger> {


}
