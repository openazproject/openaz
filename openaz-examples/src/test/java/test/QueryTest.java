package test;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.openliberty.openaz.azapi.constants.PepRequestQueryType;

import org.openliberty.openaz.azapi.AzService;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;
import org.openliberty.openaz.pep.PepRequestFactoryImpl;
import test.TestUtils;

import org.openliberty.openaz.pdp.provider.AzServiceFactory;
import org.openliberty.openaz.pdp.provider.SimpleConcreteDummyService;
import org.openliberty.openaz.pdp.provider.SimpleConcreteSunXacmlService;
import org.openliberty.openaz.pdp.resources.OpenAzTestResourceCollection;

import com.sun.xacml.ParsingException;
import com.sun.xacml.UnknownIdentifierException;

/**
 * QueryTest is designed to test the newQueryPepRequest
 * method of the PepRequestFactory.
 */
public class QueryTest {

	// Declare TestUtils to process results/obligations
	static TestUtils testUtils = new TestUtils();
    Log log = LogFactory.getLog(this.getClass());
    static Log logStatic = LogFactory.getLog(QueryTest.class);
    // test user; normally would be obtained from session info
    //public final static String SAMPLE_SESSION_USER_NAME = "josh";
    public final static String SAMPLE_SESSION_USER_NAME = "fred";
    // method the user used to authenticate - in this case the
    // "method" was by using a "password", but the actual pwd 
    // used is not provided here.
    public final static String SAMPLE_SESSION_AUTH_METHOD = "password";

    // config service and policies:
    public  final static String DEFAULT_PROVIDER_NAME = "AZ_SERVICE";
    
    // define property and value used by com.sun.xacml.ConfigurationStore
    // that contains the file name of an xml file containing elements
    // pertaining to the SunXacml setup; see sunxacml samples for
    // an example.
	public final static String PDP_CONFIG_PROPERTY =
        "com.sun.xacml.PDPConfigFile";
	public final static String PDP_CONFIG_FILE = ".\\config\\sample1.xml";

	public final static String CONFIG_POLICY_FILES =
		"SimpleConcreteSunXacmlService(policyFiles)";
	public final static String CONFIG_NO_POLICY_FILES =
		"SimpleConcreteSunXacmlService()";
	public final static String CONFIG_DUMMY = 
		"SimpleConcreteDummyService()";
		
	// variable defns to provide "context" for test program:
    // defn: "container": manages "environment" and "issues" env attributes
    public final static String CONTAINER = "orcl-weblogic"; // [a03]    
    // defn: "application": manages "resources" and "issues" resource attrs
    //public final static String APPLICATION = "hr-application-01"; // [a04]

