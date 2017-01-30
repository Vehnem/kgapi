package org.aksw.kgapi.spring;


import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.aksw.solr.Autocomplete;
import org.apache.solr.client.solrj.SolrServerException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import aksw.org.kg.KgException;
import aksw.org.sdw.kg.handler.solr.KgSolrResultDocument;
import aksw.org.sdw.kg.handler.solr.SolrHandler;
import aksw.org.sdw.kg.handler.solr.SolrHandler.AnnotationInfo;
import aksw.org.sdw.kg.handler.solr.SolrHandler.TAGGER_ANNOTATION_OVERLAP;
import aksw.org.sdw.kg.handler.solr.SolrHandler.TAGGER_LANGUAGE;
import io.swagger.annotations.ApiParam;

/**
 * Spring REsT controller for KG Utils
 * 
 * @author marvin
 *
 */
@RestController
@RequestMapping(value = "/kgutil")
public class ControllerKGUtil {

	static final String VERSION = "v1";
	
	static String solrUrl = "http://chushayashi.unbelievable-machine.net:10083/solr/companies";
	
//    private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	
	/**
	 * 
	 * @param entityName
	 * @return
	 * @throws KgException
	 * @throws IOException
	 */
	@RequestMapping(value = "getEntitiesFromText", method = RequestMethod.GET, produces = "application/"+VERSION+"+json")
	public Set<AnnotationInfo> getEntitiesFromText(
			@ApiParam(value = "query to suggest", required = true, defaultValue="Berlin is a great city in Germany") @RequestParam(value="text",required=true, defaultValue="Berlin is a great city in Germany")String text,
			@ApiParam(value = "SOLR filter queries (e.g. filter on type) ", required = false) @RequestParam(value="fq",required=false, defaultValue="")String[] fq,
			@ApiParam(value = "fields which are required from the SOLR document", required = false) @RequestParam(value="rf",required=false, defaultValue="")String[] rf,
			@ApiParam(value = "language to suggest", required = true, defaultValue="ENGLISH") @RequestParam(value="lang",required=true, defaultValue="ENGLISH")String lang,
			@RequestParam(value="ovlap",required=true, defaultValue="ALL")String ovlap
			) throws KgException, IOException {
		
		TAGGER_LANGUAGE t_lang = TAGGER_LANGUAGE.ENGLISH;
		
		if(lang == "GERMAN") {
			t_lang = TAGGER_LANGUAGE.GERMAN;
		}
		
		TAGGER_ANNOTATION_OVERLAP t_ovlap = TAGGER_ANNOTATION_OVERLAP.ALL;
		
		if(ovlap == "NO_SUB") {
			t_ovlap = TAGGER_ANNOTATION_OVERLAP.NO_SUB;
		}
		else if (ovlap == "LONGEST_DOMINANT_RIGHT"){
			t_ovlap = TAGGER_ANNOTATION_OVERLAP.LONGEST_DOMINANT_RIGHT;
		}
		
		List<String> fq_list = Arrays.asList(fq);
		
		Set<String> rf_set = new HashSet<String>(Arrays.asList(rf));
		
		SolrHandler solrHandler = new SolrHandler(solrUrl);

		Map<AnnotationInfo, List<KgSolrResultDocument>> result = solrHandler.getNamedEntitiesFromText(text, fq_list, rf_set,
				  t_lang, t_ovlap);
		
		solrHandler.close();

		return result.keySet();
	}
	
	@RequestMapping(value="suggest", method = RequestMethod.GET )
	public Map<String, List<String>> autocomplete(
			@ApiParam(value = "query to suggest", required = true, defaultValue="Deut") @RequestParam(value="query",required=false, defaultValue="Deut")String query,
			@ApiParam(value = "suggester to suggest", required = true, defaultValue="DeSuggester") @RequestParam(value="suggester",required=false, defaultValue="DeSuggester")String[] suggester 
			) throws SolrServerException, IOException {
		
		Autocomplete sugg = new Autocomplete(solrUrl);
		
		return sugg.executeSuggest(query, suggester);
	}
	
	/**
	 * 
	 * LocationMapping of companies
	 * 
	 * @param language
	 * @param name
	 * @return
	 * @throws SolrServerException
	 * @throws IOException
	 * @throws KgException
	 */
	@RequestMapping(value="location", method = RequestMethod.GET )
	public List<String> getlocation(
			@ApiParam(value = "language { de, en }", required = true, defaultValue="en") @RequestParam(value="language",required=false, defaultValue="en")String language,
			@ApiParam(value = "name", required = true, defaultValue="Siemens") @RequestParam(value="name",required=false, defaultValue="Siemens")String name
			) throws SolrServerException, IOException, KgException {
		
		SolrHandler solrHandler = new SolrHandler(solrUrl);

		List<KgSolrResultDocument> results = null;

		String inputlang = "nameEn";
		
		if( "de".equals(language) ) {
			inputlang = "nameDe";
		}
			
		
		results = solrHandler.executeQuery(inputlang+":\""+name+"\"", null);
		
		solrHandler.close();
		
		List<String> ou = new ArrayList<String>();
		
		for(KgSolrResultDocument d : results) {
			
			List<String> fv = d.getFieldValueAsStringList("locationLatLon");
			
			if(null != fv) {
				for(String v : fv) {
					ou.add(v);
				}
			}
		}
		
		return ou;
	}
	
}
