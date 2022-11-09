
package org.apache.fineract.portfolio.remittance.domain;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.domain.AbstractPersistableCustom;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.remittance.constants.RxDealConstants;
import org.apache.fineract.portfolio.remittance.enumerations.IDENTIFICATION_TYPE;
import org.apache.fineract.portfolio.remittance.enumerations.RX_DEAL_STATUS;
import org.apache.fineract.portfolio.remittance.enumerations.RX_PROVIDER;
import org.apache.fineract.portfolio.remittance.exceptions.RxDealClosedUpdateException;
import org.apache.fineract.portfolio.remittance.helper.RxDealKeyHelper;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.JoinColumn;

import java.math.BigDecimal;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.joda.time.LocalDate ;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.*;



@Entity
@Table(name="m_rx_deal")
public class RxDeal extends AbstractPersistableCustom<Long> {

	@Column(name="nid_type")
	@Enumerated(EnumType.ORDINAL)
	private IDENTIFICATION_TYPE identificationType = IDENTIFICATION_TYPE.NONE;

	@Column(name ="provider")
	@Enumerated(EnumType.ORDINAL)
	private RX_PROVIDER provider ;

	/**
	 * Recieve location
	 */
	@Column(name="location" ,nullable=true)
	private String location ;

	@JoinColumn(name="client_id" ,nullable=false)
	@ManyToOne(optional = true, fetch=FetchType.LAZY)
	private Client client;

	@JoinColumn(name="savings_account_transaction_id")
	@ManyToOne(optional = true, fetch=FetchType.LAZY)
	private SavingsAccountTransaction savingsAccountTransaction;

	@Column(name="amount")
	private BigDecimal amount ;

	@ManyToOne(optional = true, fetch=FetchType.LAZY)
	@JoinColumn(name ="office_id")
	private Office office ;

	@Column(name ="currency_code")
	private String currencyCode;

