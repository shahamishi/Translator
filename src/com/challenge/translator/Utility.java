package com.challenge.translator;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by ashah on 4/15/16.
 */
public class Utility {
    /**
     * This method helps find out different pairs of projection and selection.
     * @param input Query on which the operation is performed
     * @param start on the basis of which either the query has to be separated out or the pair's starting character like bracket
     * @param end Optional parameter if the query has to be just split, otherwise end bracket.
     * @return List of pairs.
     */
    public List<String> getPairs(String input,char start,char end){
        List<String> pairsList = new ArrayList<String>();
        int startIndex=-1;
        int endIndex=-1;
        int startTempIndex=-1;
        int startBracketCount=0;
        int endBracketCount=0;

        do {
            if (input.indexOf(start) != -1) {
                startBracketCount=0;
                endBracketCount=0;
                startIndex = startTempIndex = input.indexOf(start);
                if (end!='0' && input.indexOf(end) != -1) {
                    startBracketCount++;
                    endBracketCount++;
                    endIndex = input.indexOf(end);
                    do {
                        if (input.substring(startTempIndex + 1, endIndex).contains(String.valueOf(start))) {
                            startBracketCount++;
                            startTempIndex = StringUtils.ordinalIndexOf(input, String.valueOf(start), startBracketCount);
                            endIndex = StringUtils.ordinalIndexOf(input, String.valueOf(end), startBracketCount);
                        }
                    }while(input.substring(startTempIndex + 1, endIndex).contains(String.valueOf(start)));
                    pairsList.add(input.substring(startIndex+1, endIndex));
                    if(endIndex+2<input.length()-1){
                        input = input.substring(endIndex + 2);
                    }else{
                        break;
                    }
                }else{
                    String[] splitString = input.split(String.valueOf(start));
                    for(String string:splitString){
                        pairsList.add(string);
                    }
                    input="";
                }

            }else{
                pairsList.add(input);
            }
        }  while(input.indexOf(start)!=-1);

        return pairsList;
    }

    /**
     * This method populates the mongoDB Query object from the given pairs of projection and selection criteria.
     * @param pairs Projection and Selection Criteria
     * @param query mongoDB Query object
     */
    public void populateQuery(List<String> pairs,Query query){

        for(String pair:pairs){
            if(!hasNestedCondition(pair)) {
                getSimpleCondition(pair,query,null);
            }else{
                boolean hasMultiOperator;
                List<String> multiOperatorPairs = new ArrayList<String>();
                hasMultiOperator = doesPairStartWithMultiOperandOperator(pair);
                if(hasMultiOperator) {
                    multiOperatorPairs = getMultiOperatorPairs(pair, query);
                }else{
                    Operand operand = new Operand();
                    pair = setColumnNameFromCondition(operand, pair);
                    List<Operand> operandList = getSingleOperand(pair);
                    for(Operand operand1:operandList){
                        operand.getOperandList().add(operand1);
                    }

                    query.getOperandList().add(operand);
                }
                for(String multiOperatorPair:multiOperatorPairs){
                    if(hasNestedCondition(multiOperatorPair)){
                        if(!doesPairStartWithSingleOperand(multiOperatorPair)){
                            Operand operand1 = new Operand();
                            multiOperatorPair = setColumnNameFromCondition(operand1,multiOperatorPair);
                            List<Operand> operandList = getSingleOperand(multiOperatorPair);
                            for(Operand operand2:operandList) {
                                operand1.getOperandList().add(operand2);
                            }
                            query.getOperandList().get(0).getOperandList().add(operand1);

                        }

                    }else{
                        getSimpleCondition(multiOperatorPair,query,query.getOperandList().get(0).getOperandList());
                    }
                }
            }
        }
    }

    /**
     * This method identifies the pair and helps populate the operand object
     * @param pair Query part
     * @return List of Operand objects
     */
    private List<Operand> getSingleOperand(String pair){
        List<Operand> operandList = new ArrayList<Operand>();
        Operand operand = new Operand();
        if(pair.startsWith("{") && pair.endsWith("}")){
            if(!pair.contains(",")) {
                if (doesPairStartWithSingleOperand(pair.substring(1, pair.length() - 1))) {
                    if (pair.startsWith("{") && pair.endsWith("}")) {
                        populateSingleOperatorOperand(operand, pair.substring(1, pair.length() - 1));
                    }
                }
                operandList.add(operand);
            }else{
               List<String> pairs =  getPairs(pair.substring(1,pair.length()-1),',','0');
                for(String individualPair:pairs){
                    operand = new Operand();
                    if(doesPairStartWithSingleOperand(individualPair)){
                        operand.setOperator(individualPair.substring(individualPair.indexOf("$"),individualPair.indexOf(":")));
                        operand.setColumn_value(individualPair.substring(individualPair.indexOf(":")+1));
                    }
                    operandList.add(operand);
                }
            }


        }
        return operandList;
    }

