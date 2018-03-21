package com.challenge.translator.test;

/**
 * Created by ashah on 4/19/16.
 */


import com.challenge.translator.Translator;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Test;

public class TranslatorTest {


    static Translator translator;


    @BeforeClass
    public static void setUp(){
        translator = new Translator();
    }

    @Test
    public void testInvalidOperation(){
        String input = "db.products.insert( { item: 'card'', qty: 15 } );";
        String result = translator.translate(input);
        assertTrue(result.contains("Invalid"));
    }

    @Test
    public void testInvalidQuery(){
        String input = "";
        String result = translator.translate(input);
        assertTrue(result.contains("Invalid"));
    }

    @Test
    public void testUnformatedQuery(){
        String input = "db.products.insert( { item: 'card'', qty: 15  );";
        String result = translator.translate(input);
        assertTrue(result.contains("Invalid"));
    }

    @Test
    public void testQueryWithoutSelectClauseOrWhereCondition(){
        String input = "db.user.find();";
        String result = translator.translate(input);
        assertEquals("SELECT * FROM user", result);
    }

    @Test
    public void testQueryWithoutSelectClauseOrWhereConditionOtherType(){
        String input = "db.user.find( { } );";
        String result = translator.translate(input);
        assertEquals("SELECT * FROM user", result);
    }

    @Test
    public void testQueryWithSimpleWhereCondition(){
        String input = "db.user.find({name:'julio'});";
        String result = translator.translate(input);
        assertEquals("SELECT * FROM user WHERE name='julio'", result);
    }

    @Test
    public void testQueryWithSimpleSelectClause(){
        String input = "db.user.find({name:1,age:1});";
        String result = translator.translate(input);
        assertEquals("SELECT _id,name,age FROM user", result);
    }

    @Test
    public void testQueryWithSelectClauseAndWhereCondition(){
        String input = "db.user.find({_id:23113},{name:1,age:1});";
        String result = translator.translate(input);
        assertEquals("SELECT _id,name,age FROM user WHERE _id=23113",result);
    }

    @Test
    public void testQueryWithSelectClauseExcludingId(){
        String input = "db.user.find({name:'julio'},{name:1,age:1,_id:0});";
        String result = translator.translate(input);
        assertEquals("SELECT name,age FROM user WHERE name='julio'",result);
    }

    @Test
    public void testQueryWithAndOperator(){
        String input = "db.users.find( { $and: [ { status: 'A' } ,{ age: 50 } ] });";
        String result = translator.translate(input);
        assertEquals("SELECT * FROM users WHERE status='A' AND age=50",result);
    }

    @Test
    public void testQueryWithOrOperator(){
        String input = "db.users.find( { $or: [ { status: 'A' } ,{ age: 50 } , {name:'John'}] });";
        String result = translator.translate(input);
        assertEquals("SELECT * FROM users WHERE status='A' OR age=50 OR name='John'",result);
    }

    @Test
    public void testQueryWithGteOperator(){
        String input = "db.user.find({age:{$gte:21}},{name:1});";
        String result = translator.translate(input);
        assertEquals("SELECT _id,name FROM user WHERE age >= 21",result);
    }

    @Test
    public void testQueryWithLteOperator(){
        String input = "db.user.find({age:{$lte:21}},{name:1});";
        String result = translator.translate(input);
        assertEquals("SELECT _id,name FROM user WHERE age <= 21",result);
    }

    @Test
    public void testQueryWithNeOperator(){
        String input = "db.inventory.find({ qty: { $ne: 20 } });";
        String result = translator.translate(input);
        assertEquals("SELECT * FROM inventory WHERE qty != 20",result);
    }

    @Test
    public void testQueryWithGtAndLteOperators(){
        String input = "db.users.find( { age: { $gt: 25, $lte: 50 } });";
        String result = translator.translate(input);
        assertEquals("SELECT * FROM users WHERE age > 25 and age <= 50",result);
    }

    @Test
    public void testQueryWithInOperator(){
        String input = "db.inventory.find( { type: { $in: [ 'food', 'snacks' ] } } );";
        String result = translator.translate(input);
        assertEquals("SELECT * FROM inventory WHERE type IN ('food','snacks')",result);
    }

    @Test
    public void testQueryWithMultipleCommaSeparatedConditions(){
        String input = "db.user.find({_id:23113,name:'John',region:'US'},{name:1,age:1,_id:1});";
        String result = translator.translate(input);
        assertEquals("SELECT _id,name,age FROM user WHERE _id=23113 and name='John' and region='US'",result);
    }

    @Test
    public void testQueryWithComplexConditionIncludingMultipleOperators(){
        String input = "db.inventory.find({$or:[{qty:{$gte:100}},{price:{$lte:9.95}}]},{name:1,age:1});";
        String result = translator.translate(input);
        assertEquals("SELECT _id,name,age FROM inventory WHERE qty >= 100 OR price <= 9.95",result);
    }


}
