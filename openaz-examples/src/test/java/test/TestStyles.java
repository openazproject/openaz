package test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Date;
import java.util.StringTokenizer;

//import java.util.Iterator;
//import java.util.Map;
//import java.util.Properties;
//import java.security.AccessController;
//import java.security.AccessControlContext;
//import java.security.DomainCombiner;
//import javax.security.auth.Subject;

import java.io.FilePermission;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.Principal;
import java.security.Security;

import javax.security.auth.x500.X500Principal;
import javax.security.auth.x500.X500PrivateCredential;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openliberty.openaz.azapi.AzService;
//import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;

import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.openliberty.openaz.azapi.pep.JavaObjectMapper;
//import org.openliberty.openaz.azapi.pep.RequestAttributesFactory;

import org.openliberty.openaz.pdp.provider.AzServiceFactory;
import org.openliberty.openaz.pdp.provider.SimpleConcreteDummyService;
import org.openliberty.openaz.pdp.provider.SimpleConcreteSunXacmlService;
import org.openliberty.openaz.pep.PepRequestFactoryImpl;
import org.openliberty.openaz.pep.SimpleJavaObjectMapper;
import org.openliberty.openaz.pep.SimpleJavaPermissionMapper;
//import org.openliberty.openaz.pep.SubjectFactory;

import com.sun.xacml.ParsingException;
import com.sun.xacml.UnknownIdentifierException;

import test.TestUtils;
import test.objects.TestJaasSubjectMapper;
import test.objects.TestRolePrincipal;
import test.objects.TestResourcePermission;

public class TestStyles {
	
	// defn: "container": The "container" may be thought of as
	// the entity that manages the "environment" and thus may
	// be considered to be the "issuer"  of env attributes
	// We will use the name of the container for this purpose
	public final static String CONTAINER = "orcl-weblogic"; 
	
	// defn: "application": Similarly the "application" manages 
	// application "resources" and may be considered to be the
	// "issuer" of resource attrs
	// We will use a dummy appl name for an hr appl as the issuer.
	// Note: this is not used in these examples, usage can be
	// seen in TestAzApi, which uses lower layer azapi;
	// TBD: probably need way to include this w factory init
	// TBD: this metadata for AzAttributes is currently handled
	// under the surface, but need to decide how we want to
	// incorporate it at lower level PepRequest.
	//public final static String APPLICATION = "hr-application-01";
	
	// defn: az provider class name; OpenAz has the notion of
	// an authorization service provider, where the implementer
	// of azapi and azapi.pep provide an implementation class that
	// provides this service. In this case, we are providing an
	// impl of azapi.pep, which is the pep layer on top of the
	// underlying azapi. In the initial test version, there is
	// a dummy implementation, which actually contains quite a
	// bit of implementation of the full azapi.pep and azapi, but
	// does not front end a real pdp. i.e. the "dummy" is only
	// the pdp, but the azapi.pep and azapi are prototype impls
	// which are intended to be reusable and the subject of 
	// ongoing development.
	// In any event, the following is the class name of this
	// sample impl:
	public final static String PROVIDER_CLASS_NAME = 
		"org.openliberty.openaz.pdp.provider.SimpleConcreteService";
	
	// test user; normally would be obtained from session info
	public final static String SAMPLE_SESSION_USER_NAME = "Joe User";
	public final static String SAMPLE_SESSION_BAD_USER_NAME = "Joe Bad User";
	public final static String SAMPLE_SESSION_AUTH_METHOD = "password";
	
	// dummy defns of resource type identifier and value
	public final static String RES_TYPE_ATTR_ID = "resource-type-attr-id";
	public final static String FILE_RES_TYPE = "file";
	public static String
		OPENAZ_ATTR_RESOURCE_TYPE =
			"urn:openaz:names:xacml:1.0:resource:resource-type";
	public final static String OPENAZ_ATTR_SUBJECT_ROLE_ID =
		"urn:openaz:names:xacml:1.0:subject:role-id";
	public final static String DEFAULT_PROVIDER_NAME = "AZ_SERVICE";
	
	public enum TestNames {
		TEST_STRINGS{
			public String toString(){return "String"; } },
		TEST_MAPPERS{
			public String toString(){return "Mappers + Attributes"; } },			
		TEST_SUBJECT_MAPPER{
			public String toString(){return "JAAS Subject"; } },
		TEST_QUERY{
			public String toString(){return "Query"; } },
		TEST_MUST_BE_PRESENT{
			public String toString(){return "MustBePresentFinder"; } }
	}
	
