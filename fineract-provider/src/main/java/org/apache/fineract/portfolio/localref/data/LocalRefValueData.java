/**
 * Created by Sinatra Gunda (treyviis@gmail.com)
 * on 10 December 2022 at 05:05
 */
package org.apache.fineract.portfolio.localref.data;

public class LocalRefValueData {

    private final Long id ;
    private final Long recordId ;
    private final String value ;
    private final String columnName ;
    private final String codeValue ;

    public LocalRefValueData(Long id, Long recordId, String value,String columnName) {
        this.id = id;
        this.recordId = recordId;
        this.value = value;
        this.columnName = columnName;
        this.codeValue = null ;
    }

    public LocalRefValueData(Long id, Long recordId, String value,String columnName ,String codeValue) {
        this.id = id;
        this.recordId = recordId;
        this.value = value;
        this.columnName = columnName;
        this.codeValue = codeValue;
    }

    public Long getId() {
        return id;
    }

    public Long getRecordId() {
        return recordId;
    }

    public String getValue() {
        return value;
    }

    public String getColumnName() {
        return columnName;
    }

    public String getCodeValue() {
        return codeValue;
    }
}
