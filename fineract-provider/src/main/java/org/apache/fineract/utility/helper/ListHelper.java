/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 05 April 2023 at 07:18
 */
package org.apache.fineract.utility.helper;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class ListHelper {

    public static List fromCollection(Collection collection){
        return (List) collection.stream().collect(Collectors.toList());
    }
}
