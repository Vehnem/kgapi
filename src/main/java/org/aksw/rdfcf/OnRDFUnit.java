package org.aksw.rdfcf;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/* 
 * Use RDF Unit over the data sets
 */

/**
 * 
 * @author marvin
 *
 */
public class OnRDFUnit {

	private static final Logger log = LoggerFactory.getLogger(Scheduler.class);

	/**
	 * Call RDFUnit over Runtime
	 * 
	 * @param params
	 */
	public String runRDFUnit_cmdline(String params) {
		String result = "";

		String[] cmd = { "/bin/sh", "-c", "cd ../RDFUnit-0.8; bin/rdfunit " + params };
		log.info(Arrays.toString(cmd));
		try {
			log.info("RDFUnit start");
			Process p = Runtime.getRuntime().exec(cmd);

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				result += inputLine;
			}
			in.close();
			log.info("RDFUnit finished");
			return result;

		} catch (IOException e) {
			return "RDFUnit failed to start";
		}
	}

	public void runRDFUnit() {

	}

	/*
	 * bin/rdfunit
	 * 
	 * -d Path to File -v no LOV
	 * 
	 */

	// Runtime call example
	public void foo() {
		try {
			String result = "";
			String[] cmd = { "/bin/sh", "-c", "cd ../RDFUnit-0.8; bin/rdfunit -h" };
			Process p = Runtime.getRuntime().exec(cmd);

			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String inputLine;
			while ((inputLine = in.readLine()) != null) {
				System.out.println(inputLine);
				result += inputLine;
			}
			in.close();

			System.out.println(result);

		} catch (IOException e) {
			System.out.println(e);
		}
	}
}
