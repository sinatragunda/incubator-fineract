/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 April 2023 at 19:38
 */
package org.apache.fineract.portfolio.search.service;

import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.portfolio.search.data.SearchDataEx;
import org.apache.fineract.utility.service.EnumeratedData;

import java.util.List;

public interface SearchReadPlatformServiceEx {
    public SearchDataEx search(JsonCommand command);
}
