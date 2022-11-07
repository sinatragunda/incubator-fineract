/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 November 2022 at 06:26
 */
package org.apache.fineract.portfolio.remittance.repo;

import org.apache.fineract.portfolio.remittance.domain.RxDealTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RxDealTransactionRepository extends JpaRepository<RxDealTransaction, Long>, JpaSpecificationExecutor<RxDealTransaction> {
}


