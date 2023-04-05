/**
 * Added 12/10/2022 at 1414
 */ 

package org.apache.fineract.portfolio.charge.domain;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.generic.helper.GenericConstants;
import org.apache.fineract.portfolio.charge.api.ChargesApiConstants;
import org.apache.fineract.portfolio.charge.domain.Charge;
import org.apache.fineract.portfolio.charge.enumerations.TIER_TYPE;
import org.apache.fineract.utility.helper.EnumTemplateHelper;
import org.apache.fineract.wese.helper.JsonCommandHelper;

import java.math.BigDecimal;
import java.util.*;


import javax.persistence.Transient;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.persistence.*;

import com.google.gson.JsonObject;
import com.google.gson.JsonElement ;
import com.google.gson.JsonArray ;

@Entity
@Table(name ="m_charge_tier")
public class ChargeTier extends AbstractPersistableCustom<Long> {

	@Column(name="overlapping")
	private Boolean overlapping ;
	@Column(name="amount")
	private BigDecimal amount ;

	@Column(name="min_tier")
	private BigDecimal minTier ;

	@Column(name="max_tier")
	private BigDecimal maxTier ;

	@Enumerated(EnumType.ORDINAL)
	@Column(name="tier_type")
	private TIER_TYPE tierType ;

 	@ManyToOne
    @JoinColumn(name ="charge_id" ,nullable=false)
	private Charge charge ;

	protected ChargeTier(){}

	public ChargeTier(Boolean overlapping, BigDecimal amount, BigDecimal minTier, BigDecimal maxTier, TIER_TYPE tierType, Charge charge) {
		this.overlapping = overlapping;
		this.amount = amount;
		this.minTier = minTier;
		this.maxTier = maxTier;
		this.tierType = tierType;
		this.charge = charge;
	}

	public static ChargeTier fromJson(JsonCommand jsonCommand){

		final BigDecimal amount = jsonCommand.bigDecimalValueOfParameterNamed(GenericConstants.amountParam);
		final BigDecimal minTier = jsonCommand.bigDecimalValueOfParameterNamed(ChargesApiConstants.minTierParam);
		final BigDecimal maxTier = jsonCommand.bigDecimalValueOfParameterNamed(ChargesApiConstants.maxTierParam);
		final Boolean overlapping = jsonCommand.booleanPrimitiveValueOfParameterNamed(ChargesApiConstants.overlappingParam);
		final int tierTypeInt = jsonCommand.integerValueOfParameterNamed(ChargesApiConstants.tierTypeParam);

		final TIER_TYPE tierType = (TIER_TYPE) EnumTemplateHelper.fromInt(TIER_TYPE.values() ,tierTypeInt);
		ChargeTier chargeTier = new ChargeTier(overlapping,amount ,minTier ,maxTier ,tierType ,null);
		return chargeTier;
	}

	public static List<ChargeTier> fromJson(FromJsonHelper fromJsonHelper,JsonCommand command){
		final JsonArray chargeTierArray = command.arrayOfParameterNamed(ChargesApiConstants.chargeTiersParam);
		List<ChargeTier> chargeTierList = new ArrayList<>();
		for(JsonElement element : chargeTierArray){
			JsonCommand jsonCommand = JsonCommandHelper.fromJsonElement(fromJsonHelper ,element);
			ChargeTier chargeTier = fromJson(jsonCommand);
			chargeTierList.add(chargeTier);
		}
		return chargeTierList;

	}
}

