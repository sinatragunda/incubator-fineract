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
package org.apache.fineract.portfolio.accountdetails.data;

import java.math.BigDecimal;
import java.util.Optional;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.loanaccount.data.LoanApplicationTimelineData;
import org.apache.fineract.portfolio.loanaccount.data.LoanStatusEnumData;

/**
 * Immutable data object for loan accounts.
 */
@SuppressWarnings("unused")
public class LoanAccountSummaryData {

    private final Long id;
    private final String accountNo;
    private final String externalId;
    private final Long productId;
    private final String productName;
    private final String shortProductName;
    private final LoanStatusEnumData status;
    private final EnumOptionData loanType;
    private final Integer loanCycle;
    private final LoanApplicationTimelineData timeline;
    private final Boolean inArrears;
    private final BigDecimal originalLoan;
    private final BigDecimal loanBalance;
    private final BigDecimal amountPaid;

    // added 30/03/2022
    private final BigDecimal accruedInterest ;

    // added 31/03/2022
    private final BigDecimal loanBalanceWithAccruedInterest ;
    
    public LoanAccountSummaryData(final Long id, final String accountNo, final String externalId, final Long productId,
            final String loanProductName, final String shortLoanProductName, final LoanStatusEnumData loanStatus, final EnumOptionData loanType, final Integer loanCycle,
            final LoanApplicationTimelineData timeline, final Boolean inArrears,final BigDecimal originalLoan,final BigDecimal loanBalance,final BigDecimal amountPaid ,final BigDecimal accruedInterest) {
        this.id = id;
        this.accountNo = accountNo;
        this.externalId = externalId;
        this.productId = productId;
        this.productName = loanProductName;
        this.shortProductName = shortLoanProductName;
        this.status = loanStatus;
        this.loanType = loanType;
        this.loanCycle = loanCycle;
        this.timeline = timeline;
        this.inArrears = inArrears;
        this.loanBalance = loanBalance;
        this.originalLoan = originalLoan;
        this.amountPaid = amountPaid;
        this.accruedInterest = accruedInterest ;
        this.loanBalanceWithAccruedInterest = loanBalanceWithAccruedInterest();
    }

    public boolean isActive(){
        return status.isActive();

    }

    public Long id(){
        return this.id;
    }

    public Long productId(){
        return this.productId;
    }

    public BigDecimal loanBalanceWithAccruedInterest(){

        System.err.println("--------------what is null here ? -----------");

        Boolean isVal = Optional.ofNullable(originalLoan).isPresent();

        BigDecimal balance = BigDecimal.ZERO ;

        if(isVal){

            Boolean isVal2 = Optional.ofNullable(accruedInterest).isPresent();
            //BigDecimal balance = originalLoan.add(accruedInterest);
            System.err.println("---------orginal loan is "+originalLoan);
            if(isVal2){
                balance = originalLoan.add(accruedInterest);
            }

            Boolean isVal3 = Optional.ofNullable(amountPaid).isPresent();

            if(isVal3){
                System.err.println("-----------------amount paid is "+accruedInterest);
                balance = balance.subtract(amountPaid);
            }

            System.err.println("------------------------l take error throw now -----------?"+isVal+"---------------------val 2 rpesent "+isVal2);
            //return balance.subtract(amountPaid);
        }
        return balance ;
    }

}