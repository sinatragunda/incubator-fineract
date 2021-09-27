/*

    Created by Sinatra Gunda
    At 6:39 AM on 9/26/2021

*/
package org.apache.fineract.wese.portfolio.depreciation.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="m_depreciating_asset_transaction")
public class DepreciatingAssetTransactions extends AbstractPersistableCustom<Long>{

    @Column(name="transaction_date")
    private Date transactionDate ;

    @Column(name="charge")
    private BigDecimal depreciationCharge ;

    @ManyToOne(name="depreciating_asset_id")
    private DepreciatingAsset depreciatingAsset;

    public DepreciatingAssetTransactions(){}

}
