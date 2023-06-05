/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 May 2023 at 08:19
 */
package org.apache.fineract.portfolio.ssbpayments.repo;

import org.apache.fineract.portfolio.ssbpayments.domain.SsbTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.taat.wese.weseaddons.ssb.enumerations.PORTFOLIO_TYPE;

public interface SsbTransactionRepository extends JpaRepository<SsbTransaction,Long> , JpaSpecificationExecutor<SsbTransaction> {

    @Query("select ssb from SsbTransaction ssb where ssb.objectId = :objectId and ssb.transactionId =:transactionId and portfolioType = :portfolioType")
    SsbTransaction findByObjectIdAndTransactionIdAndPortfolioType(PORTFOLIO_TYPE portfolioType ,Long objectId , Long transactionId);

}
