/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 11 March 2023 at 14:56
 */
package org.apache.fineract.utility.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;

import java.util.List;

/**
 * Int
 */
public interface DataEnumerationService {

    public List<EnumOptionData> getDropdownData();
}
