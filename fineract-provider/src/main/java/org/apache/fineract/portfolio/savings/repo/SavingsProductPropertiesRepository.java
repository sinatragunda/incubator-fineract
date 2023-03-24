/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 22 March 2023 at 17:43
 */
package org.apache.fineract.portfolio.savings.repo;

import org.apache.fineract.portfolio.savings.domain.SavingsProductProperties;
import org.apache.fineract.portfolio.savings.domain.SavingsTransactionTrigger;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface SavingsProductPropertiesRepository  extends JpaRepository<SavingsProductProperties, Long>,
        JpaSpecificationExecutor<SavingsProductProperties> {


}
