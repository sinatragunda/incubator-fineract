/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 November 2022 at 01:25
 */
package org.apache.fineract.portfolio.remittance.data;

import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.portfolio.client.data.ClientData;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.remittance.domain.RxDeal;
import org.apache.fineract.portfolio.remittance.enumerations.IDENTIFICATION_TYPE;
import org.apache.fineract.portfolio.remittance.enumerations.RX_DEAL_STATUS;
import org.apache.fineract.portfolio.remittance.enumerations.RX_PROVIDER;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.joda.time.LocalDate;

import java.math.BigDecimal;
import java.util.Date;

public class RxDealData {

    private Long id ;
    private String nid ;
    private String emailAddress ;
    private String senderName ;
    private String senderPhoneNumber ;
    private String receiverName ;
    private String receiverPhoneNumber;
    private IDENTIFICATION_TYPE identificationType = IDENTIFICATION_TYPE.NONE ;
    private RX_PROVIDER rxProvider ;
    private boolean createClient;
    private Client client;
    private Date transactionDate ;

    /**
     * If we create new client will need this info
     */
    private Date dateOfBirth;
    private Long officeId ;
    private BigDecimal amount ;
    private Long payinAccountId ;
    private Long clientId ;
    private String currencyCode;

    private Office office ;
    private String officeName ;
    private Long savingsAccountTransactionId;
    private RX_DEAL_STATUS rxDealStatus ;

    private String key ;
    public RxDealData(){}

    public RxDealData(String nid, String emailAddress, String senderName, String senderPhoneNumber, String receiverName, String receiverPhoneNumber, IDENTIFICATION_TYPE identificationType, RX_PROVIDER rxProvider, boolean createClient, Long clientId, Date transactionDate, Long officeId, BigDecimal amount, Long payinAccountId ,String currencyCode ,Office office) {

        System.err.println("--------start function well now -------------");
        this.nid = nid;
        this.emailAddress = emailAddress;
        this.senderName = senderName;
        this.senderPhoneNumber = senderPhoneNumber;
        this.receiverName = receiverName;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.identificationType = identificationType;

        System.err.println("-------------identification type passed ");

        this.rxProvider = rxProvider;
        this.createClient = createClient;
        this.clientId = clientId;
        this.transactionDate = transactionDate;

        System.err.println("--------------passed transaction date ");
        this.officeId = officeId;
        this.amount = amount;
        this.payinAccountId = payinAccountId;
        this.currencyCode = currencyCode;
        this.office = office ;

        System.err.println("-------------what the f is null here ?");
    }

    public RxDealData(Long id ,String emailAddress, String senderName, String senderPhoneNumber, String receiverName, String receiverPhoneNumber, RX_PROVIDER rxProvider, LocalDate transactionDate, Long officeId, BigDecimal amount, Long payinAccountId, Long clientId, String currencyCode, String officeName , Long savingsAccountTransactionId , RX_DEAL_STATUS rxDealStatus ,String key) {
        this.id = id ;
        this.emailAddress = emailAddress;
        this.senderName = senderName;
        this.senderPhoneNumber = senderPhoneNumber;
        this.receiverName = receiverName;
        this.receiverPhoneNumber = receiverPhoneNumber;
        this.rxProvider = rxProvider;
        this.transactionDate = DateUtils.fromLocalDate(transactionDate);
        this.officeId = officeId;
        this.amount = amount;
        this.payinAccountId = payinAccountId;
        this.clientId = clientId;
        this.currencyCode = currencyCode;
        this.officeName = officeName;
        this.savingsAccountTransactionId = savingsAccountTransactionId;
        this.rxDealStatus = rxDealStatus;
        this.key = key ;
    }


    public Long getId() {
        return id;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public Office getOffice() {
        return office;
    }

    public String getOfficeName() {
        return officeName;
    }

    public Long getSavingsAccountTransactionId() {
        return savingsAccountTransactionId;
    }

    public Date getDateOfBirth() {
        return dateOfBirth;
    }

    public RX_DEAL_STATUS getRxDealStatus() {
        return rxDealStatus;
    }

    public void setRxDealStatus(RX_DEAL_STATUS rxDealStatus) {
        this.rxDealStatus = rxDealStatus;
    }

    public void setDateOfBirth(Date dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Client getClient() {
        return client;
    }

    public void setClient(Client client) {
        this.client = client;
    }

    public Long getPayinAccountId() {
        return payinAccountId;
    }

    public Long getOfficeId() {
        return officeId;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public boolean isCreateClient() {
        return createClient;
    }

    public Date getTransactionDate() {
        return transactionDate;
    }

    public String getNid() {
        return nid;
    }

    public void setCreateClient(boolean createClient) {
        this.createClient = createClient;
    }

    public Long getClientId() {
        return clientId;
    }

    public void setClientId(Long clientId) {
        this.clientId = clientId;
    }

    public String getEmailAddress() {
        return emailAddress;
    }
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderPhoneNumber() {
        return senderPhoneNumber;
    }

    public void setSenderPhoneNumber(String senderPhoneNumber) {
        this.senderPhoneNumber = senderPhoneNumber;
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

    public RX_PROVIDER getRxProvider() {
        return rxProvider;
    }

    public void setRxProvider(RX_PROVIDER rxProvider) {
        this.rxProvider = rxProvider;
    }

    public RxDeal rxDeal(){
        return new RxDeal(identificationType ,rxProvider,null ,client ,null ,amount ,office ,currencyCode ,transactionDate ,receiverName ,receiverPhoneNumber ,null);
    }
}
