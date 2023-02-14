/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 February 2023 at 12:30
 */
package org.apache.fineract.infrastructure.dataqueries.repo;

import org.apache.fineract.infrastructure.dataqueries.domain.ApplicationRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ApplicationRecordRepository extends JpaRepository<ApplicationRecord, Long>, JpaSpecificationExecutor<ApplicationRecord> {

}
