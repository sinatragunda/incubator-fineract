/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 November 2022 at 07:03
 */
package org.apache.fineract.portfolio.remittance.domain;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.client.api.ClientApiConstants;
import org.apache.fineract.portfolio.remittance.constants.RxDealConstants;
import org.apache.fineract.portfolio.remittance.data.RxDealData;
import org.apache.fineract.portfolio.remittance.enumerations.RX_DEAL_STATUS;

import java.util.HashMap;
import java.util.Map;

public class RxDealUpdate extends RxDeal{


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
        if (command.isChangeInLongParameterNamed(ClientApiConstants.clientIdParamName, this.getClient().getId())) {
            final Long newValue = command.longValueOfParameterNamed(RxDealConstants.receiverPhoneNumberParam);
            actualChanges.put(ClientApiConstants.clientIdParamName, newValue);
            //setReceiverPhoneNumber(newValue);
        }

        if (command.isChangeInIntegerParameterNamed(RxDealConstants.rxDealStatusParam, this.getRxDealStatus().ordinal())) {
            final Integer newValue = command.integerValueOfParameterNamed(RxDealConstants.rxDealStatusParam);
            actualChanges.put(RxDealConstants.rxDealStatusParam, newValue);
            RX_DEAL_STATUS rxDealStatus = RX_DEAL_STATUS.fromInt(newValue);
            validateRxDealState(rxDealStatus);
            setRxDealStatus(rxDealStatus);
        }

        return actualChanges;
    }

    /**
     * Added 09/11/2022 at 0721
     * Should not be able to open a closed transaction
     */
    private void validateRxDealState(RX_DEAL_STATUS rxDealStatus){
        if(rxDealStatus ==RX_DEAL_STATUS.CLOSED){
            System.err.println("-------------------not shit idea of the functionality required here");
        }
    }
}
