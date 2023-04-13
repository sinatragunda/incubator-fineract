/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 13 April 2023 at 19:47
 */
package org.apache.fineract.presentation.screen.data;

import org.apache.fineract.portfolio.localref.enumerations.REF_TABLE;

import java.util.Collection;

public class ScreenData {

    private Long id ;
    private String name ;
    private String shortName ;
    private Boolean active ;
    private Boolean multirow ;
    private REF_TABLE refTable;
    private Collection<ScreenElementData> screenElementDataList;

    public ScreenData(Long id ,String name, String shortName,Boolean active , Boolean multirow, REF_TABLE refTable) {
        this.id = id ;
        this.name = name;
        this.shortName = shortName;
        this.multirow = multirow;
        this.refTable = refTable;
        this.active = active ;
    }

    public void setScreenElementDataList(Collection<ScreenElementData> screenElementDataList){
        this.screenElementDataList =screenElementDataList;
    }
}
