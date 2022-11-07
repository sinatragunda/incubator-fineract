package org.apache.fineract.portfolio.remittance.service;

import org.apache.fineract.portfolio.remittance.data.RxData;
import org.apache.fineract.portfolio.remittance.data.RxDealData;
import org.apache.fineract.portfolio.remittance.domain.RxDeal;

import java.util.Collection;

public interface RxDealReadPlatformService {

    public RxData template();
    public Collection<RxDealData> retreiveAll();
    public RxDealData retreiveOne(String key);
    public RxDealData retreiveOne(Long id);

}
