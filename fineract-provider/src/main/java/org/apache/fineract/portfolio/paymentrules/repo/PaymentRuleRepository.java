/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 21 March 2023 at 10:33
 */
package org.apache.fineract.portfolio.paymentrules.repo;

import org.apache.fineract.portfolio.paymentrules.domain.PaymentRule;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface PaymentRuleRepository extends JpaRepository<PaymentRule, Long>, JpaSpecificationExecutor<PaymentRule> {

}
