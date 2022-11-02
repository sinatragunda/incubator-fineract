package org.apache.fineract.portfolio.loanaccount.enumerations;

public enum LOAN_TYPE {

    NONE(""),
    GROUP("group"),
    INDIVIDUAL("individual");

    private String code ;
    LOAN_TYPE(String code){
        this.code = code ;
    }

    public static LOAN_TYPE fromString(String arg){
        for(LOAN_TYPE loanType : values()){
            if(loanType.code.equalsIgnoreCase(arg)){
                return loanType;
            }
        }
        return NONE ;

    }

    public String getCode() {
        return code;
    }
}
