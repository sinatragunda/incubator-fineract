/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 December 2022 at 05:05
 */
package org.apache.fineract.portfolio.localref.data;

public class LocalRefValueData {

    private Long id ;
    private Long recordId ;
    private String value ;

    private String columnName ;

    public LocalRefValueData(Long id, Long recordId, String value,String columnName) {
        this.id = id;
        this.recordId = recordId;
        this.value = value;
        this.columnName = columnName;
    }
}
