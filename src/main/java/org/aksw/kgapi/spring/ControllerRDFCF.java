package org.aksw.kgapi.spring;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aksw.rdfcf.RDFAnalyze;
import org.aksw.rdfcf.RDFFilter;
import org.aksw.rdfcf.RDFGetter;
import org.aksw.rdfcf.Scheduler;
import org.aksw.rdfcf.SingletonStore;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


/**
 * Rest-controller for RDFConsistencyFilter
 * 
 * TODO JSON or POJO Response
 * 
 * only encoded queries supported (because of <...> parts)
 */
@RestController
@RequestMapping(value = "/rdfcf")
public class ControllerRDFCF {

	// private final String defaultSelQuery = "prefix dbo:
	// <http://dbpedia.org/ontology/>"+
	// "prefix dbp: <http://dbpedia.org/property/>"+
	// "select * where { ?film a dbo:Film. ?film dbp:runtime ?runtime.} Limit
	// 100";

	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	static final String VERSION = "v1";

	private final String defaultConQuery = "empty";

	/**
	 * Get RDF from a Sparql-endpoint over a CONSTRUCT Query and save the result
	 * at the file-system
	 * 
	 * @param endpoint
	 * @param limit
	 * @param query
	 * @return
	 */
	@RequestMapping(value = "/query", method = RequestMethod.POST, produces = { "application/v1+json" })
	public @ResponseBody String queryRDF(
			@RequestParam(value = "endpoint", required = true, defaultValue = "") String endpoint,
			@RequestParam(value = "limit", required = true, defaultValue = "") long limit,
			@RequestParam(value = "query", required = true, defaultValue = "") String query) {
		try {
			Model model = new RDFGetter().getRDF(query, endpoint, limit);

			SingletonStore store = SingletonStore.getInstance();

			String datakey = store.addRDFData(model);
			log.info("loaded");
			return "{ \"datakey\" : \"" + datakey + "\" }";
		} catch (Exception e) {
			return "{ \"datakey\" : \"\" }";
		}
	}

	// /**
	// * Import existing RDF-data-file
	// *
	// * @return
	// */
	// @ApiOperation( value = "import" , nickname = "importRDF" )
	// @RequestMapping(value="import", method=RequestMethod.POST)
	// public @ResponseBody String importRDF_upload() {
	// return "UNUSED";
	// }

