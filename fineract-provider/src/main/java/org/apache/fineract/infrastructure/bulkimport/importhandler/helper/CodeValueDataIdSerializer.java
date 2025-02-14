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
package org.apache.fineract.infrastructure.bulkimport.importhandler.helper;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.apache.fineract.infrastructure.codes.data.CodeValueData;

import java.lang.reflect.Type;

public class CodeValueDataIdSerializer implements JsonSerializer<CodeValueData> {
    @Override
    public JsonElement serialize(CodeValueData src, Type typeOfSrc, JsonSerializationContext context) {
        JsonElement jsonElement = null ;
        try{
            jsonElement = new JsonPrimitive(src.getId());
        }
        catch(NullPointerException n){
        }
        return jsonElement;
    }
}
