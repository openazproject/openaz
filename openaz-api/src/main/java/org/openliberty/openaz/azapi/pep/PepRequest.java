package org.openliberty.openaz.azapi.pep;

import java.util.List;

import org.openliberty.openaz.azapi.AzResourceActionAssociation;
import org.openliberty.openaz.azapi.AzService;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.constants.PepRequestOperation;
import org.openliberty.openaz.azapi.constants.PepRequestQueryType;

/**
 * A wrapper around the {@link AzRequestContext} that also 
 * provides the decide method that sends the request to the
 * pdp and returns a PepResponse.
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public interface PepRequest {
    /**
     * Calls the {@link AzService} for a decision and returns the information to the caller
     * in the form of a {@link PepResponse}
     * @return a {@link PepResponse} object
     * @throws PepException if there was an error in processing the request.  There
     * is some ability by the <code>PepResponseFactory</code> to determine under 
     * what circumstances (i.e. XACML results and status codes)
     * to throw an exception.
     * @see PepResponse
     * @see PepResponseFactory
     */
    public PepResponse decide() throws PepException;
    
    /**
     * This method is used to allow access to the underlying AzRequestContext
     * that was created by the PepRequestFactory.  It contains all of the state,
     * so any changes made against it are in no way reflected in any of the objects
     * in the pep package
     * @return the handle to the AzRequestContext
     */
    public AzRequestContext getAzRequestContext();
    
    /**
     * Return the {@link PepRequestOperation} for the current PepRequest,
     * which is the type of request: a single decide, a bulk request,
     * or a query.
     */
    public PepRequestOperation getOperation(); 
    
    
    /**
     * Returns the "scope" that was set if this is a query
     * type request {@link PepRequestOperation}. i.e. if this
     * request was created using 
     * {@link PepRequestFactory#newQueryPepRequest(Object, Object, String, 
     * org.openliberty.openaz.azapi.constants.PepRequestQueryType)},
     * where the String argument contains the scope.
     * @return a string if this is a query type PepRequest, 
     * otherwise, null.
     */
    public String getScope();
    
    /**
     * Returns a boolean indicating whether the results indicate
     * what is allowed (true) or not allowed (false). The boolean
     * is determined by the value of the 
     * {@link PepRequestQueryType} that was set in the 
     * {@link PepRequestFactory#newQueryPepRequest(Object, Object, String, org.openliberty.openaz.azapi.constants.PepRequestQueryType)}
     * method to specify the type of results intended to be
     * returned.
     */
    public boolean isQueryForAllowedResults();

    /**
     * Returns the {@link PepRequestFactory} object that was
     * used to create this PepRequest. It is used primarily
     * to get access to the methods of PepRequestFactory.
     */
    public PepRequestFactory getPepRequestFactory();
    
    /**
     * Helper method to return the action object from the 
     * initial request based on the AzResourceActionAssociation 
     * that was created for that action object. The action 
     * object returned will be one of 
     * those that were submitted in the newPepRequest or 
     * newBulkPepRequest or newQueryPepRequest methods.
     * @return an object from the initial list of action objects
     */
    public Object getActionObject(AzResourceActionAssociation azRaa);
    
    /**
     * Helper method to return the resource object from the 
     * initial request based on the AzResourceActionAssociation 
     * that was created for that resource object. The resource
     * object returned will be one of 
     * those that were submitted in the newPepRequest or 
     * newBulkPepRequest or newQueryPepRequest methods.
     * @return an object from the initial list of resource objects
     */
    public Object getResourceObject(AzResourceActionAssociation azRaa);

    /**
     * Set the resource and action for this PepRequest.
     * @param resource any resource object supported by a JavaObjectMapper
     * @param action any action object supported by a JavaObjectMapper
     * @throws PepException
     */
    public void setResourceAction(Object resource, Object action)
    	throws PepException;
    
    /**
     * Set multiple resource and action pairs for this request
     * using a list of actions and a list of resources, where 
     * both lists must be of equal length.
     * 
     * @param resourceObjects a List of length n of resource objects
     * @param actionObjects a List of length n of action objects
     * @throws PepException
     */
    public void setBulkResourceActions(
			List resourceObjects, 
			List actionObjects) 
		throws PepException;

    /**
     * Sets the Subject based on the mapping of the
     * native Java object passed containing Subject information.
     * @param subjectObject
     * @throws PepException
     */
    public void setAccessSubject(Object subjectObject) 
		throws PepException;
    
    /**
     * Sets the Environment based on the mapping of the
     * native Java object passed containing the
     * Environment information.
     * @param envObject
     * @throws PepException
     */
    public void setEnvironment(Object envObject) 
		throws PepException;

    /**
     * Sets the scope for this request.
     * @see PepRequestFactory#newQueryPepRequest(Object, Object, String, PepRequestQueryType)
     * for details on scope.
     * @param scope
     */
    public void setScope(String scope);
    
    /**
     * Sets the boolean to true if only a Set of Allowed decisions
     * are to be returned, and false if only a Set of Denied decisions
     * is to be returned.
     * @see PepRequestFactory#newQueryPepRequest
     * @param queryReturnAllowed
     */
    public void setQueryReturnAllowed(boolean queryReturnAllowed);


    /**
     * Returns the name of the container in which the 
     * pep is running, which MAY be used as the issuer
     * of attributes at the azapi level by the 
     * JavaOhjectMappers. 
     */
    //public String getContainerName();

}
