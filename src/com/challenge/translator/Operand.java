package com.challenge.translator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashah on 4/12/16.
 */
public class Operand {

    String column_name;
    String column_value;
    String operator;
    List<Operand> operandList = new ArrayList<Operand>();


    /**
     * Retrieves the column name
     * @return
     */
    public String getColumn_name() {
        return column_name;
    }

    /**
     * Sets the column name
     * @param column_name
     */
    public void setColumn_name(String column_name) {
        this.column_name = column_name;
    }

    /**
     * Retrieves the column value
     * @return
     */
    public String getColumn_value() {
        return column_value;
    }

    /**
     * Sets the Column value
     * @param column_value
     */
    public void setColumn_value(String column_value) {
        this.column_value = column_value;
    }

    /**
     * Retrieves the operator
     * @return
     */
    public String getOperator() {
        return operator;
    }

    /**
     * Sets the operator
     * @param operator Operator
     */
    public void setOperator(String operator) {
        this.operator = operator;
    }

    /**
     * Retrieves the operand list
     * @return List of Operand objects
     */
    public List<Operand> getOperandList() {
        return operandList;
    }

}
