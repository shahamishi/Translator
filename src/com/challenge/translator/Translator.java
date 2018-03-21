package com.challenge.translator;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ashah on 4/9/16.
 */
public class Translator {


    SQLQueryBuilder sqlQueryBuilder = new SQLQueryBuilder();

    Utility utility = new Utility();

    /**
     * This method translates the mongoDB query to SQL query.
     * @param input mongoDB query
     * @return String SQL Query
     */
    public String translate(String input){

        Query query  = new Query();
        String result;
        input=input.replaceAll("\\s","");
        if(StringUtils.isEmpty(input)){
            result= "Invalid query.";
            return result;
        }
        String[] queryParts = getQueryParts(input);
        if(!queryParts[2].equalsIgnoreCase("find")){
            result = "Oops!!! Invalid operation "+ queryParts[2] +" found.We support only find operation as part of this challenge.";
            return result;
        }else {
            query.setTable_name(queryParts[1]);
            List<String> pairs = new ArrayList<String>();

            if((input.indexOf("}") - input.indexOf("{")>=2)) {
                if (input.contains("{") && input.contains("}")) {
                    pairs = utility.getPairs(input, '{', '}');
                } else if (input.contains("{") || input.contains("}")) {
                    result = "Invalid query.";
                    return result;
                }
            }
            utility.populateQuery(pairs, query);
            String sqlQuery = sqlQueryBuilder.buildSQLQuery(query);

            return sqlQuery;
        }
    }

    /**
     * This method splits the initial part of mongoDB query, which can be used to determine the oepration as well as table name.
     * @param inputQuery mongoDB Query
     * @return String[] query parts which includes "db",table name and operation in order.
     */
    private static String[] getQueryParts(String inputQuery){
        return inputQuery.substring(0, inputQuery.indexOf("(")).split("\\.");
    }


}
