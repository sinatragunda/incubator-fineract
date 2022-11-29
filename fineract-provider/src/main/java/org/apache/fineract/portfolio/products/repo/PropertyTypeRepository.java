/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 29 November 2022 at 00:34
 */
package org.apache.fineract.portfolio.products.repo;


import org.apache.fineract.portfolio.products.domain.PropertyType;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.Collection;

public interface PropertyTypeRepository extends JpaRepository<PropertyType, Long>, JpaSpecificationExecutor<PropertyType>{

    public Collection<PropertyType> findAllByProductId(Long productId);

}
