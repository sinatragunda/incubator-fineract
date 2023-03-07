/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 07 March 2023 at 02:18
 */
package org.apache.fineract.infrastructure.dataqueries.repo;


import org.apache.fineract.infrastructure.dataqueries.domain.ApplicationRecord;
import org.apache.fineract.infrastructure.dataqueries.domain.XRegisteredTable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


import org.springframework.data.jpa.repository.Query;

import org.springframework.data.repository.query.Param;

public interface XRegisteredTableRepository extends JpaRepository<XRegisteredTable, Long>, JpaSpecificationExecutor<XRegisteredTable> {

    @Query("select x from XRegisteredTable x where x.registeredTableName = :registeredTableName")
    public XRegisteredTable findByRegisteredTableName(@Param("registeredTableName")String registeredTableName);
}
