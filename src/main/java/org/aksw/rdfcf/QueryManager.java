package org.aksw.rdfcf;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryFactory;

/** UNUSED
 * Creates a construct query from a select query
 * 
 * @author Marvin Hofer
 * 
 */

public class QueryManager {
	
/**
* query
*/
	private Query uniqueQuery;
	
/*
 * 	Constructor
 *  
 *  @param 	queryString 	query input
 */
	public QueryManager (String queryString) {
		
		Query query = QueryFactory.create(queryString);
		
		
		if(query.isSelectType()) {
			this.uniqueQuery = buildConQuery(query);
		} else if(query.isConstructType()) {
			this.uniqueQuery = query;
		}
	}
	
/*
 * Building a construct query from a select query
 * TODO [Construct,Where,Limit,Offset] finished [optional] left 
 * 
 * @return 			construct query
 */	
	public static Query buildConQuery(Query query) {
		
		String conquery = "";
		
			
			String partOffset = "";
			String partLimit = "";
		
			String partWhere = query.getQueryPattern().toString();
		
			if(query.hasOffset()) {
				partOffset = "Offset "+String.valueOf(query.getOffset());
			}
			if(query.hasLimit()){
				partLimit = "Limit "+String.valueOf(query.getLimit());
			}
		
			conquery = "Construct "+partWhere+" Where "+partWhere+" "+partOffset+" "+partLimit;
		
		return QueryFactory.create(conquery);
	}

/*
 * Check select, construct, ask, describe
 */
	@SuppressWarnings("unused")
	private static String queryType(String queryStr) {
		Query query = QueryFactory.create(queryStr);
		
		String type ="";
		
		if(query.isAskType()) 		type = "ASK";
		if(query.isConstructType()) type = "CONSTRUCT";
		if(query.isDescribeType())	type = "DESCRIBE";
		if(query.isSelectType())	type = "SELECT";
		
		return type;
	}
	
/*
 * 
 */
	public Query getQuery() {
		return this.uniqueQuery;
	}
}	



