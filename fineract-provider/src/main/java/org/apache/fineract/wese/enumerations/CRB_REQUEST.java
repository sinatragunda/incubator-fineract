/*Created by Sinatra Gunda
  At 11:52 AM on 3/9/2020 */
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



public enum CRB_REQUEST {

    TEST("Test"),
    LOAN_APPLICATION("approve") ,
    LOAN_COLLATERAL(""),
    LOAN_REJECT("reject"),
    LOAN_DISBURSE("disburse"),
    LOAN_GURANTOR("gurantor"),
    LOAN_REPAYMENT("repayment"),
    MODIFY_LOAN_APPLICATION("Modify Loan Application"),
    LOAN_UPDATE("");

    public String code ;

    CRB_REQUEST(String code){
        this.code = code ;
    }

    public String getCode(){
        return this.code ;
    }

    public static CRB_REQUEST fromCommandValue(String commandValue){

        for(CRB_REQUEST crbRequest : CRB_REQUEST.values()){

            if(crbRequest.getCode().equalsIgnoreCase(commandValue)){
                return crbRequest ;
            }
        }
        return null ;

    }
}
