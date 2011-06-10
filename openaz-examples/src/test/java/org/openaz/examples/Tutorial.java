package org.openaz.examples;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.io.StringWriter;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.AzService;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.openliberty.openaz.pdp.AzServiceFactory;
import org.openliberty.openaz.pdp.provider.SimpleConcreteSunXacmlService;
import org.openliberty.openaz.pep.PepRequestFactoryImpl;

import test.policies.OpenAzLineContext;
import test.policies.OpenAzParseException;
import test.policies.OpenAzPolicyReader;
import test.policies.OpenAzTokens.LineType;
import test.policies.OpenAzXacmlObject;
import test.TestUtils;

import com.sun.xacml.Indenter;
import com.sun.xacml.ParsingException;
import com.sun.xacml.PolicySet;
import com.sun.xacml.UnknownIdentifierException;

import junit.framework.TestCase;

public class Tutorial extends TestCase {
	public final static String CONTAINER = "orcl-weblogic"; 
	public final static String DEFAULT_PROVIDER_NAME = "AZ_SERVICE";
	public final static String SAMPLE_SESSION_USER_NAME = "Joe User";
	
	String requestFile = null; // not used; artifact from openaz testing
	
 	StringWriter sw = new StringWriter();
	
	Log log = LogFactory.getLog("OpenAzTutorialInstance");
	static TestUtils testUtils = new TestUtils();
	
	//static Log logStatic = LogFactory.getLog(OpenAzTutorial.class);
	
	//static LogFactory logFactory = LogFactory.getFactory();
	
    //private static final Logger logger = Logger.getLogger(OpenAzTutorial.class.getName());
    

	protected void setUp() throws Exception {
		super.setUp();
	}

	protected void tearDown() throws Exception {
		super.tearDown();
	}
	
	public void testBasicPolicy() throws Exception {
		String policyFile = "src/test/resources/OpenAz-Pseudo-Test-Policy.txt";
		File f = new File(policyFile);
		if(! f.canRead())
			fail();
		
		File xacmlPolicyFile = createXacmlPolicy(policyFile);		
		String policyFiles[] = new String[]{xacmlPolicyFile.getPath()};
		
		try {
			// Register an instance of SimpleConcreteSunXacmlService
			//  as the AzService provider:
			AzServiceFactory.registerProvider(
				DEFAULT_PROVIDER_NAME, 
				new SimpleConcreteSunXacmlService(
						requestFile,
						policyFiles));
		} catch (ParsingException pe) {
			//pe.printStackTrace(new PrintWriter(sw));
			log.info("SunXacml ParsingException: " +
					pe.getMessage() + "\n" + sw);
		} catch (UnknownIdentifierException uie) {
			uie.printStackTrace(new PrintWriter(sw));
			log.info("SunXacml UnknownIdentifierException: " + 
					uie.getMessage() + "\n" + sw);
		}

		
		runStringTest();
		
	}
	
