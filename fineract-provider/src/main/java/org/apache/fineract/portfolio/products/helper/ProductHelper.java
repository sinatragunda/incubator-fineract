/*

    Created by Sinatra Gunda
    At 9:43 AM on 12/18/2021

*/
package org.apache.fineract.portfolio.products.helper;

import org.apache.fineract.portfolio.products.data.ProductData;
import org.apache.fineract.portfolio.products.domain.Product;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;
import org.apache.fineract.portfolio.products.service.ProductWritePlatformService;


// Added 19/12/2021

import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class ProductHelper {


    public static Product createProduct(ProductWritePlatformService productWritePlatformService , PRODUCT_TYPE productType , Long productId){
        return productWritePlatformService.createProduct(productType.getCode() ,productId);
    }

    public static void handleRequest(HttpServletRequest request ,HttpServletResponse response){

        /// handle some complex shit here
        /// get some id of product then check it status
        /// how do we know if its shares ,savings ,or ?
        String type[] = {"savings","loans","shares"};

        String path = request.getRequestURI();

        System.err.println("-----------------------path is ----------------"+path);

        PRODUCT_TYPE productType = null ;
        // usually contains only one of the three not all
        for(String pathSegment : type){
            boolean contain = path.contains(pathSegment);
            if(contain){
                productType = PRODUCT_TYPE.fromString(pathSegment);
                break;
            }
        }

        // how do we get id of item now ?

        System.err.println("------------------handle request filter");
    }

}
