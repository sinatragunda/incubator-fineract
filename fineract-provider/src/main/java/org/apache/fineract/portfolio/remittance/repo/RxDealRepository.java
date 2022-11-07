/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 November 2022 at 06:26
 */
package org.apache.fineract.portfolio.remittance.repo;

import org.apache.fineract.portfolio.remittance.domain.RxDeal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RxDealRepository extends JpaRepository<RxDeal, Long>, JpaSpecificationExecutor<RxDeal> {

    public RxDeal findOneByKey(String key);
}

