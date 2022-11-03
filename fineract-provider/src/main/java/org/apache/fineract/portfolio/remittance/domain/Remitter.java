
package org.apache.fineract.portfolio.remittance.domain;

import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.remittance.enumerations.IDENTIFICATION_TYPE;
import org.apache.fineract.portfolio.remittance.enumerations.RX_PROVIDER;

import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
public class Remitter{

	@Column(name="phone_number")
	private String phoneNumber ;

	@Column(name="nid")
	private String nationalIdentification;

	@Column(name="identification_type")
	private IDENTIFICATION_TYPE identificationType;

	@Column(name ="provider")
	private RX_PROVIDER provider ;

	/**
	 * Recieve location
	 */
	@Column(name="location")
	private String location ;

	@Column(name="client_id")
	private Client client;

	public Remitter(){}

	public String getPhoneNumber() {
		return phoneNumber;
	}

	public void setPhoneNumber(String phoneNumber) {
		this.phoneNumber = phoneNumber;
	}

	public String getNationalIdentification() {
		return nationalIdentification;
	}

	public void setNationalIdentification(String nationalIdentification) {
		this.nationalIdentification = nationalIdentification;
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