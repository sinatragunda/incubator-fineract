package org.apache.fineract.wese.helper ;


import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;


public class Constants {

    public static boolean homeTest = true ;

    public static String apiUrl = "https://160.119.102.66/fineract-provider/api/v1";


    public static String keystoreFilename = homeTest ?"C:\\Users\\HP\\Desktop\\.keystore":"C:\\Users\\Sinatra Gunda\\Desktop\\.keystore";
    public static String dateFormat = "d MMM yyyy";
    public static String database = "jdbc:mysql://localhost:3306/";
    public static String defaultUsername = "root";
    public static String defaultPassword = "mysql";
    public static String defaultDriverClassName = "com.mysql.jdbc.Driver";
    public static String zimbabweTimeZone = "Africa/Harare";
    public static String keystoreFileLocations[] = {"C:\\Users\\HP\\Desktop\\.keystore","//root//Desktop//wese-UI//.keystore" ,"C:\\Users\\keys\\.keystore" ,"//usr//share//tomcat7//webapps//.keystore"};
    public static String dateTimeFormat = "yyyy-MM-dd HH:mm:ss";
    public static String nonTradingHours = "This transaction can only be made during trading hours .Please wait or review your settings";
    public static String invalidDate = "From date comes before after date ,please try to correct";
    public static String isNotCashier = "Please note we have detected that you are perfoming a transaction from a non registered Cashier .Transactions are done through cashiers only";
    public static String hasSettled = "Transaction can't be perfomed as the requested amount is greater than balance in your till";
    public static String invalidTick = "You have created a currency with invalid tick or market symbol please try this notation EUR/USD .Exactly 3 letters or characters";
    public static String noLocalCurrency = "You have not set your local currency .Please go to Settings and set first as it is a requirement";
    public static String noMainTrade ="Main currency trade pair is not set ,please go to settings and select where written main trade trade";
    public static String failedUpdate ="Failed to update a record to the database";
    public static String failedCreate = "Failed to create new record ,please try again and check input data";
    public static String validateDealFailed = "Failed to validate this fx deal ,please make sure you have provided data for all the input fields editable .Common field is Purpose ,kindly check that";
    public static String dailyLimitReached = "Your organisation has reached it's daily fx trade limit .Please try again tomorrow";
    public static String failedAuthorization = "Failed to authorise this action ,your credentials are invalid";
    public static String noAvailableData ="No available data";
    public static String noValidSavingsAccount ="No valid savings account";
    public static String bdxDatabaseList[] = {"deal" ,"currency_pair" ,"trading_rates" ,"live_rates" ,"trading_currency","money_account" ,"account_charge_mapping" ,"blotter" ,"bulk_deal_mapper","cashier","charges","daily_tracker","equivalant","financial_instruments","funds_action" ,"live_rates","revaluation","settings","transactions","transaction_charge_mapping"};
    public static String  bdxClassNames[] = {"FxDeal","Blotter","CurrencyPair","Charges","FinancialInstrument","FundsAction","FxCashier","FxDailyTracker","FxEquivalent","FxHistory" ,"LiveRates","MoneyAccount" ,"MoneyAccountTransactions"  ,"MoneyAccountChargesRM","FxMarkUp","Settings","StandardCurrency","TradingCurrency","TradingRates","TransactionCharge"};
    public static String bdxTenantsList[] = {"demo"};
    public static String insufficientFunds = "Transaction can't be perfomed as the requested amount is greater than balance in your till";
    public static String failedTransaction = "Transaction has failed ,please check all parameters";
    public static String transactionNotFound = "Transaction has not been found ,please check the authenticity of your key";


    public static String taskNote ="Task Note Updated";
    public static String crbInfinityCode = "zm123456789";
    public static String crbUsername = "ZMDU_AFC";
    public static String crbPassword = "2$^Dc2$H23_b";
    public static String crbDateFormat = "ddMMyyyy";
    public static String crbSecureUrl = "https://secure3.crbafrica.com/duv2/data/zm/update/";

    public static String bereauDeChangeClients[] = {"demo"};
    public static String crbClients[] = {"altus"};
    public static String dataNotCollected = "Data Not Collected";
    public static String separator = "---------------------------------------------------------------------------------------------------------------------------------------------------------------";


    public static String basePackage = "com.wese.weseaddons.controller";


   

}