    /**
     * This method sets the column name from the given condition.
     * @param operand Operand object
     * @param pair Query part
     * @return sub part of query part
     */
    private String setColumnNameFromCondition(Operand operand,String pair){
        operand.setColumn_name(
                pair.substring(0, pair.indexOf(":")));
        return pair.substring(pair.indexOf(":")+1,pair.lastIndexOf("}")+1);
    }

    /**
     * This methdo populates the query object with the given Projection and Selection Condition
     * @param pair Query part
     * @param query mongoDB Query object
     * @param optionalOperandList Optional Operand List
     */
    private void getSimpleCondition(String pair,Query query,List<Operand> optionalOperandList){
        String[] keyValuePair;
        Operand operand;
        if (!pair.startsWith("$")) {
            if (!(pair.contains(":1") || pair.contains(": 1") || pair.contains(":0") || pair.contains(": 0"))) {
                List<String> conditionPairs = getPairs(pair,',','0');
                for(String conditionPair: conditionPairs){
                    keyValuePair = conditionPair.split(":");
                    operand = new Operand();
                    operand.setColumn_name(keyValuePair[0]);
                    operand.setColumn_value(keyValuePair[1]);
                    if(optionalOperandList==null) {
                        query.getOperandList().add(operand);
                    }else{
                        optionalOperandList.add(operand);
                    }
                }
            } else {
                List<String> selectClauseList = this.getPairs(pair, ',', '0');
                query.getSelect_clause().append("_id,");
                for (String selectCondition : selectClauseList) {
                    keyValuePair = selectCondition.split(":");
                    if(keyValuePair[0].equalsIgnoreCase("_id")){
                        if(keyValuePair[1].equals("0")){
                            query.getSelect_clause().replace(0,4,"");
                        }
                    }
                    else if(keyValuePair[1].equals("1")) {
                        query.getSelect_clause().append(keyValuePair[0]).append(",");
                    }
                }
                query.getSelect_clause().deleteCharAt(query.getSelect_clause().length() - 1);
            }
        }

    }
    private boolean hasNestedCondition(String condition){
        return condition.contains("[") || condition.contains("{") || condition.contains("(");
    }

    /**
     * This method finds the multi operand pair enclosed within the query part.
     * @param pair Query part
     * @param query mongoDB Query object
     * @return List of multi operand pairs.
     */
    private List<String> getMultiOperatorPairs(String pair,Query query){
        List<String> nestedPairs = new ArrayList<String>();
        for(MultiOperandEnum multiOperatorEnum: MultiOperandEnum.values()){
            if(pair.startsWith(multiOperatorEnum.toString())){
                Operand operand = new Operand();
                operand.setOperator(multiOperatorEnum.toString());
                query.getOperandList().add(operand);
                pair = pair.substring(pair.indexOf("["));
                if(pair.startsWith("[") && pair.endsWith("]")){
                    nestedPairs = this.getPairs(pair.substring(pair.indexOf("[")), '{', '}');
                }else {
                    nestedPairs = this.getPairs(pair.substring(pair.indexOf("[")), '[', ']');
                }
                break;
            }else if(pair.contains(multiOperatorEnum.toString()) && multiOperatorEnum.toString().equals("$in")){
                Operand operand = new Operand();
                operand.setColumn_name(pair.substring(0, pair.indexOf((":"))));
                Operand operand1 = new Operand();
                operand1.setOperator(multiOperatorEnum.toString());
                operand1.setColumn_value(pair.substring(pair.indexOf('[')+1,pair.indexOf(']')));
                operand.getOperandList().add(operand1);
                query.getOperandList().add(operand);
                break;

            }
        }
        return nestedPairs;
    }

    /**
     * This method determines if the given query part starts with any Multi Operand Operator.
     * @param pair Query part
     * @return true/false
     */

    private boolean doesPairStartWithMultiOperandOperator(String pair){
        for(MultiOperandEnum multiOperatorEnum: MultiOperandEnum.values()) {
            if (pair.contains(multiOperatorEnum.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method determines if the given query part starts with any Single Operand Operator.
     * @param pair Query part
     * @return true/false
     */
    private boolean doesPairStartWithSingleOperand(String pair){

        for(SingleOperandEnum singleOperandEnum: SingleOperandEnum.values()) {
            if (pair.startsWith(singleOperandEnum.toString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * This method populates the operand object based on the given query part.
     * @param operand Operand object
     * @param pair Query part
     */
    private void populateSingleOperatorOperand(Operand operand,String pair)
    {
       operand.setOperator(pair.substring(0, pair.indexOf(":")));
       operand.setColumn_value(pair.substring(pair.indexOf(":")+ 1,pair.length()));
    }
}
