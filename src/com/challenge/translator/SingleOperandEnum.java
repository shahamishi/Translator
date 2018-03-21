package com.challenge.translator;

/**
 * Created by ashah on 4/13/16.
 */
public enum SingleOperandEnum {

    $ne("!="),

    $gt(">"),

    $gte(">="),

    $lt("<"),

    $lte("<=");

    private String symbol;

    SingleOperandEnum(String symbol) {
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
