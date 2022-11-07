/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 06 November 2022 at 23:23
 */
package org.apache.fineract.portfolio.common.helper;

import org.apache.fineract.portfolio.common.BusinessEventNotificationConstants;

import java.util.HashMap;
import java.util.Map;

public class EntityMap {

    public static Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> construct(final BusinessEventNotificationConstants.BUSINESS_ENTITY entityEvent, Object entity) {
        Map<BusinessEventNotificationConstants.BUSINESS_ENTITY, Object> map = new HashMap<>(1);
        map.put(entityEvent, entity);
        return map;
    }
}
