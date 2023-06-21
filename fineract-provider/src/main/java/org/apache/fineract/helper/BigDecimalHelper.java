/*

    Created by Sinatra Gunda
    At 2:34 PM on 9/29/2022

*/
package org.apache.fineract.helper;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalHelper {

    public static BigDecimal roundOf2(BigDecimal value){
        BigDecimal newVal = value.setScale(2 , RoundingMode.HALF_UP);
        return newVal;
    }

    public static Boolean isFirstGreater(BigDecimal left ,BigDecimal right){
        int cmp = left.compareTo(right);
        return new Integer(1).equals(cmp);
    }
}
