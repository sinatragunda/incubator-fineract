/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 08 March 2023 at 08:26
 */
package org.apache.fineract.infrastructure.dataqueries.repo;

import org.apache.fineract.infrastructure.dataqueries.domain.ApplicationRecord;
import org.apache.fineract.infrastructure.dataqueries.domain.HybridTableEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


import java.util.List ;
public interface HybridTableEntityRepository extends JpaRepository<HybridTableEntity, Long>, JpaSpecificationExecutor<HybridTableEntity> {

    @Query("select h from HybridTableEntity h where h.abstractPersistableCustom.id= :id")
    List<HybridTableEntity> findByAbstractPersistableCustomId(@Param("id") Long id);
}
