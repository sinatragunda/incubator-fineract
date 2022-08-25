/*

    Created by Sinatra Gunda
    At 7:22 AM on 7/18/2022

*/
package org.apache.fineract.notification.constants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class EventSubscriptionConstants {

    public static String businessEventsParam  ="businessEvent";
    public static String notificationBroadcastTypeParam  ="notificationBroadcastType";
    public static String messageParam  ="message";
    public static String officeIdParam  ="officeId";
    public static String eventMailListParam = "eventMailList";


    public final static String resourceNameForPermissions = "EVENT_SUBSCRIPTION";
    public final static String readPermission = "READ_EVENT_SUBSCRIPTION";
    public final static String editPermission = "UPDATE_EVENT_SUBSCRIPTION";


    public final static Set<String> DATA_PARAMETERS = new HashSet<>(Arrays.asList("id", "name", "selectAllMode","businessEvent" ,"officeName","officeId","recipientKeyName","message","eventSubscriptionId","count","notificationType","eventSubscriptionData","mailRecipientsKeyData"));




}
