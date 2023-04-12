/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 11 April 2023 at 17:09
 */
package org.apache.fineract.portfolio.search.data;

import org.apache.fineract.utility.service.EnumeratedData;

import java.util.List;

public class SearchDataEx {

    private String landingPage ;
    private List<EnumeratedData> results ;

    public SearchDataEx(String landingPage, List<EnumeratedData> dataList) {
        this.landingPage = landingPage;
        this.results = dataList;
    }
}