	// Test PepRequestFactory:
	public static PepRequestFactory pepReqFactory = null;
	// Use the org.apache.commons.logging.LogFactory logger
	// TBD: there are both logging and system.out.printlns
	//  the latter will be eventually replaced by logging,
	//  but that is currently a tbd.
	Log log = LogFactory.getLog(this.getClass()); 
	static Log logStatic = LogFactory.getLog(TestStyles.class);
	static LogFactory logFactory = LogFactory.getFactory();
	
	// TestUtils is used for printing obligations, which
	// goes thru the structures and prints relevant info
	static TestUtils testUtils = new TestUtils();
	
	/** 
	 * Main test method
	 * There are 3 subtests:
	 *   1. Simple string arguments to show how simple the
	 *      the api can be reduced to single appl call w
	 *      subject, resource, and action strings
	 *   2. Mappers: shows use of object mappers that
	 *      allows submission of native java objects for
	 *      authorization, where the mappers take the 
	 *      relevant info from the objects and translate
	 *      them to xacml attrs
	 *   3. SubjectMapper: shows how std jaas subject can
	 *      be a java object submitted in the az process,
	 *      which uses a SubjectMapper to translate info
	 *      in jaas Subject to xacml attrs.
	 */
	public static void main(String[] args) throws Exception {

		logStatic.info("Testing TestStyles version 115");		
		setLogFeatures(logStatic);
		testLogFeatures(logStatic);

		// Create the az service. In the "real world" this will be done
		// by the container and invisible to the application. We will
		// probably have some std object that users can refer to as
		// the handle to the az service much like AccessControlContext
		// is used in std java env. i.e. the following will ultimately
		// be reducible to az.decide(str, str, str) for a typical appl		

		// Set up the AzService for general use
		//setupAzService(args);
		TestUtils.setupAzService(args);
		
		// Get the AzService for use by PepRequestFactory
		AzService az = AzServiceFactory.getAzService();  		
		if ( ! (az == null) ) {
			// Set up the PepRequestFactory for use by applications
			pepReqFactory =
				(PepRequestFactory)new PepRequestFactoryImpl(
						CONTAINER,az);		
				
			// Create a TestStyles object:
			TestStyles ts = new TestStyles();
			
			// Run the tests
			ts.testStyleString();
			ts.testStyleMappers();
			ts.testStyleSubjectMapper();
			ts.testStyleQuery();
			ts.testStyleMustBePresentFinder();
		}
		else
			logStatic.warn("Test failed, configured service: az == null");		
	}
	
	/**
	 * Test #1: Strings
	 * 
	 * String style 1
	 *  i. simple strings, process obligations
	 *  
	 * @throws Exception
	 */
	public void testStyleString() throws Exception{
		
		// some preliminary sanity check prints/logs
        log.info("Log = " + log.getClass());
		printTestHeader(TestNames.TEST_STRINGS.toString());
		
		// The PepRequestFactory is available as the static variable,
		// pepReqFactory
		 			
		// Define sample strings to provide to simple string PepRequest
		String subjectId = SAMPLE_SESSION_USER_NAME;
		String resourceId = "file:C\\toplevel";
		String actionId = "Read";

		// Create the actual request using the factory to
		// create the request and passing the strings to
		// specify the details of the request
		log.info("use pepReq to create req \n\tw subject, action, resource: " +
				"\n\t  " + subjectId + ", " + actionId + ", " + resourceId);
		PepRequest pepReq = 
			pepReqFactory.newPepRequest(
					subjectId, actionId, resourceId); 
		
		// Issue the request and receive a PepResponse object
		// in return, which contains details of the results
		// of the request
		PepResponse pepRspCtx = pepReq.decide();
		if ( ! (pepRspCtx==null) ) 
			log.info("\n\t" + subjectId + 
					" allowed = " + pepRspCtx.allowed() + "\n");
		else log.warn("\n\t" + subjectId + "\n\t " + "pepRspCtx = null");
		// Print the result (allowed true or false) and
		// then log the obligations, if any were returned.
		testUtils.logObligations(pepRspCtx);
		
		// try bad user			
		subjectId = SAMPLE_SESSION_BAD_USER_NAME;
		log.info("use pepReq to create req \n\tw subject, action, resource: " +
				"\n\t  " + subjectId + ", " + actionId + ", " + resourceId);
		pepRspCtx = pepReqFactory.newPepRequest(
				subjectId, actionId, resourceId).decide(); 
		if ( ! (pepRspCtx==null) ) 
			log.info("\n\t" + subjectId + 
					" allowed = " + pepRspCtx.allowed() + "\n");
		testUtils.logObligations(pepRspCtx); 
	} // end testStyleString
	