	@Column(name ="transaction_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date transactionDate;

	@Column(name ="receiver_name")
	private String receiverName ;

	@Column(name ="receiver_phone_number")
	private String receiverPhoneNumber ;

	@Column(name ="status")
	@Enumerated(EnumType.ORDINAL)
	private RX_DEAL_STATUS rxDealStatus;

	@Column(name ="is_reversed")
	private boolean reversed = false;

	@Column(name ="rx_key")
	private String key ;

	@Column(name ="total_charges")
	private BigDecimal totalCharges;

	@Column(name ="receiver_email_address")
	private String receiverEmailAddress;


	public RxDeal(){}

	public RxDeal(IDENTIFICATION_TYPE identificationType, RX_PROVIDER provider, String location, Client client, SavingsAccountTransaction savingsAccountTransaction, BigDecimal amount, Office office,String currencyCode, Date transactionDate, String receiverName, String receiverPhoneNumber ,RX_DEAL_STATUS rxDealStatus ,String receiverEmailAddress) {
		this.identificationType = identificationType;
		this.provider = provider;
		/**
		 * Added 05/11/2022 st 0658
		 * Null value for now
		 */
		this.location = null;
		this.client = client;
		this.savingsAccountTransaction = savingsAccountTransaction;
		this.amount = amount;
		this.office = office;
		this.currencyCode = currencyCode;
		this.transactionDate = transactionDate;
		this.receiverName = receiverName;
		this.receiverPhoneNumber = receiverPhoneNumber;
		this.rxDealStatus = rxDealStatus;
		this.key = null ;
		this.receiverEmailAddress = receiverEmailAddress;
	}

	public  void initDeal(){
		setRxDealStatus(RX_DEAL_STATUS.OPENED);
		setReversed(false);
		this.key = RxDealKeyHelper.generateKey(currencyCode);
		//setKey(key);
		System.err.println("------------key is -----------"+this.key);
	}

	public Map update(JsonCommand command){

		Map<String ,Object> actualChanges = new HashMap<>();

		if (command.isChangeInStringParameterNamed(RxDealConstants.currencyParam, this.getCurrency())) {
			final String newValue = command.stringValueOfParameterNamed(RxDealConstants.currencyParam);
			actualChanges.put(RxDealConstants.currencyParam, newValue);
			setCurrency(newValue);
		}
		if (command.isChangeInStringParameterNamed(RxDealConstants.receiverNameParam, this.getReceiverName())) {
			final String newValue = command.stringValueOfParameterNamed(RxDealConstants.receiverNameParam);
			actualChanges.put(RxDealConstants.receiverNameParam, newValue);
			setReceiverName(newValue);
		}
		if (command.isChangeInStringParameterNamed(RxDealConstants.receiverPhoneNumberParam, this.getReceiverPhoneNumber())) {
			final String newValue = command.stringValueOfParameterNamed(RxDealConstants.receiverPhoneNumberParam);
			actualChanges.put(RxDealConstants.receiverPhoneNumberParam, newValue);
			setReceiverPhoneNumber(newValue);
		}


		if (command.isChangeInStringParameterNamed(RxDealConstants.receiverEmailAddressParam, this.receiverEmailAddress)) {
			System.err.println("-----------------is there any change registered  on email address son ?");
			final String newValue = command.stringValueOfParameterNamed(RxDealConstants.receiverEmailAddressParam);
			actualChanges.put(RxDealConstants.receiverEmailAddressParam, newValue);
			this.receiverEmailAddress = newValue ;
		}

		if (command.isChangeInLongParameterNamed(ClientApiConstants.clientIdParamName, this.getClient().getId())) {
			final Long newValue = command.longValueOfParameterNamed(ClientApiConstants.clientIdParamName);
			actualChanges.put(ClientApiConstants.clientIdParamName, newValue);
			//setReceiverPhoneNumber(newValue);
		}

		if (command.isChangeInStringParameterNamedWithCase(RxDealConstants.rxDealStatusParam, this.rxDealStatus.name())) {

			final String newValue = command.stringValueOfParameterNamed(RxDealConstants.rxDealStatusParam);
			actualChanges.put(RxDealConstants.rxDealStatusParam, newValue);
			RX_DEAL_STATUS rxDealStatus = RX_DEAL_STATUS.fromString(newValue);

			validateRxDealState(rxDealStatus);
			setRxDealStatus(rxDealStatus);
		}
		return actualChanges;
	}

	/**
	 * Added 09/11/2022 at 0721
	 * Should not be able to open a closed transaction
	 */
	private void validateRxDealState(RX_DEAL_STATUS newRxDealStatus){
		if(this.rxDealStatus.ordinal() == RX_DEAL_STATUS.CLOSED.ordinal()){
			throw new RxDealClosedUpdateException();
		}
	}

	public String getReceiverEmailAddress() {
		return receiverEmailAddress;
	}

	public BigDecimal getTotalCharges() {
		return totalCharges;
	}

	public void setTotalCharges(BigDecimal totalCharges) {
		this.totalCharges = totalCharges;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public boolean isReversed() {
		return reversed;
	}

	public void setReversed(boolean reversed) {
		this.reversed = reversed;
	}

	public void setRxDealStatus(RX_DEAL_STATUS rxDealStatus) {
		this.rxDealStatus = rxDealStatus;
	}

	public RX_DEAL_STATUS getRxDealStatus() {
		return rxDealStatus;
	}

	public SavingsAccountTransaction getSavingsAccountTransaction() {
		return savingsAccountTransaction;
	}

	public void setSavingsAccountTransaction(SavingsAccountTransaction savingsAccountTransaction) {
		this.savingsAccountTransaction = savingsAccountTransaction;
	}

	public BigDecimal getAmount() {
		return amount;
	}

	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}

	public Office getOffice() {
		return office;
	}

	public void setOffice(Office office) {
		this.office = office;
	}

	public String getCurrency() {
		return currencyCode;
	}

	public void setCurrency(String currencyCode) {
		this.currencyCode = currencyCode;
	}

	public Date getTransactionDate() {
		return transactionDate;
	}

	public void setTransactionDate(Date transactionDate) {
		this.transactionDate = transactionDate;
	}

	public String getReceiverName() {
		return receiverName;
	}

	public void setReceiverName(String receiverName) {
		this.receiverName = receiverName;
	}

	public String getReceiverPhoneNumber() {
		return receiverPhoneNumber;
	}

	public void setReceiverPhoneNumber(String receiverPhoneNumber) {
		this.receiverPhoneNumber = receiverPhoneNumber;
	}

	public IDENTIFICATION_TYPE getIdentificationType() {
		return identificationType;
	}

	public void setIdentificationType(IDENTIFICATION_TYPE identificationType) {
		this.identificationType = identificationType;
	}

	public RX_PROVIDER getProvider() {
		return provider;
	}

	public void setProvider(RX_PROVIDER provider) {
		this.provider = provider;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public Client getClient() {
		return client;
	}

	public void setClient(Client client) {
		this.client = client;
	}
}