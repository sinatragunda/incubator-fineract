
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
	
	@Column(name="deposit" ,nullable = false)
	private BigDecimal deposit = BigDecimal.ZERO;

	@Column(name="withdraw" ,nullable =false)
	private BigDecimal withdraw = BigDecimal.ZERO;


	@Column(name="opening_balance" ,nullable =false)
	private BigDecimal openingBalance = BigDecimal.ZERO;



	protected SavingsAccountMonthlyDeposit(){}

	public SavingsAccountMonthlyDeposit(Long savingsAccountId ,Date startDate ,BigDecimal amount ,boolean isDeposit ,BigDecimal openingBalance){
		this.savingsAccountId = savingsAccountId ;
		this.startDate = startDate ;
		if(isDeposit){
			deposit = amount;
		}
		else {
			withdraw = amount;
		}

		this.openingBalance = openingBalance;
	}

	public SavingsAccountMonthlyDeposit(Long savingsAccountId ,Date startDate ,BigDecimal deposit ,BigDecimal withdraw ,BigDecimal openingBalance){
		this.savingsAccountId = savingsAccountId ;
		this.startDate = startDate ;
		this.deposit = deposit;
		this.withdraw = withdraw;
		this.openingBalance = openingBalance;
	}

	public Long getSavingsAccountId() {
		return savingsAccountId;
	}

	public void setStartDate(Date date){
		this.startDate = date;
	}


	public BigDecimal getDeposit() {
		return deposit;
	}

	public void setDeposit(BigDecimal deposit) {
		this.deposit = deposit;
	}

	public BigDecimal getWithdraw() {
		return withdraw;
	}

	public void setWithdraw(BigDecimal withdraw) {
		this.withdraw = withdraw;
	}

	public void setSavingsAccountId(Long id){
		this.savingsAccountId = id;
	}

	public Date getStartDate(){
		return this.startDate;
	}

	public BigDecimal getOpeningBalance(){
		return this.openingBalance;
	}

	public void setOpeningBalance(BigDecimal openingBalance){
		this.openingBalance = openingBalance;
	}


}