/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 April 2023 at 01:57
 */
package org.apache.fineract.portfolio.charge.repo;

import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.domain.ChargeTier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ChargeTierRepository extends JpaRepository<ChargeTier, Long>, JpaSpecificationExecutor<ChargeTier> {
    // no added behaviour
}
