package org.aksw.rdfcf;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import org.apache.jena.rdf.model.Model;

/**
 * RDFStore in Memory w/o cache
 * 
 * @author marvin
 *
 */
public class SingletonMemoryStore implements StoreInterface {

	/*
	 * Initialization
	 */
	private static SingletonMemoryStore instance = null;

	private SingletonMemoryStore() {
	}

	public static synchronized SingletonMemoryStore getInstance() {
		if (instance == null) {
			instance = new SingletonMemoryStore();
		}
		return instance;
	}
	
	
	/**
	 * Maps a modelKey to in memory creation time of a RDFModel
	 */
	private HashMap<String, Long> modelTimes = new HashMap<String, Long>();
	
	/**
	 * Maps a Key to a RDFModel 
	 */
	private HashMap<String, Model> models = new HashMap<String, Model>();
	
	/**
	 * 
	 */
	private HashMap<String, Model> resModels = new HashMap<String, Model>(); 
	

	/*
	 * Management
	 */

	public String addRDFData(Model model) {
		String datakey = UUID.randomUUID().toString();
		
		//Unique 
		while(models.containsKey(datakey)) {
			datakey = UUID.randomUUID().toString();
		}
		
		//addModel
		models.put(datakey, model);
				
		//addModelTime
		modelTimes.put(datakey, System.currentTimeMillis());

		return datakey;
	}

	public void addRDFData(Model model, String datakey) {
		//addModel
		models.put(datakey, model);
				
		//addModelTime
		modelTimes.put(datakey, System.currentTimeMillis()*2);
	}
	
	public Model getRDFData(String datakey) {
		
		return models.get(datakey);
	}

	public void deleteRDFData(String datakey) {
		
		// log.info("delete "+datatkey);
		models.remove(datakey);
		resModels.remove(datakey);
		
	}

	@SuppressWarnings("rawtypes")
	public void autoDelete() {

		long time_now = System.currentTimeMillis();
		
		long maxdiff = 15*60*60;
		
		Iterator<?> it = modelTimes.entrySet().iterator();
	    while (it.hasNext()) {
	        Map.Entry pair = (Map.Entry) it.next();
	        //System.out.println(pair.getKey() + " = " + pair.getValue());
	        //it.remove(); // avoids a ConcurrentModificationException
	        
	        String datakey = (String) pair.getKey();
	        long settime = (Long) pair.getValue();
	        
	        if( maxdiff < time_now - settime ) {
	        	models.remove(datakey);
	        	resModels.remove(datakey);
	        	modelTimes.remove(datakey);
	        }
	    }
	    //TODO logger
	}
	
	public void cleanStore() {
		
		models.clear();
		resModels.clear();
		modelTimes.clear();
		
	}

	public void addRDFDataResult(String datakey, Model model) {
		
		if( false == resModels.containsKey(datakey)) {
		resModels.put(datakey, model);
		} else {
			resModels.put(datakey, model);
		}
	}

	public Model getRDFDataResult(String datakey) {
	
		return resModels.get(datakey);
	}
	
}
