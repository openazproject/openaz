package test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
//import java.util.Map;
import java.io.FilePermission;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.constants.PepResponseBehavior;
import org.openliberty.openaz.azapi.AzService;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;
import org.openliberty.openaz.pdp.provider.AzServiceFactory;
import org.openliberty.openaz.pdp.provider.SimpleConcreteDummyService;
import org.openliberty.openaz.pdp.provider.SimpleConcreteSunXacmlService;
import org.openliberty.openaz.pep.PepRequestFactoryImpl;

import com.sun.xacml.ParsingException;
import com.sun.xacml.UnknownIdentifierException;

import test.TestUtils;
/**
 * BulkDecideTest demonstrates how to set up and execute
 * multiple requests within a single PepRequest
 * See also TestStyles for more details on the comments
 * in this code. The comments here emphasize the bulk
 * aspects.
 */
public class BulkDecideTest {
	// use TestUtils for log obligations
	static TestUtils testUtils = new TestUtils();
    Log log = LogFactory.getLog(this.getClass());
	
    // variable defns to provide "context" for test program:
    // defn: "container": manages "environment" and "issues" env attributes
    public final static String CONTAINER = "orcl-weblogic"; // [a03]    
    // defn: "application": manages "resources" and "issues" resource attrs
    //public final static String APPLICATION = "hr-application-01"; // [a04]
    
    // test user; normally would be obtained from session info
    public final static String SAMPLE_SESSION_USER_NAME = "Joe User";
    public final static String SAMPLE_SESSION_AUTH_METHOD = "password";

	public  final static String DEFAULT_PROVIDER_NAME = "AZ_SERVICE";

	// PDP_CONFIG_PROPERTY is the file path, absolute or relative
	// to the SunXacml config file that is used if the default
	// com.sun.xacml.ConfigurationStore constructor is used 
	public final static String PDP_CONFIG_PROPERTY =
        "com.sun.xacml.PDPConfigFile";
	// an example config file supplied by SunXacml and used here
	public final static String PDP_CONFIG_FILE = ".\\config\\sample1.xml";

    /**
     * Main program is just to create an instance and run
     * the test from the instance.
     * @param args
     */
    public static void main(String[] args) throws Exception {
    	
		System.out.println("Testing BulkDecide Test version 105");
		System.out.println("LogFactory = " + LogFactory.class);
        System.out.println("args[] = " + Arrays.toString(args));
        
        String requestFile = null;
        String [] policyFiles = null;
        
        // parse the args: 
        //   (this is "for now" using SunXacml config paradigm
        // no args => use dummy test
        // if arg[0] = "-config" => args[1] = sample request file and
        // policy files are inferred from PDP_CONFIG_PROPERTY
        // value that must be set to resolvable file path to
        // and xml config file.
        // if arg[0] != (NOT=) "-config" => args is a list of 
        // one sample request file followed by a set of policy files
        if (args.length > 0){
	        if (args[0].equals("-config")) {
	            requestFile = args[1];
	            System.out.println("args[1] = " + requestFile);
	        } else {
	            requestFile = args[0];
	            policyFiles = new String[args.length - 1];            
	            for (int i = 1; i < args.length; i++) {
	                policyFiles[i-1] = args[i];
	                System.out.println("args[" + i + "] = " + policyFiles[i-1]);
	            }
	        }
        }
        else
        	System.out.println("no args provided to " + 
        			" TestAzApi.main(String[] args)");
        
		// Register the local AzService provider
		// TODO: when configuration strategy is decided, we
		// need to suppress local provider exceptions so
		// that users don't need to be concerned about them.
      	StringWriter sw = new StringWriter();
		try {
	        if (args.length > 0){
		        if (policyFiles == null) {
		        	// use default constructor, which uses default
		        	// attr finders, PDPConfig, etc.
			        System.setProperty(PDP_CONFIG_PROPERTY, PDP_CONFIG_FILE);
					String configFile = System.getProperty(PDP_CONFIG_PROPERTY);
			        System.out.println("configFile = " + configFile);
		        	System.out.println("Registering: " + 
		        		"SimpleConcreteSunXacmlService()");
					AzServiceFactory.registerProvider(
						DEFAULT_PROVIDER_NAME, 
						new SimpleConcreteSunXacmlService());
		        } else {
		        	System.out.println("Registering: " + 
	        			"SimpleConcreteSunXacmlService(policyFiles)");
					AzServiceFactory.registerProvider(
						DEFAULT_PROVIDER_NAME, 
						new SimpleConcreteSunXacmlService(
								requestFile,
								policyFiles));		        	
		        }		       
	        } else {
	        	System.out.println("Registering: " + 
        			"SimpleConcreteDummyService()");
				AzServiceFactory.registerProvider(
					DEFAULT_PROVIDER_NAME, 
					new SimpleConcreteDummyService());	        	
	        }
		} catch (ParsingException pe) {
			pe.printStackTrace(new PrintWriter(sw));
			System.out.println("SunXacml ParsingException: " +
					pe.getMessage() + "\n" + sw);
		} catch (UnknownIdentifierException uie) {
			uie.printStackTrace(new PrintWriter(sw));
			System.out.println("SunXacml UnknownIdentifierException: " + 
					uie.getMessage() + "\n" + sw);
		}
				
		BulkDecideTest bdt = new BulkDecideTest();
    	bdt.testBulkDecide();
    } // End of main()

