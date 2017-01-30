package org.aksw.rdfcf;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.UUID;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;

/**
 * Data Storage in Filesystem
 * 
 * @author marvin
 *
 */
public final class SingletonFileStore implements StoreInterface {
  
	/*
	 * Init
	 */
	private static SingletonFileStore instance = null;
    private SingletonFileStore() {}
    public static synchronized SingletonFileStore getInstance() {
        if (instance == null) {
        	instance = new SingletonFileStore();
        }
        return instance;
    }
    

    /*
     * Management
     */
    
    private String rootLocation = "./RDF_DATA/";
    
	public String addRDFData(Model model) {
		
		FileOutputStream fileStream;
		OutputStreamWriter outputWriter;
		String dataKey = UUID.randomUUID().toString();
		
		try{
		new File(rootLocation+dataKey).mkdirs();
		fileStream = new FileOutputStream(new File(rootLocation+dataKey+"/dataset.nt"));
		outputWriter = new OutputStreamWriter(fileStream, "UTF-8");
		
		model.write(outputWriter, "N-TRIPLES");
		} catch (IOException e){
			
		}
		return dataKey;
	}
	
	//TODO
	public void addRDFData(Model model, String datakey) {
		// TODO Auto-generated method stub
		
	}
	
	public Model getRDFData(String datakey) {
		
		Model model = ModelFactory.createDefaultModel();
		
		model.read(rootLocation+datakey+"/dataset.nt", "N-TRIPLES");
		return model;
	}

	public void deleteRDFData(String datakey) {
		
		File folder = new File("./RDF_DATA/"+datakey);
		
		deleteFolder(folder);
	}
	
	public void autoDelete() {
		File rootdir = new File("./RDF_DATA/");
		
		String[] subdirs = rootdir.list();

		for(Object subdir : subdirs) {
	
			File m = new File("./RDF_DATA/"+subdir.toString());
	
			long time_diff = Long.valueOf(new SimpleDateFormat("mm").format(System.currentTimeMillis() - m.lastModified()));
	
			if(time_diff > 30) {
				if( false == subdir.toString().equals( "README.md") 
						|| false == subdir.toString().equals(".gitkeep") ) deleteFolder(m);
				//log.info("Deleted : "+subdir.toString());
			} else {
				//log.info((30 -time_diff)+" min remain for "+subdir.toString());
			}
		}
	}
	
	public void cleanStore() {
		File rootdir = new File("./RDF_DATA/");
		
		String[] subdirs = rootdir.list();
		
		for(Object subdir_str : subdirs) { 
			
			File subdir = new File("./RDF_DATA/"+subdir_str.toString());
			
			deleteFolder(subdir);
		}
	}
	
	public void addRDFDataResult(String datakey, Model model) {
		FileOutputStream fileStream;
		OutputStreamWriter outputWriter;
		
		try{
		new File(rootLocation+datakey).mkdirs();
		fileStream = new FileOutputStream(new File(rootLocation+datakey+"/result_dataset.nt"));
		outputWriter = new OutputStreamWriter(fileStream, "UTF-8");
		
		model.write(outputWriter, "N-TRIPLES");
		} catch (IOException e){
			e.printStackTrace();
		}
	}

	public Model getRDFDataResult(String datakey) {

		Model model = ModelFactory.createDefaultModel();
		model.read(rootLocation+datakey+"/result_dataset.nt", "N-TRIPLES");	
		return model;
	}
    
	/*
	 * Helper
	 */
	private static void deleteFolder(File folder) {
        File[] files = folder.listFiles();
       
        if (null != files) { //some JVMs return null for empty dirs
            
        	for (File f: files) {
                
        		if (f.isDirectory()) {
        			
                    deleteFolder(f);
                
        		} else {
        			
        			f.delete();
        			
                }
            }
        }
        
     // has to delete file or empty folder
        folder.delete();
	}
}

//public final abstract class SingletonStore {
//	static boolean useMemory = false;
//	
//	static SingletonStore instance = null;
//
//	Lock lock = new ReentrantLock(true);
//
//	private SingletonStore() {}
//
//	public static void init(final boolean useMemory) { SingletonStore.useMemory = useMemory; }
//	
//	public static SingletonStore getInstance() {
//		lock.lock();
//		try {
//			if (null == instance) {
//				instance = useMemory ? new ChildMemory() : new ChildFile();
//			}
//
//			return instance;
//		} finally {
//			lock.unlock();
//		}
//	 }
//
//	public abstract void delete();
//}
//
//public class ChildMemory extends SingletonStore {
//
//	Override
//	public void delete() {}
//}
//
//public class ChildFile extends SingletonStore {
//
//	Override
//	public void delete() {}
//}