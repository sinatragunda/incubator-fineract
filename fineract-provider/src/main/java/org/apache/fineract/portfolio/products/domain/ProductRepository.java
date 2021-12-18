/*

    Created by Sinatra Gunda
    At 9:34 AM on 12/18/2021

*/
package org.apache.fineract.portfolio.products.domain;

import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProductRepository extends JpaRepository<Product, Long>, JpaSpecificationExecutor<Product>{


    public static final String FIND_BY_PRODUCT_TYPE_AND_PRODUCT_ID = "select p from Product p where p.productType = :productType and "
            + "p.productId = :productId";


    @Query(FIND_BY_PRODUCT_TYPE_AND_PRODUCT_ID)
    public Product findOneByProductTypeAndProductId(@Param("productType")Integer productType, @Param("productId") Long productId);



}
