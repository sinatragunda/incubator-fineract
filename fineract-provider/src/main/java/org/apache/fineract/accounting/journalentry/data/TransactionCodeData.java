/*

    Created by Sinatra Gunda
    At 12:11 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.data;

import org.apache.fineract.accounting.journalentry.domain.TransactionCode;

public class TransactionCodeData {

    private Long id ;
    private Long code ;
    private String name ;
    private Long debitAccountId ;
    private Long creditAccountId ;
    private String debitAccountName;
    private String creditAccountName;

    public TransactionCodeData(){}

    public TransactionCodeData(Long id, Long code, String name, Long debitAccountId, Long creditAccountId, String debitAccountName, String creditAccountName) {
        this.id = id;
        this.code = code;
        this.name = name;
        this.debitAccountId = debitAccountId;
        this.creditAccountId = creditAccountId;
        this.debitAccountName = debitAccountName;
        this.creditAccountName = creditAccountName;
    }

    public static TransactionCodeData looup(Long id ,Long code ,String codeName){
        return new TransactionCodeData(id ,code ,codeName,null ,null ,null ,null);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getCode() {
        return code;
    }

    public void setCode(Long code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Long getCreditAccountId() {
        return creditAccountId;
    }

    public void setCreditAccountId(Long creditAccountId) {
        this.creditAccountId = creditAccountId;
    }

    public Long getDebitAccountId() {
        return debitAccountId;
    }

    public void setDebitAccountId(Long debitAccountId) {
        this.debitAccountId = debitAccountId;
    }
}
