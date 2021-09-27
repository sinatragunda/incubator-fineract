/*

    Created by Sinatra Gunda
    At 6:34 AM on 9/26/2021

*/
package org.apache.fineract.wese.portfolio.depreciation.helper;

public class DepreciatingAssetAccountNumberGen {


    public static String accountNumberGen(Long id ,String tag){
        return String.format("%s%d",tag ,id);
    }
}
