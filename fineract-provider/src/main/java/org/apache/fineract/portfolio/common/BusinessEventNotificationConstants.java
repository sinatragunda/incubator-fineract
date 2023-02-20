/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements. See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership. The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.fineract.portfolio.common;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static org.apache.fineract.portfolio.charge.service.ChargeEnumerations.chargeCalculationType;

public class BusinessEventNotificationConstants {

    public static enum BUSINESS_EVENTS {
        LOAN_APPROVED("loan_approved","Loan Approved"), 
        LOAN_REJECTED("loan_reject" ,"Loan Rejected"), 
        LOAN_UNDO_APPROVAL("loan_undo_approval","Loan Undo Approval"), 
        LOAN_UNDO_DISBURSAL("loan_undo_disbursal",""), 
        LOAN_UNDO_LASTDISBURSAL("loan_undo_lastdisbursal",""), 
        LOAN_UNDO_TRANSACTION("loan_undo_transaction",""), 
        LOAN_ADJUST_TRANSACTION("loan_adjust_transaction",""), 
        LOAN_MAKE_REPAYMENT("loan_repayment_transaction",""), 
        LOAN_WRITTEN_OFF("loan_writtenoff",""), 
        LOAN_UNDO_WRITTEN_OFF("loan_undo_writtenoff",""), 
        LOAN_DISBURSAL("loan_disbursal",""), 
        LOAN_WAIVE_INTEREST("loan_waive_interest",""), 
        LOAN_CLOSE("loan_close","Close Loan"), 
        LOAN_CLOSE_AS_RESCHEDULE("loan_close_as_reschedule","Reschedule Loan"),
        LOAN_ADD_CHARGE("loan_add_charge","Add Loan Charge"), 
        LOAN_UPDATE_CHARGE("loan_update_charge","Update Loan Charge"),
        LOAN_WAIVE_CHARGE("loan_waive_charge","Loan Waive Charge"), 
        LOAN_DELETE_CHARGE("loan_delete_charge","Loan Delete Charge"),
        LOAN_CHARGE_PAYMENT("loan_charge_payment",""), 
        LOAN_INITIATE_TRANSFER("loan_initiate_transfer",""), 
        LOAN_ACCEPT_TRANSFER("loan_accept_transfer",""),
        LOAN_WITHDRAW_TRANSFER("loan_withdraw_transfer",""), 
        LOAN_REJECT_TRANSFER("loan_reject_transfer",""), 
        LOAN_REASSIGN_OFFICER("loan_reassign_officer",""), 
        LOAN_REMOVE_OFFICER("loan_remove_officer",""), 
        LOAN_APPLY_OVERDUE_CHARGE("loan_apply_overdue_charge",""), 
        LOAN_INTEREST_RECALCULATION("loan_interest_recalculation",""),
        LOAN_REFUND("loan_refund","Loan Refund"), 
        LOAN_FORECLOSURE("loan_foreclosure","Loan Foreclosure"), 
        LOAN_CREATE("loan_create","Create Loan"), 
        LOAN_PRODUCT_CREATE("loan_product_create","Create Loan Product"), 
        SAVINGS_ACTIVATE("savings_activated","Savings Activated"),
        SAVINGS_REJECT("savings_reject",""), 
        SAVINGS_POST_INTEREST("savings_post_interest",""), 
        SAVINGS_DEPOSIT("savings_deposit","Account Deposit"), 
        SAVINGS_CLOSE("savings_close","Close Account"),
        SAVINGS_WITHDRAWAL("savings_withdrawal","Account Withdrawal"),
        SAVINGS_APPROVE("savings_approve","Approve Savings"),
        SAVINGS_CREATE("savings_create","Create Account"),
        CLIENTS_ACTIVATE("clients_activate",""), 
        SHARE_ACCOUNT_CREATE("share_account_create",""),
        CLIENTS_REJECT("clients_reject",""), 
        CLIENTS_CREATE("clients_create",""),
        CENTERS_CREATE("centers_create",""), 
        GROUPS_CREATE("groups_create",""),
        SHARE_PRODUCT_DIVIDENDS_CREATE("share_product_dividends_create",""),
        FIXED_DEPOSIT_ACCOUNT_CREATE("fixed_deposit_account_create",""),
        SHARE_ACCOUNT_APPROVE("share_account_approve",""), 
        RECURRING_DEPOSIT_ACCOUNT_CREATE("recurring_deposit_account_create","") ,
        SHARES_PURCHASE("shares_purchase","Purchase Shares"),
        FT_CHARGES("loan_charge_payment" ,"Funds Transfer Charges"),
        RX_SEND("rx_send","Rx Deal Send"),
        RX_RECEIVE("rx_out" ,"Rx Deal Receive"),
        RX_UPDATE("rx_update","Rx Deal Update");
        
        
        private final String value;
        private final String code ;

        private BUSINESS_EVENTS(final String value ,final String code) {
            this.value = value;
            this.code = code ;
        }

        public static BUSINESS_EVENTS fromInt(int arg){
            for(BUSINESS_EVENTS b : BUSINESS_EVENTS.values()){
                if(b.ordinal()==arg){
                    return b;
                }
            }
            return null ;
        }

        private static final Set<String> values = new HashSet<>();
        static {
            for (final BUSINESS_EVENTS type : BUSINESS_EVENTS.values()) {
                values.add(type.value);
            }
        }

        public String getCode() {
            return code;
        }

        public static Set<String> getAllValues() {
            return values;
        }

        public String getValue() {
            return this.value;
        }
    }

    public static enum BUSINESS_ENTITY {
        LOAN("loan"), LOAN_TRANSACTION("loan_transaction"), LOAN_CHARGE("loan_charge"), LOAN_ADJUSTED_TRANSACTION(
        "loan_adjusted_transaction"), SAVING("saving"), CLIENT("client"), SAVINGS_TRANSACTION("Savings Transaction"), GROUP("group"),
        SHARE_ACCOUNT("share_account"), SHARE_PRODUCT("share_product"), DEPOSIT_ACCOUNT("deposit_account"), LOAN_PRODUCT("loan_product"),FT("Funds Transfer"),RX("Remittances");

        private final String value;

        private BUSINESS_ENTITY(final String value) {
            this.value = value;
        }

        public String getValue() {
            return this.value;
        }

    }

    // added 24/024/2022
    public static List<EnumOptionData> businessEventEnumOption(){
        
        List<EnumOptionData> enumList = new ArrayList();
        for(BUSINESS_EVENTS e : BUSINESS_EVENTS.values()){
            EnumOptionData enumOptionData = new EnumOptionData(new Long(e.ordinal()) ,e.getCode() ,e.getValue());
            enumList.add(enumOptionData);
        }
        return enumList ;
    }

}
