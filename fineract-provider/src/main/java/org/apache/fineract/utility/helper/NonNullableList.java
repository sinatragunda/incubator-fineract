/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 May 2023 at 18:57
 */
package org.apache.fineract.utility.helper;

import org.apache.fineract.helper.OptionalHelper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Optional;

import java.util.List;

public class NonNullableList<T extends Object> extends ArrayList {

    /**
     * Only non null values should be insterted into array list
     */
    @Override
    public boolean add(java.lang.Object o){
        boolean has = OptionalHelper.isPresent(o);
        if(has){
            System.err.println("---------------only add if its not null son ");
            has = super.add(o);
        }
        return has;
    }
}
