/*

    Created by Sinatra Gunda
    At 8:30 AM on 1/5/2022

*/
package org.apache.fineract.portfolio.commissions.repo;
import org.apache.fineract.portfolio.commissions.domain.AttachedCommissionCharges;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface AttachedCommissionChargesRepository extends JpaRepository<AttachedCommissionCharges ,Long> ,JpaSpecificationExecutor<AttachedCommissionCharges>{

}

