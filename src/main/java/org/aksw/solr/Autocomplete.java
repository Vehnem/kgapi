package org.aksw.solr;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.SuggesterResponse;

public class Autocomplete {
	
	SolrClient solrClient;
	
	public Autocomplete(String solrUrl) {
		solrClient = new HttpSolrClient(solrUrl);
	}
	
	public Map<String, List<String>> executeSuggest(String query, String[] suggester) throws SolrServerException, IOException {
	
		SolrQuery solrQuery = new SolrQuery();
		
		solrQuery.setRequestHandler("/suggest");
		
		solrQuery.setRows(10);
		
		solrQuery.set("suggest", "true");
		
		solrQuery.set("suggest.dictionary", suggester);
		
		solrQuery.set("suggest.q", query);
		
		QueryResponse re = solrClient.query(solrQuery);
		
		SuggesterResponse sr = re.getSuggesterResponse();
		
		return sr.getSuggestedTerms();
	}

}
