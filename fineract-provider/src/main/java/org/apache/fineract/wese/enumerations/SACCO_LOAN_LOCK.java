/*Created by Sinatra Gunda
  At 0830 AM on 22/10/2020 */
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

public enum SACCO_LOAN_LOCK {

    FULLLOCK(0 ,"Full lock"),
    RELEASE_ON_REPAYMENT(1,"Release Of Repayment") ,
    NONE(2,"None");

    private String code ;
    private Integer value ;

    SACCO_LOAN_LOCK(Integer value ,String code){
        this.code = code ;
        this.value = value ;
    }

    public String getCode(){
        return this.code ;
    }

    public Integer getValue(){
        return this.value ;
    }

    public static SACCO_LOAN_LOCK fromInt(Integer commandValue){

        if(commandValue != null){
            for(SACCO_LOAN_LOCK crbRequest : SACCO_LOAN_LOCK.values()){

                if(crbRequest.ordinal() == commandValue){
                    return crbRequest ;
                }
            }
        }
        return SACCO_LOAN_LOCK.NONE ;
    }
}
