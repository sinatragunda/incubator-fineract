/*

    Created by Sinatra Gunda
    At 11:05 AM on 8/24/2022

*/
package org.apache.fineract.notification.data;

public class MailRecipientsKeyData {

    private final Long id ;
    private final String name ;
    private final Long officeId ;
    private final String officeName ;
    private final Integer count ;
    private final boolean selectAllMode ;


    public MailRecipientsKeyData(Long id, String name, Long officeId, String officeName, Integer count, boolean selectAllMode) {
        this.id = id;
        this.name = name;
        this.officeId = officeId;
        this.officeName = officeName;
        this.count = count;
        this.selectAllMode = selectAllMode;
    }

}
