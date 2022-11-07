/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 November 2022 at 01:27
 */
package org.apache.fineract.portfolio.remittance.helper;

import org.apache.fineract.commands.service.PortfolioCommandSourceWritePlatformService;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.client.helper.CreateClientHelper;
import org.apache.fineract.portfolio.client.service.ClientWritePlatformService;
import org.apache.fineract.portfolio.remittance.data.RxDealData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class RxDealDataHelper {

    private ClientWritePlatformService clientWritePlatformService;
    private ClientRepositoryWrapper clientRepositoryWrapper;
    private PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService;


    @Autowired
    public RxDealDataHelper(ClientWritePlatformService clientWritePlatformService ,final PortfolioCommandSourceWritePlatformService portfolioCommandSourceWritePlatformService ,final ClientRepositoryWrapper clientRepositoryWrapper) {
        this.clientWritePlatformService = clientWritePlatformService;
        this.portfolioCommandSourceWritePlatformService = portfolioCommandSourceWritePlatformService;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
    }


    public Client createOrGetClient(RxDealData rxDealData){

        boolean isCreateClient = rxDealData.isCreateClient();
        Client client = null;

        if(isCreateClient){

            String clientName = rxDealData.getSenderName();
            String emailAddress = rxDealData.getEmailAddress();
            String nid = rxDealData.getNid();

            String name = tokenizeData(clientName ,"",0);
            String surname = tokenizeData(clientName,"",1);

            System.err.println("--------------------activation date "+rxDealData.getTransactionDate());

            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMM yyyy");

            String transactionDate = simpleDateFormat.format(rxDealData.getTransactionDate());

            System.err.println("---------------activation date string "+transactionDate);

            Map<String ,Object> map = new HashMap<>();
            map.put("emailAddress" ,emailAddress);
            map.put("externalId" ,nid);
            map.put("firstname" ,name);
            map.put("lastname" ,surname);
            map.put("officeId" ,rxDealData.getOfficeId());
            map.put("emailAddress" ,rxDealData.getEmailAddress());
            map.put("dateOfBirth" ,transactionDate);
            map.put("locale" ,"en");
            map.put("dateFormat" ,"dd MMM yyyy");
            map.put("active" ,true);
            map.put("activationDate",transactionDate);
            map.put("submittedOnDate" ,transactionDate);

            Long clientId = CreateClientHelper.createClient(portfolioCommandSourceWritePlatformService ,map);
            //rxDealData.setClient(cli);

            client = clientRepositoryWrapper.findOneWithNotFoundDetection(clientId);

            Optional.ofNullable(client).ifPresent(e->{
                //rxDealData.setClient(e);
            });

            return client;
        }

        Long clientId = rxDealData.getClientId();
        client = clientRepositoryWrapper.findOneWithNotFoundDetection(clientId,true);
        return client ;
    }

    private String tokenizeData(String data ,String delim ,int index){
        StringTokenizer tokenizer = new StringTokenizer(data ,delim);
        int i =0 ;
        while (tokenizer.hasMoreTokens()){
            String value = tokenizer.nextToken();
            if(i > tokenizer.countTokens()){
                return null;
            }
            if(i==index){
                return value;
            }
        }
        return null ;
    }
}
