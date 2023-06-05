/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 31 May 2023 at 07:10
 */
package org.apache.fineract.portfolio.ssbpayments.service;

import org.apache.fineract.portfolio.ssbpayments.data.SsbTransactionRecordData;
import org.apache.fineract.portfolio.ssbpayments.domain.SsbTransaction;
import org.apache.fineract.portfolio.ssbpayments.domain.SsbTransactionRecord;

import java.util.Collection;

public interface SsbTransactionReadPlatformService {

    public SsbTransactionRecordData retrieveOne(Long id);
    public Collection<SsbTransactionRecordData> retrieveAll();
}
