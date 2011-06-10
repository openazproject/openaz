package test;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.AzService;
import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.pdp.provider.AzServiceFactory;
import org.openliberty.openaz.pep.PepRequestFactoryImpl;

import test.objects.OAuthRequest;
import test.objects.OaAzServer;
import test.objects.OaClient;
import test.objects.OaResourceServer;
import test.objects.OaResourceOwner;
/**
 * This simulator is intended to demonstrate the sequence of
 * OAuth 2.0 http message exchanges by constructing actors for
 * each of the OAuth 2.0 active modules and passing oauth requests
 * and responses between these modules.
 * <p>
 * This simulator uses PepApi (openaz.azapi.pep) to do the
 * official authorizations necessary by the OAuth Authorization
 * Server in the OAuth 2.0 sequences.
 * <p>
 * The following activity diagram shows the basic operational structure,
 * of the first OAuth 2.0 sequence to be demonstrated, which is the
 * "Authorization Code" sequence as described in section 4.1 of
 * OAuth 2.0 Protocol Version 2-13:
 * <br>
 *   http://tools.ietf.org/html/draft-ietf-oauth-v2-13
 * <pre>
 * 
     +-------------------------------------------+           +-----------------+
     | authorization server               +------|           | resource server |
     |  (scopes)                          .  ^   | &lt;-----{3}-o  (resource-ids) |
     |  (az-end-uri) (for cl redir)       .valid-| o-{4}-----&gt;  (resources)    |
     |                                    .ation |           |                 |
     |                                    .endpt | &lt;----{15}-o                 |
     |                                    .  v   | o-{16}----&gt;                 |
     |                                    +------|           |                 |
     |+..............+            +.............+|           |                 |
     ||-- az endpt --|            |-token endpt-||           |                 |
     +-o--^--------------------------------------+           +-----------------+
       |  |      o  ^                   ^  |                   ^  |       ^  |
      {8} |      |  |                   | {13}                 | {17}     | {5}
       | {9}   {10} |                   |  |                   |  |       |  |
       |  |      | {7}                {12} |                 {14} |      {2} |
       V  o      |  |                   |  |                   |  |       |  |
     +-----------v--o-+               +-o--v-------------------o--v-------o--v-+
     | resource owner |               | client application                     |
     | user-agent     | &lt;-------{6}---o  (client-id)                           |
     | web browser    | o---{11}------&gt;  (client-uri) (for az redirect back)   |
     |  (client-uri)  |               |                                        |
     |  (user-id)     o---{1}-------&gt; |                                        |
     |  (resource-id) &lt;------{18}---o |                                        |
     |  (action-id)   |               |                                        |
     +----------------+               +----------------------------------------+

</pre>
 * where the numbered arrows show the sequence of http messages
 * that are sent between the active components (note the initiator
 * (calling) side has an "o" on the box perimeter line
 * to indicate requesting (sending), and a "v" on the box line to
 * indicate response (receiving), while recipient (callee) side has
 * an unbroken box line "----"):
<pre>

   Message content (key data elements for oauth protocol to process):

       {1}         contains the user-id, action-id, resource-id that it wants
                    client to perform
       {2}         contains resource-id, action-id
       {3}         contains resource-id, action-id, no access token
       {4}         contains az-end-uri, so res svr can tell client where to
                    get res owner authorization
       {5}         contains az-end-uri
       {6},{7}     contains the client-id, user-id, action-id, resource-id to
                    give to az so it can get res own ok
       {8}         contains the client-id, user-id, action-id, resource-id,
                    scope (scope determined from resource-id, action-id)
       {9}         contains user-creds, client-authorization wrt
                    client-id, user-id, action-id, resource-id, scope
       {10},{11}   contains the authorization code that encapsulates
                    user-authorization, client-id,  user-id, action-id,
                    resource-id, scope
       {12}        contains authorization code, client-creds
       {13},{14}   contains access-token that covers scope, duration
       {15}        contains access-token for az to validate
       {16}        contains ok from az that access-token is valid
       {17}        contains resource(resource-id) for client to process
       {18}        contains resource processing completion info for user


   HTTP Exchanges (from http sender perspective):

      {1,18}            initial user req + final client rsp
      {2,5}             client tries to get resource w/o access token,
                         resource server returns az-end-uri
      {3,4}             resource server tries to get token validation, but
                         w/o access token, az svr returns az-end-uri
                         so client can get resource owner authorization
      {6,11},{7,10}     redirect pair (client uses client-id, user-id,
                         and resource-id to get user authorization packaged
                         in an "authorization code")
      {8,9}             user authentication and authorization for client
                         az svr passes user-id, resource-id, client-id
                         + ?client-uri + scope to grant for ro to approve
      {12,13}           client uses authorization code to get access token
      {14,17}           client uses access token to get resource
      {15,16}           resource server validates access token to get an
                         ok to return the resource

 * </pre>
 * @author rlevinson
 *
 */
