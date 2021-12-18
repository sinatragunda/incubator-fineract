/*

    Created by Sinatra Gunda
    At 9:44 AM on 12/18/2021

*/
package org.apache.fineract.portfolio.products.enumerations;

public enum PRODUCT_TYPE {

    SAVINGS("SAVINGS"),
    LOANS("LOANS"),
    SHARES("SHARES"),
    DEPOSITS("DEPOSITS");

    String code ;

    PRODUCT_TYPE(String code){
        this.code = code ;
    }

    public static PRODUCT_TYPE fromString(String code){
        for(PRODUCT_TYPE productType : values()){
            boolean match = productType.code.equalsIgnoreCase(code);
            if(match){
                return productType;
            }
        }
        return null ;
    }

    public String getCode() {
        return code;
    }
}
