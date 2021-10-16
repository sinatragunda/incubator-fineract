/*

    Created by Sinatra Gunda
    At 8:19 AM on 10/16/2021

*/
package org.apache.fineract.portfolio.shareaccounts.domain;

import org.joda.time.LocalDate;
import javax.persistence.Transient;

public class ApplyAdditionalShares {

    private Long sharesAccountId ;
    private Integer requestedShares ;
    private LocalDate requestedDate ;
    private Integer rowIndex ;
    private String locale = "en";
    private String dateFormat = "dd MMMM yyyy";

    public ApplyAdditionalShares(Long sharesAccountId, Integer requestedShares, LocalDate requestedDate, Integer rowIndex) {
        this.sharesAccountId = sharesAccountId;
        this.requestedShares = requestedShares;
        this.requestedDate = requestedDate;
        this.rowIndex = rowIndex;
    }

    public Long getSharesAccountId() {
        return sharesAccountId;
    }

    public void setSharesAccountId(Long sharesAccountId) {
        this.sharesAccountId = sharesAccountId;
    }

    public Integer getRequestedShares() {
        return requestedShares;
    }

    public void setRequestedShares(Integer requestedShares) {
        this.requestedShares = requestedShares;
    }

    public LocalDate getRequestedDate() {
        return requestedDate;
    }

    public void setRequestedDate(LocalDate requestedDate) {
        this.requestedDate = requestedDate;
    }

    public Integer getRowIndex() {
        return rowIndex;
    }

    public void setRowIndex(Integer rowIndex) {
        this.rowIndex = rowIndex;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public void setDateFormat(String dateFormat) {
        this.dateFormat = dateFormat;
    }
}
