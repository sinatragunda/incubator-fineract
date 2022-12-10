/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 December 2022 at 01:29
 */
package org.apache.fineract.portfolio.localref.repo;

import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface LocalRefRepository extends JpaRepository<LocalRef, Long>, JpaSpecificationExecutor<LocalRef> {

}
