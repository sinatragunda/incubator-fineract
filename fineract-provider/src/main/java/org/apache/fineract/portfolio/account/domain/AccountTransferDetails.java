/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.account.domain;

import java.beans.Transient;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.loanaccount.domain.Loan;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.shareaccounts.domain.ShareAccount;

@Entity
@Table(name = "m_account_transfer_details")
public class AccountTransferDetails extends AbstractPersistableCustom<Long> {

    @ManyToOne
    @JoinColumn(name = "from_office_id", nullable = false)
    private Office fromOffice;

    @ManyToOne
    @JoinColumn(name = "from_client_id", nullable = false)
    private Client fromClient;

    @ManyToOne
    @JoinColumn(name = "from_savings_account_id", nullable = true)
    private SavingsAccount fromSavingsAccount;

    @ManyToOne
    @JoinColumn(name = "to_office_id", nullable = false)
    private Office toOffice;

    @ManyToOne
    @JoinColumn(name = "to_client_id", nullable = false)
    private Client toClient;

    @ManyToOne
    @JoinColumn(name = "to_savings_account_id", nullable = true)
    private SavingsAccount toSavingsAccount;

    @ManyToOne
    @JoinColumn(name = "to_loan_account_id", nullable = true)
    private Loan toLoanAccount;

    @ManyToOne
    @JoinColumn(name = "from_loan_account_id", nullable = true)
    private Loan fromLoanAccount;

    @Column(name = "transfer_type")
    private Integer transferType;

    @OneToMany(cascade = CascadeType.ALL, mappedBy = "accountTransferDetails", orphanRemoval = true, fetch=FetchType.EAGER)
    private List<AccountTransferTransaction> accountTransferTransactions = new ArrayList<>();

    @OneToOne(mappedBy = "accountTransferDetails", cascade = CascadeType.ALL, optional = true, orphanRemoval = true, fetch = FetchType.EAGER)
    private AccountTransferStandingInstruction accountTransferStandingInstruction;

    // Added 29/12/2021 ,make it possible for equity accounts to act as savings account and make transfers
    @Column(name = "is_equity_transfer" ,nullable = true)
    private Boolean isEquityTransfer;


    // added 31/01/2022
    @ManyToOne
    @JoinColumn(name = "to_share_account_id", nullable = true)
    private ShareAccount toShareAccount;

    /**
     * Added 23/04/2023 at 1435
     */ 
    @ManyToOne
    @JoinColumn(name = "from_share_account_id", nullable = true)
    private ShareAccount fromShareAccount;



    public static AccountTransferDetails savingsToSavingsTransfer(final Office fromOffice, final Client fromClient,
            final SavingsAccount fromSavingsAccount, final Office toOffice, final Client toClient, final SavingsAccount toSavingsAccount,
            Integer transferType ,Boolean isEquityTransfer) {

        return new AccountTransferDetails(fromOffice, fromClient, fromSavingsAccount, null, toOffice, toClient, toSavingsAccount, null,
                transferType, null ,isEquityTransfer);
    }

    // added 31/01/2022    
    public static AccountTransferDetails savingsToShareTransfer(final Office fromOffice, final Client fromClient,
            final SavingsAccount fromSavingsAccount, final Office toOffice, final Client toClient, final ShareAccount toShareAccount,
            Integer transferType) {
        return new AccountTransferDetails(fromOffice, fromClient, fromSavingsAccount,toOffice, toClient, toShareAccount,
                transferType);
    }

    /**
     * Added 23/04/2023 at 1823
     */ 
    public static AccountTransferDetails shareToSavingsTransfer(final Office office, final Client client,
            final SavingsAccount toSavingsAccount, final ShareAccount fromShareAccount,
            Integer transferType) {
        return new AccountTransferDetails(office, client, toSavingsAccount,office, client, fromShareAccount,
                transferType);
    }

    public static AccountTransferDetails savingsToLoanTransfer(final Office fromOffice, final Client fromClient,
            final SavingsAccount fromSavingsAccount, final Office toOffice, final Client toClient, final Loan toLoanAccount,
            Integer transferType ,Boolean isEquityTransfer) {
        return new AccountTransferDetails(fromOffice, fromClient, fromSavingsAccount, null, toOffice, toClient, null, toLoanAccount,
                transferType, null ,isEquityTransfer);
    }

