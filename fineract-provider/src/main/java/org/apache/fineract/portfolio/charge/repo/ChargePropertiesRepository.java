/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 16 February 2023 at 03:52
 */
package org.apache.fineract.portfolio.charge.repo;

import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeProperties;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChargePropertiesRepository extends JpaRepository<ChargeProperties, Long>, JpaSpecificationExecutor<Charge> {
    // no added behaviour
}
