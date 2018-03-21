package com.challenge.translator;

/**
 * Created by ashah on 4/13/16.
 */
public enum MultiOperandEnum {

    $and("AND"),

    $in("IN"),

    $or("OR");


    private String symbol;

    MultiOperandEnum(String symbol) {
        this.symbol = symbol;
    }

    /**
     * Retrieves the associated symbol.
     * @return
     */
    public String getSymbol(){
        return symbol;
    }


}
