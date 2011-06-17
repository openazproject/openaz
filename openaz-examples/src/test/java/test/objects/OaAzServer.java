package test.objects;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;
import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepResponse;

import test.OAuthSimulator;
/**
 * This class simulates an OAuth 2.0 Authorization Service
 * for the "Web Server (Authorization Code) Flow".
 * <p>
 * It uses OpenAz PepApi to make calls to a SunXacml Policy Server
 * wrapped in OpenAz AzApi.
 * <p>
 * It processes simulated http requests, which are represented by
 * a collection of parameters that are passed to the endpoint, each
 * of which is represented by a method call:
 * <ul>
 * <li>{@link #validateToken(Map)}
 * <li>{@link #getAuthorizationFromRO(Map)}
 * <li>{@link #getAccessTokenUsingAzCodeCliCreds(Map)}
 * </ul>
 * <p>
 * It contains 3 "endPoints":
 * <ul>
 * <li>authorization endpoint URI
 * <li>token endpoint URI
 * <li>token validation endpt URI
 * </ul>
 * @author rlevinson
 *
 */
public class OaAzServer {
	OAuthRequest oaRequest = null;
	Log log = LogFactory.getLog(this.getClass()); 
	PepRequestFactory pepReqFactory = null;
	public OaAzServer() {
		pepReqFactory = OAuthSimulator.getPepRequestFactory();
	}
	public void setOaRequest(OAuthRequest oaRequest) {
		this.oaRequest = oaRequest;
	}
	public OAuthRequest getOaRequest() {
		return oaRequest;
	}
	public void setAuthorizationURI(String azURI) {
		oaRequest.setAzEndpointURI(azURI);
	}
	public void setValidationURI(String vlURI) {
		oaRequest.setValidationEndpointURI(vlURI);
	}
	public void setTokenEndpointURI(String oaTokenEndpointURI) {
		oaRequest.setTokenEndpointURI(oaTokenEndpointURI);
	}
	public String getTokenEndpointURI() {
		return oaRequest.getTokenEndpointURI();
	}
	/**
	 * This is the Authorization Server Validation Endpoint
	 * that is used to validate access tokens requested by
	 * an OaResourceServer.
	 * <p>
	 * An objective of OAuth 2.0 is have resource servers only
	 * handle tokens from an external authorization service.
	 * However, clients w/o a token will need to ask where to
	 * get a token, and depending on what the request is, there
	 * may be a specific authoriZation service configured.
	 * <p>
	 * Therefore, a validation request may contain either a
	 * token or a bare request (or both, but then the token
	 * will be validated and the request ignored)
	 * <p>
	 * The parameter passed is a Map&lt;String,String&gt; that
	 * contains a key "TokenFlag", which, if it contains the value,
	 * "t" indicates the rest of the values in the Map may be
	 * considered values from a hypothetical token. In fact,
	 * it is basically the same parameters that get passed
	 * in both the token and no token requests, so all we need
	 * is the TokenFlag to say whether the params should be
	 * considered to be inside a token or not.
	 * <p>
	 * i.e. a token in the real world has some meaning, which can
	 * probably be expressed as a set of attribute string values,
	 * and those values are either packaged and encrypted to a
	 * "token" or the token is a reference to something on the
	 * authorization server that generated the token and that
	 * something has the info that was used as the basis for
	 * generating the token. That way, when the az server validates
	 * the token it knows what its validating.
	 * <p>
	 * In the case of "no token", the same parameters are passed
	 * anyway so the appropriate authorization URI for redirection.
	 * <p>
	 * Validation should be done using a PepRequest: TBD
	 * @param oaAccReqParams a Map&lt;String,String&gt; containing
	 * a token or a "request".
	 * @return
	 */
	public String validateToken(Map<String,String> oaAccReqParams) {
		String response = null;
		boolean reqContainsAccToken = false;
		String accTokenFlag = oaAccReqParams.get("TokenFlag");
		if ( ! ( accTokenFlag == null ) ) {
			reqContainsAccToken = accTokenFlag.toLowerCase().equals("t");
		}
		String resSvr = oaAccReqParams.get("res-svr");
		String vlEndptURI = oaAccReqParams.get("vl-endpt-uri");
		if ( reqContainsAccToken ) {
			log.info("Msg #15 (RS-> AS): OaAzServer.validateToken: " + 
					"\n    AS (az-server) processing message #15 (null token): RS->AS " + 
					"\n\t(valid client request to resource server): " +
					"\n\toaAccessToken = " + oaAccReqParams);
			//TODO: come up w some validation criteria once
			// there is some defn to the token structure
			// i.e. need to do a PepRequest and the Policy
			// needs to eval whatever params we can pass
			// from cracking open the token (which, since
			// it is our token should be easy).
			
			boolean accessTokenValid = false;
			// set valid to true
			accessTokenValid = true;
			
			// do a PepRequest to confirm the token
			
			try{ 
				
				// create parameter map for subject
				HashMap<String,Object> subjectParams = new HashMap<String,Object>();
				subjectParams.put("res-svr", resSvr);
				subjectParams.put(
						"token-present", new Boolean(reqContainsAccToken));
				subjectParams.put(
						"token-valid", new Boolean(accessTokenValid));
				
				// create parameter map for resource
				HashMap<String,Object> resourceParams = new HashMap<String,Object>();
				// use vlEndptURI from above
				resourceParams.put("vl-endpt-uri", vlEndptURI);
				// resourceId is from approval above
				resourceParams.put(
					AzXacmlStrings.X_ATTR_RESOURCE_ID, 
						"oauth-access-token-confirmation");

				// create parameter map for action
				String actionId = "return-token-confirmation";
				
				// Create a Date for a simple environment attribute
				Date now = new Date();

				PepRequest pepReq = 
					pepReqFactory.newPepRequest(
							subjectParams, actionId, resourceParams, now); 
				PepResponse pepRsp = pepReq.decide();
				log.info("pepRsp.allowed() = " + pepRsp.allowed());
			} catch (PepException pep) {
				System.out.println("PepException: " + pep);
			}
			
			
			
			log.info("Msg #16 (RS <-AS): OaAzServer.validateToken: " + 
					"\n    AS (az-server) returning message #16: AS->RS " + 
					"\n\t(valid client request to resource server): " +
					"\n\toaAccessToken = " + oaAccReqParams);
			response = "ok";
		}
		else {
			// if no token then send back the oaAuthorizationURI
			// that the client can redirect to
			log.info("Msg  #3 (RS-> AS): OaAzServer.validateToken: " + 
					"\n    AS (az-server) processing message #3 (null token): RS->AS " + 
					"\n\t(initial client request to resource server): " +
					"\n\toaAccessToken = " + oaAccReqParams);
			// Here is where AzService needs to be called.
			// Define sample strings to provide to simple string PepRequest
			String userId = oaAccReqParams.get("user-id");
			String resourceId = oaAccReqParams.get("resource-id");
			String actionId = oaAccReqParams.get("action-id");
			String clientId = oaAccReqParams.get("client-id");
			try{ 
				// Create the actual request using the factory to
				// create the request and passing the params to
				// specify the details of the request
				log.info("use pepReq to create req \n\tw subject, action, resource: " +
						"\n\t  " + userId + ", " + actionId + ", " + resourceId);
				HashMap<String,Object> subjectParams = new HashMap<String,Object>();
				subjectParams.put("AttrIdUserId",userId);
				subjectParams.put("AttrIdClientId",clientId);
				//subjectParams.put("IsTokenPresent", 
				subjectParams.put("token-present", 
						new Boolean(reqContainsAccToken));
				subjectParams.put("res-svr", resSvr);
				HashMap<String,Object> resourceParams = new HashMap<String,Object>();
				resourceParams.put("vl-endpt-uri", vlEndptURI);
				resourceParams.put(AzXacmlStrings.X_ATTR_RESOURCE_ID, resourceId);
				// Create a Date for a simple environment attribute
				Date now = new Date();
				//PepRequest pepReq = 
				//	pepReqFactory.newPepRequest(
				//			subjectParams, actionId, resourceId, now); 
				PepRequest pepReq = 
					pepReqFactory.newPepRequest(
							subjectParams, actionId, resourceParams, now); 
				PepResponse pepRsp = pepReq.decide();
				// there should be some obligations indicating whether
				// an Authorization URI or a grant is being returned
				log.info("Msg  #4 (RS <-AS): OaAzServer.validateToken: " + 
						"\n    AS (az-server) returning message #4 (null token): AS->RS " + 
						"\n\t(initial client request to resource server): " +
						"\n\toaAccessRequest = " + oaAccReqParams);
				response = oaRequest.getAzEndpointURI();
			} catch (PepException pep) {}
		}
		return response;
	}
	/**
	 * This module, OaAzServer.getAuthorizationFromRO(), authenticates
	 * the Resource Owner, and gets an Authorization from the Resource Owner:
	 * <ul>
	 * <li>Processes message 7
	 * <li>Sends message 8
	 * <li>Receives message 9
	 * <li>Returns message 10
	 * </ul>
	 * @param oaAccReqParams a collection of named parameters that should
	 * include:
	 * <ul>
	 * <li> az-endpt-uri: tells azServer what URI it is calling
	 * <li> cl-redir-uri: tells azServer where to redirect back to
	 * <li> client-id
	 * <li> resource-id
	 * <li> action-id
	 * <li> user-id: id by which res-owner known at client, res-svr
	 * </ul>
	 * @return
	 */
	public String getAuthorizationFromRO(
					Map<String,String> oaAccReqParams) {

		// This is a 3-step process:
		//  1. issue PepReq to approve the client for an az-code req
		//  2. call resOwner for authentication and client authorizaion
		//  3. issue PepReq to approve giving an azCode to client
		String scope = null;
		
		log.info("Msg  #7 (RO-> AS): OaAzServer.getAuthorizationFromRO: " + 
				"\n    AS (az-server) processing message #7: RO->AS " + 
				"\n\t(2nd leg of redirect from CL->RO->AZ): " + 
				"oaAccReqParams: \n\t" + oaAccReqParams);
		
		// Set up the PepRequest. First collect the variables that
		// are going to be needed, most of which come from the
		// collection oaAccReqParams that represents an incoming
		// http request. (the redirected msg #7)
		String userId = oaAccReqParams.get("user-id");
		String resourceId = oaAccReqParams.get("resource-id");
		String actionId = oaAccReqParams.get("action-id");
		String clientId = oaAccReqParams.get("client-id");
		String clRedirURI = oaAccReqParams.get("cl-redir-uri");
		String azEndptURI = oaAccReqParams.get("az-endpt-uri");
		
		// The "scope" being asked for, effectively consists of
		// the userId, resourceId, and actionId. So if any of
		// these are not provided, then set scope-provided to false:
		boolean scopeProvided = true;
		if ( ( userId == null ) ||
			 ( resourceId == null ) || 
			 ( actionId == null ) ) {
			scopeProvided = false;
		}
		
		// This is step 1: build and issue PepReq for pre-approval
		// This request basically determines if the client is authorized
		// to have the authorization server get an authorization code
		// by having the authorization server ask the resource owner
		// for an ok. If not, kick out the req and don't bother
		// the resource owner unnecessarily
		try{ 
			log.info("use pepReq to create req \n\tw subject, action, resource: " +
					"\n\t  " + userId + ", " + actionId + ", " + resourceId);
			
			// create parameter map for subject
			HashMap<String,Object> subjectParams = new HashMap<String,Object>();
			subjectParams.put("AttrIdUserId",userId);
			subjectParams.put("client-id",clientId);
			subjectParams.put("scope-provided", 
								new Boolean(scopeProvided));
	
			// create parameter map for resource
			HashMap<String,Object> resourceParams = new HashMap<String,Object>();
			resourceParams.put("az-endpt-uri", azEndptURI);
			resourceParams.put(AzXacmlStrings.X_ATTR_RESOURCE_ID, resourceId);
			resourceParams.put("cl-redir-uri", clRedirURI);

			// Create a Date for a simple environment attribute
			Date now = new Date();

			// actionId is taken directly from the oaAccReqParams above
			PepRequest pepReq = 
				pepReqFactory.newPepRequest(
						subjectParams, actionId, resourceParams, now); 
			PepResponse pepRsp = pepReq.decide();
		} catch (PepException pep) {
			System.out.println("PepException: " + pep);
		}
		
		// step 2: assuming approval rcvd above, now set up to
		// call the resource owner to get the resource owner
		// to authenticate, plus the resource owner should
		// indicate approval (authorization) of the client
		// request and scope
		// snd msg 8, rcv msg 9
		
		// collection to collect args to send to res-owner
		Map<String,Object> approval = new HashMap<String,Object>();
		
		OaResourceOwner resOwner = oaRequest.getResourceOwner();
		approval = resOwner.gatherCredsGetAz(clientId, resourceId,
							actionId, userId, scope);
		
		// the result az-code is for the scope that the client requested,
		// namely the resourceId and actionId originally requested
		// note: use the returned result as the Authorization Code string
		// note: this is preliminary, since we don't return it
		// until we rcv the res-owner az, which is next.

		String result = null;	// on success will be authorization code
		
		result = approval.toString();
		oaRequest.setAuthorizationCode(result);
		
		// step 3: get the authorization to generate the az-code
		// need a map for the action params this time, as well
		// the res-owner parameters are in the approval Map
		
		// Set up the PepRequest. First collect the variables that
		// are going to be needed, most of which come from the
		// collection, approval, that contains the response from
		// the resOwner of authentication and approval
		String subjectId = (String)approval.get("subject-id");
		String subjectCreds = (String)approval.get("subject-creds");
		String oaAzApprove = (String)approval.get("oa-act-id");
		clientId = (String)approval.get("client-id");
		resourceId = (String)approval.get("cl-res-id");
		actionId = (String)approval.get("cl-act-id");
		userId = (String)approval.get("cl-user-id");
		
		// The "issue" of whether subject-creds should be visible
		// here is not substantive to the example. The creds are
		// provided, whether they are screened and intercepted and
		// some dummy blob put in their place is not of concern here.
		// We are going to do a dummy authenticate. (In general, the
		// authenticate could be done in the policy with a callout
		// to get the authenticated attribute and passing in the
		// subject-id and creds.
		boolean subjectAuthenticated = false;
		if ( ! (subjectCreds==null) ) {
			subjectAuthenticated = true; // ultra secure authn scheme :)
		}
		
		// The "scope" being asked for, effectively consists of
		// the userId, resourceId, and actionId. So if any of
		// these are not provided, then set scope-provided to false:
		// Note: assuming here that the resOwn can respond with a
		// different "approved scope", it is not required but may
		// be the basis of negotiation. Ultimately, the resOwner
		// should probably be able to approve any desired scope
		// within range of own privileges, which will be checked
		// in the upcoming az call as well; i.e. resOwner by the
		// Policy will need to have coverage for resource-action
		scopeProvided = true;
		if ( ( userId == null ) ||
			 ( resourceId == null ) || 
			 ( actionId == null ) ) {
			scopeProvided = false;
		}
		
		// This continues step 3: build and issue PepReq for approval
		// This request basically determines if the resOwner is authorized
		// to have the authorization server grant an authorization code
		// to the client, as well as that resOwner is authenticated,
		// and authorized to perform actions on resources that are
		// being granted
		try{ 
			
			// create parameter map for subject
			HashMap<String,Object> subjectParams = new HashMap<String,Object>();
			subjectParams.put(AzXacmlStrings.X_ATTR_SUBJECT_ID,subjectId);
			subjectParams.put("subject-creds",subjectCreds);
			subjectParams.put("subject-authenticated", 
					new Boolean(subjectAuthenticated));
			// optional params for now, but no harm providing
			subjectParams.put("user-id", userId);
			subjectParams.put(
					"scope-provided", new Boolean(scopeProvided));
	
			// create parameter map for resource
			HashMap<String,Object> resourceParams = new HashMap<String,Object>();
			// use azEndptURI from above
			resourceParams.put("az-endpt-uri", azEndptURI);
			// resourceId is from approval above
			resourceParams.put(AzXacmlStrings.X_ATTR_RESOURCE_ID, resourceId);
			resourceParams.put("cl-redir-uri", clRedirURI);

			// create parameter map for action
			HashMap<String,Object> actionParams = new HashMap<String,Object>();
			actionParams.put(AzXacmlStrings.X_ATTR_ACTION_ID, actionId);
			actionParams.put("client-id",clientId);
			actionParams.put("oa-act-id", oaAzApprove);

			// Create a Date for a simple environment attribute
			Date now = new Date();

			// actionId is taken directly from the oaAccReqParams above
			PepRequest pepReq = 
				pepReqFactory.newPepRequest(
						subjectParams, actionParams, resourceParams, now); 
			PepResponse pepRsp = pepReq.decide();
		} catch (PepException pep) {
			System.out.println("PepException: " + pep);
		}
		
		log.info(
				"Msg #10 (RO <-AS): OaAzServer.getAuthorizationFromRO: " + 
				"\n    AS (az-server) returning message #10: AS->RO " + 
				"\n\t(1st leg of redirect going back to (AS->RO->CL): " +
				clRedirURI + ")");
		return result;
	}
	
