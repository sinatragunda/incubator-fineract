/*Created by Sinatra Gunda
  At 11:31 AM on 21/07/2020 */
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


public enum EMAIL_MESSAGE_TYPE{

    ALLOCATED_TASK("Allocated Task"),
    CLOSED_TASK("Closed Task");

    public String code ;

    EMAIL_MESSAGE_TYPE(String code){
        this.code = code ;
    }

    public String getCode(){
        return this.code ;
    }

    public static EMAIL_MESSAGE_TYPE fromInt(int arg){

        for(EMAIL_MESSAGE_TYPE emailMessageType : EMAIL_MESSAGE_TYPE.values()){

            if(emailMessageType.ordinal()==arg){
                return emailMessageType ;
            }
        }
        return null ;

    }
}
