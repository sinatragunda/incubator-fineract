/*

    Created by Sinatra Gunda
    At 11:51 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;
import org.apache.fineract.portfolio.charge.domain.ChargeTimeType;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public class CommissionChargeData implements Comparable<CommissionChargeData> ,Serializable{

    private Long id;
    private String name;
    private boolean active;
    private String currencyCode ;
    private CurrencyData currency;
    private BigDecimal amount;
    private EnumOptionData chargeTimeType;
    private EnumOptionData chargeAppliesTo;
    private EnumOptionData chargeCalculationType;

    private Collection<CurrencyData> currencyOptions;
    private List<EnumOptionData> chargeCalculationTypeOptions;//
    private List<EnumOptionData> chargeAppliesToOptions;//
    private List<EnumOptionData> chargeTimeTypeOptions;//


    @Override
    public int compareTo(CommissionChargeData o) {
        return 0;
    }


    public CommissionChargeData(){}



    public CommissionChargeData(Long id, String name,String currencyCode , boolean active, BigDecimal amount, EnumOptionData chargeTimeType, EnumOptionData chargeAppliesTo, EnumOptionData chargeCalculationType) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.currencyCode = currencyCode ;
        this.currency = null;
        this.amount = amount;
        this.chargeTimeType = chargeTimeType;
        this.chargeAppliesTo = chargeAppliesTo;
        this.chargeCalculationType = chargeCalculationType;
        this.currencyOptions = null;
        this.chargeCalculationTypeOptions = null;
        this.chargeAppliesToOptions = null;
        this.chargeTimeTypeOptions = null;
    }


    public CommissionChargeData(Long id, String name,String currencyCode , boolean active, CurrencyData currency, BigDecimal amount, EnumOptionData chargeTimeType, EnumOptionData chargeAppliesTo, EnumOptionData chargeCalculationType, Collection<CurrencyData> currencyOptions, List<EnumOptionData> chargeCalculationTypeOptions, List<EnumOptionData> chargeAppliesToOptions, List<EnumOptionData> chargeTimeTypeOptions) {
        this.id = id;
        this.name = name;
        this.active = active;
        this.currencyCode = currencyCode ;
        this.currency = currency;
        this.amount = amount;
        this.chargeTimeType = chargeTimeType;
        this.chargeAppliesTo = chargeAppliesTo;
        this.chargeCalculationType = chargeCalculationType;
        this.currencyOptions = currencyOptions;
        this.chargeCalculationTypeOptions = chargeCalculationTypeOptions;
        this.chargeAppliesToOptions = chargeAppliesToOptions;
        this.chargeTimeTypeOptions = chargeTimeTypeOptions;
    }


    public CommissionChargeData(CommissionChargeData commissionChargeData ,CommissionChargeData template) {
        this.id = commissionChargeData.id;
        this.name = commissionChargeData.name;
        this.active = commissionChargeData.active;
        this.currencyCode = commissionChargeData.currencyCode ;
        this.currency = template.currency;
        this.amount = commissionChargeData.amount;
        this.chargeTimeType = commissionChargeData.chargeTimeType;
        this.chargeAppliesTo = commissionChargeData.chargeAppliesTo;
        this.chargeCalculationType = commissionChargeData.chargeCalculationType;
        this.currencyOptions = template.currencyOptions;
        this.chargeCalculationTypeOptions = template.chargeCalculationTypeOptions;
        this.chargeAppliesToOptions = template.chargeAppliesToOptions;
        this.chargeTimeTypeOptions = template.chargeTimeTypeOptions;
    }



    public static CommissionChargeData template(Collection<CurrencyData> currencyOptions, List<EnumOptionData> chargeCalculationTypeOptions, List<EnumOptionData> chargeAppliesToOptions, List<EnumOptionData> chargeTimeTypeOptions) {
        return new CommissionChargeData(null ,null ,null ,false ,null ,null ,null ,null ,null ,currencyOptions,chargeCalculationTypeOptions ,chargeAppliesToOptions , chargeTimeTypeOptions);

    }

    public static CommissionChargeData instance(Long id ,String name ,String currencyCode ,BigDecimal amount ,EnumOptionData chargeAppliesTo ,EnumOptionData chargeCalculationType ,EnumOptionData chargeTimeType ,boolean isActive){
        return new CommissionChargeData(id ,name  ,currencyCode ,isActive ,amount ,chargeTimeType ,chargeAppliesTo ,chargeCalculationType);
    }

    public static CommissionChargeData withTemplate(CommissionChargeData commissionChargeData ,CommissionChargeData template){
        return new CommissionChargeData(commissionChargeData ,template);
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
    }

    public void setCurrency(CurrencyData currency) {
        this.currency = currency;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public void setChargeTimeType(EnumOptionData chargeTimeType) {
        this.chargeTimeType = chargeTimeType;
    }

    public void setChargeAppliesTo(EnumOptionData chargeAppliesTo) {
        this.chargeAppliesTo = chargeAppliesTo;
    }

    public void setChargeCalculationType(EnumOptionData chargeCalculationType) {
        this.chargeCalculationType = chargeCalculationType;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public CurrencyData getCurrency() {
        return currency;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public EnumOptionData getChargeTimeType() {
        return chargeTimeType;
    }

    public EnumOptionData getChargeAppliesTo() {
        return chargeAppliesTo;
    }

    public EnumOptionData getChargeCalculationType() {
        return chargeCalculationType;
    }
}
