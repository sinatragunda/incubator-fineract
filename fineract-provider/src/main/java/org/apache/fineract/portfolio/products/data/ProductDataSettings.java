/*

    Created by Sinatra Gunda
    At 9:56 AM on 10/3/2022

*/
package org.apache.fineract.portfolio.products.data;

import org.apache.fineract.portfolio.products.enumerations.ACCOUNT_TYPE;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;

/**
 * Non interface class
 */
public class ProductDataSettings {

    private PRODUCT_TYPE productType ;
    private Long productId;
    private Boolean deductChargesOnAccountBalance;
    private ACCOUNT_TYPE accountType;
    private boolean active ;

    public ProductDataSettings(PRODUCT_TYPE productType, Long productId, Boolean deductChargesOnAccountBalance ,ACCOUNT_TYPE accountType ,boolean isActive) {
        this.productType = productType;
        this.productId = productId;
        this.deductChargesOnAccountBalance = deductChargesOnAccountBalance;
        this.accountType =accountType;
        this.active = isActive;
    }

    public boolean isActive() {
        return active;
    }

    public ACCOUNT_TYPE getAccountType() {
        return accountType;
    }

    public void setAccountType(ACCOUNT_TYPE accountType) {
        this.accountType = accountType;
    }

    public PRODUCT_TYPE getProductType() {
        return productType;
    }

    public void setProductType(PRODUCT_TYPE productType) {
        this.productType = productType;
    }

    public Long getProductId() {
        return productId;
    }

    public void setProductId(Long productId) {
        this.productId = productId;
    }

    public Boolean getDeductChargesOnAccountBalance() {
        return deductChargesOnAccountBalance;
    }

    public void setDeductChargesOnAccountBalance(Boolean deductChargesOnAccountBalance) {
        this.deductChargesOnAccountBalance = deductChargesOnAccountBalance;
    }
}
