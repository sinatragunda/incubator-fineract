/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 30 March 2023 at 20:20
 */
package org.apache.fineract.infrastructure.generic.service;

import org.apache.fineract.infrastructure.core.data.EnumOptionData;
import org.apache.fineract.utility.service.EnumeratedData;

import java.util.Collection;
import java.util.List;

public interface GenericReadPlatformService {
    List<EnumOptionData> getDropdownData();
    Collection<? extends EnumeratedData> retrieveAll();

}
