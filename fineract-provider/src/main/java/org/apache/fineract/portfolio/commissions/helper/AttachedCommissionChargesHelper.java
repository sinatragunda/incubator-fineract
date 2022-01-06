/*

    Created by Sinatra Gunda
    At 8:29 PM on 1/4/2022

*/
package org.apache.fineract.portfolio.commissions.helper;

import org.apache.fineract.portfolio.commissions.data.CommissionChargeData;
import org.apache.fineract.portfolio.commissions.data.LoanAgentData;
import org.apache.fineract.portfolio.commissions.data.LoanAgentDataBridge;

import java.util.List;
import java.util.Optional;

public class AttachedCommissionChargesHelper {


    public static boolean isValidEntry(LoanAgentDataBridge loanAgentDataBridge){

        LoanAgentData loanAgentData = loanAgentDataBridge.getLoanAgentData();

        List<CommissionChargeData> commissionChargeDataList = loanAgentDataBridge.getCommissionChargesList();

        boolean hasAgent = Optional.ofNullable(loanAgentData).isPresent();
        boolean hasCommissionCharges = !commissionChargeDataList.isEmpty();

        // only valid if it has agent and commission charges
        boolean validEntry = hasAgent && hasCommissionCharges;

        // validate if the data is correct as well
        validEntry = validateValidCommissionsData(loanAgentDataBridge ,validEntry);
        return validEntry;
    }

    public static boolean validateValidCommissionsData(LoanAgentDataBridge loanAgentDataBridge ,boolean isValidEntry){

        if(!isValidEntry) {
            return false ;
        }

        LoanAgentData loanAgentData = loanAgentDataBridge.getLoanAgentData();
        List<CommissionChargeData> commissionChargeDataList = loanAgentDataBridge.getCommissionChargesList();

        return true ;

    }

}
