/*

    Created by Sinatra Gunda
    At 1:37 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.repo;
import org.apache.fineract.portfolio.commissions.domain.LoanAgent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LoanAgentRepository extends JpaRepository<LoanAgent ,Long> ,JpaSpecificationExecutor<LoanAgent> {

}
