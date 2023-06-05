/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 31 May 2023 at 07:11
 */
package org.apache.fineract.portfolio.ssbpayments.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.taat.wese.weseaddons.ssb.enumerations.PORTFOLIO_TYPE;

import java.math.BigDecimal;

public class SsbTransactionData {
    private Long id;
    private Long objectId ;
    private Long transactionId;
    private String clientName;
    private BigDecimal amount ;
    private PORTFOLIO_TYPE portfolioType;
    private EnumOptionData portfolioTypeData ;

    public SsbTransactionData(Long id, Long objectId, Long transactionId, String clientName, BigDecimal amount, PORTFOLIO_TYPE portfolioType) {
        this.id = id;
        this.objectId = objectId;
        this.transactionId = transactionId;
        this.clientName = clientName;
        this.amount = amount;
        this.portfolioType = portfolioType;
        this.portfolioTypeData = EnumTemplateHelper.template(portfolioType);
    }

}
