package org.aksw.rdfcf;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Program configuration
 * 
 * @author marvin
 *
 */
public final class SingletonStore implements StoreInterface {

	private static SingletonStore instance = null;
	
	private static StoreInterface storeInstance;

	private SingletonStore() {
	}

	public static synchronized SingletonStore getInstance() {
		if (instance == null) {
			instance = new SingletonStore();
		}
		return instance;
	}

	/**
	 * Use Memory or File system
	 */
	static boolean useMemory = false;

	/**
	 * Init useMemory
	 * 
	 * @param useMemory
	 */
	public void init(final boolean useMemory) {

		SingletonStore.useMemory = useMemory;

		if (false == useMemory) {
			SingletonStore.storeInstance = SingletonFileStore.getInstance();
		} else {
			SingletonStore.storeInstance = SingletonMemoryStore.getInstance();
		}
	}

	/*
	 * Management
	 */



	public String addRDFData(Model model) {
		return SingletonStore.storeInstance.addRDFData(model);
	}
	

	public void addRDFData(Model model, String datakey) {
		SingletonStore.storeInstance.addRDFData(model, datakey);
	}

	
	public Model getRDFData(String datakey) {
				return SingletonStore.storeInstance.getRDFData(datakey);
	}


	public void deleteRDFData(String datakey) {
		SingletonStore.storeInstance.deleteRDFData(datakey);
	}


	public void autoDelete() {
		SingletonStore.storeInstance.autoDelete();
		
	}

	public void cleanStore() {
		SingletonStore.storeInstance.cleanStore();
		
	}

	public void addRDFDataResult(String datakey, Model model) {
		SingletonStore.storeInstance.addRDFDataResult(datakey, model);
		
	}

	public Model getRDFDataResult(String datakey) {
		return SingletonStore.storeInstance.getRDFDataResult(datakey);
	}
	
	//TODO not removable
	public void loadExample() {
		Model raw_data = ModelFactory.createDefaultModel();
		Model res_data = ModelFactory.createDefaultModel();
		
		raw_data.read("./RDF_EXAMPLES/film_runtime_100/dataset.nt","N-TRIPLES");
		res_data.read("./RDF_EXAMPLES/film_runtime_100/result.nt","N-TRIPLES");
		
		SingletonStore.storeInstance.addRDFData(raw_data, "example");
		SingletonStore.storeInstance.addRDFDataResult("example", res_data);
				
	}
}
