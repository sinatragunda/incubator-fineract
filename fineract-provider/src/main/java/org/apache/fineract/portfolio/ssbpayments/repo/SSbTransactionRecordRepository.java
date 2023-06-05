/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 May 2023 at 08:20
 */
package org.apache.fineract.portfolio.ssbpayments.repo;

import org.apache.fineract.portfolio.ssbpayments.domain.SsbTransactionRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface SSbTransactionRecordRepository extends JpaRepository<SsbTransactionRecord,Long> , JpaSpecificationExecutor<SsbTransactionRecord> {

    public SsbTransactionRecord findOneByFilename(String filename);
}
