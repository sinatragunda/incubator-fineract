/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 29 November 2022 at 00:18
 */
package org.apache.fineract.portfolio.products.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.products.enumerations.PRODUCT_TYPE;
import org.apache.fineract.portfolio.products.enumerations.PROPERTY_TYPE;

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

@Entity
@Table(name="m_property_type")
public class PropertyType extends AbstractPersistableCustom<Long> {
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = true)
    private Product product ;

    @Enumerated(EnumType.ORDINAL)
    @Column(name = "property_type", nullable = false)
    protected PROPERTY_TYPE propertyType;

    protected PropertyType(){}

    public PropertyType(Product product, PROPERTY_TYPE propertyType) {
        this.product = product;
        this.propertyType = propertyType;
    }

    public Product getProduct() {
        return product;
    }

    public PROPERTY_TYPE getPropertyType() {
        return propertyType;
    }
}