	public String getAccessTokenUsingAzCodeCliCreds(
							Map<String,Object> oaAccReqParams) {
									//String azCode, String cliCreds) {
		String oaAccessToken = null;
		// get parameters out of Map
		String cliCreds = (String)oaAccReqParams.get("client-pwd");
		String azCode = (String)oaAccReqParams.get("az-code");
		String clientId = (String)oaAccReqParams.get("client-id");
		boolean clientAuthenticated = false;
		// declare client to be authenticated
		clientAuthenticated = true;
		
		boolean azCodeProvided = false;
		if ( ! ( azCode==null ) ) azCodeProvided = true;
		boolean azCodeValidated = false;
		// declare the azCode to be valid (this is a simulation):
		azCodeValidated = true;
		
		// the resource we want is an accessToken
		String requestedResource = "oauth-access-token"; 
		// the action we want performed is to return the access token
		String requestedAction = "return-access-token";
		
		// Now prepare the PepRequest
		try{ 
			
			// create parameter map for subject
			HashMap<String,Object> subjectParams = new HashMap<String,Object>();
			subjectParams.put(AzXacmlStrings.X_ATTR_SUBJECT_ID,clientId);
			subjectParams.put("subject-creds",cliCreds);
			subjectParams.put(
					"subject-authenticated", new Boolean(clientAuthenticated));
			
			// optional params for now, but no harm providing
			subjectParams.put(
					"az-code-provided", new Boolean(azCodeProvided));
			subjectParams.put(
					"az-code-validated", new Boolean(azCodeValidated));
	
			// create parameter map for resource
			HashMap<String,Object> resourceParams = new HashMap<String,Object>();
			// use azEndptURI from above
			resourceParams.put("tk-endpt-uri", getTokenEndpointURI());
			// resourceId is from approval above
			resourceParams.put(
				AzXacmlStrings.X_ATTR_RESOURCE_ID, "oauth-access-token");

			// create parameter map for action
			String actionId = "return-access-token";
			// Create a Date for a simple environment attribute
			Date now = new Date();

			// actionId is taken directly from the oaAccReqParams above
			PepRequest pepReq = 
				pepReqFactory.newPepRequest(
						subjectParams, actionId, resourceParams, now); 
			PepResponse pepRsp = pepReq.decide();
		} catch (PepException pep) {
			System.out.println("PepException: " + pep);
		}
		
		
		// set up dummy access token
		if ( ! (cliCreds == null) ) {
			oaAccessToken = azCode + cliCreds;
		}
		log.info(
				"Msg #12 (CL-> AS): OaAzServer.getAccessTokenUsingAzCodeCliCreds: " + 
					"\n    AS (az-server) processing message #12 " + 
					"(azCode, cliCreds): CL->AS " + 
					"\n\t(valid client request to resource server): " +
					"\n\tazCode = " + azCode +
					"\n\tcliCreds = " + cliCreds);
		
		
		log.info(
				"Msg #13 (CL <-AS): OaAzServer.getAccessTokenUsingAzCodeCliCreds: " + 
					"\n    AS (az-server) returning message #13: AS->CL " + 
					"\n\t(valid client azCode, cli creds to az server): " +
					"\n\toaAccessToken = " + oaAccessToken);
		oaRequest.setAccessToken(oaAccessToken);
		return oaAccessToken;
	}

}
