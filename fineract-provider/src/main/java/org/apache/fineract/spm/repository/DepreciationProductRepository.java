/*

    Created by Sinatra Gunda
    At 11:33 AM on 9/19/2021

*/
package org.apache.fineract.spm.repository;

import com.sun.tracing.dtrace.DependencyClass;
import org.apache.fineract.wese.portfolio.depreciation.domain.DepreciationProduct;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public interface DepreciationProductRepository extends JpaRepository<DepreciationProduct,Long> ,JpaSpecificationExecutor<DepreciationProduct>{

    DepreciationProduct findOne(Long id);

}