	/**
	 * Test #2: Mappers
	 * 
	 * 		String + attr style 1a
	 * ii. convert from JAAS Subject, Permission
	 * 
	 * Set the mappers that will be used by each category:
	 * Subject, Action, Resource, Environment.
	 * The list of mappers for each category defines the objects
	 * that may be submitted for that category and how the
	 * information in those objects get mapped to xacml
	 * AttributeId and AttributeValue.
	 * 
	 * @throws Exception
	 */
	public void testStyleMappers() throws Exception {
		
		// preliminary log/print stuff:
		String testName = TestNames.TEST_MAPPERS.toString();
		printTestHeader(testName);
		configureMappers(TestNames.TEST_MAPPERS);

		// Create subject and password in a hashmap to pass
		// as a subject object.
        //HashMap<String,String> subject = new HashMap<String,String>();           
        HashMap<String,Object> subject = new HashMap<String,Object>();           
        subject.put(
        	AzXacmlStrings.X_ATTR_SUBJECT_ID,"josh");
        subject.put(
        	AzXacmlStrings.X_ATTR_SUBJECT_AUTHN_LOC_AUTHENTICATION_METHOD,
        	SAMPLE_SESSION_AUTH_METHOD);
        
        // Test a boolean attribute
        Boolean testBoolAttr = new Boolean(true);
        subject.put("ThisIsABooleanTestAttr", testBoolAttr);
        
        // Test an Integer attribute
        Integer testIntegerAttr = new Integer(1234);
        subject.put("ThisIsAnIntegerTestAttr", testIntegerAttr);
        
        // Test a Date attribute
        Date testDateAttr = new Date(); // this one goes in HashMap
        subject.put("ThisIsADateTestAttr", testDateAttr);
 			
        // Create resource and action strings for the permission
        // object that, itself, represents a resource type
		String file = "file:///C/toplevel/permissionTest";
		String action = "Read";
		FilePermission filePermission = new FilePermission(file, action);
            
		// Create a Date for a simple environment attribute
		Date now = new Date();
		
		// Create a request, supplying the above objects, which
		// under the surface will be processed by the mappers
		// that were configured above.
		log.info("Creating pepReq w : " +
				"\n\tSubject as Object Hashmap: \n\t\t"  +  subject +
				",\n\tResource/Action as Object FilePermission: \n\t\t" +
					filePermission + "\n\t and " + 
				"Environment as Object: Date: \n\t\t" + now + "\n");
		PepRequest pepReq = 
			pepReqFactory.newPepRequest(
					subject, filePermission, now); 
		
		// Issue the request and process the response, 
		// same as test 1
		PepResponse pepRspCtx = pepReq.decide();			
		if (pepRspCtx != null) {
			log.info("TestStyles: Mappers: \n\t allowed() = " +
						pepRspCtx.allowed() + "\n");
			testUtils.logObligations(pepRspCtx);
		}
		else
			log.warn("pepRsp seems to be null");
		
		// repeat the test requesting "Write"
        // Create resource and action strings for the permission
        // object that, itself, represents a resource type
		file = "file:///C/toplevel/permissionTest";
		action = "Write";
		filePermission = new FilePermission(file, action);
            
		// Create a Date for a simple environment attribute
		now = new Date();
		
		// Create a request, supplying the above objects, which
		// under the surface will be processed by the mappers
		// that were configured above.
		log.info("Creating pepReq w : " +
				"\n\tSubject as Object Hashmap: \n\t\t"  +  subject +
				",\n\tResource/Action as Object FilePermission: \n\t\t" +
					filePermission + "\n\t and " + 
				"Environment as Object: Date: \n\t\t" + now);
		pepReq = 
			pepReqFactory.newPepRequest(
					subject, filePermission, now); 
		
		// Issue the request and process the response, 
		// same as test 1
		pepRspCtx = pepReq.decide();			
		if (pepRspCtx != null) {
			log.info("TestStyles: Mappers: \n\t allowed() = " +
					pepRspCtx.allowed() + "\n");
			testUtils.logObligations(pepRspCtx);
		}
		else
			log.warn("pepRsp seems to be null");
	} // end testStyleMappers  
	
