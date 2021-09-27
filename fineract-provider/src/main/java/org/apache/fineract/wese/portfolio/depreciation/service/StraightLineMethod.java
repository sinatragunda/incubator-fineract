/*

    Created by Sinatra Gunda
    At 8:00 PM on 9/25/2021

*/
package org.apache.fineract.wese.portfolio.depreciation.service;

import org.apache.fineract.wese.enumerations.DURATION_TYPE;
import org.apache.fineract.wese.portfolio.depreciation.domain.DepreciatingAsset;
import org.apache.fineract.wese.portfolio.depreciation.domain.DepreciationSchedule;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class StraightLineMethod implements DepreciationMethod{

    private DepreciatingAsset depreciatingAsset ;

    public StraightLineMethod(DepreciatingAsset depreciatingAsset ){
        this.depreciatingAsset = depreciatingAsset ;
    }

    @Override
    public List<DepreciationSchedule> depreciation(){

        int duration = depreciatingAsset.getUsefullLife();
        int index = 1 ;

        BigDecimal annualExpense = annualExpense();
        BigDecimal assetCost = depreciatingAsset.getAssetCost() ;

        List<DepreciationSchedule> depreciationScheduleList = new ArrayList<>(duration);

        for(DepreciationSchedule depreciationSchedule : depreciationScheduleList){

            BigDecimal accumulatedDepreciation = annualExpense.multiply(index) ;
            BigDecimal bookValue = assetCost.subtract(accumulatedDepreciation) ;
            straightLine = new StraightLine(annualExpense ,accumulatedDepreciation ,bookValue);
            ++index ;
        }

        return  depreciationScheduleList ;

    }


    public double annualExpense(){

        double costOfFixedAssets = assetCosts.getCostOfAsset() ;
        double residualValue = assetCosts.getResidualValue();
        int duration = assetCosts.getDuration();

        double expense = (costOfFixedAssets - residualValue) /duration ;

        return expense ;
    }
}