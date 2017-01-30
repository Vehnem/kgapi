package org.aksw.rdfcf;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;

/**
 * RDFFilter Removes non valid datatypes
 * 
 */
public class RDFFilter {

	/**
	 * Model of the RDF-data
	 */
	private Model dataModel;

	/**
	 * Constructor
	 * 
	 * @param dataModel
	 *            RDF-InMemory Model
	 */
	public RDFFilter(Model dataModel) {
		this.dataModel = dataModel;
	}

	/**
	 * Filter for property and data-type
	 * 
	 * @param property
	 *            predicate/property
	 * @param datatype
	 * @param remove_duplicates
	 *            remove
	 * @param consistent
	 *            only Resources with this property/datatype after filtering
	 * 
	 * @return filtered RDF-data
	 * 
	 */
	public Model new_filter(final String property, final List<String> datatypes, final boolean remove_duplicates,
			final boolean consistent) {

		Model new_model = ModelFactory.createDefaultModel();
		StmtIterator iter = dataModel.listStatements();
		String temp_subject = "";
		boolean contains_property = false;
		List<Statement> stmtlist = new ArrayList<Statement>();

		while (iter.hasNext()) {

			// Triple
			Statement stmt = iter.nextStatement();
			// Subject
			Resource subject = stmt.getSubject();
			// Predicate
			Property predicate = stmt.getPredicate();
			// Object
			RDFNode object = stmt.getObject();

			if (false == temp_subject.equals(subject.toString())) {

				temp_subject = subject.toString();

				if (contains_property) {

					new_model.add(stmtlist);
					contains_property = false;

				} else if (false == consistent) {

					new_model.add(stmtlist);

				}

				stmtlist = new ArrayList<Statement>();
			}

			// 0 0 | 1 # 0 0 | 1
			// 0 1 | 1 # 0 1 | 0
			// 1 0 | 1 # 1 0 | 1
			// 1 1 | 0 # 1 1 | 1
			//
			// -(a und b) # (a oder -b)
			// (-a oder -b #

			if (predicate.toString().equals(property)) {

				if ((false == contains_property) || (false == remove_duplicates)) {

					if (object.isLiteral() && datatypes.contains(object.asLiteral().getDatatypeURI().toString())) {

						stmtlist.add(stmt);
						contains_property = true;

					} else if (datatypes.contains(object.toString())) {

						stmtlist.add(stmt);
						contains_property = true;
					}
				}
			} else {

				stmtlist.add(stmt);
			}

			// naive for last Resource
			if (false == iter.hasNext()) {

				if (contains_property) {

					new_model.add(stmtlist);
					contains_property = false;

				} else if (false == consistent) {

					new_model.add(stmtlist);
				}
			}
		}
		return new_model;
	}

	/**
	 * With Multiselect
	 * 
	 * @param property
	 * @param datatypes
	 * @param remove_duplicates
	 * @param consistent
	 * @return
	 */
	public Model new_filter2(final List<String> properties, final List<List<String>> datatypes, final boolean remove_duplicates,
			final boolean consistent) {

		Model new_model = ModelFactory.createDefaultModel();
		StmtIterator iter = dataModel.listStatements();
		String temp_subject = "";
		boolean contains_property = false;
		List<Statement> stmtlist = new ArrayList<Statement>();

		while (iter.hasNext()) {

			// Triple
			Statement stmt = iter.nextStatement();
			// Subject
			Resource subject = stmt.getSubject();
			// Predicate
			Property predicate = stmt.getPredicate();
			// Object
			RDFNode object = stmt.getObject();

			if (false == temp_subject.equals(subject.toString())) {

				temp_subject = subject.toString();

				if (contains_property) {

					new_model.add(stmtlist);
					contains_property = false;

				} else if (false == consistent) {

					new_model.add(stmtlist);

				}

				stmtlist = new ArrayList<Statement>();
			}

		
			//Logic
			//TODO contains_property need to be a list each propertie which was added should be there
			// and if not added contains gives false
			
			String str_p = predicate.toString();
			//if (predicate.toString().equals(property)) {
			if (properties.contains(str_p)) {
				
				int index = properties.indexOf(str_p);
				
				if ((false == contains_property) || (false == remove_duplicates)) {
					
					//if (object.isLiteral() && datatypes.contains(object.asLiteral().getDatatypeURI().toString())) {
					if (object.isLiteral() && datatypes.get(index).contains(object.asLiteral().getDatatypeURI().toString())) {

						stmtlist.add(stmt);
						contains_property = true;
					
					//} else if (datatypes.contains(object.toString())) {
					} else if (datatypes.get(index).contains(object.toString())) {

						stmtlist.add(stmt);
						contains_property = true;
					}
				}
			} else {

				stmtlist.add(stmt);
			}

			// naive for last Resource
			if (false == iter.hasNext()) {

				if (contains_property) {

					new_model.add(stmtlist);
					contains_property = false;

				} else if (false == consistent) {

					new_model.add(stmtlist);
				}
			}
		}
		return new_model;
	}

	public Model getModel() {
		return this.dataModel;
	}

	/**
	 * Creates a table from a String with properties and datatypes
	 * 
	 * @param input
	 * @return
	 */
	public List<HashMap<String, List<String>>> get_pos(String input) {

		String[] pos = input.split(";");

		List<HashMap<String, List<String>>> filter = new ArrayList<HashMap<String, List<String>>>();

		for (String po : pos) {

			HashMap<String, List<String>> po_map = new HashMap<String, List<String>>();

			String[] splited_po = po.split(":");

			po_map.put(splited_po[0], Arrays.asList(splited_po[1].split(",")));

		}

		return filter;
	}

	public List<String> get_p(String input) {

		String[] pos = input.split(";");

		List<String> ps = new ArrayList<String>();

		for (String po : pos) {

			ps.add(po.split(":")[0]);
		}

		return ps;
	}

	public List<List<String>> get_o(String input) {

		String[] pos = input.split(";");

		List<List<String>> os = new ArrayList<List<String>>();

		for (String po : pos) {

			os.add(Arrays.asList(po.split(":")[1].split(",")));
		}

		return os;
	}
}
