/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 09 December 2022 at 01:03
 */
package org.apache.fineract.portfolio.localref.helper;

import org.apache.fineract.portfolio.client.api.ClientApiConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class LocalRefConstants {


    public static String LOCAL_REF_WRITE_PERMISSION = "CREATE_LOCAL_REF";
    public static String nameParam = "name";
    public static String refTableParam = "refTable";
    public static String refValueTypeParam = "refValueType";
    public static String codeIdParam = "codeId";
    public static String descriptionParam = "description";
    public static String submittedDateParam = "submittedDate";

    public static String refValueParam = "refValue";
    public static String localRefIdParam = "localRefId";

    public static final Set<String> LOCAL_REF_REQUEST_DATA_PARAMETERS = new HashSet<>(
            Arrays.asList(LocalRefConstants.nameParam ,LocalRefConstants.descriptionParam ,LocalRefConstants.codeIdParam ,LocalRefConstants.refTableParam ,
                    LocalRefConstants.refValueTypeParam , ClientApiConstants.officeIdParamName,"locale" ,"dateFormat" ,LocalRefConstants.submittedDateParam));

}
