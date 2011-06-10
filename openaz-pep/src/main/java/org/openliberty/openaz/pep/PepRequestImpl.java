package org.openliberty.openaz.pep;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.ListIterator;
import java.util.Set;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openliberty.openaz.azapi.pep.Attributes;
import org.openliberty.openaz.azapi.pep.JavaObjectMapper;
import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.RequestAttributesFactory;
import org.openliberty.openaz.azapi.pep.RequestAttributes;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;
import org.openliberty.openaz.azapi.constants.PepRequestOperation;
import org.openliberty.openaz.azapi.constants.PepRequestQueryType;
import org.openliberty.openaz.azapi.pep.PreDecisionHandler;
import org.openliberty.openaz.azapi.pep.PostDecisionHandler;
import org.openliberty.openaz.azapi.pep.DecisionHandler;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.AzResourceActionAssociation;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryId;

/**
 * A wrapper around the <code>AzRequestContext</code> that implements
 * the PepRequest interface for PDPs that use AzApi to issue requests
 * and process responses.
 * <p>
 * Basically, the purpose of this class is to translate the objects
 * passed to the PepRequestFactoryImpl to the associated AzAttributes
 * used by AzApi, by using the JavaObjectMappers that are designed
 * to translate specific types of Java Objects to specific sets
 * of AzAttributes.
 * 
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public class PepRequestImpl implements PepRequest {
    
    private AzRequestContext azRequestContext;
    
    private List bulkActionObjects = null;   
    private List bulkResourceObjects = null;
    private List actionList = null;
    private List resourceList = null;

    private String defaultContainerName;
    private PepRequestFactory pepRequestFactory;
    private PepRequestOperation operation;
    private String scope;
    private boolean queryReturnAllowed = false;
    private Log log = LogFactory.getLog(this.getClass());

    /**
     * Internal PepRequestImpl constructor used by PepRequestFactoryImpl
     * to support the factory's newPepRequest methods.
     * 
     * @param ctx the actual AzRequestContext
     * @param pepRequestFactory the pepRequestFactory used to create this request.  This handle 
     * is used to access the <code>EnvironmentFactory</code>,<code>SubjectFactory</code>,<code>ActionFactory</code>,<code>ResourceFactory</code>
     * and <code>PepResponseFactory</code> to create the objects needed to create the request and response.
     * @see EnvironmentFactory
     * @see ActionFactory
     * @see ResourceFactory
     * @see SubjectFactory
     * @see PepResponseFactoryImpl
     */
    protected PepRequestImpl(AzRequestContext ctx, 
    		PepRequestFactory pepRequestFactory, 
    		PepRequestOperation operation) {
        super();
        this.azRequestContext = ctx;
        this.pepRequestFactory = pepRequestFactory;
        this.defaultContainerName = pepRequestFactory.getContainerName();
        this.operation = operation;
    }

    /**
     * Calls the AzService for a decision and returns the information to the caller
     * in the form of a <code>PepResponse</code>
     * @return a PepResponse from the configured {@link DecisionHandler}
     * @throws PepException if there was an error in processing the request.  There
     * is some ability by the <code>PepResponseFactory</code> to determine under 
     * what circumstances (i.e. XACML results and status codes)
     * to throw an exception.
     * @see PepResponseImpl
     * @see PepResponseFactoryImpl
     */
    public PepResponse decide() throws PepException {
    
        for (PreDecisionHandler preHandler: 
        	 this.getPepRequestFactory().getPreDecisionHandlers()) {
            preHandler.preDecide(this);
        }
        
        PepResponse response = 
        	this.getPepRequestFactory().getDecisionHandler().decide(this);
 
        for (PostDecisionHandler postHandler: 
        	 this.getPepRequestFactory().getPostDecisionHandlers()) {
            postHandler.postDecide(this,response);
        }
        return response;
    }

    /**
     * This method is used to allow access to the underlying AzRequestContext
     * that was created by the PepRequestFactory.  It contains all of the state,
     * so any changes made against it are in no way reflected in any of the objects
     * in the pep package
     * @return the handle to the AzRequestContext
     */
    public AzRequestContext getAzRequestContext() {
        return this.azRequestContext;
    }
    
    /**
     * This method is used to translate a PepApi input subject object
     * to the corresponding individual AzApi AzAttributes for a Xacml
     * Subject request.
     */
    public void setAccessSubject(Object subjectObject) 
    	throws PepException {
    	createAccessSubject(subjectObject);
    }
    /**
     * Creates a Subject 
     * 
     * @param subjectObj object representing the Subject 
     *   ex: String or JAASSubject
     * @return Wrapper object around an 
     *   <code>AzEntity&lt;AzCategoryAccessSubjectId&gt;</code>
     * @throws PepException if there is no 
     *   <code>JavaObjectMapper</code> configured for the object
     * @see JavaObjectMapperImpl
     * @see Subject
     */
    protected Subject createAccessSubject(Object subjectObj) 
    	throws PepException {
        if (log.isTraceEnabled()) log.trace(
        	"\n\tPepRequest.createAccessSubject() w object: " +
        		subjectObj.getClass().getSimpleName());
        SubjectFactory subjectFactory = 
        	(SubjectFactory)getPepRequestFactory().
        		getRequestAttributesFactory(
        			AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS);
        Subject subject = subjectFactory.createObject(this);
        if (log.isTraceEnabled()) log.trace(
        	"Created Subject object");
        return 
    		(Subject)mapJavaObject(subjectObj, subject, subjectFactory);
    }
    
    public void setEnvironment(Object envObject) 
    		throws PepException {
    	createEnvironment(envObject);
    }
    /**
     * Creates an  Environment 
     * @param envObject object representing the Environment ex: Map
     * @return Wrapper object around an <code>AzEntity&lt;AzCategoryEnvironmentId&gt;</code>
     * @throws PepException if there is no <code>JavaObjectMapper</code> configured for the object
     * @see JavaObjectMapperImpl
     * @see Environment
     */
    protected Environment createEnvironment(Object envObject) 
    		throws PepException {
        EnvironmentFactory environmentFactory = 
        	(EnvironmentFactory)getPepRequestFactory().
        		getRequestAttributesFactory(
        				AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT);
        Environment env = environmentFactory.createObject(this);
        return 
        	(Environment)mapJavaObject(envObject, env, environmentFactory);
    }
    
    public void setResourceAction(Object resourceObj, Object actionObj)
    		throws PepException {
        Resource resource = createResource(resourceObj);       
        Action action = createAction(actionObj);
        
        Set<Action> actions = new HashSet<Action>();
        actions.add(action);
        addResourceActionAssociation(resource,actions);
    }
    
    /**
     * Creates the Resource 
     * @param resourceObj object representing the Resource ex: String
     * @return Wrapper object around an <code>AzEntity&lt;AzCategoryResourceId&gt;</code>
     * @throws PepException if there is no <code>JavaObjectMapper</code> configured for the object
     * @see JavaObjectMapperImpl
     * @see Environment
     */
    protected Resource createResource(Object resourceObj) 
    		throws PepException {
        ResourceFactory resourceFactory = 
        	(ResourceFactory)getPepRequestFactory().
        		getRequestAttributesFactory(
        			AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE);
        Resource resource = resourceFactory.createObject(this);
        return (Resource)mapJavaObject(
        					resourceObj, resource, resourceFactory);
    }
    
    /**
     * Creates the Action 
     * @param actionObj object representing the Action ex: String
     * @return Wrapper object around an <code>AzEntity&lt;AzCategoryActionId&gt;</code>
     * @throws PepException if there is no <code>JavaObjectMapper</code> configured for the object
     * @see JavaObjectMapperImpl
     * @see Action
     */
    protected Action createAction(Object actionObj) 
    		throws PepException {
    	
    	RequestAttributesFactory<AzCategoryIdAction> attrActionFactory = 
    		getPepRequestFactory().getRequestAttributesFactory(
        		AzCategoryIdAction.AZ_CATEGORY_ID_ACTION);
    	
    	//TODO: some more work needs to be done here to remove
    	// the need for these casts. Basically, Action extends
    	// RequestAttributesImpl, that has a mapJavaObject
    	// method that the RequestAttributes intf does not have.
		ActionFactory actionFactory = 
			(ActionFactory)attrActionFactory;		
		Action action = actionFactory.createObject(this);
        return 
        	(Action)mapJavaObject(actionObj, action, actionFactory);
    }
    
    /**
     * Iterates through all of the <code>JavaObjectMapper</code> mappers.  
     * The first mapper that says that
     * it can do the mapping does, and the object is returned.
     * @param javaObject
     * @return an Attributes collection containing the mapped attributes
     * @throws PepException if there is no mapper which can map the javaObject
     */
    protected <T extends Enum<T> & AzCategoryId>
    Attributes<T> mapJavaObject(Object javaObject,
    							RequestAttributes<T> reqAttrT,
    							RequestAttributesFactory<T> reqAttrFactoryT) 
    				throws PepException{
        
    	//if (log.isTraceEnabled()) log.trace(
    	log.info(
    		"\nMapping javaObject for: AzEntity<" + 
				reqAttrT.getAzEntity().
					getAzCategoryId().getClass().getSimpleName() +
    		"> \n\t\twhere T = AzCategoryId: " + 
    			reqAttrT.getAzEntity().getAzCategoryId());
        List<JavaObjectMapper> mappers = reqAttrFactoryT.getMappers();
        if (log.isTraceEnabled()) log.trace(
        		"mappers.size() = " + mappers.size());
        for (int i=0; i<mappers.size(); i++) {
            
            JavaObjectMapper mapper =
                mappers.get(i);
            if (log.isTraceEnabled()) log.trace(
            	"\n\tLooking at mapper type: " +
            		mapper.getClass().getName());
            
            if (mapper.canMapObject(javaObject)) {
                return mapper.map(javaObject,reqAttrT);
            }                 
        }
        
        //Couldn't find a converter
        //What should we do?
        throw new RuntimeException(
        		"Couldn't find a converter for "+
        		javaObject+" "+javaObject);
    }

    /**
     * Sets the bulk resource-action objects for this PepRequestImpl.
     * Preserves to original objects for correlation with returned
     * results in PepResponseImpl.
     */
    public void setBulkResourceActions(
    			List resourceObjects, 
    			List actionObjects) 
    		throws PepException {
    	
        bulkActionObjects = actionObjects; // save the original objects
        bulkResourceObjects = resourceObjects; // save the original objects
        
        if (bulkActionObjects.size()!=bulkResourceObjects.size()) {
            throw new PepException("Actions "+bulkActionObjects+
            		" size("+bulkActionObjects.size()+
            		") does not match Resources "+bulkResourceObjects+
            		" size("+bulkResourceObjects.size()+")");
        }

        actionList = new ArrayList(); // use to correlate to input actions
        resourceList = new ArrayList(); // use to correlate to input resources
        for (int i=0; i<bulkResourceObjects.size(); i++) {
            Resource resource = createResource(bulkResourceObjects.get(i));
            resourceList.add(resource.getAzEntity().getId());
            if (log.isTraceEnabled()) log.trace(
            	"added resource with " + 
            		"resource.getWrappedObject().getId() = " + 
            		resource.getAzEntity().getId());
            Action action = createAction(bulkActionObjects.get(i));
            if (log.isTraceEnabled()) log.trace(
            	"added action with " + 
            		"action.getWrappedObject().getId() = " + 
            		action.getAzEntity().getId());
            actionList.add(action.getAzEntity().getId());
            Set<Action> actions = new HashSet<Action>();
            actions.add(action);
            addResourceActionAssociation(resource,actions);
        }    	
    }
    
    
    /**
     * Helper method to return the resource object from the 
     * initial request based on the AzResourceActionAssociation 
     * that was created for that resource object.
     * @return an object from the initial list of resource objects
     */
    public Object getResourceObject(AzResourceActionAssociation azRaa) {
    	if ( ! ( resourceList == null ) ) {
	    	// Get Iterator of the Resource object ids
	    	Iterator it = resourceList.iterator();
	    	// Get Iterator of original resource objects
	    	ListIterator itBulk = bulkResourceObjects.listIterator();
	    	// Iterate thru original objects
	    	while (itBulk.hasNext()){
	    		// Get next resource id of corresponding Resource Object
	    		Object resourceId = it.next();
	    		if (log.isTraceEnabled()) log.trace(
	    				"\n    resourceList resourceId: " + resourceId);
	    		// Get next original resource object
	    		Object obj = itBulk.next();
	    		// Does the requested resource object match 
	    		//  current original resource object?
	    		Object azRaaResourceId = azRaa.getAzResource().getId();
	    		if (azRaaResourceId == resourceId){
	    			if (log.isTraceEnabled()) log.trace(
	    				"\n\tFound resource object match: \n\t\t" + obj + 
	    					 "\n\t with input resource object: " + 
	    					 azRaaResourceId + "\n");
	    			return obj;
	    		}
	    		else {
	    			if (log.isTraceEnabled()) log.trace(
	    				"\n\tDid not find resource object match: \n\t\t" + obj +
	    					 "\n\t with input resource object: " + 
	    					 azRaaResourceId);
	    		}
	    	}
	    	if (log.isTraceEnabled()) log.trace("Object not found.\n");
	    	return null;
    	} else {
    		// must be query
    		return azRaa.getAzResourceActionAssociationId().getResourceId();
    	}
    }
    
    /**
     * Helper method to return the action object from the 
     * initial request based on the AzResourceActionAssociation 
     * reference that was created for that action object.
     * 
     * @param azRaa an AzResourceActionAssociation reference, which is used as
     * an index into the List of Action objects
     * @return the action object corresponding to the AzResourceActionAssociation
     * reference parameter
     */
    public Object getActionObject(AzResourceActionAssociation azRaa) {
    	if ( ! ( actionList == null) ) {
	    	// Get Iterator of the Action object ids
	    	Iterator it = actionList.iterator();
	    	// Get Iterator of original Action objects
	    	ListIterator itBulk = bulkActionObjects.listIterator();
	    	// Iterate thru original objects
	    	while (itBulk.hasNext()){
	    		// Get next resource id of corresponding Resource Object
	    		Object actionId = it.next();
	    		if (log.isTraceEnabled()) log.trace(
	    				"\n    actionList actionId: " + actionId);
	    		// Get next original action object
	    		Object obj = itBulk.next();
	    		// Does the requested action object match 
	    		//  current original action object?
	    		Object azRaaActionId = azRaa.getAzAction().getId();
	    		if (azRaaActionId == actionId){
	    			if (log.isTraceEnabled()) log.trace(
	    				"\n\tFound action object match: \n\t\t" + obj + 
	    					 "\n\t with input action object: " + 
	    					 azRaaActionId + "\n");
	    			return obj;
	    		}
	    		else {
	    			if (log.isTraceEnabled()) log.trace(
	    				"\n\tDid not find action object match: \n\t\t" + obj +
	    					 "\n\t with input action object: " + 
	    					 azRaaActionId);
	    		}
	    	} // end while(itBulk.hasNext())
	    	if (log.isTraceEnabled()) log.trace("Object not found.\n");
	    	return null;
    	} else {
    		// must be a query
    		return azRaa.getAzResourceActionAssociationId().getActionId();
    	}
    }
    
    protected void addResourceActionAssociation(
    		Resource azResource, Set<Action> actions) {
        Set<AzEntity<AzCategoryIdAction>> newSet = 
        	new HashSet<AzEntity<AzCategoryIdAction>>();
        Iterator<Action> it = actions.iterator();        
        while (it.hasNext()) {
            Action a = it.next();
            newSet.add(a.getAzEntity());
        }
        Set<AzResourceActionAssociation> setRA = 
        	this.azRequestContext.addResourceActionAssociation(
        			azResource.getAzEntity(), newSet);
        Iterator<AzResourceActionAssociation> itRA = setRA.iterator();
        while (itRA.hasNext()) {
        	AzResourceActionAssociation azRA = itRA.next();
        	if (log.isTraceEnabled()) log.trace(
        		"Added RA w correlationId: " + 
        			azRA.getCorrelationId());
        }
    }
    
    /*
    protected void createAndAddResourceActionAssociation(
    		Resource azResource, Action action) {
        this.azRequestContext.
        	createAndAddResourceActionAssociation(
        			azResource.getAzEntity(), action.getAzEntity());
    }
    */

    /**
     * Returns the name of the container in which the 
     * pep is running, which MAY be used as the issuer
     * of attributes at the azapi level by the 
     * JavaOhjectMappers. 
     */
    public String getContainerName() {
        return this.defaultContainerName;
    }

    /**
     * Returns the {@link PepRequestFactory} object that was
     * used to create this PepRequest. It is used primarily
     * to get access to the methods of PepRequestFactory.
     */
    public PepRequestFactory getPepRequestFactory() {
        return pepRequestFactory;
    }
    
    public void setScope(String scope) {
        this.scope = scope;
    }

    /**
     * Returns the "scope" that was set if this is a query
     * type request {@link PepRequestOperation}.
     * @return a string if this is a query type PepRequest, 
     * otherwise, null.
     */
    public String getScope() {
        return scope;
    }

    public void setQueryReturnAllowed(boolean queryReturnAllowed) {
        this.queryReturnAllowed = queryReturnAllowed;
    }

    /**
     * Returns a boolean indicating whether the results indicate
     * what is allowed (true) or not allowed (false). The boolean
     * is determined by the value of the 
     * {@link PepRequestQueryType} that was set in the 
     * {@link PepRequestFactory#newQueryPepRequest(Object, Object, String, org.openliberty.openaz.azapi.constants.PepRequestQueryType)}
     * method to specify the type of results intended to be
     * returned.
     */
    public boolean isQueryForAllowedResults() {
        return queryReturnAllowed;
    }

    /**
     * Return the PepRequestOperation for the current PepRequest,
     * which is the type of request: a single decide, a bulk request,
     * or a query.
     */
    public PepRequestOperation getOperation() {
        return operation;
    }
    
}
