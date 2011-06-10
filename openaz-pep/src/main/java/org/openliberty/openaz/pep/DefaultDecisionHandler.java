package org.openliberty.openaz.pep;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.constants.PepRequestOperation;

import org.openliberty.openaz.azapi.pep.PepResponse;
import org.openliberty.openaz.azapi.pep.DecisionHandler;
import org.openliberty.openaz.azapi.pep.PreDecisionHandler;
import org.openliberty.openaz.azapi.pep.PostDecisionHandler;

import org.openliberty.openaz.azapi.AzResourceActionAssociation;
import org.openliberty.openaz.azapi.AzResponseContext;
import org.openliberty.openaz.azapi.AzService;

//import org.openliberty.openaz.pdp.AzServiceFactory;
import org.openliberty.openaz.pdp.provider.AzServiceFactory;
/**
 * Provides a default implementation of the decide() method 
 * and has null implementations of preDecide() and postDecide().
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public class DefaultDecisionHandler 
	implements DecisionHandler, 
				PreDecisionHandler,
                PostDecisionHandler {

    private Log log = LogFactory.getLog(this.getClass());
    
    /** 
    * Implements the decide() method by
    * using the underlying AzService.decide(requestContext) method to
    * execute the request, and handles the AzResponseContext that is 
    * returned by wrapping it in a PepResponse. 
    * <p>
    * @param request a pepRequest object populated with the attributes
    * to be included with the request.
    * @return a pepResponse object containing one or more XACML Results
    */
    public PepResponse decide(PepRequest request) {
       
    	// Get handle to AzService
        AzService azHandle = AzServiceFactory.getAzService();        
        AzResponseContext azRspCtx = null;
        if (log.isTraceEnabled()) log.trace(
        	"\n   Calling decide w PepRequest.getOperation() = " + 
    		request.getOperation() +
    		"\n\tusing AzService =   " + azHandle.getClass().getName() + "\n");
           
        // If this is decide or bulk-decide call then do the 
        // ordinary call to underlying azapi AzService.decide():
        // Note: for bulk requests, the multiple resource action
        // associations were already built into the AzRequestContext
        // so there is only one AzRequestContext and one decide() call.
        // However, multiple results are returned within the 
        // AzResponseContext.
        if ((request.getOperation()==PepRequestOperation.DECIDE) ||
        	(request.getOperation()==PepRequestOperation.BULK_DECIDE)) {
            azRspCtx = azHandle.decide(request.getAzRequestContext());
            
        // If query verbose, this is ordinary use 
        // AzService.queryVerbose() call which returns ordinary
        // responses
        } else if (request.getOperation()==PepRequestOperation.QUERY_VERBOSE) {
            azRspCtx = azHandle.queryVerbose(
		            			request.getScope(),
		            			request.getAzRequestContext());
        
        // If "simple" query then use AzService.query() which
        // returns Set of AzResourceActionAssociation's
        } else {
            Set<AzResourceActionAssociation> actionResourceAssociations =
                azHandle.query(
                                request.getScope(),
                                request.getAzRequestContext(),
                                request.isQueryForAllowedResults());
            
            return request.getPepRequestFactory().getResponseFactory().
            	createPepResponse(actionResourceAssociations,
            					  request,
            					  request.isQueryForAllowedResults());            
        }
        
        //Return the PepResponse for the OPERATION.DECIDE or 
        // OPERATION.QUERY_VERBOSE    
        return request.getPepRequestFactory().getResponseFactory().
        		createPepResponse(azRspCtx,
        						  request,
        						  request.getOperation());
    }

    /**
     * Null implementation.
     */
    public void preDecide(PepRequest request) {
    }

    /**
     * Null implementation.
     */
    public void postDecide(PepRequest request, PepResponse response) {
    }
}
