/*

    Created by Sinatra Gunda
    At 12:28 AM on 9/6/2021

*/
package org.apache.fineract.wese.helper;

public class ComparatorUtility {

    public static boolean compareLong(Long l ,Long r){
        int cmp = l.compareTo(r);
        return cmpToBoolean(cmp);
    }

    public static boolean cmpToBoolean(int cmp){
        if(cmp == 0){
            return true;
        }
        return false ;
    }
}
