/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 April 2023 at 19:41
 */
package org.apache.fineract.portfolio.search.service;

import com.wese.component.defaults.enumerations.BEAN_LOADER;
import com.wese.component.defaults.exceptions.BeanLoaderNotFoundException;
import com.wese.component.search.domain.SearchRequest;
import com.wese.component.search.helper.SearchRequestFactory;
import com.wese.component.search.helper.SearchRequestHelper;
import org.apache.fineract.helper.OptionalHelper;
import org.apache.fineract.infrastructure.core.api.JsonCommand;
import org.apache.fineract.infrastructure.core.serialization.FromJsonHelper;
import org.apache.fineract.infrastructure.core.serialization.ToApiJsonSerializer;
import org.apache.fineract.infrastructure.dataqueries.repo.ApplicationRecordRepository;
import org.apache.fineract.infrastructure.security.service.PlatformSecurityContext;
import org.apache.fineract.portfolio.search.data.SearchDataEx;
import org.apache.fineract.utility.helper.ListHelper;
import org.apache.fineract.utility.service.EnumeratedData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public class SearchReadPlatformServiceExImpl implements SearchReadPlatformServiceEx{

    ApplicationContext applicationContext;
    FromJsonHelper fromJsonHelper;
    PlatformSecurityContext context;
    private final ToApiJsonSerializer toApiJsonSerializer;

    @Autowired
    public SearchReadPlatformServiceExImpl(PlatformSecurityContext context , ApplicationContext applicationContext, FromJsonHelper fromJsonHelper,final ToApiJsonSerializer toApiJsonSerializer) {
        this.applicationContext = applicationContext;
        this.fromJsonHelper = fromJsonHelper;
        this.context = context;
        this.toApiJsonSerializer = toApiJsonSerializer;
    }

    public SearchDataEx search(JsonCommand command){

        // need some validation here to check if string request is null or not
        //context.getAuthenticatedUserIfPresent().validateHasPermissionTo("CREATE_SEARCH");
        String className =  command.stringValueOfParameterNamed("class");

        //System.err.println("-----------------------classname is "+className);

        BEAN_LOADER beanLoader = BEAN_LOADER.fromString(className);
        boolean hasBeanLoader = OptionalHelper.isPresent(beanLoader);
        if(!hasBeanLoader){
            throw new BeanLoaderNotFoundException(className);
        }

        Set<SearchRequest> searchRequestSet = SearchRequestHelper.createRequest(command ,fromJsonHelper);
        List<SearchRequest> searchRequestList = ListHelper.fromSet(searchRequestSet);
        
        List responseData = SearchRequestFactory.invoke(applicationContext ,beanLoader ,searchRequestList);

        String landingPage = beanLoader.getLandingPage();
        SearchDataEx searchDataEx = new SearchDataEx(landingPage ,responseData);
        return searchDataEx;
    }

}
