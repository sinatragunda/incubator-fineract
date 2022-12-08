/*

    Created by Sinatra Gunda
    At 9:05 AM on 12/18/2021

*/
package org.apache.fineract.portfolio.products.domain;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.products.constants.ProductConstants;
import org.apache.fineract.portfolio.products.enumerations.ACCOUNT_TYPE;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.*;

import org.joda.time.LocalDate;
import com.google.gson.JsonArray;

/**
 * Modified 05/09/2022
 * All additional product settings would be inserted here to avoid cluttering the product account
 */

@Entity
@Table(name = "m_product")
public class Product extends AbstractPersistableCustom<Long> {

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "product_type", nullable = false)
    protected PRODUCT_TYPE productType;

    @Column(name = "product_id",nullable=false)
    private Long productId;

    @Column(name = "active" ,nullable =false)
    private Boolean active;

    /**
     * Added 05/09/2022
     * Enables charges to be deducted on transaction amount instead of savings account balance 
     * If false charges are deducted on transaction amount 
     */
    @Column(name = "deduct_charges_on_balance" ,nullable =false)
    private Boolean deductChargesOnAccountBalance;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "account_type", nullable = false ,columnDefinition = "short default 0")
    protected ACCOUNT_TYPE accountType;

    /**
     * Added 29/11/2022 at 0017
     */

    protected Product(){}

    public Product(PRODUCT_TYPE productType, Long productId, Boolean active ,Boolean deductChargesOnBalance ,ACCOUNT_TYPE accountType) {
        this.productType = productType;
        this.productId = productId;
        this.active = active;
        this.deductChargesOnAccountBalance = deductChargesOnBalance;
        this.accountType = accountType;
    }

    public Map<String, Object> update(final JsonCommand command) {

        final Map<String, Object> actualChanges = new LinkedHashMap<>(10);

        final String localeAsInput = command.locale();

        if(command.isChangeInBooleanParameterNamed(ProductConstants.active, this.active)) {
            final boolean newValue = command.booleanObjectValueOfParameterNamed(ProductConstants.active);
            actualChanges.put(ProductConstants.active, newValue);
            this.active = newValue;
        }

        if (command.isChangeInBooleanParameterNamed(ProductConstants.deductChargesOnBalance, this.deductChargesOnAccountBalance)) {

            final boolean newValue = command.booleanObjectValueOfParameterNamed(ProductConstants.deductChargesOnBalance);
            
            System.err.println("-------------value changed here to "+newValue);

            actualChanges.put(ProductConstants.deductChargesOnBalance, newValue);
            
            this.deductChargesOnAccountBalance = newValue;
        }

        if (command.isChangeInIntegerParameterNamed(ProductConstants.accountTypeParam, this.accountType.ordinal())) {
            final Integer newValue = command.integerValueOfParameterNamed(ProductConstants.accountTypeParam);
            actualChanges.put(ProductConstants.accountTypeParam, newValue);
            this.accountType = ACCOUNT_TYPE.fromInt(newValue);
        }

        System.err.println("----------------------how many actual changes from this ? ");

        return actualChanges;
    }

    public Boolean getDeductChargesOnAccountBalance() {
        return deductChargesOnAccountBalance;
    }

    public void setDeductChargesOnAccountBalance(Boolean deductChargesOnAccountBalance) {
        this.deductChargesOnAccountBalance = deductChargesOnAccountBalance;
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

    public Boolean isActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public Boolean getActive() {
        return active;
    }

    public Boolean isDeductChargesOnAccountBalance() {
        return deductChargesOnAccountBalance;
    }
}
