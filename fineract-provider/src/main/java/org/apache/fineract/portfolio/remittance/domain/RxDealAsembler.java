/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 04 November 2022 at 04:51
 */
package org.apache.fineract.portfolio.remittance.domain;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.service.DateUtils;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.organisation.office.domain.Office;
import org.apache.fineract.organisation.office.domain.OfficeRepositoryWrapper;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.client.domain.Client;
import org.apache.fineract.portfolio.client.domain.ClientRepositoryWrapper;
import org.apache.fineract.portfolio.remittance.constants.RxDealConstants;
import org.apache.fineract.portfolio.remittance.data.RxData;
import org.apache.fineract.portfolio.remittance.data.RxDealData;
import org.apache.fineract.portfolio.remittance.enumerations.IDENTIFICATION_TYPE;
import org.apache.fineract.portfolio.remittance.enumerations.RX_DEAL_STATUS;
import org.apache.fineract.portfolio.remittance.enumerations.RX_PROVIDER;
import org.apache.fineract.portfolio.remittance.exceptions.RxDealStatusNotOpenedException;
import org.apache.fineract.portfolio.remittance.repo.RxDealRepositoryWrapper;
import org.apache.fineract.portfolio.remittance.service.RxDealReadPlatformService;
import org.apache.fineract.portfolio.remittance.service.RxDealReadPlatformServiceImpl;
import org.apache.fineract.portfolio.savings.domain.SavingsAccount;
import org.apache.fineract.portfolio.savings.domain.SavingsAccountAssembler;

