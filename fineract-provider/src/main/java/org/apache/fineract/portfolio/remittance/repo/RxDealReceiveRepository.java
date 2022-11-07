/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 06 November 2022 at 20:56
 */
package org.apache.fineract.portfolio.remittance.repo;

import org.apache.fineract.portfolio.remittance.domain.RxDeal;
import org.apache.fineract.portfolio.remittance.domain.RxDealReceive;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RxDealReceiveRepository extends JpaRepository<RxDealReceive, Long>, JpaSpecificationExecutor<RxDealReceive> {}

