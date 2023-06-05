/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 May 2023 at 07:16
 */
package org.apache.fineract.portfolio.ssbpayments.domain;


import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.presentation.menu.domain.MenuTable;
import org.taat.wese.weseaddons.ssb.enumerations.PORTFOLIO_TYPE;
import org.taat.wese.weseaddons.ssb.pojo.ExcelClientData;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;


@Entity
@Table(name = "m_ssb_transaction")
public class SsbTransaction extends AbstractPersistableCustom<Long>{

    @Column(name = "object_id")
    private Long objectId ;

    @Column(name = "transaction_id")
    private Long transactionId ;

    @Column(name = "timestamp")
    private Long timestamp ;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "portfolio_type")
    private PORTFOLIO_TYPE portfolioType;

    @ManyToOne
    @JoinColumn(name = "ssb_transaction_record_id")
    private SsbTransactionRecord ssbTransactionRecord;

    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "amount")
    private BigDecimal amount;


    protected  SsbTransaction(){}

    public SsbTransaction(Long objectId, Long transactionId,Long timestamp, PORTFOLIO_TYPE portfolioType ,SsbTransactionRecord ssbTransactionRecord) {
        this.objectId = objectId;
        this.transactionId = transactionId;
        this.portfolioType = portfolioType;
        this.ssbTransactionRecord = ssbTransactionRecord;
        this.timestamp = timestamp;
    }

    public SsbTransaction(ExcelClientData excelClientData ,SsbTransactionRecord ssbTransactionRecord){
        this.objectId = excelClientData.getObjectId();
        this.transactionId = excelClientData.getResourceId();
        this.timestamp = excelClientData.getTimestamp();
        this.portfolioType = excelClientData.getPortfolioType();
        this.client =  excelClientData.getClient();
        this.ssbTransactionRecord = ssbTransactionRecord;
        this.amount = excelClientData.getAmount();
    }

    public Long getObjectId() {
        return objectId;
    }

    public Long getTransactionId() {
        return transactionId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public PORTFOLIO_TYPE getPortfolioType() {
        return portfolioType;
    }
}
