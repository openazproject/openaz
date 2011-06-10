package test.objects;

import java.util.HashMap;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class OaResourceServer {
	OAuthRequest oaRequest = null;
	Log log = LogFactory.getLog(this.getClass()); 
	public void setOaRequest(OAuthRequest oaRequest) {
		this.oaRequest = oaRequest;
	}
	public OAuthRequest getOaRequest() {
		return oaRequest;
	}
	public void setResourceServerURI(String oaResSvrURI){
		if ( ! (oaRequest==null) ) {
			oaRequest.setResourceServerURI(oaResSvrURI);
		} else { // else log error
			System.out.println(
					"Error: setResourceServerURI: oaRequest not set");
		}
	}
	public String getResourceServerURI() {
		if ( ! ( oaRequest==null ) ) {
			return oaRequest.getResourceServerURI();
		} else { // else log error
			System.out.println(
					"Error: getResourceServerURI: oaRequest not set");
		}
		return null;
	}
	public String processRequestNoToken(
			 String clientId,
			 String resourceId,
			 String actionId,
			 String userId,
			 String oaAccessToken) {
		
		// Set up request parameters to pass to authorization service
		HashMap<String,String> oaReqParams = new HashMap<String,String>();
		
		// this request is going to az-server validation uri:
		oaReqParams.put("vl-endpt-uri", oaRequest.getValidationEndpointURI());
		
		// Determine if we have an access token to send or not
		if ( (oaAccessToken == null) ) {
			log.info("Msg  #2 (CL-> RS): OaResourceServer.processRequestNoToken: " + 
				"\n    RS (res-server) processing message #2: CL->RS " + 
				"\n\t(initial client request to resource server): " +
				"\n\tresourceOwner = " + userId +
				"\n\tresourceId = " + resourceId +
				"\n\tactionId = " + actionId +
				"\n\toaAccessToken = " + oaAccessToken);
			oaReqParams.put("TokenFlag", "F");
		}
		else {
			log.info("Msg #14 (CL-> RS): OaResourceServer.processRequestNoToken: " + 
					"\n    RS (res-server) processing message #14: CL->RS " + 
					"\n\t(initial client request to resource server): " +
					"\n\tresourceOwner = " + userId +
					"\n\tresourceId = " + resourceId +
					"\n\tactionId = " + actionId +
					"\n\toaAccessToken = " + oaAccessToken);			
			oaReqParams.put("TokenFlag", "T");
		}
		
		oaReqParams.put("client-id", oaRequest.getClientId());
		oaReqParams.put("user-id", userId);
		oaReqParams.put("action-id", actionId);
		oaReqParams.put("resource-id", resourceId);
		
		//String resSvrId = "http://res-svr/";
		String resSvrId = oaRequest.getResourceServerURI();
		oaReqParams.put("res-svr", resSvrId);
		String result = "dummyClientResult";
		// Note: resource server addr is part of the resourceId
		OaAzServer oaAzSvr = oaRequest.getAzServer();
		result = oaAzSvr.validateToken(oaReqParams);
		System.out.println(
				"    OaResourceServer: token validation result = " + result);
		if (  (result.equals("ok")) ) {
			log.info("Msg #17 (CL <-RS): OaResourceServer.processRequestNoToken: " + 
					"\n    RS (res-server) returning message #17: RS->CL " + 
					"\n\t(resource server): " +
					"\n\tresourceOwner = " + userId +
					"\n\tresourceId = " + resourceId +
					"\n\tactionId = " + actionId +
					"\n\toaAccessToken = " + oaAccessToken);
			// need to come up with a resource to return
			return "ok, this is: " + resourceId + "  " + actionId;
		}
		else {
			log.info("Msg  #5 (CL <-RS): OaResourceServer.processRequestNoToken: " + 
					"\n    RS (res-server) returning message #5: RS->CL " + 
					"\n\t(resource server): " +
					"\n\tresourceOwner = " + userId +
					"\n\tresourceId = " + resourceId +
					"\n\tactionId = " + actionId +
					"\n\toaAccessToken = " + oaAccessToken);
		}
		// if not "ok" then should be the authorization endpoint URI
		return result;
	}
}