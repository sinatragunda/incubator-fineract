/*Created by Sinatra Gunda
  At 12:47 AM on 02/11/2020 */
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

public enum PROPERTY_TYPE{
	
	BOSA(0 ,"Bosa"),
	FOSA(1 ,"Fosa"),
	DEFAULT(2,"Default");

	Integer value ;
	String code ;

	PROPERTY_TYPE(Integer value ,String code){
		this.value = value ;
		this.code =code ;
	}

	public Integer getValue(){
		return this.value ;
	}

	public String getCode(){
		return this.code ;
	}

	public static PROPERTY_TYPE fromInt(int arg){

		for(PROPERTY_TYPE p : PROPERTY_TYPE.values()){
			if(p.ordinal()==arg){
				return p ;
			}
		}
		return DEFAULT ;
	}
}