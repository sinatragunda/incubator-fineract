/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 01 June 2023 at 02:29
 */
package org.apache.fineract.infrastructure.generic.exceptions;

import org.apache.fineract.helper.OptionalHelper;

import java.util.List;
public class ArrayListExceptionsHelper {


    public static boolean hasIndexBoundError(List list ,int index){

        boolean has = OptionalHelper.has(list);
        if(has){
            try{
                list.get(index);
            }
            catch (IndexOutOfBoundsException n){
                return true ;
            }
            return false ;
        }
        return true ;

    }
}
