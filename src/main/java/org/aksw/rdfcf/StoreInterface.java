package org.aksw.rdfcf;

import org.apache.jena.rdf.model.Model;

 /**
  * Interface for a RDFStore Class
  * 
  * @author marvin
  *
  */
public interface StoreInterface {
	
	/**
	 * Adds RDFData as model object to store
	 * 
	 * @param model
	 * @return
	 */
	public String addRDFData(Model model);
	
	public void addRDFData(Model model, String datakey);
	
	/**
	 * Get RDFData from store
	 * 
	 * @return
	 */
	public Model getRDFData(String datakey);
	
	/**
	 * removes RDFData from store
	 * 
	 * @param datakey
	 */
	public void deleteRDFData(String datakey);
	
	/**
	 * delete stored RDFData > 30min
	 */
	public void autoDelete();
	
	/**
	 * Cleanup
	 */
	public void cleanStore();
	
	/**
	 * Needed?
	 * 
	 * @param datakey
	 * @param model
	 */
	public void addRDFDataResult(String datakey, Model model);
	
	/**
	 * Needed?
	 * 
	 * @param datakey
	 * @return
	 */
	public Model getRDFDataResult(String datakey);
}
