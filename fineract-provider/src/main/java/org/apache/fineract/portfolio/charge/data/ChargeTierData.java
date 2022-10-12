
package org.apache.fineract.portfolio.charge.data;

import java.math.BigDecimal;

public class ChargeTierData{


	private Long chargeId;
	private Long id ;
	private BigDecimal amount ;
	private BigDecimal upperLimit;


	public ChargeTierData(Long chargeId, Long id, BigDecimal amount, BigDecimal upperLimit) {
		this.chargeId = chargeId;
		this.id = id;
		this.amount = amount;
		this.upperLimit = upperLimit;
	}

	public Long getChargeId() {
		return chargeId;
	}

	public void setChargeId(Long chargeId) {
		this.chargeId = chargeId;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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
}