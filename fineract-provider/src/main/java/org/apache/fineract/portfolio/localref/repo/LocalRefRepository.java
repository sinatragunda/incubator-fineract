/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 December 2022 at 01:29
 */
package org.apache.fineract.portfolio.localref.repo;

import org.apache.fineract.portfolio.localref.domain.LocalRef;
import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;


import java.util.Collection;

public interface LocalRefRepository extends JpaRepository<LocalRef, Long>, JpaSpecificationExecutor<LocalRef> {

    @Query("select lr from LocalRef lr where lr.refTable= :refTable")
    Collection<LocalRef> findByRefTable(@Param("refTable") REF_TABLE refTable);

    @Query("select lr from LocalRef lr where lr.name = :name")
    LocalRef findByName(@Param("name") String name);

}
