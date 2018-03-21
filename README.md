Requirement:
•	Create a MongoDB to SQL translator.
•	The translator should only support the following mongodb operators:

$or  
$and(remember $and & comma separated values on an object are the same)
$lt
$lte
$gt
$gte
$ne
$in

•	There can be any combinations of operators in a single query.
•	The translator only needs to support db.find.
•	The answer must be developed in Java, targeting JDK 1.7 or superior. No external tool should be used as a SQL query builder or for json parsing.


Class Structure:

•	Translator.java : Translates the mongoDB Query to SQL Query.
•	Query.java : Represents mongoDB Query.
•	Operand.java: Represents the Projection elements like column name, column value and operator. To represent Multi Operand Operators like $in, $or, $and it contains a list of itself too.
•	SQLQueryBuilder: Represents the SQL Query elements.
•	SingleOperandEnum: Contains operators like $ne,$gt etc and its SQL equivalent. 
•	MultiOperandEnum : Contains operators like $or,$in etc and its SQL equivalent.

High Level Algorithm:

1)	Validate the given mongoDB Query.
2)	If the given query is invalid in terms of formatting or some other operation usage other than find, throw an error and terminate.
3)	If the query is successfully validated and good to go, start parsing the query. Parsing has two high level steps.
a.	Read the table name.
b.	Read the part enclosed between () to fetch the selection and projection criteria.
4)	To fetch the projection and selection criteria, parse the query to get the pairs.
5)	Populate the mongoDB Query object from the given pairs.
a.	Query object contains the table name.
b.	Query object contains OperandList which contains the projection criteria. The concept for populating the Operandlist is there is a new object getting created every time there is a new level found. Every level is represented with {}. At any level, the operand object should at least contain one of the column_name, column_value or operator.
c.	Query object contains the selection criteria.
6)	Populate the SQL Query object based on the Query object.
7)	Use Enums for the operator conversion.
  

Testing Criteria:

	The code has been unit tested with Junit.
	Some valid and invalid scenarios are tested.

External Jars Used:
	Commons-lang.jar
Junit.jar
Hamcrest-core.jar

Methodology used:
	Agile

Reference :

https://docs.mongodb.org/manual/tutorial/query-documents/ 

Disclosure:
This code shows the very initial version of parsing. It is very much possible that the code has to be altered/enhanced to accommodate few more combinations.  

