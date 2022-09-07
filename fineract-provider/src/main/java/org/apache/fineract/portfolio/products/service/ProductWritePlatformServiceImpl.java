/*

    Created by Sinatra Gunda
    At 10:38 AM on 12/18/2021

*/
package org.apache.fineract.portfolio.products.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.products.constants.ProductConstants;
import org.apache.fineract.portfolio.products.domain.Product;
import org.apache.fineract.portfolio.products.domain.ProductRepository;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;
import org.apache.fineract.portfolio.products.helper.DisableEnableProductHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.apache.commons.lang.StringUtils;

import java.util.Optional;


@Service
public class ProductWritePlatformServiceImpl implements ProductWritePlatformService {


    private final Logger logger ;
    private final ProductRepository productRepository;


    @Autowired
    public ProductWritePlatformServiceImpl(ProductRepository productRepository){
        this.productRepository = productRepository;
        this.logger = LoggerFactory.getLogger(ProductWritePlatformServiceImpl.class);

    }

    @Override
    public Product disableEnableProduct(String productType ,Long productId){
        return DisableEnableProductHelper.disableEnableProduct(productRepository ,productType ,productId);
    }


    @Override
    public Product createProduct(String productType ,Long productId){

        // just create new ones if nothing found
        PRODUCT_TYPE productType1 = PRODUCT_TYPE.fromString(productType);
        Product product = productRepository.findOneByProductTypeAndProductId(productType1 ,productId);

        boolean isPresent = Optional.ofNullable(product).isPresent();

        // Only create new entry if value not present
        if(!isPresent){
            System.err.println("-------------------create new only if record doesnt exist ---------------"+productId);
            product = new Product(productType1 ,productId ,true,true);
            productRepository.saveAndFlush(product);
        }

        return product ;
    }


    @Override
    public Product createProduct(JsonCommand jsonCommand, Long productId){

        // just create new ones if nothing found

        String productTypeArg = jsonCommand.stringValueOfParameterNamed(ProductConstants.productType);
        PRODUCT_TYPE productType = PRODUCT_TYPE.fromString(productTypeArg);

        Product product = productRepository.findOneByProductTypeAndProductId(productType,productId);

        Boolean deductChargesOnBalance = jsonCommand.booleanObjectValueOfParameterNamed(ProductConstants.deductChargesOnBalance);

        boolean isPresent = Optional.ofNullable(product).isPresent();

        // Only create new entry if value not present
        if(!isPresent){
            product = new Product(productType ,productId ,true,deductChargesOnBalance);
            productRepository.saveAndFlush(product);
        }

        return product ;
    }
}
