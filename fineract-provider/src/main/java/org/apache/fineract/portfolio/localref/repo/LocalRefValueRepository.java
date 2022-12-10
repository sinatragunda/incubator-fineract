/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 December 2022 at 02:03
 */
package org.apache.fineract.portfolio.localref.repo;
import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.domain.LocalRefValue;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LocalRefValueRepository extends JpaRepository<LocalRefValue, Long>, JpaSpecificationExecutor<LocalRef> {

}