	/**
	 * Test #3: JAAS Subject Mapper:
	 * 
	 * JAAS Subject style 
	 * 
	 * iii. Adding additional context information from
	 * the JAAS Subject
	 * 
	 * @throws Exception
	 */
	public  void testStyleSubjectMapper() throws Exception {
		String testName = TestNames.TEST_SUBJECT_MAPPER.toString();
		printTestHeader(testName);
		configureMappers(TestNames.TEST_SUBJECT_MAPPER);
	    			
		// Create some dummy Principals
	    Set<Principal> xPrins = new HashSet<Principal>();
	    xPrins.add(new X500Principal(
	    		"CN=Rich, OU=Identity Management, O=Oracle, C=US"));
	    xPrins.add(new TestRolePrincipal("developer"));

	    // Create some dummy Credential containers
	    Set<X500PrivateCredential> xCreds = 
	    			new HashSet<X500PrivateCredential>();
	    Set<X500PrivateCredential> xPubCreds = 
	    			new HashSet<X500PrivateCredential>();
	    
	    // Create a dummy JAAS Subject using the Principals
	    // and Credentials defined above
	    javax.security.auth.Subject jaasSubject = 
	    	new javax.security.auth.Subject(false, xPrins, xCreds, xPubCreds);
	    
	    // Get Principals from the JAAS Subject
	    // TBD: examine the JAAS Subject just created:
	    //Set<? extends Principal> yPrins = jaasSubject.getPrincipals();
	    //Iterator<? extends Principal> itPrin = yPrins.iterator();
		// Create a FilePermission as a resource to request
		String file = "file:///C/toplevel/permissionTest/level2";
		String action = "Read";
		FilePermission filePermission = new FilePermission(file, action);
          
		// An environment attribute
		Date now = new Date();
		
		// Create a single decision 3 param request
		PepRequest pepReq = null;
		PepResponse pepRspCtx = null; 

		log.info("Creating pepReq w : " +
				"\n\tSubject as JAAS Subject: \n\t\t"  +  jaasSubject +
				",\n\tResource/Action as Object FilePermission: \n\t\t" +
					filePermission + "\n\t and " + 
				"Environment as Object: Date: \n\t\t" + now);
		
		// try "shorthand": create req, issue decide in one line
		pepRspCtx = 
			pepReqFactory.newPepRequest(
				jaasSubject, filePermission, now).decide() ;
		
		// process the results as in test 1 and 2 
		if (pepRspCtx != null) {
			log.info("TestStyles: SubjectMapper; \n\tallowed() = " +
						pepRspCtx.allowed() + "\n");
			testUtils.logObligations(pepRspCtx);
		}
		else
			log.warn("pepRsp seems to be null");
		
		// try same test with write
		action = "write";
		filePermission = new FilePermission(file, action);
		// Create a single decision 3 param request
		log.info("Creating pepReq w : " +
				"\n\tSubject as JAAS Subject: \n\t\t"  +  jaasSubject +
				",\n\tResource/Action as Object FilePermission: \n\t\t" +
					filePermission + "\n\t and " + 
				"Environment as Object: Date: \n\t\t" + now);
		pepRspCtx = 
			pepReqFactory.newPepRequest(
					jaasSubject, filePermission, now).decide(); 
		
		// process the results as in test 1 and 2 
		if (pepRspCtx != null) {
			log.info("TestStyles: SubjectMapper; \n\tallowed() = " +
						pepRspCtx.allowed() + "\n");
			testUtils.logObligations(pepRspCtx);
		}
		else
			log.info("pepRsp seems to be null");
	} // end testStyleSubjectMapper
	
