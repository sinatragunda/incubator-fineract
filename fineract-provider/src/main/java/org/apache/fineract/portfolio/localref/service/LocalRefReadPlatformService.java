/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 December 2022 at 16:35
 */
package org.apache.fineract.portfolio.localref.service;

import org.apache.fineract.portfolio.localref.data.LocalRefData;
import org.apache.fineract.portfolio.localref.data.LocalRefValueData;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;

import java.util.Collection;

public interface LocalRefReadPlatformService {

    public LocalRefData template(REF_TABLE refTable);
    public Collection<LocalRefData> retrieveAll(Long officeId);

    public Collection<LocalRefValueData> retrieveRecord(REF_TABLE refTable , Long recordId);


}
