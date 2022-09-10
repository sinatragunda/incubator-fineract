/*

    Created by Sinatra Gunda
    At 12:14 PM on 9/7/2022

*/
package org.apache.fineract.accounting.journalentry.repo;


import org.apache.fineract.accounting.journalentry.domain.TransactionCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface TransactionCodeRepository extends JpaRepository<TransactionCode, Long>, JpaSpecificationExecutor<TransactionCode>{

}
