package org.openliberty.openaz.azapi.pep;

import java.util.Map;

import org.openliberty.openaz.azapi.AzResponseContext;
import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.constants.PepRequestQueryType;
import org.openliberty.openaz.azapi.constants.PepResponseBehavior;

/**
 * PepResponse is the main entry point for processing the results
 * of a {@link PepRequest#decide()} call.
 * <br>
 * It enables processing one result at a time for an {@link AzResponseContext},
 * which may contain multiple decisions from one of the
 * <ul>
 * <li> 
 * {@link PepRequestFactory#newBulkPepRequest(Object, java.util.List, Object)} 
 * calls, 
 * </ul>
 * or a 
 * <ul>
 * <li>
 * {@link PepRequestFactory#newQueryPepRequest(Object, Object, String, PepRequestQueryType)}
 * call.
 * </ul>
 * It also provides a wrapper around the {@link AzResponseContext}
 * which enables access to the underlying low level AzApi functions.
 * <p>
 * Please refer to {@link PepRequestFactory} for code example containing
 * processing of a PepResponse.
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public interface PepResponse {
	 
	/**
	 * Returns the decision associated with the current result.
	 * @return true if the user was granted access to the resource, 
	 * otherwise false
	 * @throws PepException if the {@link PepResponseBehavior} 
	 * configured in the {@link PepResponseFactory}
	 * indicates that for the response should be thrown
	 */
    public boolean allowed() 
    	throws PepException;
    
    /**
     * For bulk requests and query requests, move the 
     * internal iterator to the next result. 
     * <br>
     * For bulk requests and query requests, the iterator initially
     * points prior to the first result.
     * <br> 
     * For single newPepRequest, the iterator always points to the
     * first and only result and next() always returns false.
     * @return true if there is another result for bulk requests
     * and query requests, false otherwise.
     */
    public boolean next() throws PepException;
    
 	/**
     * Return the action object associated with the current
     * result. The action object is the same object that was
     * used to create the PepRequest and may be used to 
     * correlate the PepResponse results with the action-resource
     * pairs that were used to create the PepRequest.
     * @return an object that was used as an action in the PepRequest
     * @throws PepException
     */
    public Object getAction() 
    	throws PepException;   
    
    /**
     * Return the resource object associated with the current
     * result. The resource object is the same object that was
     * used to create the PepRequest and may be used to 
     * correlate the PepResponse results with the action-resource
     * pairs that were used to create the PepRequest.
     * @return an object that was used as a resource in the PepRequest
     * @throws PepException
     */
    public Object getResource() 
    	throws PepException;
    
    /**
     * Return the set of {@link Obligation}s associated with the 
     * current result indexed by ObligationId.
     * @return a Map of ObligationId, Obligation pairs
     * @throws PepException
     * @see Obligation#getObligationId()
     */
    public Map<String,Obligation> getObligations() 
    	throws PepException;
    
    /**
     * The handle to the actual {@link AzResponseContext}, which
     * may be used with the interfaces provided in the
     * {@link org.openliberty.openaz.azapi} package.   
     * @return an {@link AzResponseContext} object
     */
    public AzResponseContext getAzResponseContext() 
    	throws PepException;
 	 	 
}
