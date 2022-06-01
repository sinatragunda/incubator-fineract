/*

    Created by Sinatra Gunda
    At 12:41 AM on 5/31/2022

*/
package org.apache.fineract.portfolio.loanproduct.domain;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface LoanProductSettingsRepository extends JpaRepository<LoanProductSettings, Long>, JpaSpecificationExecutor<LoanProductSettings>{

}
