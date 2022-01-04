/*

    Created by Sinatra Gunda
    At 11:51 AM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.organisation.monetary.data.CurrencyData;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Collection;
import java.util.List;

public class CommissionChargeData implements Comparable<CommissionChargeData> ,Serializable{


    private final Long id;
    private final String name;
    private final boolean active;
    private final CurrencyData currency;
    private final BigDecimal amount;
    private final EnumOptionData chargeTimeType;
    private final EnumOptionData chargeAppliesTo;
    private final EnumOptionData chargeCalculationType;

    private final Collection<CurrencyData> currencyOptions;
    private final List<EnumOptionData> chargeCalculationTypeOptions;//
    private final List<EnumOptionData> chargeAppliesToOptions;//
    private final List<EnumOptionData> chargeTimeTypeOptions;//


    @Override
    public int compareTo(CommissionChargeData o) {
        return 0;
    }

    public CommissionChargeData(Long id, String name, boolean active, CurrencyData currency, BigDecimal amount, EnumOptionData chargeTimeType, EnumOptionData chargeAppliesTo, EnumOptionData chargeCalculationType, Collection<CurrencyData> currencyOptions, List<EnumOptionData> chargeCalculationTypeOptions, List<EnumOptionData> chargeAppliesToOptions, List<EnumOptionData> chargeTimeTypeOptions) {
        this.id = id;
        this.name = name;
        this.active = active;
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

    public static CommissionChargeData template(Collection<CurrencyData> currencyOptions, List<EnumOptionData> chargeCalculationTypeOptions, List<EnumOptionData> chargeAppliesToOptions, List<EnumOptionData> chargeTimeTypeOptions) {
        return new CommissionChargeData(null ,null ,false ,null ,null ,null ,null ,null ,currencyOptions,chargeCalculationTypeOptions ,chargeAppliesToOptions , chargeTimeTypeOptions);

    }

}
