/*

    Created by Sinatra Gunda
    At 4:50 PM on 7/21/2021

*/
package org.apache.fineract.portfolio.savings.repo;

import org.apache.fineract.portfolio.savings.domain.EquityGrowthOnSavingsAccount;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface EquityGrowthOnSavingsAccountRepository extends JpaRepository<EquityGrowthOnSavingsAccount ,Long> ,JpaSpecificationExecutor<EquityGrowthOnSavingsAccount>{

    EquityGrowthOnSavingsAccount findOneBySavingsAccountId(Long id);
    List<EquityGrowthOnSavingsAccount> findByEquityGrowthDividendsId(Long id);
    List<EquityGrowthOnSavingsAccount> findBySavingsAccountId(Long id);
}