	/**
	 * Test #4: Query
	 * 
	 * Use PolicySet w parallel policy to implement query that
	 * returns a filter that can be used to collect lists of
	 * allowed resources. Query is implemented by special
	 * string "/-" that returns obligation w filter.
	 * 
	 * @throws Exception
	 */
	public void testStyleQuery() throws Exception {
		
		// preliminary log/print stuff:
		String testName = TestNames.TEST_QUERY.toString();
		printTestHeader(testName);
		configureMappers(TestNames.TEST_QUERY);
		
		// Create subject and password in a hashmap to pass
		// as a subject object.
        //HashMap<String,String> subject = 
        HashMap<String,Object> subject = 
        	new HashMap<String,Object>();           
        subject.put(AzXacmlStrings.X_ATTR_SUBJECT_ID,
        			"User1");
        subject.put(AzXacmlStrings.
        	X_ATTR_SUBJECT_AUTHN_LOC_AUTHENTICATION_METHOD,
        	SAMPLE_SESSION_AUTH_METHOD);
 			
        // Create resource and action strings for the permission
        // object that, itself, represents a resource type
		String requestedResource = "http://www.example.com/A/B/C";
		String action = "Read";
		TestResourcePermission testResourcePermission = 
			new TestResourcePermission(
				requestedResource, action);
            
		// Create a Date for a simple environment attribute
		Date now = new Date();
		
		// Create a request, supplying the above objects, which
		// under the surface will be processed by the mappers
		// that were configured above.
		log.info("Creating pepReq w : " +
				"\n\tSubject as HashMap: \n\t\t"  +  subject +
				",\n\tResource/Action as Object FilePermission: \n\t\t" +
					testResourcePermission + "\n\t and " + 
				"Environment as Object: Date: \n\t\t" + now);
		PepRequest pepReq = 
			pepReqFactory.newPepRequest(
					subject, testResourcePermission, now); 
		
		// Issue the request and process the response, 
		PepResponse pepRspCtx = pepReq.decide();			
		if (pepRspCtx != null) {
			log.info("TestStyles: Query: allowed() = " +
						pepRspCtx.allowed() + "\n");
			testUtils.logObligations(pepRspCtx);
		}
		else
			log.warn("pepRsp seems to be null");
		
		// repeat the test using query string:  /-
		requestedResource = "/-";
		action = "Write";
		testResourcePermission = 
			new TestResourcePermission(
				requestedResource, action);
            
		// Create a Date for a simple environment attribute
		now = new Date();
		
		// Create a request, supplying the above objects, which
		// under the surface will be processed by the mappers
		// that were configured above.
		log.info("Creating pepReq w : " +
				"\n\tSubject as HashMap: \n\t\t"  +  subject +
				",\n\tResource/Action as Object FilePermission: \n\t\t" +
					testResourcePermission + "\n\t and " + 
				"Environment as Object: Date: \n\t\t" + now + "\n");
		pepReq = 
			pepReqFactory.newPepRequest(
					subject, testResourcePermission, now); 
		
		// Issue the request and process the response, 
		pepRspCtx = pepReq.decide();			
		if (pepRspCtx != null) {
			log.info("TestStyles: Query: allowed() = " +
						pepRspCtx.allowed() + "\n");
			testUtils.logObligations(pepRspCtx);
		}
		else
			log.warn("pepRsp seems to be null");
	} // end testStyleQuery  
	
