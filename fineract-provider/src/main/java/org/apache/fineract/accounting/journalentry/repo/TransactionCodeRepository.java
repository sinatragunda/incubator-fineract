/*

    Created by Sinatra Gunda
    At 12:14 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.repo;


import org.apache.fineract.accounting.journalentry.domain.TransactionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface TransactionCodeRepository extends JpaRepository<TransactionCode, Long>, JpaSpecificationExecutor<TransactionCode>{

    @Query("select transactionCode from TransactionCode transactionCode where transactionCode.creditAccount.id =:creditAccountId and transactionCode.debitAccount.id=:debitAccountId")
    public List<TransactionCode> getByGLAccounts(@Param("debitAccountId")Long debitAccountId , @Param("creditAccountId") Long creditAccountId);
    
}
