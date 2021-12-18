/*

    Created by Sinatra Gunda
    At 10:34 AM on 12/18/2021

*/
package org.apache.fineract.portfolio.products.service;

import org.apache.fineract.portfolio.products.domain.Product;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;

public interface ProductWritePlatformService {

    Product disableEnableProduct(String productType ,Long productId);
    Product createProduct(String productType ,Long productId);

}