	/**
	 * Test #5: MustBePresentFinder
	 * 
	 * Use PolicySet w MustBePresent attribute to
	 * force invocation of attribute finder
	 * 
	 * @throws Exception
	 */
	public void testStyleMustBePresentFinder() throws Exception {
		
		// preliminary log/print stuff:
		String testName = TestNames.TEST_MUST_BE_PRESENT.toString();
		printTestHeader(testName);
		configureMappers(TestNames.TEST_MUST_BE_PRESENT);
					
		// Create subject and password in a hashmap to pass
		// as a subject object.
        HashMap<String,String> subject = 
        	new HashMap<String,String>();           
        subject.put(AzXacmlStrings.X_ATTR_SUBJECT_ID,
        			"fred");
        subject.put(AzXacmlStrings.
        	X_ATTR_SUBJECT_AUTHN_LOC_AUTHENTICATION_METHOD,
        	SAMPLE_SESSION_AUTH_METHOD);
 			
        HashMap<String,String> resourceAttrs = 
        	new HashMap<String,String>();           
        resourceAttrs.put(AzXacmlStrings.X_ATTR_RESOURCE_ID,
        			"resource-id-FrisBee-3");
        resourceAttrs.put(OPENAZ_ATTR_RESOURCE_TYPE,
					"FrisBee");
        HashMap<String,String> actionAttrs = 
        	new HashMap<String,String>();           
        actionAttrs.put(AzXacmlStrings.X_ATTR_ACTION_ID,
        			"throw");
		// Create a Date for a simple environment attribute
		Date now = new Date();
		
		log.info("\n  Creating pepReq w : " +
			"\n    Subject as HashMap: \n\t"  + 
			objectToString(subject,",") +
			",\n    Resource as HashMap: \n\t" +
			objectToString(resourceAttrs, ",") + 
			",\n    Action as HashMap: \n\t" +
			objectToString(actionAttrs, ",") + 
			"\n    and Environment as Object: Date: \n\t" + 
			now + "\n");
		
		// Create a request, supplying the above objects, which
		// under the surface will be processed by the mappers
		// that were configured above.
		PepRequest pepReq = 
			pepReqFactory.newPepRequest(
					subject, actionAttrs, resourceAttrs, now); 
		
		// Issue the request and process the response, 
		PepResponse pepRspCtx = pepReq.decide();			
		if (pepRspCtx != null) {
			log.info(
				"\n    Test: " + testName + ": pepRspCtx.allowed() = " +
					pepRspCtx.allowed() + "\n");
			testUtils.logObligations(pepRspCtx);
		}
		else log.warn(
				"Test: " + testName + " failed, pepRsp seems to be null");			
		
		// Change name (resource-id) of resource
		resourceAttrs = new HashMap<String,String>();           
        resourceAttrs.put(OPENAZ_ATTR_RESOURCE_TYPE, "FrisBee");
        resourceAttrs.put(AzXacmlStrings.X_ATTR_RESOURCE_ID, 
        		"resource-id-FrisBee-4");
        
        // Change name of action
		actionAttrs = new HashMap<String,String>();           
        actionAttrs.put(AzXacmlStrings.X_ATTR_ACTION_ID, "catch");
        
		// Create a Date for a simple environment attribute
		now = new Date();
		
		log.info("\n  Creating pepReq w : " +
			"\n    Subject as HashMap: \n\t"  + objectToString(subject,",") +
			",\n    Resource as HashMap: \n\t" + 
			objectToString(resourceAttrs, ",") + 
			",\n    Action as HashMap: \n\t" + 
			objectToString(actionAttrs, ",") + 
			"\n    and Environment as Object: Date: \n\t" + now + "\n");
		pepReq = 
			pepReqFactory.newPepRequest(
					subject, actionAttrs, resourceAttrs, now); 
		
		// Issue the request and process the response, 
		pepRspCtx = pepReq.decide();			
		if (pepRspCtx != null) {
			log.info("\n    Test: " + testName + ": pepRspCtx.allowed() = " +
						pepRspCtx.allowed() + "\n");
			testUtils.logObligations(pepRspCtx);
		}
		else log.warn(
				"Test: " + testName + " failed, pepRsp seems to be null");			
	} // end testStyleMustBePresentFinder

	
	// migrate this method over to TestUtils
	/**
	 * This section is for system services
	 * 
	 * @param args
	 */
	/*
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
	*/
	
