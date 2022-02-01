/*

    Created by Sinatra Gunda
    At 12:25 PM on 1/31/2022

*/
package org.apache.fineract.portfolio.shareaccounts.domain;

import org.apache.fineract.infrastructure.core.data.CommandProcessingResultBuilder;
import org.apache.fineract.portfolio.note.domain.Note;
import org.apache.fineract.portfolio.paymentdetail.domain.PaymentDetail;
import org.joda.time.LocalDate;

import java.math.BigDecimal;

public interface ShareAccountDomainService {

    ShareAccountTransaction purchaseShares(final ShareAccount shareAccount, final CommandProcessingResultBuilder builderResult,final LocalDate transactionDate, final BigDecimal transactionAmount, final PaymentDetail paymentDetail, final Note note,
                                                  final String txnExternalId, boolean isAccountTransfer);

}

