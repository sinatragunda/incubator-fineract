/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 11 March 2023 at 14:56
 */
package org.apache.fineract.utility.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.List;
import java.util.Collection;
/**
 * Int
 */
public interface DataEnumerationService {

    default public List<EnumOptionData> getDropdownData(){ return null ;};
    public Collection<? extends EnumeratedData> retrieveUsingQuery(String whereSql);

    /**
     * Added 05/05/2023 at 0730
     * Added to cater for CodeValues options ...
     * Still plan is needed for EnumOptionData fields
     */
    default public List<EnumOptionData> getDropdownData(String codeValue){ return  null ;}

}
