/*

    Created by Sinatra Gunda
    At 11:25 AM on 8/2/2021

*/
package org.apache.fineract.portfolio.savings.repo;

import org.apache.fineract.portfolio.savings.domain.ProductBinding;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;


public class ProductBindingRepository extends JpaRepository<ProductBinding, Long>,
        JpaSpecificationExecutor<ProductBinding> {

    List<ProductBinding> findByProductBindingRootId(Long id);

}
