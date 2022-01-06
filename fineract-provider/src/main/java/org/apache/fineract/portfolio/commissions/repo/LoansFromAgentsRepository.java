/*

    Created by Sinatra Gunda
    At 4:40 AM on 1/5/2022

*/
package org.apache.fineract.portfolio.commissions.repo;
import org.apache.fineract.portfolio.commissions.domain.LoansFromAgents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoansFromAgentsRepository extends JpaRepository<LoansFromAgents ,Long> ,JpaSpecificationExecutor<LoansFromAgents> {

}
