package test.objects;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OaClient {
	OAuthRequest oaRequest = null;
	Log log = LogFactory.getLog(this.getClass()); 
	
	public void setOaRequest(OAuthRequest oaRequest) {
		this.oaRequest = oaRequest;
	}
	public OAuthRequest getOaRequest() {
		return oaRequest;
	}
	public void setClientId(String clientId) {
		oaRequest.setClientId(clientId);
	}
	public String getClientId() {
		return oaRequest.getClientId();
	}
	public void setClientPwd(String clientPwd) {
		oaRequest.setClientPwd(clientPwd);
	}
	public String getClientPwd(){
		return oaRequest.getClientPwd();
	}
	public void setClientRedirectURI(String clientRedirectURI) {
		oaRequest.setClientRedirectURI(clientRedirectURI);
	}
	public String getClientRedirectURI() {
		return oaRequest.getClientRedirectURI();
	}
	
	/**
	 * This module:
	 * <ul>
	 * <li>Processes message 1
	 * <li>Sends message 2
	 * <li>Receives message 3
	 * <li>Returns message 18
	 * </ul>

	 * @param clientId
	 * @param resourceId
	 * @param actionId
	 * @param userId
	 * @return
	 */
	public String processRequest(String clientId,
								 String resourceId,
								 String actionId,
								 String userId) {
		log.info("Msg  #1 (RO-> CL): OaClient.processRequest: " + 
				"\n    CL (client) processing message #1: RO->CL " + 
				"\n\t(initial resource owner request to client): " +
				"\n\tresourceOwner = " + userId +
				"\n\tclientId = " + clientId +
				"\n\tresourceId = " + resourceId +
				"\n\tactionId = " + actionId);
		/*System.out.println("Msg  #1 (RO-> CL): OaClient.processRequest: " + 
				"\n    CL (client) processing message #1: RO->CL " + 
				"\n\t(initial resource owner request to client): " +
				"\n\tresourceOwner = " + userId +
				"\n\tclientId = " + clientId +
				"\n\tresourceId = " + resourceId +
				"\n\tactionId = " + actionId);*/
		
		if ( ! (clientId.equals(oaRequest.getClientId()))) {
			System.out.println(
					"ERROR: clientId in request: " + clientId + 
					"\n\tdoes not match the client's client-id: " +
					oaRequest.getClientId());
		}
		String resSvrResp = "dummyClientResult";
		// Note: resource server addr is part of the resourceId
		// Note: can check if clientId param matches this.getClientId,
		//  it does, but in general need to have the target of the
		//  request handy for real world http calls to client
		OaResourceServer oaResSvr = oaRequest.getResourceServer();
		
		// snd msg 2, rcv msg 5
		// This endpoint will return the az-endpt-uri address
		// if there is no access token supplied. Therefore
		// resSvrResp can be either the az-endpt-uri OR the
		// requested-resource. Since this is for demo purposes,
		// at this time a simple string test is done to determine
		// if the resp is the uri or the resource.
		resSvrResp = oaResSvr.processRequestNoToken(
				clientId,
				resourceId, actionId, userId,
				oaRequest.getAccessToken());
		System.out.println(
				"    OaClient: do oaResSvr.processRequest() to get azURI:" +
				"\n\tresult = " + resSvrResp);
		
		// now need to determine if result is to redirect or if
		// we got the resource; We can "cheat" by comparing the
		// result to the know azURI and if equal then do the
		// redirect
		// if result contains redirect then go get the azToken,
		// and then reissue the request w the token, and at end
		// of this "if" we should have the result
		if ( resSvrResp.equals(oaRequest.getAzEndpointURI()) ) {
			String azEndpointURI = resSvrResp; // make it "official" :)
			// do the redirect call
			// Note: OAuth says send the "scope" but all client
			// knows is the request from the resource owner
			// Also, OAuth says "azSvr defines the scope", so let
			// azSvr figure it out and tell the resourceOwner what
			// the scope needs to be !
			OaResourceOwner oaResOwner = oaRequest.getResourceOwner();
			clientId = oaRequest.getClientId();
			String clientRedirectURI = oaRequest.getClientRedirectURI();
			
			// send redirecting msg 6, rcv redirected msg 11
			String azCode = null;
			System.out.println(
					"\n\tOaClient: do oaResOwner.redirect() to get azCode:" +
					"\n\t\tazCode = " + azCode);
			// need to callback thru the caller to do the redirect
			// just using the object is good enough because in http
			// we would just be sending a response to the caller
			// w/o getting a specific URI
			// the info we are passing includes:
			//   azEndpointURI: the URI for the ro to redir to azSver
			//   clientRedirectURI: URI of this client for azSvr to snd back to
			//   clientId: oauth reqd clientId to tell azSvr who client is
			//   "scope" includes:
			//     resourceId: the resource the client is trying to get
			//     actionId: the action the client is trying to perform
			//     userId: res-owner who asked the client to do this
			azCode = oaResOwner.redirectRoToAuthorizer(
								azEndpointURI, clientRedirectURI, 
								clientId, resourceId, actionId, userId);
			
			// snd msg 12, rcv msg 13 (the access token)
			// use clientId as dummy client creds
			OaAzServer azSvr = oaRequest.getAzServer();
			Map<String,Object> oaAccReqParams = new HashMap<String,Object>();
			oaAccReqParams.put("client-id", getClientId());
			oaAccReqParams.put("client-pwd", getClientPwd());
			oaAccReqParams.put("az-code", azCode);
			String oaAccessToken =
				azSvr.getAccessTokenUsingAzCodeCliCreds(oaAccReqParams);
			log.info("oaAccessToken = " + oaAccessToken);

			// snd msg 14, rcv msg 17 (the resource)
			System.out.println(
					"    OaClient: do oaResSvr.processRequest() to get azURI:" +
					"\n\tresult = " + resSvrResp);
			resSvrResp = oaResSvr.processRequestNoToken(
					clientId,
					resourceId, actionId, userId,
					oaRequest.getAccessToken());
		}
		
		log.info("Msg #18 (RO <-CL): OaClient.processRequest: " + 
				"\n    CL (client) returning message #18: CL->RO " + 
				"\n\t(result describing how requset handling went): " +
				"\n\tclientId = " + clientId +
				"\n\tresourceId = " + resourceId);
		/*System.out.println("Msg #18 (RO <-CL): OaClient.processRequest: " + 
				"\n    CL (client) returning message #18: CL->RO " + 
				"\n\t(result describing how requset handling went): " +
				"\n\tclientId = " + clientId +
				"\n\tresourceId = " + resourceId); */
		return resSvrResp;
	}
}
