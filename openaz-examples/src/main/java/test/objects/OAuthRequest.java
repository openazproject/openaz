package test.objects;


/**
 * This class contains the information used in various requests
 * made in the OAuth 2.0 protocol. 
 * <p>
 * This class serves as a common repository for all the data elements
 * required in a single OAuth transaction. It enables initialization
 * from a single point, and provides the data parameters that
 * each entity needs as the scenario unfolds.
 * <p>
 * Each OAuth entity in the simulator is expected to supply info under
 * its control when submitting a request, and to add information
 * appropriately when it is processing a request, which will often be
 * the same object. i.e. when an oauth entity method is invoked, usually
 * the OAuthRequest will be a parameter passed in, and when the next
 * oauth entity is invoked the OAuthRequest (updated) will be a 
 * parameter that is passed.
 * 
 * @author rlevinson
 *
 */
public class OAuthRequest {
	// OAuthRequest data structure:
	// abbreviations: resOwner(ro), azSvr(az), client(cl), resSvr(rs)
	//                userAgent(ua)
	OaResourceOwner oaResOwner = null; 	// ro object active in this oaReq
	OaClient oaClient = null; 			// cl object active in this oaReq
	OaResourceServer oaResSvr = null; 	// rs object active in this oaReq
	OaAzServer oaAzSvr = null; 			// az object active in this oaReq
	// Note: primarily going to use oaResOwner instead of agent
	OaUserAgent oaUserAgent = null; 		// ua object active in this oaReq
	
	String ownerId = null;	// "id" by which resOwner known to az
	String ownerPwd = null; // password for owner login to azSvr
	String userId = null;	// "id" by which resOwner known to cl
	String clientId = null;	// "id" by which client known to ro, az
	String clientPwd = null; // password for client login to azSvr
	String resourceId = null; // "id" by which reqRes known to ro,cl,az,rs
	String actionId = null; // "id" by which action known
	String scope = null;	// set of space-delim strs, known to az,rs,ro
	
	String oaAzEndpointURI = null; // authorization endpoint URI
	String oaTokenEndpointURI = null; // token endpoint URI
	String oaValidationEndpointURI = null; // token validation endpt URI
	String oaClientRedirectURI = null; // URI client wants redirected back to
	String oaResSvrURI = null; // URI for resource server, and its "id"
	
	String oaAuthorizationCode = null; // az-code to return to client
	String oaAccessToken = null; // access token client gets for az-code
	
	/**
	 * constructor: tbd what's most useful for params
	 */
	public OAuthRequest() {
	}
	public OAuthRequest(OaAzServer oaAzSvr,
						OaClient oaClient,
						OaResourceServer oaResSvr,
						OaResourceOwner oaResOwner) {
		this.oaAzSvr = oaAzSvr;
		this.oaClient = oaClient;
		this.oaResSvr = oaResSvr;
		this.oaResOwner = oaResOwner;
		oaAzSvr.setOaRequest(this);
		oaClient.setOaRequest(this);
		oaResSvr.setOaRequest(this);
		oaResOwner.setOaRequest(this);
	}
	
	// Setters and Getters for the OAuth entity objects:
	public void setResourceOwner(OaResourceOwner oaResOwner) {
		this.oaResOwner = oaResOwner;
	}
	public OaResourceOwner getResourceOwner() {
		return oaResOwner;
	}
	public void setResourceServer(OaResourceServer oaResSvr) {
		this.oaResSvr = oaResSvr;
	}
	public OaResourceServer getResourceServer() {
		return oaResSvr;
	}
	public void setClient(OaClient oaClient) {
		this.oaClient = oaClient;
	}
	public OaClient getClient() {
		return oaClient;
	}
	public void setAzServer(OaAzServer oaAzSvr) {
		this.oaAzSvr = oaAzSvr;
	}
	public OaAzServer getAzServer() {
		return oaAzSvr;
	}
	
	// Set up resource owner id and password w getters
	public void setOwnerId(String ownerId, String ownerPwd){
		this.ownerId = ownerId; // owner id at azSvr
		this.userId = ownerId; // use same id for owner at az and cl
		this.ownerPwd = ownerPwd; // owner pwd at azSvr
	}
	public String getOwnerId() {
		return ownerId;
	}
	String getOwnerPwd() {
		return ownerPwd;
	}
	
	// Set resource and action ids along w getters
	public void setResourceId(String resSvrURI, 
							  String resourceName,
							  String actionId){
		this.resourceId = resSvrURI + resourceName; // std URI
		this.actionId = actionId; // requested action
	}
	public String getResourceId() {
		return resourceId;
	}
	public String getActionId() {
		return actionId;
	}
	
	// set up client-id
	public void setClientId(String clientId) {
		this.clientId = clientId;
	}
	public String getClientId() {
		return clientId;
	}
	// set up some client creds
	public void setClientPwd(String clientPwd) {
		this.clientPwd = clientPwd;
	}
	public String getClientPwd() {
		return clientPwd;
	}
	
	// set up access token
	public void setAccessToken(String oaAccessToken) {
		this.oaAccessToken = oaAccessToken;
	}
	public String getAccessToken() {
		return oaAccessToken;
	}
	
	// set up Authorization Endpoint URI
	public void setAzEndpointURI(String oaAzEndpointURI) {
		this.oaAzEndpointURI = oaAzEndpointURI;
	}
	public String getAzEndpointURI() {
		return oaAzEndpointURI;
	}
	
	// set up Token Endpoint URI
	public void setTokenEndpointURI(String oaTokenEndpointURI) {
		this.oaTokenEndpointURI = oaTokenEndpointURI;
	}
	public String getTokenEndpointURI() {
		return oaTokenEndpointURI;
	}
	
	// set up Validation Endpoint URI
	public void setValidationEndpointURI(String oaValidationEndpointURI) {
		this.oaValidationEndpointURI = oaValidationEndpointURI;
	}
	public String getValidationEndpointURI() {
		return oaValidationEndpointURI;
	}
	
	// set up client redirect URI
	public void setClientRedirectURI(String oaClientRedirectURI) {
		this.oaClientRedirectURI = oaClientRedirectURI;
	}
	public String getClientRedirectURI() {
		return oaClientRedirectURI;
	}
	
	// set up the resource server URI
	public void setResourceServerURI(String oaResSvrURI) {
		this.oaResSvrURI = oaResSvrURI;
	}
	public String getResourceServerURI() {
		return oaResSvrURI;
	}
	
	// set up scope
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getScope() {
		return scope;
	}
	
	// set up authorization code
	public void setAuthorizationCode(String oaAuthorizationCode) {
		this.oaAuthorizationCode = oaAuthorizationCode;
	}
	public String getAuthorizationCode() {
		return oaAuthorizationCode;
	}
}

