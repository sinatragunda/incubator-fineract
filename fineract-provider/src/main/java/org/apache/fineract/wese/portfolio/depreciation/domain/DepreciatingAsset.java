/*

    Created by Sinatra Gunda
    At 8:29 PM on 9/25/2021

*/
package org.apache.fineract.wese.portfolio.depreciation.domain;

import org.apache.fineract.infrastructure.codes.data.CodeData;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.wese.enumerations.DURATION_TYPE;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Table(name="m_depreciating_asset")
public class DepreciatingAsset extends AbstractPersistableCustom<Long>{

    @Column(name="account_number" ,nullable=true)
    private String accountNumber ;

    @Column(name="name")
    private String name ;

    @Column(name="serial_number")
    private String serialNumber ;

    @Column(name="supplier")
    private String supplier ;

    @Column(name="commisioned_date")
    private Date commisionedDate ;

    @Column(name="asset_cost")
    private BigDecimal assetCost;

    @Column(name="total_accumulated_depreciation")
    private BigDecimal totalAccumulatedDepreciation;

    @Column(name="asset_type")
    private CodeData assetType ;

    @Column(name="location")
    private String location ;

    @Column(name="condition")
    private String condition ;

    @Column(name="duration")
    private Integer usefullLife;

    @Column(name="accrual_period")
    private DURATION_TYPE accrualPeriod ;


    @Column(name="salvage_value")
    private BigDecimal salvageValue ;


    public DepreciatingAsset(){}

    public DepreciatingAsset(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public DepreciatingAsset(String accountNumber, String description, String serialNumber, String supplier, Date commisionedDate, BigDecimal assetCost, BigDecimal totalAccumulatedDepreciation, CodeData assetType, String location, String condition, Integer usefullLife, DURATION_TYPE accrualPeriod ,BigDecimal salvageValue) {
        this.accountNumber = accountNumber;
        this.description = description;
        this.serialNumber = serialNumber;
        this.supplier = supplier;
        this.commisionedDate = commisionedDate;
        this.assetCost = assetCost;
        this.totalAccumulatedDepreciation = totalAccumulatedDepreciation;
        this.assetType = assetType;
        this.location = location;
        this.condition = condition;
        this.usefullLife = usefullLife;
        this.accrualPeriod = accrualPeriod;
        this.salvageValue = salvageValue ;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }

    public String getCondition() {
        return condition;
    }

    public void setCondition(String condition) {
        this.condition = condition;
    }

    public BigDecimal getAssetCost() {
        return assetCost;
    }

    public void setAssetCost(BigDecimal assetCost) {
        this.assetCost = assetCost;
    }

    public BigDecimal getTotalAccumulatedDepreciation() {
        return totalAccumulatedDepreciation;
    }

    public void setTotalAccumulatedDepreciation(BigDecimal totalAccumulatedDepreciation) {
        this.totalAccumulatedDepreciation = totalAccumulatedDepreciation;
    }

    public CodeData getAssetType() {
        return assetType;
    }

    public void setAssetType(CodeData assetType) {
        this.assetType = assetType;
    }

    public Date getCommisionedDate() {
        return commisionedDate;
    }

    public void setCommisionedDate(Date commisionedDate) {
        this.commisionedDate = commisionedDate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getUsefullLife() {
        return usefullLife;
    }

    public void setUsefullLife(Integer usefullLife) {
        this.usefullLife = usefullLife;
    }

    public DURATION_TYPE getAccrualPeriod() {
        return accrualPeriod;
    }

    public void setAccrualPeriod(DURATION_TYPE accrualPeriod) {
        this.accrualPeriod = accrualPeriod;
    }

    public BigDecimal getSalvageValue() {
        return salvageValue;
    }

    public void setSalvageValue(BigDecimal salvageValue) {
        this.salvageValue = salvageValue;
    }
}
