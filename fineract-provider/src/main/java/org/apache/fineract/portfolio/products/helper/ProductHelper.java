/*

    Created by Sinatra Gunda
    At 9:43 AM on 12/18/2021

*/
package org.apache.fineract.portfolio.products.helper;

import org.apache.fineract.portfolio.products.data.ProductData;
import org.apache.fineract.portfolio.products.domain.Product;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;
import org.apache.fineract.portfolio.products.service.ProductWritePlatformService;

public class ProductHelper {


    public static Product createProduct(ProductWritePlatformService productWritePlatformService , PRODUCT_TYPE productType , Long productId){
        return productWritePlatformService.createProduct(productType.getCode() ,productId);
    }

}