    public static AccountTransferDetails LoanTosavingsTransfer(final Office fromOffice, final Client fromClient,
            final Loan fromLoanAccount, final Office toOffice, final Client toClient, final SavingsAccount toSavingsAccount,
            Integer transferType ,Boolean isEquityTransfer) {
        return new AccountTransferDetails(fromOffice, fromClient, null, fromLoanAccount, toOffice, toClient, toSavingsAccount, null,
                transferType, null ,isEquityTransfer);
    }

    protected AccountTransferDetails() {
        //
    }

    private AccountTransferDetails(final Office fromOffice, final Client fromClient, final SavingsAccount fromSavingsAccount,
            final Loan fromLoanAccount, final Office toOffice, final Client toClient, final SavingsAccount toSavingsAccount,
            final Loan toLoanAccount, final Integer transferType,
            final AccountTransferStandingInstruction accountTransferStandingInstruction ,Boolean isEquityTransfer) {
        this.fromOffice = fromOffice;
        this.fromClient = fromClient;
        this.fromSavingsAccount = fromSavingsAccount;
        this.fromLoanAccount = fromLoanAccount;
        this.toOffice = toOffice;
        this.toClient = toClient;
        this.toSavingsAccount = toSavingsAccount;
        this.toLoanAccount = toLoanAccount;
        this.transferType = transferType;
        this.accountTransferStandingInstruction = accountTransferStandingInstruction;
        this.isEquityTransfer = isEquityTransfer ;
    }


    private AccountTransferDetails(final Office fromOffice, final Client fromClient, final SavingsAccount fromSavingsAccount,final Office toOffice, final Client toClient, final ShareAccount toShareAccount,final Integer transferType) {
        this.fromOffice = fromOffice;
        this.fromClient = fromClient;
        this.fromSavingsAccount = fromSavingsAccount;
        this.fromLoanAccount = null;
        this.toOffice = toOffice;
        this.toClient = toClient;
        this.toSavingsAccount = null;
        this.toLoanAccount = null;
        this.transferType = transferType;
        this.accountTransferStandingInstruction = null;
        this.isEquityTransfer = false ;
        this.toShareAccount = toShareAccount ;
    }


    private AccountTransferDetails(final Office office, final Client client, final SavingsAccount toSavingsAccount,final ShareAccount toShareAccount,final Integer transferType) {
        this.fromOffice = office;
        this.fromClient = client;
        this.fromSavingsAccount = null;
        this.fromLoanAccount = null;
        this.toOffice = office;
        this.toClient = client;
        this.toSavingsAccount = toSavingsAccount;
        this.toLoanAccount = null;
        this.transferType = transferType;
        this.accountTransferStandingInstruction = null;
        this.isEquityTransfer = false ;
        this.fromShareAccount = fromShareAccount ;
    }

    public SavingsAccount toSavingsAccount() {
        return this.toSavingsAccount;
    }

    public SavingsAccount fromSavingsAccount() {
        return this.fromSavingsAccount;
    }

    public void addAccountTransferTransaction(AccountTransferTransaction accountTransferTransaction) {
        this.accountTransferTransactions.add(accountTransferTransaction);
    }

    public void updateAccountTransferStandingInstruction(final AccountTransferStandingInstruction accountTransferStandingInstruction) {
        this.accountTransferStandingInstruction = accountTransferStandingInstruction;
    }

    public Loan toLoanAccount() {
        return this.toLoanAccount;
    }

    public Loan fromLoanAccount() {
        return this.fromLoanAccount;
    }

    public AccountTransferStandingInstruction accountTransferStandingInstruction() {
        return this.accountTransferStandingInstruction;
    }

    public AccountTransferType transferType() {
        return AccountTransferType.fromInt(this.transferType);
    }

    public static AccountTransferDetails LoanToLoanTransfer(Office fromOffice, Client fromClient, Loan fromLoanAccount, Office toOffice, Client toClient,
            Loan toLoanAccount, Integer transferType) {
        return new AccountTransferDetails(fromOffice, fromClient, null, fromLoanAccount, toOffice, toClient, null, toLoanAccount,
                transferType, null,false);
    }

    public Boolean getEquityTransfer() {
        return isEquityTransfer;
    }

    public void setEquityTransfer(Boolean equityTransfer) {
        isEquityTransfer = equityTransfer;
    }

    public ShareAccount toShareAccount(){
        return this.toShareAccount ;
    }


    public ShareAccount fromShareAccount(){
        return this.fromShareAccount ;
    }

    public List accountTransferTransactions(){
        return this.accountTransferTransactions;
    }
}