	public void runStringTest() throws Exception {

		// This may be useful if non-default logging is used.
		// Default logging is "info". i.e. "info" and higher level log messages
		//  are printed to system.err in default configuration, which is
		//  basically unzip the files, build, and run.
		//if (log.isTraceEnabled()) checkLogFeatures(log);

		log.info("\n&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n" +
				 "      Running Test Style: String          \n" +
				 "&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&\n");
		log.info("Begin test style: String");
		
		// Create the az service. In the "real world" this will be done
		// by the container and invisible to the application. We will
		// probably have some std object that users can refer to as
		// the handle to the az service much like AccessControlContext
		// is used in std java env. i.e. the following will ultimately
		// be reducible to az.decide(str, str, str) for a typical appl
		
        //AzService az = new org.openliberty.openaz.
		//		pdp.provider.SimpleConcreteDummyService();
        //AzService az = new org.openliberty.openaz.
		//		pdp.provider.SimpleConcreteSunXacmlService();
		AzService az = AzServiceFactory.getAzService();
        
		if (!(az==null)){
			// The PepRequestFactory is used to create az requests by
			// passing it the parameters that are needed for az, in
			// this case, 3 strings:
			// init the factory w the container name and the az service
			PepRequestFactory pepReqFactory = 
				(PepRequestFactory)new PepRequestFactoryImpl(CONTAINER,az);
			
			// Define sample strings to provide to simple string PepRequest
 			String subjectId = SAMPLE_SESSION_USER_NAME;
 			String resourceId = "file:C\\toplevel";
 			
 			// try to match the policy:
 			subjectId = "Joe Smith";
 			resourceId = "C:\\\\jsmith\\\\a\\\\b\\\\c";
 			//String actionId = "read";
 			String actionId = "Tutorial-Read";
 			
 			// Create the actual request using the factory to
 			// create the request and passing the strings to
 			// specify the details of the request
			log.info("Creating pepReq - \n\t  w subject,action,resource: " +
					subjectId + ", " + actionId + ", " + resourceId);
			PepRequest pepReq = 
				pepReqFactory.newPepRequest(
						subjectId, actionId, resourceId); 
			
			// Issue the request and receive a PepResponse object
			// in return, which contains details of the results
			// of the request
			PepResponse pepRspCtx = pepReq.decide();
			
			// Print the result (allowed true or false) and
			// then log the obligations, if any were returned.
			log.info("\n\t" + subjectId + 
					" allowed() = " + pepRspCtx.allowed() + "\n");
			testUtils.logObligations(pepRspCtx);
			
			// Try bad user:			
			subjectId = "Joe Bad User";
			log.info("Creating pepReq - " +
				"\n\t  w subject,action,resource: " +
				subjectId + ", " + actionId + ", " + resourceId);
			pepRspCtx = pepReqFactory.newPepRequest(
					subjectId, actionId, resourceId).decide();
			log.info("\n\t" + subjectId + 
					" allowed() = " + pepRspCtx.allowed() + "\n");
			testUtils.logObligations(pepRspCtx);			
		}
		else
			log.info("az == null");		
	}
	

	/**
	 * This method uses the simple "xacml shorthand" syntax to create
	 * a xacml policy that is returned as a byte array.
	 * @return a File object containing the generated XML Policy file
	 */
	public File createXacmlPolicy(String policyFile) {
		//boolean result = false;
		//FileWriter fw = null;
		//byte[] policyBytes = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		FileOutputStream fos = null;
		File xacmlPolicyFile = null;
		log.debug(
			"Logging with log.getClass().getName() = " + 
				log.getClass().getName());
		
		OpenAzLineContext oAzLineContext =
			new OpenAzLineContext(0, LineType.INIT);
		// create an OpenAzPolicyReader
		OpenAzPolicyReader oAzPolicyReader =
			new OpenAzPolicyReader(policyFile);
		try {
			OpenAzXacmlObject oAzXacmlObject =
				oAzPolicyReader.readAzLine(oAzLineContext);
			Object parsedObject = 
				oAzXacmlObject.getObject();
			PolicySet ps = (PolicySet) parsedObject;
			if (log.isInfoEnabled()) {
				log.info("Output Policy: ");
				ps.encode(System.out, new Indenter());
			}
			ps.encode(baos);
			//policyBytes = baos.toByteArray();
			//ps.encode(fw);
			// for the output file, we need a name
			// take existing name and append ".xml"
			xacmlPolicyFile = new File(policyFile+".xml");
			fos = new FileOutputStream(xacmlPolicyFile);
			ps.encode(fos);
			
	
		} catch (OpenAzParseException oape) {
			log.fatal(
				"Exception reading policy pseudo file: " +
				oape.getMessage());
		} catch (FileNotFoundException fnf) {
			log.fatal(
				"FileNotFoundException: " + fnf.getMessage());
		}
		
		return xacmlPolicyFile;
	}
	

}
