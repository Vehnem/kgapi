package org.aksw.rdfcf;

import org.apache.jena.query.Query;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.QueryFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.WebContent;
import org.apache.jena.sparql.engine.http.QueryEngineHTTP;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Query a construct query to get RDF data from the 
 * endpoint, without endpoint limit problems
 */

public class RDFGetter {
	
	/**
	 * Logger
	 */
	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);
	
	
	/**
	 * Constructor
	 */
	public RDFGetter() {
		
	}
	
		
	/** UNUSED
	 * 
	 * Package-Size
	 * 
	 * @param queryString
	 * @return
	 */
	private static long getPackagesize(String queryString) {
		return 0;
	}
	
	/**
 	* Store the RDF N_Triples file from the construct query without limit problems
 	* 
 	* @param queryString query
 	* @param file_location location of the dataset
 	*/
	public Model getRDF(final String queryString, final String endpoint, final long endpointLimit) {
		
		// Declare
	
		Model container = ModelFactory.createDefaultModel();
		
		Query query = QueryFactory.create(queryString);
		QueryEngineHTTP queryExec;
		Model model = ModelFactory.createDefaultModel();
		
		//
		long packetsize = getPackagesize("") + endpointLimit/10 ; // TODO function
		
		//Analyse Limit
		long limit = 0;
		boolean noLimit = true;
		if(query.hasLimit()){
			noLimit = false;
			limit = query.getLimit();
			if(limit > packetsize) {
				query.setLimit(packetsize);
			}
		} else {
			query.setLimit(packetsize);
		}
		
		//Analyse offset
		long start_offset;
		if(query.hasOffset()){
			 start_offset = query.getOffset();
		}
		else {
			 start_offset = 0;
		}
		
		//execute query
		try {
			do {
				
				query.setOffset(start_offset);
				
				queryExec = QueryExecutionFactory.createServiceRequest(endpoint, query);
				queryExec.setModelContentType(WebContent.contentTypeJSONLD);
			
				model= queryExec.execConstruct();
			
				start_offset += packetsize;
			
				if(limit > 0) {
					limit -= packetsize;
					if(limit < packetsize) {
					query.setLimit(limit);
					}
				}
			
				container.add(model);
				
			} while(!model.isEmpty() && (limit > 0 || noLimit));
			
			model.close();
		}
		catch ( Exception e ) {
			e.printStackTrace(System.out);
			log.info("failed to get RDF-data");
			
		} 
		
		return container;
		
	}
}
