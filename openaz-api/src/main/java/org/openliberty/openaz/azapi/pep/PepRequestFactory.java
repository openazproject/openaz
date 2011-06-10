package org.openliberty.openaz.azapi.pep;

import java.util.List;

import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;
import org.openliberty.openaz.azapi.constants.PepRequestQueryType;
import org.openliberty.openaz.azapi.pep.PepResponseFactory;
import org.openliberty.openaz.azapi.AzResourceActionAssociation;
import org.openliberty.openaz.azapi.AzService;

/**
 * The main entry point for building a PEP
 * <p>
 * The basic pattern is to as follows:
 * <ul>
 * <li>Instantiate the PepRequestFactory<br>
 * <code>
 * <pre>
 *       AzService azService = new org.openliberty.openaz.pdp.provider.SimpleConcreteService();
 *       PepRequestFactory pep = new PepRequestFactory(CONTAINER,azService);
 * </pre>
 * </code>
 * <li>Instantiate the PepRequest by calling one of the newPepRequest methods
 * <code>
 * <pre>
 * {@link #newPepRequest(String, String, String)}
 * {@link #newPepRequest(Object, Object, Object)}
 * {@link #newPepRequest(Object, Object, Object, Object)}
 * {@link #newBulkPepRequest(Object, List, Object)}
 * {@link #newBulkPepRequest(Object, List, List, Object)}
 * {@link #newQueryPepRequest(Object, Object, String, PepRequestQueryType)}
 * </pre>
 * </code>
 * <li>For example, create a bulk request with list of Permissions
 * <code>
 * <pre>
 * // Define an environment attribute
 * Date now = new Date();
 * 
 * // Define a collection of Subject attributes
 * HashMap&lt;String, String&gt; subject = new HashMap&lt;String, String&gt;();
 * subject.put(AzXacmlStrings.X_ATTR_SUBJECT_ID, "josh");
 * subject.put(AzXacmlStrings.X_ATTR_SUBJECT_AUTHN_LOC_AUTHENTICATION_METHOD, SAMPLE_SESSION_AUTH_METHOD);
 *
 * // Define an array of action strings
 * String[] actions =  new String[] { "read", "write", "delete", "read", "write", "delete" };
 * 
 * // Define a collection: of Permissions using
 * //  the resourceNames and actions for each Permission
 * String resourceName = "file:\\\\toplevel";
 * ArrayList resourceActions = new ArrayList();
 * for (int i = 0; i < actions.length; i++) {
 * 	String resource = resourceName + "0" + new Integer(i);
 * 	resourceActions.add(new FilePermission(resource, actions[i]));
 * }
 * 
 * // Create the request object
 * try {
 * 	PepRequest req = pep.newBulkPepRequest(subject,resourceActions,now);
 * </pre>
 * </code>
 * <li>Call the pepRequest.decide() and process the response
 * <code>
 * <pre>
 *	PepResponse resp = req.decide();
 *	System.out.println(resp.allowed());
 *	if (resp.allowed()) {
 *		Map&lt;String,Obligation&gt; obligations = resp.getObligations();
 *		Iterator&lt;String&gt; itObligationIds = obligations.keySet().iterator();
 *		while (itObligationIds.hasNext()){
 *			String obligationId = itObligationIds.next();
 *			Obligation obligation = obligations.get(obligationId);
 *			Map&lt;String,String&gt; oblAttrs = obligation.getStringValues();
 *			System.out.println("ObligationId: " + obligationId +
 *				"\nObligation Attributes: " + oblAttrs);
 *		}
 *	}
 * </pre>
 * </code>
 * </ul>
 * </p>
 * Note: in the method descriptions below, those that are blank generally
 * indicate that the method is primarily used for lower level non-application facing operations.
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public interface PepRequestFactory {
	
    /**
     * Create a PepRequest using objects, where a list of n action objects and
     *  a corresponding list of n resource objects are provided to represent
     *  n resource-action pairs. A decision for each resource-action pair will
     *   be returned, when PepRequest.decide() is invoked.
     * @param subjectObj Object representing the Subject
     *  ex: javax.auth.security.Subject
     * @param actionObjects a list of length n, of actionObj Objects representing the Actions
     *  ex: String (read)
     * @param resourceObjects a list of length n, of resourceObj Objects representing the Resources
     *  ex: String (file) or File
     * @param environmentObj Object representing the Environment
     *  ex: Map containing attributes name and values
     * @return a {@link PepRequest} object
     * @throws PepException
     */
    public PepRequest newBulkPepRequest(
	    		Object subjectObj, 
	    		List actionObjects, 
	    		List resourceObjects, 
	    		Object environmentObj) 
	    	throws PepException ;
    /**
     * Create a PepRequest using objects, where a list of n action-resource objects 
     * are provided. A decision for each resource-action pair will be returned, 
     * when PepRequest.decide() is invoked.
     * @param subjectObj Object representing the Subject
     *  ex: javax.auth.security.Subject
     * @param actionResourceObjects a list of length n, of actionResourceObjects representing the
     *  Action Resource pairs ex: String (read), String (file)
     * @param environmentObj Object representing the Environment
     *  ex: Map containing attributes name and values
     * @return a {@link PepRequest} object
     * @throws PepException
     */
    public PepRequest newBulkPepRequest(
    						Object subjectObj, 
    						List actionResourceObjects, 
    						Object environmentObj) 
    		throws PepException ;
    /**
     * Creates a PEP request using objects, but the action and the resource are
     *  both derived from the same object ex:  Permission or HttpServletRequest
     * @param subjectObj Object representing the Subject
     *  ex: javax.auth.security.Subject
     * @param actionResourceObject Object representing both the action and the
     *  resource ex: FilePermission or HttpServletRequest
     * @param environmentObj Object representing the Environment 
     *  ex: Map containing attributes name and values
     * @return a {@link PepRequest} object
     * @throws PepException if there is no <code>JavObjectMapper</code> configured
     *  for the objects passed into the factory.
     * @see JavaObjectMapper
     */
    public PepRequest newPepRequest(
    						Object subjectObj, 
    						Object actionResourceObject, 
    						Object environmentObj) 
    		throws PepException;
    /**
     *Create a PepRequest using objects 
     * @param subjectObj Object representing the Subject 
     * 	ex: javax.auth.security.Subject
     * @param actionObj Object representing the Action
     *  ex: String (read)
     * @param resourceObj Object representing the Resource
     *  ex: String (file) or File
     * @param environmentObj Object representing the Environment
     *  ex: Map containing attributes name and values
     * @return a {@link PepRequest} object
     * @throws PepException if there is no <code>JavObjectMapper</code> configured
     *  for the objects passed into the factory.
     * @see JavaObjectMapper
     */
    public PepRequest newPepRequest(
    						Object subjectObj, 
    						Object actionObj, 
    						Object resourceObj, 
    						Object environmentObj)  
    				throws PepException;
   
    /**
     * Create a PepRequest just using Strings
     * @param subjectName String representing the name of the subject 
     * ex: Josh
     * @param resourceId String representing the resource 
     * ex: file1234
     * @param actionId String representing the action ex: read
     * @return a {@link PepRequest} object
     * @throws PepException Indicates that there is some issue creating the <code>PEPRequest</code>
     */
    public PepRequest newPepRequest(
    					String subjectName, 
    					String actionId, 
    					String resourceId) 
    			throws PepException;
    /**
     * Create a PepRequest using subject and environment objects, plus a "scope"
     * String that represents a PDP policy-specific resource representation.
     * 
     * When the PepRequest.decide() method is invoked, based on 
     * {@link PepRequestQueryType} it
     * will return either
     * <pre>
     * 		- a list of Allowed ResourceAction pairs within scope
     * 		- a list of Denied ResourceAction pairs within scope
     * 		- or a list of full detailed results for all 
     * 			ResourceAction pairs within scope
     * </pre>
     * 
     * @param subjectObj
     * @param environmentObj
     * @param scope a string containing a PDP policy-specific resource
     *  representation
     * @param queryType an enum containing a choice of allowed, denied,
     *  or full/verbose
     * @return a {@link PepRequest} object
     * @throws PepException
     */
    public PepRequest newQueryPepRequest(
    						Object subjectObj, 
    						Object environmentObj, 
    						String scope, 
    						PepRequestQueryType queryType) 
    		throws PepException;
    
    /**
     * Return the List of PreDecisionHandlers that was set when
     * the PepRequestFactory was created.
     */
    public List<PreDecisionHandler> getPreDecisionHandlers();
    
    /**
     * Return the DecisionHandler that was set when the PepRequestFactory
     * was created.
     */
    public DecisionHandler getDecisionHandler();
    
    /**
     * Return the List of PostDecisionHandlers that was set when
     * the PepRequestFactory was created.
     */
    public List<PostDecisionHandler> getPostDecisionHandlers();
    
    /**
     * Return a factory for a specific 
     * <code>AzEntity&lt;T&gt;</code> type. Pass
     * an AzCategoryId argument and get a 
     * {@link RequestAttributesFactory}
     * for that particular category. For example:
     * <br>
     * 		<code>getRequestAttributesFactory}({@link AzCategoryIdAction#AZ_CATEGORY_ID_ACTION})</code>
     * <br>
     * returns a factory for AzEntity&lt;AzCategoryIdAction&gt; objects.
     * <p>
     * This is a helper method used to get a reference to the appropriate
     * AzCategoryId factory to set the mappers for that type of AzEntity.
     */
    public <T extends Enum<T> & AzCategoryId> 
    	RequestAttributesFactory<T> 
    		getRequestAttributesFactory(T t);

    /**
     * Helper method to return the action object from the 
     * initial request based on the AzResourceActionAssociation 
     * that was created for that action object. The action 
     * object returned will be one of 
     * those that were submitted in the newPepRequest or 
     * newBulkPepRequest or newQueryPepRequest methods.
     * @return an object from the initial list of action objects
     */
    //public Object getActionObject(AzResourceActionAssociation azRaa);
    
    /**
     * Helper method to return the resource object from the 
     * initial request based on the AzResourceActionAssociation 
     * that was created for that resource object. The resource
     * object returned will be one of 
     * those that were submitted in the newPepRequest or 
     * newBulkPepRequest or newQueryPepRequest methods.
     * @return an object from the initial list of resource objects
     */
    //public Object getResourceObject(AzResourceActionAssociation azRaa);

    /**
     * Helper method to return the PepResponseFactory that was
     * configured with this PepRequestFactory. May be used by
     * applications to set the PepResponseBehavior that controls
     * how the {@link PepResponse#allowed()} method behaves
     * when it returns its result.
     * @return a PepResponseFactory object
     */
    public PepResponseFactory getResponseFactory(); 
    
    /**
     * Return the instance of the AzService configured for
     * this PepRequestFactory.
     * 
     */
    public AzService getAzService();
    
    /**
     * Return the name of the class that implements this
     * instance of PepRequestFactory and overall set of
     * openliberty.openaz.azapi.pep interfaces
     */
    public String getProviderClassName();

    /**
     * Return the name of the container configured for this
     * PepRequestFactory.
     */
    public String getContainerName();
    
}
