package test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.pep.Obligation;
import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.openliberty.openaz.pdp.provider.AzServiceFactory;
import org.openliberty.openaz.pdp.provider.SimpleConcreteDummyService;
import org.openliberty.openaz.pdp.provider.SimpleConcreteSunXacmlService;

import com.sun.xacml.ParsingException;
import com.sun.xacml.UnknownIdentifierException;

/**
 * TestUtils provides helper methods for analyzing test
 * results.
 * TBD: separate out logging and system.out.println's
 */
public class TestUtils {
	Log log = LogFactory.getLog(this.getClass()); 
	
	/**
	 * This methods gets the Obligations from the PepResponse
	 * and prints/logs them.
	 * @param pepRsp
	 * @throws PepException
	 */
	public void logObligations(PepResponse pepRsp) 
			throws PepException {
		if (pepRsp != null) {
			if (log.isTraceEnabled()) log.trace(
					"\nTestUtils: Strings; allowed() = " +
						pepRsp.allowed());
			// Get the Map of ObligationId, Obligation pairs
			Map<String,Obligation> obligations =
				pepRsp.getObligations();
			// Get an Iterator of the Map's keySet
			Iterator<String> itObligationIds = 
				obligations.keySet().iterator();
			// Outer loop is over each Obligation
			int numObligations = 0;
			while (itObligationIds.hasNext()){
				numObligations++;
				// The Iterator returns a key which is an ObligationId
				String itObligationId = itObligationIds.next();
				if (log.isTraceEnabled()) log.trace(
					"\n    itObligationId = " + itObligationId);
				// Get the Obligation associated w that ObligationId
				Obligation obligation = 
					obligations.get(itObligationId);
				String obligationId = obligation.getObligationId();
				if (log.isTraceEnabled()) log.trace(
					"\n    obligationId (from obl obj): " + 
						obligationId);
				
				// The Obligation, itself, contains a Map of 
				//  attributeId, value pairs:
				Map<String,String> oblAttrs = 
					obligation.getStringValues();
				// Print the attrId,attr pairs in one stmt:
				if (log.isInfoEnabled()) log.info(
					"\n    ObligationId: " + obligationId +
					"\n\tObligation Attributes: \n\t\t"+oblAttrs + 
					"\n");
				
				// Get an Iterator over the Obligation's Map
				Iterator<String> itObligationAttr =
					oblAttrs.keySet().iterator();
				// Inner loop is over each attr within an Obligation
				int numObligationAttributes = 0;
				while (itObligationAttr.hasNext()){
					numObligationAttributes++;
					String obligationAttrId = itObligationAttr.next();
					String obligationAttrValue =
						oblAttrs.get(obligationAttrId);
					// log each attrId, attrVal pair:
					if (log.isTraceEnabled()) log.trace(
							"\n    ObligationAttrId: " + obligationAttrId +
								"\n\tObligationAttrValue: " + 
								obligationAttrValue + "\n");
				} // end while itObligationAttr.hasNext()
				if (log.isTraceEnabled()) log.trace(
					"\n\tNumber of obligation attributes = " + 
					numObligationAttributes + "\n");
			} // end while itObligationIds.hasNext()
			if (log.isTraceEnabled()) log.trace(
					"\n    Number of obligations = " + 
					numObligations + "\n");
		} // end if ! null
		else {
			if (log.isTraceEnabled()) log.trace(
				"pepRsp is unexpectedly null - need to investigate");
		}
	} // end logObligations
	
	public final static String DEFAULT_PROVIDER_NAME = "AZ_SERVICE";
	// Test PepRequestFactory:
	public static PepRequestFactory pepReqFactory = null;
	// Use the org.apache.commons.logging.LogFactory logger
	// TBD: there are both logging and system.out.printlns
	//  the latter will be eventually replaced by logging,
	//  but that is currently a tbd.
	//Log log = LogFactory.getLog(this.getClass()); 
	static Log logStatic = LogFactory.getLog(TestUtils.class);
	static LogFactory logFactory = LogFactory.getFactory();

	/**
	 * This section is for system services
	 * 
	 * @param args
	 */
	public static void setupAzService(String[] args) {

        // process the args parameters
		logStatic.info("args[] = " + Arrays.toString(args));
        String requestFile = null;
        String [] policyFiles = null;
        if (args.length > 0){
	        if (args[0].equals("-config")) {
	            requestFile = args[1];
	            logStatic.info("args[1] = " + requestFile);
	        } else {
	            requestFile = args[0];
	            policyFiles = new String[args.length - 1];            
	            for (int i = 1; i < args.length; i++) {
	                policyFiles[i-1] = args[i];
	                logStatic.info("args[" + i + "] = " + policyFiles[i-1]);
	            }
	        }
        }
        else
        	logStatic.info("no args provided to " + 
        			" TestAzApi.main(String[] args)");
        
		// Register the local AzService provider
		// TODO: when configuration strategy is decided, we
		// need to suppress local provider exceptions so
		// that users don't need to be concerned about them.
      	StringWriter sw = new StringWriter();
		try {
			String PDP_CONFIG_PROPERTY =
		        "com.sun.xacml.PDPConfigFile";
	        System.setProperty(PDP_CONFIG_PROPERTY, ".\\config\\sample1.xml");
			String configFile = System.getProperty(PDP_CONFIG_PROPERTY);
			logStatic.info("configFile = " + configFile);
	        if (args.length > 0){
		        if (policyFiles == null) {
		        	logStatic.info("Registering: " + 
		        		"SimpleConcreteSunXacmlService()");
					AzServiceFactory.registerProvider(
						DEFAULT_PROVIDER_NAME, 
						new SimpleConcreteSunXacmlService());
		        } else {
		        	logStatic.info("Registering: " + 
	        			"SimpleConcreteSunXacmlService(policyFiles)");
					AzServiceFactory.registerProvider(
						DEFAULT_PROVIDER_NAME, 
						new SimpleConcreteSunXacmlService(
								requestFile,
								policyFiles));		        	
		        }		       
	        } else {
	        	logStatic.info("Registering: " + 
        			"SimpleConcreteDummyService()");
				AzServiceFactory.registerProvider(
					DEFAULT_PROVIDER_NAME, 
					new SimpleConcreteDummyService());	        	
	        }
		} catch (ParsingException pe) {
			pe.printStackTrace(new PrintWriter(sw));
			logStatic.info("SunXacml ParsingException: " +
					pe.getMessage() + "\n" + sw);
		} catch (UnknownIdentifierException uie) {
			uie.printStackTrace(new PrintWriter(sw));
			logStatic.info("SunXacml UnknownIdentifierException: " + 
					uie.getMessage() + "\n" + sw);
		}
		
	} // end setupAzService
} // end TestUtils
