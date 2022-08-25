/*

    Created by Sinatra Gunda
    At 2:41 AM on 7/16/2022

*/
package org.apache.fineract.notification.enumerations;

public enum NOTIFICATION_BROADCAST_TYPE {

    BROADCAST("Broadcast"),
    TOPIC("Topic");

    private String code ;

    NOTIFICATION_BROADCAST_TYPE(String code){
        this.code = code ;
    }

    public String getCode() {
        return code;
    }

    public static NOTIFICATION_BROADCAST_TYPE fromInt(int arg){

        for(NOTIFICATION_BROADCAST_TYPE n : values()){
            if(n.ordinal()==arg) {
                return n;
            }
        }
        return null ;
    }


    public static NOTIFICATION_BROADCAST_TYPE fromString(String arg){

        for(NOTIFICATION_BROADCAST_TYPE n : values()){
            if(n.getCode().equalsIgnoreCase(arg)){
                return n;
            }
        }
        return null ;
    }

}
