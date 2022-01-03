/*

    Created by Sinatra Gunda
    At 10:14 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.repo;


import org.apache.fineract.portfolio.commissions.domain.LoanCommissionCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface LoanCommissionChargesRepository extends JpaRepository<LoanCommissionCharge ,Long> ,JpaSpecificationExecutor<LoanCommissionCharge>{

    public LoanCommissionCharge findOne(Long id);

}
