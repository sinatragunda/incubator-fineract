/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 December 2022 at 02:03
 */
package org.apache.fineract.portfolio.localref.repo;
import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.domain.LocalRefValue;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

import java.util.Collection;


public interface LocalRefValueRepository extends JpaRepository<LocalRefValue, Long>, JpaSpecificationExecutor<LocalRef> {

    @Query("select v from LocalRefValue v where v.recordId = :recordId")
    Collection<LocalRefValue> findByRecordId(@Param("recordId")Long recordId);
}

