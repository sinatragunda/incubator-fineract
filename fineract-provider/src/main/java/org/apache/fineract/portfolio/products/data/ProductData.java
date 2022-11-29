/*

    Created by Sinatra Gunda
    At 9:56 AM on 10/3/2022

*/
package org.apache.fineract.portfolio.products.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.products.enumerations.ACCOUNT_TYPE;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;
import org.apache.fineract.portfolio.products.enumerations.PROPERTY_TYPE;

import java.util.Collection;

/**
 * Non interface class
 */
public class ProductData {

    private Long id ;
    private Long productId;
    private Boolean deductChargesOnAccountBalance;
    //private ACCOUNT_TYPE accountType;
    private boolean active ;

    /**
     * Added 14/11/2022 at 0255
     */
    private EnumOptionData accountType ;
    private EnumOptionData productType ;
    private Collection<EnumOptionData> accountTypeOptions ;
    private Collection<EnumOptionData> productTypeOptions  ;

    /**
     * Added 29/11/2022 at 0009
     */
    private Collection<EnumOptionData> propertyTypeOptions ;
    private Collection<PropertyTypeData> propertyTypeDataCollection;

    public static ProductData template(){
        return new ProductData();
    }

    public ProductData(){
        this.productTypeOptions = PRODUCT_TYPE.template() ;
        this.accountTypeOptions = ACCOUNT_TYPE.template();
        this.propertyTypeOptions = PROPERTY_TYPE.template();
    }
    public ProductData(Long id ,Long productId ,PRODUCT_TYPE productType ,ACCOUNT_TYPE accountType , Boolean deductChargesOnAccountBalance , Boolean isActive , Collection<PropertyTypeData> propertyTypeDataCollection) {
        this.id = id ;
        this.productId = productId;
        this.deductChargesOnAccountBalance = deductChargesOnAccountBalance;
        this.active = isActive;
        this.accountType = accountType.toEnumData();
        this.productType = productType.toEnumData();

        this.accountTypeOptions = ACCOUNT_TYPE.template();
        this.productTypeOptions = PRODUCT_TYPE.template();

        this.propertyTypeDataCollection = propertyTypeDataCollection ;

    }

    public Long getId() {
        return id;
    }

    public Collection<EnumOptionData> getAccountTypeOptions() {
        return accountTypeOptions;
    }

    public Collection<EnumOptionData> getProductTypeOptions() {
        return productTypeOptions;
    }

    public boolean isActive() {
        return active;
    }


    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Boolean isDeductChargesOnAccountBalance() {
        return deductChargesOnAccountBalance;
    }

    public void setDeductChargesOnAccountBalance(Boolean deductChargesOnAccountBalance) {
        this.deductChargesOnAccountBalance = deductChargesOnAccountBalance;
    }

    public void setPropertyTypeDataCollection(Collection<PropertyTypeData> propertyTypeDataCollection) {
        this.propertyTypeDataCollection = propertyTypeDataCollection;
    }

    public Collection<PropertyTypeData> getPropertyTypeDataCollection() {
        return propertyTypeDataCollection;
    }
}
