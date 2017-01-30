package org.aksw.rdfcf;
import java.util.ArrayList;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * Shows RDF specific information to properties (predicates) and datatypes (objects)
 * 
 * @author Marvin Hofer
 */
public class RDFAnalyze {

/**
 * model from loaded file
 */
	private Model model;
	
/**
 * Constructor loads the RDF data-model
 * 
 */
	public RDFAnalyze(Model model){
		this.model = model;
	}
	
/**
 * Lists the possible properties that can be filter 
 * 
 * @return possible properties in the RDF file
 */
	public ArrayList<String> possibleProperties() {
		
		ArrayList<String> properties = new ArrayList<String>();	
		StmtIterator iter = model.listStatements();
		
		//Serialized search in the RDF data
		while ( iter.hasNext() ) {
			
		    Statement stmt      = iter.nextStatement();  
		    Property  predicate = stmt.getPredicate();   

		    //Only properties for the subject not the subject itself
		    if ( !properties.contains(predicate.toString() ) 
		    		&& !predicate.toString().equals("http://www.w3.org/1999/02/22-rdf-syntax-ns#type")) {
		    	
		    	properties.add(predicate.toString());
		    } 
		}
		return properties;
	}
	
	

/**
 * Counts data types of properties
 * 
 * @param	property 	counts the datatypes of the specific property
 * @return				table of datatypes and her amount
 */
	public String[][] countDatatype(String property) {
		
		StmtIterator iter = model.listStatements();
		
		ArrayList<String> datatypes = new ArrayList<String>();
		ArrayList<Long>	counter_datatypes = new ArrayList<Long>();
		
		//Serialized search in the RDF data
		while (iter.hasNext()) {
		    Statement stmt      = iter.nextStatement(); 
		    Property  predicate = stmt.getPredicate();   
		    RDFNode   object    = stmt.getObject();     
		    
		    if(object.isLiteral() && predicate.toString().equals(property)) {
		    	String datatype = object.asLiteral().getDatatypeURI();
		    	
		    	if(!datatypes.contains(datatype)){
		    		datatypes.add(datatype);
		    		counter_datatypes.add((long) 1);
		    	}
		    	else {
		    		int list_pos = datatypes.indexOf(datatype);
		    		
		    		long tmp = (counter_datatypes.get(list_pos) + 1);
		    		counter_datatypes.set(list_pos, tmp);
		    	}
		    } else if(predicate.toString().equals(property)) {
		    	if(!datatypes.contains("Unknown Type")){
		    		datatypes.add("Unknown Type");
		    		counter_datatypes.add((long) 1);
		    	}
		    	else {
		    		int list_pos = datatypes.indexOf("Unknown Type");
		    		counter_datatypes.set(list_pos, counter_datatypes.get(list_pos)+1);
		    	}
		    }
		 }
		
		String[][] out = new String[datatypes.size()][2];
		
		for(int i = 0; i < datatypes.size(); i++) {
			out[i][0] = datatypes.get(i);
			out[i][1] = counter_datatypes.get(i).toString();
		}
		
		return out;
	}

/*
 * Terminate model
 */
	public void teminate() {
		model.close();
	}
}