	public static void configureMappers(TestNames testName) {
		// By default these are the mappers set for an AzEntityWrapper factory
	    List<JavaObjectMapper> defaultMappers = 
	    	Arrays.asList(new JavaObjectMapper[] {
	    			new SimpleJavaPermissionMapper(),
	    			new SimpleJavaObjectMapper()});
		List<JavaObjectMapper> objectMapper = 
	    	Arrays.asList(new JavaObjectMapper[] {
	    		(JavaObjectMapper)new SimpleJavaObjectMapper()});
		List<JavaObjectMapper> permissionMapper = 
	    	Arrays.asList(new JavaObjectMapper[] {
	    		(JavaObjectMapper)new SimpleJavaPermissionMapper()});
		switch (testName) {
		case TEST_STRINGS:
			// Use default mapper configuration
			break;
		case TEST_MAPPERS:
			//
			// Now we are going to show how to create some mappers
			// and make sure they are available for use by the
			// authorization service. Appls will not have to do
			// this in general, i.e. it will be done by deployment
			// of services in the container and will be transparent
			// to end users but is shown here for instructive purposes
			
			// Here we create a SimpleJavaObjectMapper, which is
			// used to map Dates, Strings, and HashMaps to xacml
			// attributes. Please see that class in the azapi.pep
			// project for example of how mapper is built
			// Note: we are also adding an extraneous PermissionMapper
			// here to show that it does not impact results, i.e. that
			// the presence of mappers for other objects does not impact
			// the processing of the intended objects
			objectMapper = 
		    	Arrays.asList(new JavaObjectMapper[] {
		    			(JavaObjectMapper)new SimpleJavaObjectMapper(),
		    			(JavaObjectMapper)new SimpleJavaPermissionMapper()});
			
			// This is a PermissionMapper that will be configured
			// such that it is in place for the right types of
			// objects
			permissionMapper = 
		    	Arrays.asList(new JavaObjectMapper[] {
		    			(JavaObjectMapper)new SimpleJavaPermissionMapper()});
			
			// XACML provides logically for 4 classes of entities
			// involved in authorization:
			//   1. Subject, i.e. the entity requesting access
			//   2. Resource, the entity to which access is being 
			//       requested
			//   3. Action, the operation the subject wants to apply 
			//       to the resource
			//   4. Environment, other entities, such as the system, 
			//       container, etc that may have relevant info to 
			//       supply
			
			// In general java objects may be applied to one or more of
			// the above categories. ObjectMappers are configured to
			// the category deemed appropriate. In this case, since
			// Java permissions generally apply to the resource and
			// action, the permission mapper is applied to resource
			// and action.
			// Similarly, since subject is a string in this case, and
			// the env variable supplied is a Date, then the simple
			// javaobjectmapper is used for those categories.

			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdAction.AZ_CATEGORY_ID_ACTION).
						setMappers(permissionMapper);
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE).
						setMappers(permissionMapper);
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS).
						setMappers(objectMapper);
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT).
						setMappers(objectMapper);
			
			logStatic.info("Subject Mapper supported classes: \n\t" +
				pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS).
						getMappers().iterator().next().getSupportedClasses());
			logStatic.info("Subject Mapper supported classes: \n\t" +
				pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS).
						getSupportedClasses());
			break;
		case TEST_SUBJECT_MAPPER:
			// Create 3 Mappers using different Mapper Impls
			// see test 2 for additional info on Mappers
			
			// First create list of one SubjectMapper, which
			// will use TestJaasSubjectMapper to process the
			// JAAS Subject object when it is submitted
			List<JavaObjectMapper> subjectMapper = 
		    	Arrays.asList(new JavaObjectMapper[] {
		    		new TestJaasSubjectMapper()});
			// SimpleJavaObjectMapper for Date,String or HashMap
			objectMapper = 
		    	Arrays.asList(new JavaObjectMapper[] {
		    		(JavaObjectMapper)new SimpleJavaObjectMapper()});
			// SimpleJavaPermissionMapper for resource action pairs
			permissionMapper = 
		    	Arrays.asList(new JavaObjectMapper[] {
		    		(JavaObjectMapper)new SimpleJavaPermissionMapper()});
			
			// Set the appropriate Mappers on respective factories
			// Action and Resource get the SimpleJavaPermissionMapper
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdAction.AZ_CATEGORY_ID_ACTION).
						setMappers(permissionMapper);
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE).
						setMappers(permissionMapper);
			// Subject gets the TestJaasSubjectMapper
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS).
						setMappers(subjectMapper);
			// Environment gets the SimpleJavaObjectMapper
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT).
						setMappers(objectMapper);
			break;
		case TEST_QUERY:
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS).
						setMappers(objectMapper);
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdAction.AZ_CATEGORY_ID_ACTION).
						setMappers(permissionMapper);
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE).
						setMappers(permissionMapper);
			
			logStatic.info("Subject Mapper supported classes: \n\t" +
				pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS).
						getSupportedClasses());
			break;
		case TEST_MUST_BE_PRESENT:
			permissionMapper = 
		    	Arrays.asList(new JavaObjectMapper[] {
	    			(JavaObjectMapper)new SimpleJavaObjectMapper(),
		    		(JavaObjectMapper)new SimpleJavaPermissionMapper()});
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdAction.AZ_CATEGORY_ID_ACTION).
						setMappers(permissionMapper);
			pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE).
						setMappers(permissionMapper);
			
			logStatic.info("\n   Classes supported by Mappers configured " +
					" for Subject entities = \n\t" +
				objectToString(pepReqFactory.getRequestAttributesFactory(
					AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS).
						getSupportedClasses(), ",") + "\n");
			break;
		default:
			// TBD:
		}		
	} // end configureMappers
	
	public static void setLogFeatures(Log log) {
		// Following, logLevel must be one of (ow take default): 
		//		("trace", "debug", "info", "warn", "error", or "fatal").
		String logLevel = (String)logFactory.getAttribute(
				"org.apache.commons.logging.simplelog.defaultlog");
		if (logStatic.isDebugEnabled()) {
			logStatic.info("logFactory.getAttr(...defaultlog) = " +
					logFactory.getAttribute(
						"org.apache.commons.logging.simplelog.defaultlog"));
			logStatic.info("classpath: " + System.getProperty("java.class.path"));
			String classPath = System.getProperty("java.class.path");
			StringTokenizer st = new StringTokenizer(classPath," \t\n\r\f();");
			StringBuffer sb = new StringBuffer();
			String sToken = null;
			int tokenNumber = 0;
			while (st.hasMoreTokens()) {
				tokenNumber++;
				sToken = st.nextToken();
				sb.append("\n\t" + tokenNumber + ": " + sToken);
			}
			logStatic.info("classpath = " + sb.toString());
			logStatic.info("logStatic.isInfoEnabled() = " + 
						logStatic.isInfoEnabled());
			logStatic.info("logStatic.isDebugEnabled() = " + 
						logStatic.isDebugEnabled());
			logStatic.info(
					"\nTRACE: Log logStatic = " + 
						"LogFactory.getLog(OpenAzTutorial.class) -> " +
					"\n\tlogStatic.getClass().getName() = " + 
						logStatic.getClass().getName() +
					"\n\tlogFactory.getClass().getName() = " + 
						logFactory.getClass().getName() +
					"\n\tLogFactory.FACTORY_PROPERTIES = " + 
						LogFactory.FACTORY_PROPERTIES +
					"\n\tLogFactory.FACTORY_PROPERTY = " + 
						 LogFactory.FACTORY_PROPERTY +
					"\n\tLogFactory.FACTORY_DEFAULT = " + 				
						LogFactory.FACTORY_DEFAULT);
			
			String[] attributeNames = logFactory.getAttributeNames();
			int attrNamesLen = attributeNames.length;
			if (attrNamesLen > 0) {
				for (int i=0;i<attrNamesLen;i++){
					logStatic.info("TRACE: logFactory.getAttributeNames()[" 
						+ i + "] = " + attributeNames[i] + 
						"\n\t\t\tvalue = " + 
						logFactory.getAttribute(attributeNames[i]));
				}
			} else {
				logStatic.info(
					"TRACE: attrNamesLen=0 => " + 
						"probably not reading a simplelog.properties file");
			}
		}
	} // end setLogFeatures
	
	public static void testLogFeatures(Log log) {
		if (logStatic.isDebugEnabled()) {
			logStatic.info("LogFactory = " + LogFactory.class);
	
			logStatic.info("System: java.version = " +
				System.getProperty("java.version"));
			logStatic.info("System: java.home = " +
				System.getProperty("java.home"));
			logStatic.info("System: user.name = " + 
				System.getProperty("user.name"));
			logStatic.info("System: user.home = " + 
				System.getProperty("user.home"));
			
			logStatic.info("Security: policy.provider = " + 
				Security.getProperty("policy.provider"));
			logStatic.info("Security: policy.url.1 = " + 
				Security.getProperty("policy.url.1"));
			logStatic.info("Security: policy.url.2 = " + 
				Security.getProperty("policy.url.2"));
			logStatic.info("Security: java.security.policy = " + 
				Security.getProperty("java.security.policy"));
			logStatic.info("Security: java.security.manager = " + 
				Security.getProperty("java.security.manager"));
			logStatic.info("Security: policy.allowSystemProperty = " + 
				Security.getProperty("policy.allowSystemProperty"));
		}
	} // end testLogFeatures

	private static StringTokenizer st;
	/**
	 * Helper method to print collections that have a delimiter
	 * between values.
	 * @param o
	 * @param delimiter
	 * @return a string for display, multiline, 
	 * 	one "name + (delim(=)) + value" per line
	 */
	public static String objectToString(Object o, String delimiter) {
		
		if (delimiter.equals(""))
			return o.toString();
		
		String outputString = "";
		boolean firstToken = true;
		st = new StringTokenizer(o.toString(), delimiter);
		while (st.hasMoreTokens()) {
			if ( ! firstToken )
				if (delimiter.equals(" "))
					// in other cases a leading space is part of the next token,
					// which is useful for alignment w bracket of set
					outputString += delimiter + "\n\t ";
				else
					outputString += delimiter + "\n\t";
			else
				firstToken = false;
			outputString += st.nextToken();
		}
		return outputString;
	}
	
	public void printTestHeader(String testName) {
		if (log.isTraceEnabled()) log.trace(
		  "\n\n" +
		  "\n************************************" + 
		  		"***********************************" +
		  "\n************************************" + 
		  		"***********************************" +
		  "\n************************************" + 
		  		"***********************************" +
		  "\n\n");
		log.info(
				"\n&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n" +
				"     Running Test Style: " + testName + "\n" +
				"&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n");
		log.info("\nBegin test style: " + testName);
	}
}
