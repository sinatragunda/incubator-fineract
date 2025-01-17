/*Created by Sinatra Gunda
  At 10:28 AM on 12/10/2020 */
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

package org.apache.fineract.wese.enumerations;


public enum SACCO_LOAN_STATUS {

    SHARE_ACCOUNT_ERROR("Share Account Error") ,
    SHARE_ACCOUNT_VALIDITY_ERROR("Share Account Validity Error"),
    LOAN_AMOUNT_GREATER("Loan amount greater than allowed"),
    SUCCESS("Success");

    public String code ;

    SACCO_LOAN_STATUS(String code){
        this.code = code ;
    }

    public String getCode(){
        return this.code ;
    }

    public static SACCO_LOAN_STATUS fromCommandValue(String commandValue){

        for(SACCO_LOAN_STATUS saccoLoanStatus : SACCO_LOAN_STATUS.values()){

            if(saccoLoanStatus.getCode().equalsIgnoreCase(commandValue)){
                return saccoLoanStatus ;
            }
        }
        return null ;

    }
}
