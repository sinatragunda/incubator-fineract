/*

    Created by Sinatra Gunda
    At 9:56 AM on 10/3/2022

*/
package org.apache.fineract.portfolio.products.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.products.enumerations.ACCOUNT_TYPE;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;

import java.util.Collection;
import java.util.List;

/**
 * Non interface class
 */
public class ProductDataSettings {
    //private PRODUCT_TYPE productType ;
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


    public static ProductDataSettings template(){
        System.err.println("-----------why are we initialixing this template ? ---------");
        return new ProductDataSettings(ACCOUNT_TYPE.template() ,PRODUCT_TYPE.template());
    }



    public ProductDataSettings(Collection accountTypeOptions , Collection productTypeOptions){
        this.productTypeOptions = productTypeOptions ;
        this.accountTypeOptions = accountTypeOptions;
    }


    public ProductDataSettings(PRODUCT_TYPE productType, Long productId, Boolean deductChargesOnAccountBalance ,ACCOUNT_TYPE accountType ,boolean isActive) {
        System.err.println("--------------------setting product data now son --------------------"+deductChargesOnAccountBalance);
        //this.productType = productType;
        this.productId = productId;
        this.deductChargesOnAccountBalance = deductChargesOnAccountBalance;
        //this.accountType = accountType;
        this.active = isActive;

        System.err.println("--------------is active ? "+this.active);
        this.accountType = accountType.toEnumData();
        this.productType = productType.toEnumData();

        System.err.println("------------------now why we returning some old set of data ? ");

        this.accountTypeOptions = ACCOUNT_TYPE.template();
        this.productTypeOptions = PRODUCT_TYPE.template();

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
}
