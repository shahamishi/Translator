package com.challenge.translator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashah on 4/13/16.
 */
public class Query {
    List<Operand> operandList;
    String table_name;
    StringBuilder select_clause;

    /**
     * Constructor
     */
    public Query() {
        this.operandList = new ArrayList<Operand>();
        this.table_name = "";
        this.select_clause = new StringBuilder();
    }

    /**
     * Retrieves the list of operand
     * @return
     */
    public List<Operand> getOperandList() {
        return operandList;
    }

    /**
     * Retrieves the table name
     * @return
     */
    public String getTable_name() {
        return table_name;
    }

    /**
     * Sets the table name
     * @param table_name
     */
    public void setTable_name(String table_name) {
        this.table_name = table_name;
    }

    /**
     * Retrives the select clause
     * @return
     */
    public StringBuilder getSelect_clause() {
        return select_clause;
    }

}
