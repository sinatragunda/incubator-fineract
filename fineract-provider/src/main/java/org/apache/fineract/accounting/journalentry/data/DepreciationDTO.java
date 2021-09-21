/*

    Created by Sinatra Gunda
    At 7:45 AM on 9/14/2021

*/
package org.apache.fineract.accounting.journalentry.data;


import java.util.List;

public class DepreciationDTO {

    private Long depreciationAssetId;
    private Long depreciationProductId;
    private String currencyCode;
    private boolean cashBasedAccountingEnabled;
    private boolean accrualBasedAccountingEnabled;
    private List<DepreciationTransactionDTO> depreciationTransactions;


    public DepreciationDTO(Long depreciationAssetId, Long depreciationProductId, String currencyCode, boolean cashBasedAccountingEnabled, boolean accrualBasedAccountingEnabled, List<DepreciationTransactionDTO> depreciationTransactions) {
        this.depreciationAssetId = depreciationAssetId;
        this.depreciationProductId = depreciationProductId;
        this.currencyCode = currencyCode;
        this.cashBasedAccountingEnabled = cashBasedAccountingEnabled;
        this.accrualBasedAccountingEnabled = accrualBasedAccountingEnabled;
        this.depreciationTransactions = depreciationTransactions;
    }

    public Long getDepreciationAssetId() {
        return depreciationAssetId;
    }

    public void setDepreciationAssetId(Long depreciationAssetId) {
        this.depreciationAssetId = depreciationAssetId;
    }

    public Long getDepreciationProductId() {
        return depreciationProductId;
    }

    public void setDepreciationProductId(Long depreciationProductId) {
        this.depreciationProductId = depreciationProductId;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public boolean isCashBasedAccountingEnabled() {
        return cashBasedAccountingEnabled;
    }

    public void setCashBasedAccountingEnabled(boolean cashBasedAccountingEnabled) {
        this.cashBasedAccountingEnabled = cashBasedAccountingEnabled;
    }

    public boolean isAccrualBasedAccountingEnabled() {
        return accrualBasedAccountingEnabled;
    }

    public void setAccrualBasedAccountingEnabled(boolean accrualBasedAccountingEnabled) {
        this.accrualBasedAccountingEnabled = accrualBasedAccountingEnabled;
    }

    public List<DepreciationTransactionDTO> getDepreciationTransactions() {
        return depreciationTransactions;
    }

    public void setDepreciationTransactions(List<DepreciationTransactionDTO> depreciationTransactions) {
        this.depreciationTransactions = depreciationTransactions;
    }
}
