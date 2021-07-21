/*

    Created by Sinatra Gunda
    At 4:54 PM on 7/21/2021

*/
package org.apache.fineract.portfolio.savings.repo;

import org.apache.fineract.portfolio.savings.domain.EquityGrowthDividends;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface EquityGrowthDividendsRepository extends JpaRepository<EquityGrowthDividends ,Long> ,JpaSpecificationExecutor<EquityGrowthDividends>{

    EquityGrowthDividends findOneBySavingsProductId(Long id);
}
