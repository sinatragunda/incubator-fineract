/**
 * Added 12/10/2022 at 1414
 */ 

package org.apache.fineract.portfolio.charge.domain;

import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.portfolio.charge.domain.Charge;

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


@Entity
@Table(name ="m_charge_tier")
public class ChargeTier extends AbstractPersistableCustom<Long> {
	@Column(name="amount")
	private BigDecimal amount ;

	@Column(name="upper_limit")
	private BigDecimal upperLimit ;

 	@ManyToOne
    @JoinColumn(name = "charge_id")
	private Charge charge ;

	protected ChargeTier(){}


	public ChargeTier(Charge charge ,BigDecimal amount ,BigDecimal upperLimit){
		this.charge = charge ;
		this.amount = amount ;
		this.upperLimit = upperLimit;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public BigDecimal getUpperLimit() {
		return upperLimit;
	}

	public void setUpperLimit(BigDecimal upperLimit) {
		this.upperLimit = upperLimit;
	}

	public Charge getCharge() {
		return charge;
	}

	public void setCharge(Charge charge) {
		this.charge = charge;
	}
}

