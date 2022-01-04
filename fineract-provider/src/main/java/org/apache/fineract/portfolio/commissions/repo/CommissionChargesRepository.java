/*

    Created by Sinatra Gunda
    At 10:14 PM on 1/2/2022

*/
package org.apache.fineract.portfolio.commissions.repo;


import org.apache.fineract.portfolio.commissions.domain.CommissionCharge;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface CommissionChargesRepository extends JpaRepository<CommissionCharge,Long> ,JpaSpecificationExecutor<CommissionCharge>{

    public CommissionCharge findOne(Long id);

}