	/**
	 * Returns the result data-set
	 * 
	 * @param datakey
	 * @param format
	 * @return
	 */
	@RequestMapping(value = "/show/{datakey}", method = RequestMethod.GET, produces = { "application/v1+json" })
	public @ResponseBody String showRDF(@PathVariable String datakey,
			@RequestParam(value = "format", defaultValue = "TURTLE") String format) {

		SingletonStore store = SingletonStore.getInstance();

		Model model = ModelFactory.createDefaultModel();

		model = store.getRDFDataResult(datakey);

		if (model == null) {
			log.info("model ist ####");
		}

		ByteArrayOutputStream os = new ByteArrayOutputStream();

		if (format.equals("TURTLE")) {
			try {
				model.write(os, "TURTLE");
				return os.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else if (format.equals("N-TRIPLES")) {
			try {
				model.write(os, "N-TRIPLES");
				return os.toString();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return "Unknown Format";
		}
	}

	/**
	 * 
	 * Analyze a RDF data-set
	 * 
	 * @param datakey
	 * @return
	 */
	@RequestMapping(value = "/analyze/{datakey}", method = RequestMethod.GET, produces = { "application/v1+json" })
	public @ResponseBody String analyzeRDF(@PathVariable(value = "datakey") String datakey) {

		String response = "{ ";
		SingletonStore store = SingletonStore.getInstance();
		RDFAnalyze ra = new RDFAnalyze(store.getRDFData(datakey));

		ArrayList<String> properties = ra.possibleProperties();

		for (int i = 0; i < properties.size(); i++) {
			String[][] datatypes = ra.countDatatype(properties.get(i));

			response += " \"properties\" : \"" + properties.size() + "\"" + " , \"" + i + "\" : { "
					+ " \"property\" : \"" + properties.get(i) + "\" ," + " \"numberofdatatypes\" : \""
					+ datatypes.length + "\" ," + " \"datatypes\" : {";

			for (int k = 0; k < datatypes.length; k++) {

				response += " \"" + k + "\" : {" + " \"name\" : \"" + datatypes[k][0] + "\" ," + " \"value\" : \""
						+ datatypes[k][1] + "\" }";
				if ((k + 1) != datatypes.length) {
					response += " ,";
				} else {
					response += " }";
				}
			}

			if ((i + 1) != properties.size()) {
				response += " } ,";
			} else {
				response += " } }";
			}
		}

		return response;
	}

	/**
	 * Filter the RDF-data
	 * 
	 * @param datakey
	 * @param property
	 * @param datatypes
	 * @param remove_duplicates
	 * @param consistent
	 * @return
	 */
	@RequestMapping(value = "/filter/{datakey}", method = RequestMethod.POST, produces = { "application/v1+json" })
	public @ResponseBody String filterRDF(@PathVariable String datakey,
			@RequestParam(value = "filter[]", defaultValue = "http://dbpedia.org/property/runtime;http://www.w3.org/2001/XMLSchema#integer") String[] filter,
			@RequestParam(value = "remove_duplicates", defaultValue = "true") boolean remove_duplicates,
			@RequestParam(value = "consistent", defaultValue = "true") boolean consistent,
			@RequestParam(value = "rdfunit_params", defaultValue = "") String rdfunit_params) {

		SingletonStore store = SingletonStore.getInstance();
		RDFFilter rf = new RDFFilter(store.getRDFData(datakey));

		List<String> propertylist = new ArrayList<String>();
		List<List<String>> datatypelist = new ArrayList<List<String>>();

		log.info(Arrays.toString(filter));

		for (String f : filter) {
			String[] splited = f.split(";");
			List<String> types = new ArrayList<String>();
			for (int i = 0; i < splited.length; i++) {
				if (i == 0) {
					propertylist.add(splited[i]);
				} else {
					types.add(splited[i]);
				}
			}
			datatypelist.add(types);
		}

		try {
			String rdfunit_msg = "skipped";
			Model model = rf.new_filter2(propertylist, datatypelist, remove_duplicates, consistent);

			store.addRDFDataResult(datakey, model);

			// //RDFUnit TODO
			// if( false == rdfunit_params.equals("skip")) {
			// OnRDFUnit rdfu = new OnRDFUnit();
			// String params = "-d
			// ../RDFConsistencyFilter/RDF_EXAMPLES/film_runtime_100/result_dataset.nt";
			// rdfunit_msg = rdfu.runRDFUnit_cmdline(params);
			// }
			return "{ \"message\" : \"filtered\" , \"rdfunit\" : \"" + rdfunit_msg + "\"}";
		} catch (Exception e) {
			return "{ \"message\" : \"failed\" }";
		}

		// try( final ByteArrayOutputStream os = new ByteArrayOutputStream() ){
		// model.write(os, "N-TRIPLES");
		// return os.toString();
		// } catch (IOException e) {
		// e.printStackTrace();
		// return null;
		// }
	}

	/**
	 * deletes a data-set
	 * 
	 * @param datakey
	 */
	@RequestMapping(value = "/delete/{datakey}", method = RequestMethod.DELETE, produces = { "application/v1+json" })
	public String deleteDataset(@PathVariable(value = "datakey") String datakey) {

		SingletonStore store = SingletonStore.getInstance();

		try {
			store.deleteRDFData(datakey);
			return "{ \"message\" : \"deleted\" }";
		} catch (Exception e) {
			return "{ \"message\" : \"failed\" }";
		}

	}

	@RequestMapping(value = "/test", method = RequestMethod.GET, produces = { "application/v1+json" })
	public String test() {

		SingletonStore store = SingletonStore.getInstance();

		return "rdfcf controller test";

	}
}