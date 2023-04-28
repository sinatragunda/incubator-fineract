/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 26 April 2023 at 11:51
 */
package org.apache.fineract.utility.helper;

import org.apache.fineract.helper.OptionalHelper;
import org.springframework.data.jpa.repository.JpaRepository;

import java.io.Serializable;

public class JpaHelper {

    public static <T> T findOneWithNotFoundDetection(JpaRepository jpaRepository ,Serializable id , Exception exception) {

        boolean hasId = OptionalHelper.isPresent(id);

        if(hasId){
            T t = (T)jpaRepository.findOne(id);
            Boolean hasObject = OptionalHelper.isPresent(t);
            if(hasObject){
                return t ;
            }
        }
        try{
            throw new Exception(exception);
        }
        catch (Exception e){
        }
        return null ;
    }

    public static <T> T findOne(JpaRepository jpaRepository ,Serializable id) {

        boolean hasId = OptionalHelper.isPresent(id);

        if(hasId){
            T t = (T)jpaRepository.findOne(id);
            Boolean hasObject = OptionalHelper.isPresent(t);
            if(hasObject){
                return t ;
            }
        }
        return null ;
    }

    public static <T> T save(JpaRepository jpaRepository ,T t){
        jpaRepository.saveAndFlush(t);
        return t ;
    }


}