    /**
     * Test the newPepBulkRequest options
     */
    public void testBulkDecide() {
        System.out.println("Log = " + log.getClass());
        StringBuffer results = new StringBuffer();

        // get ref to AzService, create the configured PepRequestFactory
		AzService azService = AzServiceFactory.getAzService();
        PepRequestFactory pep = new PepRequestFactoryImpl(CONTAINER, azService);
        
        // The following code is to demo the PepResponseBehavior
        // capabilities: In general, a Permit returns true, and
        // a Deny returns false, but there are also other types
        // of returns, including NotApplicable and Indeterminate.
        // The configuration is to specify for each of the 4
        // xacml-defined conditions, what the behavior will be.
        // i.e. for each of the "special" conditions there is 
        // a choice to return either true (Permit), false (Deny),
        // or throw an Exception. See PepResponseBehavior for
        // default values.
        
        // For bulk requests we do not want to throw exceptions
        // when processing the results, so we will simply have
        // allowed() return deny (false) for all these cases:
        pep.getResponseFactory().setMissingAttributeBehavior(
        		PepResponseBehavior.RETURN_NO);
        pep.getResponseFactory().setProcessingErrorBehavior(
        		PepResponseBehavior.RETURN_NO);
        pep.getResponseFactory().setSyntaxErrorBehavior(
        		PepResponseBehavior.RETURN_NO);
        pep.getResponseFactory().setNotApplicableBehavior(
        		PepResponseBehavior.RETURN_NO);
        
        // An environment attribute
        Date now = new Date();

        // A collection of Subject attributes
        HashMap<String, String> subject = new HashMap<String, String>();
        subject.put(AzXacmlStrings.X_ATTR_SUBJECT_ID, SAMPLE_SESSION_USER_NAME);
        subject.put(AzXacmlStrings.X_ATTR_SUBJECT_AUTHN_LOC_AUTHENTICATION_METHOD,
                    SAMPLE_SESSION_AUTH_METHOD);

        // There are 2 newBulkPepRequest calls in PepRequestFactory
        // Please see javadoc on PepRequestFactory for details on
        // these 2 calls. The example below demonstrates both types
        // of call:
        
        // First define an array of actions that will be used for
        // both types of call:
        // An array of action strings that get submitted as list 
        // below
        String[] actions =
            new String[] { "read", "write", "delete", "read", "write",
                           "delete" };
        
        // Define 2 collections: one of resourceNames to go with 
        //  the actions in the 4-parameter call below, and the 
        //  other of Permissions using the same resourceNames and 
        //  actions for each Permission, but for use in the 
        //  3-parameter newBulkPepRequest call below
        String resourceName = "file:\\\\toplevel";
        ArrayList resources = new ArrayList();
        ArrayList resourceActions = new ArrayList();
        for (int i = 0; i < actions.length; i++) {
        	// Define a distinct string for each resource
        	//String resource = resourceName + "0" + new Integer(i);
        	String resource = resourceName + "0" + new Integer(i) +"\\";
        	//String resource = resourceName + "0" + new Integer(i) +"\\x.y";
        	// add the string to the list of resources
            resources.add(resource);
            // use the resource string and action string to
            // define a FilePermission
            resourceActions.add(
            		new FilePermission(resource, actions[i]));
        }

        String subjectId = 
        	subject.get(AzXacmlStrings.X_ATTR_SUBJECT_ID);
        // Try both the 4 param and 3 param bulk PepRequests
        try {
        	// first, try the 4 argument request w Strings
	        PepRequest request = pep.newBulkPepRequest(
	        		subject,
	        		Arrays.asList(actions),
	        		resources,
	        		now);
	        
	        // Issue the decide() call:
	        PepResponse response = null;
        	response = request.decide();
        	
        	// Now we need to be prepared to process multiple
        	// return results, each of which has an individual
        	// decision and possibly Obligations as well.
        	// The test pdp has been set up to return a sequence
        	// of each of the possible test results, and since
        	// we are submitting 6 requests, we expect each type
        	// of result to be returned once.
        	
        	// Let us iterate over the results using the list
        	// of resources as an iterator. However, the results
        	// are not automatically correlated with order of
        	// the resources in the input list. Therefore, to
        	// correlate the results, one should look at the
        	// resource-id in each result to determine which
        	// resource-action the result is associated with.
        	// Since, in our input list of resources, each has
        	// a unique name, one can correlate simply by looking
        	// at the String returned by response.getResource()
        	// to determine which request the result is associated
        	// with:
        	Iterator it = resources.iterator();
        	int i=0;
        	// outer loop is over the individual results
        	while (it.hasNext()){
                response.next();
        		i++;
        		Object obj = it.next();
                log.info(
            		"\n\t" + i + ". Subject = " + subjectId +
            		"\n\t   Resource="+response.getResource()+
            		"\n\t   Action="+response.getAction()+
            		"\n\t    allowed="+response.allowed());
                // TestUtils will iterate over the obligations
                // within the result
                testUtils.logObligations(response);
        	}
        	
        	// Try the 3-argument version w Permissions
        	request = pep.newBulkPepRequest(
	        		subject,
	        		resourceActions,
	        		now);
        	
        	// Processing results is same as above in the
        	// 4-argument version
	        response = null;
        	response = request.decide();
        	it = resourceActions.iterator();
        	i=0;
        	while (it.hasNext()){
                response.next();
        		i++;
        		Object obj = it.next();
        		String result = "\n" + i + 
	        		". Subject = " + subjectId +
	        		"\n\t Resource="+response.getResource()+
	        		"\n\t Action="+response.getAction()+
	        		"\n\t  allowed="+response.allowed();
                //System.out.println(result);
                log.info(result);
                results = results.append(result);
                testUtils.logObligations(response);
        	}
        }catch (PepException pepEx){
        	 StringWriter sw = new StringWriter();
        	 pepEx.printStackTrace(new PrintWriter(sw));
        	 log.warn("Caught Exception: " + pepEx + "\n" + sw);
        }
        System.out.println("Total result = " + results);
        log.info("Total results (log) = " + results);
    }
}