import org.apache.fineract.portfolio.savings.domain.SavingsAccountTransaction;
import org.apache.fineract.useradministration.domain.AppUser;
import org.apache.fineract.wese.helper.JsonCommandHelper;
import org.apache.fineract.wese.helper.JsonHelper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import org.joda.time.LocalDate ;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class RxDealAsembler {

    private final FromJsonHelper fromJsonHelper;
    private final SavingsAccountAssembler savingsAccountAssembler;
    private final ClientRepositoryWrapper clientRepositoryWrapper;
    private final RxDealReadPlatformService rxDealReadPlatformService;
    private final RxDealRepositoryWrapper rxDealRepositoryWrapper;
    private final OfficeRepositoryWrapper officeRepositoryWrapper;
    private final PlatformSecurityContext context;

    @Autowired
    public RxDealAsembler(FromJsonHelper fromJsonHelper, SavingsAccountAssembler savingsAccountAssembler, final ClientRepositoryWrapper clientRepositoryWrapper , final RxDealReadPlatformService rxDealReadPlatformService ,final  RxDealRepositoryWrapper rxDealRepositoryWrapper ,final  OfficeRepositoryWrapper officeRepositoryWrapper ,final PlatformSecurityContext context) {
        this.fromJsonHelper = fromJsonHelper;
        this.savingsAccountAssembler = savingsAccountAssembler;
        this.clientRepositoryWrapper = clientRepositoryWrapper;
        this.rxDealReadPlatformService = rxDealReadPlatformService;
        this.rxDealRepositoryWrapper = rxDealRepositoryWrapper;
        this.officeRepositoryWrapper = officeRepositoryWrapper;
        this.context = context;
    }

    public RxDealData assembleFrom(JsonCommand command){

        JsonElement element = command.parsedJson();

        Long clientId = null;

        String nid = null ;
        String emailAddress = null;
        String receiverName = fromJsonHelper.extractStringNamed(RxDealConstants.receiverNameParam ,element);
        String receieverPhoneNumber = fromJsonHelper.extractStringNamed(RxDealConstants.receiverPhoneNumberParam ,element);
        String senderName = null ;
        String senderPhoneNumber = null;
        String lastName = null ;
        String receiverEmailAddress= null ;

        Boolean isCreateClient = false ;
        if(fromJsonHelper.parameterExists(RxDealConstants.createNewClientParam ,element)){
            isCreateClient =  fromJsonHelper.extractBooleanNamed(RxDealConstants.createNewClientParam ,element);
        }

        if(isCreateClient){
            nid = fromJsonHelper.extractStringNamed(RxDealConstants.nidParam ,element);
            emailAddress = fromJsonHelper.extractStringNamed(ClientApiConstants.emailAddressParamName ,element);
            StringBuilder stringBuilder = new StringBuilder();

            senderName = fromJsonHelper.extractStringNamed(RxDealConstants.senderNameParam ,element);
            senderPhoneNumber = fromJsonHelper.extractStringNamed(RxDealConstants.senderPhoneNumberParam ,element);
            lastName = fromJsonHelper.extractStringNamed(ClientApiConstants.lastnameParamName ,element);

            stringBuilder.append(senderName);
            stringBuilder.append("#");
            stringBuilder.append(lastName);

            senderName = stringBuilder.toString();

        }
        else{
            clientId = fromJsonHelper.extractLongNamed(ClientApiConstants.clientIdParamName,element);
        }

        Long officeId = fromJsonHelper.extractLongNamed(ClientApiConstants.officeIdParamName ,element);

        final Office office = officeRepositoryWrapper.findOneWithNotFoundDetection(officeId);

        BigDecimal amount = fromJsonHelper.extractBigDecimalWithLocaleNamed(RxDealConstants.amountParam ,element);

        IDENTIFICATION_TYPE identificationType = null;
        if(fromJsonHelper.parameterExists(RxDealConstants.nidTypeParam ,element)){
            Integer nidInt = fromJsonHelper.extractIntegerWithLocaleNamed(RxDealConstants.nidParam ,element);
            identificationType = IDENTIFICATION_TYPE.fromInt(nidInt);
        }

        LocalDate transactionDate = fromJsonHelper.extractLocalDateNamed(RxDealConstants.transactionDateParam ,element);

        RX_PROVIDER rxProvider = RX_PROVIDER.INTERNAL;
        if(fromJsonHelper.parameterExists(RxDealConstants.providerIdParam ,element)){
            Integer pInt = fromJsonHelper.extractIntegerWithLocaleNamed(RxDealConstants.providerIdParam ,element);
            rxProvider = RX_PROVIDER.fromInt(pInt);
        }

        if(command.hasParameter(RxDealConstants.receiverEmailAddressParam)){
            receiverEmailAddress = fromJsonHelper.extractStringNamed(RxDealConstants.receiverEmailAddressParam ,element);
        }
        String currencyCode = fromJsonHelper.extractStringNamed(RxDealConstants.currencyParam ,element);

        final Long payinAccountId = fromJsonHelper.extractLongNamed(RxDealConstants.payinAccountParam ,element);
        Date transactionDateEx = DateUtils.fromLocalDate(transactionDate);

        RxDealData rxDealData = new RxDealData(nid ,emailAddress ,senderName ,senderPhoneNumber ,receiverName ,receieverPhoneNumber ,identificationType,rxProvider ,isCreateClient,clientId ,transactionDateEx ,officeId ,amount ,payinAccountId ,currencyCode ,office ,receiverEmailAddress);
        return rxDealData ;
    }


    /**
     * Added 08/11/2022 at 0242
     * This is a hack
     */
    public Map assembleForWithdrawal(final RxDealReceive rxDealReceive){


        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd MMMM yyyy");
        String transactionDate = simpleDateFormat.format(rxDealReceive.getTransactionDate());
        Map<String ,Object> map = new HashMap<>();
        map.put("locale","en");
        map.put("transactionAmount",rxDealReceive.getRxDeal().getAmount());
        map.put("transactionDate" ,transactionDate);
        map.put("dateFormat" ,"dd MMMM yyyy");
        map.put("paymentTypeId" ,1);

        return map ;

    }

    public RxDealReceive assemblerForReceive(final Long id ,JsonCommand jsonCommand){

        // some validate for update function here son
        AppUser appUser = context.authenticatedUser();

        JsonElement element = jsonCommand.parsedJson();

        RxDeal rxDeal = rxDealRepositoryWrapper.findOneWithNotFoundDetection(id);
        /**
         * Added 10/11/2022 at 0707
         * Throw error if rxdeal status is not open .Only open deals can be remitted
         */
        isRxDealOpen(rxDeal);

        final Office office = appUser.getOffice();

        final String name = fromJsonHelper.extractStringNamed(RxDealConstants.receiverNameParam ,element);

        String emailAddress= null ;
        String phoneNumber = null ;

        if(jsonCommand.hasParameter(RxDealConstants.receiverPhoneNumberParam)){
            phoneNumber = fromJsonHelper.extractStringNamed(RxDealConstants.receiverPhoneNumberParam ,element);

        }
        if(jsonCommand.hasParameter(ClientApiConstants.emailAddressParamName)){
            emailAddress = fromJsonHelper.extractStringNamed(ClientApiConstants.emailAddressParamName ,element);
        }

        LocalDate transactionDate = fromJsonHelper.extractLocalDateNamed(RxDealConstants.transactionDateParam ,element);

        RX_DEAL_STATUS rxDealStatus = RX_DEAL_STATUS.CLOSED;
        /**
         * transaction to be update once withdrawal is done
         */
        SavingsAccountTransaction savingsAccountTransaction = rxDeal.getSavingsAccountTransaction();

        RxDealReceive rxDealReceive = new RxDealReceive(rxDeal ,savingsAccountTransaction ,name ,phoneNumber ,emailAddress ,transactionDate ,office ,rxDealStatus ,BigDecimal.ZERO ,BigDecimal.ZERO);

        return rxDealReceive;
    }

    private void isRxDealOpen(RxDeal rxDeal){
        RX_DEAL_STATUS rxDealStatus = rxDeal.getRxDealStatus();
        boolean isOpen  = rxDealStatus.equals(RX_DEAL_STATUS.OPENED);
        if(!isOpen){
            throw new RxDealStatusNotOpenedException();
        }
    }
}
