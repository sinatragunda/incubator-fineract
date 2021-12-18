/*

    Created by Sinatra Gunda
    At 8:51 AM on 12/18/2021

*/
package org.apache.fineract.portfolio.products.helper;

import org.apache.fineract.portfolio.products.data.ProductData;
import org.apache.fineract.portfolio.products.domain.Product;
import org.apache.fineract.portfolio.products.domain.ProductRepository;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;

import java.util.Optional;

public class DisableEnableProductHelper {


    public static Product disableEnableProduct(ProductRepository productRepository , String type , Long productId){

         PRODUCT_TYPE productType = PRODUCT_TYPE.fromString(type);
         Product product[] = {null} ;

         Optional.ofNullable(productType).ifPresent(productType1->{

             Integer type1 = new Integer(productType1.ordinal());

             product[0] = productRepository.findOneByProductTypeAndProductId(type1 ,productId);

             // if product is null create it in that class there then create one since this is a late addition function

             // just invert the current status of the item
             boolean isActive = !product[0].isActive();
             product[0].setActive(isActive);

             productRepository.saveAndFlush(product[0]);

             System.err.println("-------------active status is ");
         });

         return product[0];

    }
}
