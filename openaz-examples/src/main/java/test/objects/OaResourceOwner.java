package test.objects;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OaResourceOwner {
	OAuthRequest oaRequest = null;
	Log log = LogFactory.getLog(this.getClass()); 

	public void setOaRequest(OAuthRequest oaRequest) {
		this.oaRequest = oaRequest;
	}
	public OAuthRequest getOaRequest() {
		return oaRequest;
	}
	public void setOwnerId(String ownerId, String ownerPwd) {
		if ( ! ( oaRequest == null ) ) {
			oaRequest.setOwnerId(ownerId, ownerPwd);
		}
	}
	public void setResource(String resSvrURI,
							String resourceName,
							String actionId) {		
		if ( ! ( oaRequest == null ) ) {
			oaRequest.setResourceId(resSvrURI, resourceName, actionId);
		}
	}
	/**
	 * Send a request to the client with resourceId, actionId,
	 * and userId, where userId is how resourceOwner is known
	 * to the client. 
	 * <p>
	 * Note: userId is not a defined OAuth parameter, but it
	 * is probably a practical use case and is provided for
	 * real world consideration. It can be blank.
	 * <p>
	 * Note: the client knows the address of the resource server
	 * since it is part of the resourceId URI, which is just a
	 * String in this simulation.
	 * <p>
	 * @return a String result indicating success or failure
	 * and other detail if desired.
	 */
	public String issueRequest() {
		String result = "dummyResult";
		// Start request by calling the client
		OaClient oaClient = oaRequest.getClient();
		result = oaClient.processRequest(oaRequest.getClientId(),
								oaRequest.getResourceId(),
								oaRequest.getActionId(),
								oaRequest.getOwnerId());
		return result;
	}
	
	/**
	 * This module redirects out to the authorization server
	 * thru the resource owner:
	 * <ul>
	 * <li>Processes message 6 (CL -> RO)
	 * <li>Sends message 7 (RO -> AS)
	 * <li>Receives message 10 (AS -> RO)
	 * <li>Returns message 11 (RO -> CL)
	 * </ul>
	 *
	 * @param azEndpointURI
	 * @param clientId
	 * @param resourceId
	 * @param actionId
	 * @param userId
	 * @return
	 */
	public String redirectRoToAuthorizer(
							String azEndpointURI,
							String clientRedirectURI,
							String clientId,
							String resourceId,
							String actionId,
							String userId) {
		String result = null;
		log.info("Msg  #6 (CL-> RO): OaResourceOwner.redirectRoToAuthorizer: " + 
				"\n    RO (res-owner) processing message #6: CL->RO " + 
				"\n\t(1st leg of redirect to az server): " +
				"\n\tazEndpointURI = " + azEndpointURI +
				"\n\tclientRedirectURI = " + clientRedirectURI +
				"\n\tclientId = " + clientId +
				"\n\tresourceId = " + resourceId +
				"\n\tuserId = " + userId);
		
		// Set up request parameters to pass to authorization service
		HashMap<String,String> oaReqParams = new HashMap<String,String>();
		
		// basically just pass the params on thru
		// this request is going to az-server authorization uri:
		oaReqParams.put("az-endpt-uri", azEndpointURI);
		oaReqParams.put("cl-redir-uri", clientRedirectURI);
		oaReqParams.put("client-id", clientId);
		oaReqParams.put("user-id", userId);
		oaReqParams.put("action-id", actionId);
		oaReqParams.put("resource-id", resourceId);

		// this is just a "pass-thru" to the azSvr
		// and then back to the client
		// the client calls us, so by calling the AzServer, we are
		// effectively simulating the http redirect to the AzServer
		OaAzServer azSvr = oaRequest.getAzServer();
		result = azSvr.getAuthorizationFromRO(oaReqParams);
		
		// since we were originally called by the client, simply
		// returning to the client simulates the http redirect back to
		// the client.
		log.info("Msg #11 (CL <-RO): OaResourceOwner.redirectRoToAuthorizer: " + 
				"\n    RO (res-owner) returning message #11: RO->CL " + 
				"\n\t(authorization code from az server): ");
		return result;
	}
	/**
	 * This module requests the Resource Owner to authenticate
	 * to the Authorization Server, and at the same time include
	 * an Authorization ok that the Client can execute the indicated
	 * operation on the requested resource, which will effectively
	 * serve as the basis for the Az Server authorizing the client
	 * to access the Resource Owner's resource:
	 * <ul>
	 * <li>Processes message 8
	 * <li>Returns message 9
	 * </ul>
	 * 
	 * @param clientId
	 * @param resourceId
	 * @param actionId
	 * @param userId
	 * @param scope
	 * @return
	 */
	public Map<String,Object> gatherCredsGetAz(
								String clientId, 
								String resourceId,
								String actionId, 
								String userId, 
								String scope) {
		Map<String,Object> result = new HashMap<String,Object>();
		log.info("Msg  #8 (AS-> RO): OaResourceOwner.gatherCredsGetAz: " + 
				"\n    RO (res-owner) processing message #8: AS->RO " + 
				"\n\t(challenge to get Resource Owner AS-auth then RO-az): " +
				"\n\tclientId = " + clientId +
				"\n\tresourceId = " + resourceId +
				"\n\tactionId = " + actionId +
				"\n\tuserId = " + userId +
				"\n\tscope = " + scope);
		
		// the resOwner "knows" what it sent to the client originally,
		// so it can compare what the azSvr sends now for the resOwner
		// to approve.
		if ( userId.equals(oaRequest.getOwnerId()) ) {
			System.out.println("    OaResourceOwner.gatherCredsGetAz: " +
					"userId = resOwnerId = " + userId);
			if ( clientId.equals(oaRequest.getClientId()) ) {
				System.out.println("    OaResourceOwner.gatherCredsGetAz: " +
						"clientId = " + clientId);
				System.out.println("    OaResourceOwner.gatherCredsGetAz: " +
						"userId = ownerId = " + userId);
				
				// return result w ownerId,pwd + ok for clientId to have scope
				// use "subject-id" as the resource-owner
				result.put("subject-id", oaRequest.getOwnerId());
				result.put("subject-creds", oaRequest.getOwnerPwd());
				result.put("client-id", clientId);
				result.put("oa-act-id", "approve-client");
				// the following is the approved scope, which confirms
				// the resource, action, and the user-id that
				// the res-owner is known by at the client and resource server
				result.put("cl-res-id", resourceId);
				result.put("cl-act-id", actionId);
				result.put("cl-user-id", userId);
			}
		}
		log.info("Msg  #9 (AS <-RO): OaResourceOwner.gatherCredsGetAz: " + 
				"\n    RO (res-owner) returning message #9: RO->AS" +
				"\n\tresult = " + result);
		return result;
	}

}