public class OAuthSimulator {
	
	// defn: "container": The "container" may be thought of as
	// the entity that manages the "environment" and thus may
	// be considered to be the "issuer"  of env attributes
	// We will use the name of the container for this purpose
	public final static String CONTAINER = "orcl-weblogic"; 
	
	// Test PepRequestFactory:
	public static PepRequestFactory pepReqFactory = null;
	
	// TestUtils is used for printing obligations, which
	// goes thru the structures and prints relevant info
	static TestUtils testUtils = new TestUtils();
	Log log = LogFactory.getLog(this.getClass()); 
	static Log logStatic = LogFactory.getLog(TestStyles.class);
	static LogFactory logFactory = LogFactory.getFactory();

	public static void main(String[] args) {
		
		logStatic.info("Testing OAuthSimulator version 115");
		
		// Set up the AzService for general use
		TestUtils.setupAzService(args);
		
		// Get the AzService for use by PepRequestFactory
		AzService az = AzServiceFactory.getAzService();  		
		if ( ! (az == null) ) {
			// Set up the PepRequestFactory for use by applications
			pepReqFactory =
				(PepRequestFactory)new PepRequestFactoryImpl(
						CONTAINER,az);		
				
			// create an instance of the simulator, which contains
			// a set of actor components
			OAuthSimulator oaSim = new OAuthSimulator();
			oaSim.oaTestWebServerFlow();
		}
		else
			logStatic.warn("Test failed, configured service: az == null");		
	}
	
	public void oaTestWebServerFlow() {
        log.info("Log = " + log.getClass());

		// create a set of OAuth components
		OaAzServer oaAzSvr = new OaAzServer();
		OaClient oaClient = new OaClient();
		OaResourceServer oaResSvr = new OaResourceServer();
		OaResourceOwner oaResOwner = new OaResourceOwner();
		
		// create an OAuth context with the set of active components
		OAuthRequest oaRequest = new OAuthRequest(
				oaAzSvr, oaClient, oaResSvr, oaResOwner);
		
		// Set up the authorization URI for the AzServer
		//oaAzSvr.setAuthorizationURI("http://www.bigidpserver.com/authorize");
		oaAzSvr.setAuthorizationURI("http://az-svr/az-endpt/");
		// Set up the validation URI for the AzServer
		oaAzSvr.setValidationURI("http://az-svr/vl-endpt/");
		// Set up the token endpoint URI for the AzServer
		oaAzSvr.setTokenEndpointURI("http://az-svr/tk-endpt/");
		
		// Set up the resource server URI
		oaResSvr.setResourceServerURI("http://res-svr/");
		
		// Set up the client with a clientId and pwd
		oaClient.setClientId("http://www.client01.com");
		oaClient.setClientPwd("passwordClient");
		// make the client URI the same as the clientId for now
		oaClient.setClientRedirectURI(oaClient.getClientId());
		
		// Start the flow by giving the ResourceOwner its id info,
		// and the info it needs to ask a client to perform an
		// action on a resource
		oaResOwner.setOwnerId("OaResOwner01", "password");
		oaResOwner.setResource("http://res-svr/",
							   "OaResOwner01/resource01", "read");
		
		// start the transaction:
		String result = oaResOwner.issueRequest();
	}

	public static PepRequestFactory getPepRequestFactory() {
		return pepReqFactory;
	}
}
