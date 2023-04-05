
package org.apache.fineract.portfolio.charge.data;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.portfolio.charge.enumerations.TIER_TYPE;
import org.apache.fineract.utility.helper.EnumTemplateHelper;

import java.math.BigDecimal;
import java.util.List;

public class ChargeTierData{

	private Long id ;
	private Long chargeId;
	private BigDecimal amount ;
	private BigDecimal minTier;
	private BigDecimal maxTier;
	private Boolean overlapping ;
	private TIER_TYPE tierType ;
	private EnumOptionData tierTypeData ;
	private List<EnumOptionData> tierTypeOptions ;

	public static ChargeTierData template(){
		List tierTypeOptions = EnumTemplateHelper.template(TIER_TYPE.values());
		return new ChargeTierData(tierTypeOptions);
	}
	public ChargeTierData(List<EnumOptionData> tierTypeOptions){
		this.tierTypeOptions = tierTypeOptions;
	}

	public ChargeTierData(Long id, Long chargeId, BigDecimal amount, BigDecimal minTier, BigDecimal maxTier, Boolean overlapping, TIER_TYPE tierType) {
		this.id = id;
		this.chargeId = chargeId;
		this.amount = amount;
		this.minTier = minTier;
		this.maxTier = maxTier;
		this.overlapping = overlapping;
		this.tierType = tierType;
		this.tierTypeData = EnumTemplateHelper.template(tierType);
	}

	public ChargeTierData(Long id, Long chargeId, BigDecimal amount, BigDecimal minTier, BigDecimal maxTier, Boolean overlapping, TIER_TYPE tierType, EnumOptionData tierTypeData, List<EnumOptionData> tierTypeOptions) {
		this.id = id;
		this.chargeId = chargeId;
		this.amount = amount;
		this.minTier = minTier;
		this.maxTier = maxTier;
		this.overlapping = overlapping;
		this.tierType = tierType;
		this.tierTypeData = tierTypeData;
		this.tierTypeOptions = tierTypeOptions;
	}
}