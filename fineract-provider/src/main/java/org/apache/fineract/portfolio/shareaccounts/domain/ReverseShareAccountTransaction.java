/*

    Created by Sinatra Gunda
    At 8:04 AM on 3/27/2022

*/
package org.apache.fineract.portfolio.shareaccounts.domain;

import java.util.Date;

public class ReverseShareAccountTransaction {

    private Long id ;
    private Date transactionDate ;
    private String locale = "en";
    private String dateFormat = "dd MMM yyyy";


    public ReverseShareAccountTransaction(Long id, Date transactionDate) {
        this.id = id;
        this.transactionDate = transactionDate;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(Date transactionDate) {
        this.transactionDate = transactionDate;
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