	// Main program sets up the config and calls the
	// test instance file.
    public static void main(String[] args) throws Exception {
    	
		logStatic.info("Testing Query Test version 114");
		logStatic.info("LogFactory = " + LogFactory.class);
        logStatic.info("args[] = " + Arrays.toString(args));
        
        String requestFile = null;
        String [] policyFiles = null;
        String config = "";
        
        // parse the args: 
        //   (this is "for now" using SunXacml config paradigm
        // no args => use dummy test
        // arg[0] = "-config" => use all policy files in directory
        // arg[0] != "-config" => args is a list of files
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
	        System.setProperty(PDP_CONFIG_PROPERTY, PDP_CONFIG_FILE);
			String configFile = System.getProperty(PDP_CONFIG_PROPERTY);
	        logStatic.info("configFile = " + configFile);
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
	        if (args.length > 0){
		        if (policyFiles == null) {
		        	config = CONFIG_NO_POLICY_FILES;
		        	logStatic.info("Registering: " + 
		        		config);
        				//"SimpleConcreteSunXacmlService()");
					AzServiceFactory.registerProvider(
						DEFAULT_PROVIDER_NAME, 
						new SimpleConcreteSunXacmlService());
		        } else {
		        	config = CONFIG_POLICY_FILES;
		        	logStatic.info("Registering: " + 
        				config);
	        			//"SimpleConcreteSunXacmlService(policyFiles)");
					AzServiceFactory.registerProvider(
						DEFAULT_PROVIDER_NAME, 
						new SimpleConcreteSunXacmlService(
								requestFile,
								policyFiles));		        	
		        }		       
	        } else {
	        	config = CONFIG_DUMMY;
	        	logStatic.info("Registering: " + 
    				config);
        			//"SimpleConcreteDummyService()");
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
		logStatic.info("Config being used: "+ config + "\n\n\n");
    	QueryTest qt = new QueryTest();
    	qt.testQuery();
    }
    
    public void testQuery() 
    		throws PepException {

        // get ref to AzService, create the configured PepRequestFactory
		AzService azService = AzServiceFactory.getAzService();
        PepRequestFactory pep = new PepRequestFactoryImpl(CONTAINER, azService);
        if (pep == null) throw new PepException();

        // Create AzService and PepRequestFactory - same as in
    	// the TestStyles test class:
        //AzService azService =
        //    	new org.openliberty.openaz.pdp.
        //    			provider.SimpleConcreteDummyService();
        //PepRequestFactory pep = 
        //		new PepRequestFactoryImpl("QueryPep", azService);

        // an environment attr
        Date now = new Date();

        // Create a test subject using a hashmap, which will be
        // handled by the SimpleJavaObjectMapper (see TestStyles
        // for more info)
        HashMap<String, String> subject = 
        	new HashMap<String, String>();
        subject.put(
        	AzXacmlStrings.X_ATTR_SUBJECT_ID, SAMPLE_SESSION_USER_NAME);
        subject.put(
        	AzXacmlStrings.X_ATTR_SUBJECT_AUTHN_LOC_AUTHENTICATION_METHOD,
        	SAMPLE_SESSION_AUTH_METHOD);
        
        
        // Test: PepRequestQueryType.RETURN_ONLY_ALLOWED_RESULTS;
        // Create a scope
        String scope = "Menu "; 
        PepRequestQueryType pepReqQueryType =
        	PepRequestQueryType.RETURN_ONLY_ALLOWED_RESULTS;
        //String scope = "Menu, " + 
        //	TestResourceCollection.menuTypes.redMenu.ordinal();
        log.info("\n*****************************************\n" +
        		"Begin Query test w scope = " + scope +
        		"\n\t QueryType = " + pepReqQueryType +
        		"\n*****************************************\n");

        // Issue a request that returns only allowed results:
		log.info("Creating Query PepRequest w : " +
				"\n\tSubject as HashMap: \n\t\t"  +  subject +
				"Environment as Object: Date: \n\t\t" + now +
				"\n\tscope: \n\t\t" + scope +
				"\n\tpepReqQueryType: \n\t\t" + pepReqQueryType);
        PepRequest pepRequest =
            pep.newQueryPepRequest(
        		subject, 
        		now, 
        		//"actionsOnResource:foo",
        		scope,
        		pepReqQueryType);

        // Process the response
        // TBD: the underlying impl does not yet have an adequate
        // dummy to return results for the query tests and currently
        // throws exceptions (should be corrected soon)
        PepResponse response = pepRequest.decide();

        String subjectId = 
        	subject.get(AzXacmlStrings.X_ATTR_SUBJECT_ID);
        // outer loop to process multiple results
        int numResults = 0;
        while (response.next()) {
        	numResults++;
            log.info(
    				"\n\tSubject=\"" + subjectId +
    				"\n\tResource=\"" + response.getResource() +
    				"\"\n\tAction=\"" + response.getAction() +
    				"\"\n\t allowed=\"" + response.allowed() + "\"");
            //Obligations are not returned except for the 
            // verbose query (see below)
        }
        log.info("\n\tResults for scope = " + scope +
        		"\n\t QueryType = " + pepReqQueryType +
        		"\n\t numResults = " + numResults +
        		"\n\n\n");
        
        // Test: PepRequestQueryType.RETURN_ONLY_DENIED_RESULTS;
        // Create a scope
        scope = "Menu "; 
        pepReqQueryType =
        	PepRequestQueryType.RETURN_ONLY_DENIED_RESULTS;
        //String scope = "Menu, " + 
        //	TestResourceCollection.menuTypes.redMenu.ordinal();
        log.info("\n*****************************************\n" +
        		"Begin Query test w scope = " + scope +
        		"\n\t QueryType = " + pepReqQueryType +
        		"\n*****************************************\n");

        // Issue a request that returns only allowed results:
		log.info("Creating Query PepRequest w : " +
				"\n\tSubject as HashMap: \n\t\t"  +  subject +
				"Environment as Object: Date: \n\t\t" + now +
				"\n\tscope: \n\t\t" + scope +
				"\n\tpepReqQueryType: \n\t\t" + pepReqQueryType);
        pepRequest =
            pep.newQueryPepRequest(
        		subject, 
        		now, 
        		//"actionsOnResource:foo",
        		scope,
        		pepReqQueryType);

        // Process the response
        // TBD: the underlying impl does not yet have an adequate
        // dummy to return results for the query tests and currently
        // throws exceptions (should be corrected soon)
        response = pepRequest.decide();

        subjectId = 
        	subject.get(AzXacmlStrings.X_ATTR_SUBJECT_ID);
        // outer loop to process multiple results
        numResults = 0;
        while (response.next()) {
        	numResults++;
            log.info(
    				"\n\tSubject=\"" + subjectId +
    				"\n\tResource=\"" + response.getResource() +
    				"\"\n\tAction=\"" + response.getAction() +
    				"\"\n\t allowed=\"" + response.allowed() + "\"");
            //Obligations are not returned except for the 
            // verbose query (see below)
        }
        log.info("\n\tResults for scope = " + scope +
        		"\n\t QueryType = " + pepReqQueryType +
        		"\n\t numResults = " + numResults +
        		"\n\n\n");
        
        
        
        // Test: PepRequestQueryType.VERBOSE;
        // set a new scope
        scope = "EngineeringServer " ; 
        //scope = "Menu, " +
        pepReqQueryType = PepRequestQueryType.VERBOSE;
        //	TestResourceCollection.menuTypes.blueMenu.ordinal();
        //logStatic.info("Begin Query test w scope = " + scope);
        log.info("\n*****************************************\n" +
        		"Begin Query test w scope = " + scope +
        		"\n\t QueryType = " + pepReqQueryType +
        		"\n*****************************************\n");

        // Test the verbose (full result test)
        // TBD: again, the underlying test impl needs to be set
        // up to produce appropriate dummy results.
		log.info("Creating Query PepRequest w : " +
				"\n\tSubject as HashMap: \n\t\t"  +  subject +
				"Environment as Object: Date: \n\t\t" + now +
				"\n\tscope: \n\t\t" + scope +
				"\n\tpepReqQueryType: \n\t\t" + pepReqQueryType);
        pepRequest =
                pep.newQueryPepRequest(
                		subject, 
                		now, 
                		scope,
                		//"actionsOnResource:foo",
                		pepReqQueryType);

        response = pepRequest.decide();

        // outer loop for multiple full results
        log.info("\n\tResults for scope = " + scope +
    		"\n\t QueryType = " + pepReqQueryType +
    		"\n\t numResults = " + 
    			response.getAzResponseContext().getResults().size());
        int posResults = 0;
        int negResults = 0;
        while (response.next()) {
        	try {
        		log.info(
        				"\n\tSubject=\"" + subjectId +
        				"\n\tResource=\"" + response.getResource() +
        				"\"\n\tAction=\"" + response.getAction() +
        				"\"\n\t allowed=\"" + response.allowed() + "\"");
        		if (response.allowed()) posResults++;
        		else negResults++;
        	} catch (PepException pepEx) {
        		log.info("PepException caught: " +
        				pepEx.getMessage());
        	}
            // each full result can contain obligations
            testUtils.logObligations(response);
        }       
        // outer loop for multiple full results
        log.info("\n\tResults for scope = " + scope +
    		"\n\t QueryType = " + pepReqQueryType +
    		"\n\t numResults = " + 
    			response.getAzResponseContext().getResults().size()+
    		"\n\t\t posResults = " + posResults +
    		"\n\t\t negResults = " + negResults);
    }
}
