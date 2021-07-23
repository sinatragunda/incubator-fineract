
package org.apache.fineract.portfolio.savings.domain ;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import java.math.BigDecimal ;
import java.util.Date ;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;


@Entity
@Table(name = "m_savings_account_monthly_deposit")
public class SavingsAccountMonthlyDeposit extends AbstractPersistableCustom<Long>{

	@Column(name="savings_account_id" ,nullable=false)
	private Long savingsAccountId;
  	
  	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="start_date" ,nullable=false)
	private Date startDate ;
	
	@Column(name="amount" ,nullable=false)
	private BigDecimal amount ;


	protected SavingsAccountMonthlyDeposit(){}

	public SavingsAccountMonthlyDeposit(Long savingsAccountId ,Date startDate ,BigDecimal amount){
		this.savingsAccountId = savingsAccountId ;
		this.startDate = startDate ;
		this.amount =amount ;
	}

	public Long getSavingsAccountId() {
		return savingsAccountId;
	}

	public void setStartDate(Date date){
		this.startDate = date;
	}

	// public void setId(Long id){
	// 	this.id = id ;
	// }

	public void setAmount(BigDecimal amount){
		this.amount = amount;
	}

	public void setSavingsAccountId(Long id){
		this.savingsAccountId = id;
	}

	public Date getStartDate(){
		return this.startDate;
	}

	public BigDecimal getAmount(){
		return this.amount;
	}

	// public Long id(){
	// 	return this.id;
	// }

}