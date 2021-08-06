/*

    Created by Sinatra Gunda
    At 10:22 AM on 8/2/2021

*/
package org.apache.fineract.portfolio.savings.domain;

import org.apache.fineract.portfolio.savings.enumerations.BINDING_PRODUCT_TYPE;


import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@Entity
@Table(name ="m_product_binding_mapper")
public class ProductBinding {


    @Column(name="product_binding_root_id")
    private ProductBindingRoot productBindingRoot;

    @Column( name="product_id")
    private Long productId ;

    @Column(name= "product_type")
    private BINDING_PRODUCT_TYPE bindingProductType;

    public ProductBinding(){}

    public ProductBindingRoot getProductBindingRoot() {
        return productBindingRoot;
    }

    public void setProductBindingRoot(ProductBindingRoot productBindingRoot) {
        this.productBindingRoot = productBindingRoot;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public BINDING_PRODUCT_TYPE getBindingProductType() {
        return bindingProductType;
    }

    public void setBindingProductType(BINDING_PRODUCT_TYPE bindingProductType) {
        this.bindingProductType = bindingProductType;
    }
}
