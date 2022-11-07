package org.apache.fineract.portfolio.remittance.constants;

import org.apache.fineract.portfolio.client.api.ClientApiConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class RxDealConstants {

    public static String RESOURCE_NAME = "RXDEAL";
    private static String providerParam = "provider";
    private static String identificationTypeParam = "identificationType";
    private static String savingsAccountsParam = "savingsAccount";
    private static String rxData = "rxData";
    public static String createNewClientParam = "isCreateClient";

    public static String isClientParam = "isClient";
    public static String nidParam = "nid";
    public static String currencyParam = "currencyId";
    public static String providerIdParam = "providerId";
    public static String amountParam = "amount";
    public static String payinAccountParam = "payinAccountId";
    public static String senderNameParam = "senderName";
    public static String senderEmailAddressParam = "senderEmailAddress";
    public static String senderPhoneNumberParam = "senderPhoneNumber";
    public static String receiverNameParam = "receiverName";
    public static String receiverPhoneNumberParam = "receiverPhoneNumber";
    public static String transactionDateParam = "transactionDate";
    public static String nidTypeParam = "identificationType";
    public static String keyIdParam ="key";

    public static final Set<String> RX_DEAL_PARAMETERS = new HashSet<>(Arrays.asList(createNewClientParam,providerParam ,identificationTypeParam ,savingsAccountsParam ,rxData ,receiverPhoneNumberParam ,receiverNameParam ,currencyParam ,senderNameParam ,senderPhoneNumberParam ,senderEmailAddressParam ,payinAccountParam ,providerIdParam ,amountParam ,isClientParam ,nidParam , ClientApiConstants.emailAddressParamName ,transactionDateParam ,nidTypeParam ,keyIdParam ,providerIdParam ,rxData));

}
