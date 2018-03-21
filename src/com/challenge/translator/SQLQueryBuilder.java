package com.challenge.translator;

/**
 * Created by ashah on 4/13/16.
 */
public class SQLQueryBuilder {

    private String select_clause="";
    private StringBuilder condition;
    private String table_name="";
    private String query = "";
    private boolean hasMultiOperator=false;
    private String multiOperandOperator;

    /**
     * This method builds the SQL query string from the query object.
     * @param query
     * @return
     */
    public String buildSQLQuery(Query query){

        this.condition=new StringBuilder();
        this.table_name = query.getTable_name();
        this.select_clause = query.getSelect_clause().length()==0?"*":query.getSelect_clause().toString();
        int counter=0;
        for(Operand operand:query.getOperandList()) {
            if (operand.getOperandList().size() == 0) {
                counter++;
                if(counter>1){
                    this.condition.append(" and ");
                }
                this.condition.append(getSimpleCondition(operand));
            }else{
                counter++;
                if(counter>1){
                    this.condition.append(" and ");
                }
                if(operand.getOperator()!=null){
                    for(MultiOperandEnum multiOperandEnum: MultiOperandEnum.values()) {
                        if (operand.getOperator().equals(multiOperandEnum.toString())) {
                            hasMultiOperator=true;
                            multiOperandOperator = multiOperandEnum.getSymbol();
                            if(hasMultiOperator) {
                                for (Operand operand1:operand.getOperandList()) {
                                    if(operand1.getOperandList().size() == 0){
                                        this.condition.append(" " + (getSimpleCondition(operand1)));
                                    }else{
                                        if(operand1.getColumn_name().length()!=0){
                                            populateSingleOperandCondition(operand1);
                                        }
                                    }
                                    this.condition.append(" " + multiOperandOperator);
                                }
                            }
                        }
                    }
                   this.condition = new StringBuilder(this.condition.subSequence(0,this.condition.length()-multiOperandOperator.length()));
                }else{
                    populateSingleOperandCondition(operand);
                }
            }
        }
        return getQuery();
    }

    /**
     * This method helps build Where Clause.
     * @param operand containing conditions
     */
    private void populateSingleOperandCondition(Operand operand){
        this.condition.append(" " + operand.getColumn_name());
        int counter=0;
        boolean conditionMet=false;
        if(operand.getOperandList().size()!=0){
            for(Operand operand2:operand.getOperandList()) {
                counter++;
                if (counter >= 2) {
                    this.condition.append(" " + "and " + operand.getColumn_name());
                }

                for (SingleOperandEnum singleOperandEnum : SingleOperandEnum.values()) {
                    if (operand2.getOperator().equals(singleOperandEnum.toString())) {
                        this.condition.append(" " + singleOperandEnum.getSymbol());
                        this.condition.append(" " + operand2.column_value);
                        conditionMet=true;
                        break;
                    }
                }
                if(!conditionMet){
                    this.condition.append(" " + MultiOperandEnum.$in.getSymbol()+" ("+operand2.getColumn_value()+")");
                }
            }
        }
    }

    /**
     * This method combines the different parts to generate a SQL Query.
     * @return String SQL Query
     */
    private String getQuery(){
        if(this.condition.toString().length()>0){
            this.query = "SELECT "+this.select_clause+" FROM "+this.table_name+" WHERE "+this.condition.toString().trim();
        }else{
            this.query = "SELECT "+this.select_clause+" FROM "+this.table_name;
        }

        return this.query;
    }

    /**
     * This method helps build Where Clause.
     * @param operand Containing Conditions
     * @return String condition.
     */
    private StringBuilder getSimpleCondition(Operand operand){
        return new StringBuilder(operand.getColumn_name().concat(operand.getOperator() != null ? operand.getOperator() : "=").concat(operand.getColumn_value()));
    }

}
