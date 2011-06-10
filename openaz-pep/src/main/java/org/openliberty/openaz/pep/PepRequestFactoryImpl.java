package org.openliberty.openaz.pep;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openliberty.openaz.azapi.pep.RequestAttributesFactory;
import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepResponseFactory;
import org.openliberty.openaz.azapi.pep.DecisionHandler;
import org.openliberty.openaz.azapi.pep.PreDecisionHandler;
import org.openliberty.openaz.azapi.pep.PostDecisionHandler;
import org.openliberty.openaz.azapi.pep.PepException;

import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;
import org.openliberty.openaz.azapi.constants.PepRequestQueryType;
import org.openliberty.openaz.azapi.constants.PepRequestOperation;

import org.openliberty.openaz.azapi.AzAttribute;
import org.openliberty.openaz.azapi.AzAttributeValue;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.AzResourceActionAssociation;
import org.openliberty.openaz.azapi.AzResult;
import org.openliberty.openaz.azapi.AzService;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;

import org.openliberty.openaz.pdp.provider.AzServiceFactory;

// imports for javadoc:
import org.openliberty.openaz.pdp.provider.SimpleConcreteDummyService;
import org.openliberty.openaz.pdp.provider.SimpleConcreteSunXacmlService;
import org.openliberty.openaz.azapi.AzResponseContext;
import org.openliberty.openaz.azapi.pep.JavaObjectMapper;

//import test.TestStyles;

/**
 * The {@link org.openliberty.openaz.pep.PepRequestFactoryImpl} is an 
 * implementation class of the OpenAz project that implements the
 * {@link org.openliberty.openaz.azapi.pep.PepRequestFactory} 
 * interface.
 * <p>
 * The primary purpose of this class and the others in this package
 * and the classes in the companion {@link org.openliberty.openaz.pdp}
 * package and subpackages is to provide a reference implementation
 * for the test programs in test 
 * to use in conjunction with demonstrating the use of the overall 
 * "PepApi" ({@link org.openliberty.openaz.azapi.pep} interfaces and the
 * "AzApi" ({@link org.openliberty.openaz.azapi}) interfaces.
 * <p>
 * Please reference the javadoc in
 * {@link org.openliberty.openaz.azapi.pep.PepRequestFactory}
 * for further information as to the general purpose of this class
 * and the methods in it. The javadoc in this class and package will 
 * focus on the specific objectives of this reference implementation
 * and how it supports the interface methods and any particular
 * specifics regarding the OpenAz AzApi implementation that it is 
 * intended to utilize.
 * <p>
 * {@link PepRequestFactory} is the main entry point for building 
 * Policy Enforcement Point (PEP) capabilities that can be utilized
 * by applications and containers.
 * <p>
 * The main use of this factory class is to enable clients to
 * easily submit the objects that contain information that is
 * expected to be used in Security Policy evaluation to the
 * authorization system. There are two major steps that need
 * to be taken to enable requests to be submitted:
 * <ul>
 * <li>First an authorization service reference must be provided
 * to the factory that it can link to the request objects that
 * it will instantiate.
 * <ul>
 * <li>Instantiate the PepRequestFactory by obtaining a registered
 * instance of AzService. (Note: see 
 * <a href="http://openaz.svn.sourceforge.net/viewvc/openaz/test/doc/test/TestStyles.html#setupAzService(java.lang.String[])">TestStyles.setUpAzService()</a>
 * for an example of how to register service implementations.<br>
 * <code>
 * <pre>
 *       AzService azService = 
 *       		AzServiceFactory.getAzService();
 *       PepRequestFactory pep = 
 *       	(PepRequestFactory)new PepRequestFactoryImpl(CONTAINER,azService);
 * </pre>
 * </code>
 * </ul>
 * 
 * <li>To enable this capability there are
 * six {@link PepRequest} instance creation methods provided as
 * specified by the {@link PepRequestFactory} interface:
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
 * </ul>
 * <p>
 * 
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public class PepRequestFactoryImpl implements PepRequestFactory {
   
   private String providerClassName;
   private String containerName;
   private AzService azService;
   
   private RequestAttributesFactory<? extends AzCategoryId> 
   					actionFactory = new ActionFactory();
   private RequestAttributesFactory<? extends AzCategoryId> 
   					resourceFactory = new ResourceFactory();
   private RequestAttributesFactory<? extends AzCategoryId> 
   					subjectFactory = new SubjectFactory();
   private RequestAttributesFactory<? extends AzCategoryId> 
   					environmentFactory = new EnvironmentFactory();

   private PepResponseFactory responseFactory = 
	   (PepResponseFactory) new PepResponseFactoryImpl();
   
   private List<PreDecisionHandler> preDecisionHandlers;
   private DecisionHandler decisionHandler;
   private List<PostDecisionHandler> postDecisionHandlers;
   
   private static DecisionHandler  DEFAULT_DECIDE_HANDLER  = 
	   (DecisionHandler) new DefaultDecisionHandler();
   private static PreDecisionHandler  DEFAULT_PRE_DECIDE_HANDLER  = 
	   (PreDecisionHandler) new DefaultDecisionHandler();
   private static PostDecisionHandler  DEFAULT_POST_DECIDE_HANDLER  = 
	   (PostDecisionHandler) new DefaultDecisionHandler();
   
   public static final String DEFAULT_PROVIDER_NAME = "AZ_SERVICE";
   
   private Log log = LogFactory.getLog(this.getClass()); 
   
    /**
     * Constructor that allows for the inclusion of custom PreDecisionHandler, 
     * DecisionHandler and PostDecsionHandler 
     * @param containerName The name of the PEP. This is used as the issuer of all of 
     * the attributes.
     * @param azService A handle to the AzService 
     * @param preDecideHandlers 
     * @param decideHandler 
     * @param postDecideHandlers 
     */
    public PepRequestFactoryImpl(
					String containerName, 
					AzService azService, 
					List<PreDecisionHandler> preDecideHandlers,
                    DecisionHandler decideHandler,
                    List<PostDecisionHandler> postDecideHandlers) {
        this(preDecideHandlers,decideHandler,postDecideHandlers);
        this.containerName = containerName;
        this.azService = azService;
        String name = DEFAULT_PROVIDER_NAME;
        
        if (log.isTraceEnabled()) log.trace(
        		"\n    Registering provider, \n\t" + name + " = " + 
        		azService.getClass().getName() + ".\n");
        
        AzServiceFactory.registerProvider(name, azService);
    }
    
    /**
     * Constructor of Pep Request Factory uses the DefaultDecisionHandler
     * @param containerName The name of the PEP. This is used as the issuer of 
     * all of the attributes.
     * @param azService A handle to the AzService 
     */
    public PepRequestFactoryImpl(
		    		String containerName, 
		    		AzService azService) {
        this();
        this.containerName = containerName;
        this.azService = azService;
        String name = DEFAULT_PROVIDER_NAME;
        
        if (log.isTraceEnabled()) log.trace(
        		"\n    Registering provider, \n\t" + name + " = " + 
        		azService.getClass().getName() + ".\n");
        AzServiceFactory.registerProvider(name, azService);
    }

    /**
     * private default constructor
     */
    private PepRequestFactoryImpl() {
        this(Arrays.asList(
        		new PreDecisionHandler[]{DEFAULT_PRE_DECIDE_HANDLER}),
             DEFAULT_DECIDE_HANDLER,
             Arrays.asList(
            		 new PostDecisionHandler[]{DEFAULT_POST_DECIDE_HANDLER}));
    }
    
    /**
     * private constructor
     * 
     * @param preDecideHandlers
     * @param decideHandler
     * @param postDecideHandlers
     */
    private PepRequestFactoryImpl(
    					List<PreDecisionHandler> preDecideHandlers,
                        DecisionHandler decideHandler,
                        List<PostDecisionHandler> postDecideHandlers) {
        super();
        this.preDecisionHandlers = preDecideHandlers;
        this.decisionHandler = decideHandler;
        this.postDecisionHandlers = postDecideHandlers;
    }

   /**
     * Create a PepRequest just using Strings
     * <p>
     * Note to developers: all the newPepRequest methods eventually
     * call {@link #newPEPRequest}({@link PepRequestOperation}), which 
     * 
     * @param subjectName String representing the name of the subject ex: Josh
     * @param actionId String representing the action ex: read
     * @param resourceId String representing the resource ex: file1234
     * @return a PepRequest populated with attributes mapped from the params
     * @throws PepException Indicates that there is some issue creating the 
     * <code>PEPRequest</code>
     */
    public PepRequest newPepRequest(
    					String subjectName, 
    					String actionId, 
    					String resourceId) 
    			throws PepException {
        
        if (log.isTraceEnabled()) log.trace("Start Simple Decide");
        
        PepRequest request = 
        	this.newPEPRequest(PepRequestOperation.DECIDE);
        
        request.setAccessSubject(subjectName);
        request.setResourceAction(resourceId, actionId);
        //request.setEnvironment(new Date());
       
        return request;
    }
    
    /**
     * Create a PepRequest using any set of objects for the
     * Subject, Action, Resource, Environment entities. 
     * <p>
     * Note: for this PepRequest to work, the appropriate
     * {@link JavaObjectMapper} classes must be configured
     * and set using {@link RequestAttributesFactoryImpl#setMappers(List)}
     *
     * @param subjectObj Object representing the Subject 
     * 	ex: javax.auth.security.Subject
     * @param actionObj Object representing the Action
     *  ex: String (read)
     * @param resourceObj Object representing the Resource
     *  ex: String (file) or File
     * @param environmentObj Object representing the Environment
     *  ex: Map containing attributes name and values
     * @return a PepRequest populated with attributes mapped from params
     * @throws PepException if there is no <code>JavObjectMapper</code> 
     *  configured for the objects passed into the factory.
     * @see JavaObjectMapper
     */
    public PepRequest newPepRequest(
    						Object subjectObj, 
    						Object actionObj, 
    						Object resourceObj, 
    						Object environmentObj)  
    				throws PepException {
        if (log.isTraceEnabled()) log.trace(
        		"\n\tBegin creation of Mapper-based PepRequest");
        PepRequest request = 
        	this.newPEPRequest(PepRequestOperation.DECIDE);
        
        request.setAccessSubject(subjectObj);
        
        request.setResourceAction(resourceObj, actionObj);
        
        request.setEnvironment(environmentObj);
        
        if (log.isTraceEnabled()) log.trace(
        		"\n\tCompleted creation of Mapper-based PepRequest");
        return request;
        
    }
    
    
    /**
     * Creates a PEP request using objects, but the action and the resource are
     *  both derived from the same object ex:  Permission or HttpServletRequest
     * @param subjectObj Object representing the Subject
     *  ex: javax.auth.security.Subject
     * @param actionResourceObject Object representing both the action and the
     *  resource ex: FilePermission or HttpServletRequest
     * @param environmentObj Object representing the Environment 
     *  ex: Map containing attributes name and values
     * @return a PepRequest with the params mapped to AzAttributes
     * @throws PepException if there is no <code>JavObjectMapper</code> configured
     *  for the objects passed into the factory.
     * @see JavaObjectMapperImpl
     */
    public PepRequest newPepRequest(
    						Object subjectObj, 
    						Object actionResourceObject, 
    						Object environmentObj) 
    		throws PepException {
        
        return this.newPepRequest(
        		subjectObj,
        		actionResourceObject,
        		actionResourceObject,
        		environmentObj);
        
    }
    /**
     * Create a PepRequest using objects, where a list of n action objects and
     *  a corresponding list of n resource objects are provided to represent
     *  n resource-action pairs. A decision for each resource-action pair will
     *   be returned, when PepRequest.decide() is invoked.
     * @param subjectObj Object representing the Subject
     *  ex: javax.auth.security.Subject
     * @param actionObjects a list of length n, of actionObj Objects 
     *  representing the Actions
     *  ex: String (read)
     * @param resourceObjects a list of length n, of resourceObj Objects 
     *  representing the Resources
     *  ex: String (file) or File
     * @param environmentObj Object representing the Environment
     *  ex: Map containing attributes name and values
     * @return a PepRequest populated with mapped Attributes
     * @throws PepException
     */
    public PepRequest newBulkPepRequest(
	    		Object subjectObj, 
	    		List actionObjects, 
	    		List resourceObjects, 
	    		Object environmentObj) 
	    	throws PepException {
        
        if (log.isTraceEnabled()) log.trace(
        		"\n\tBegin creation of Bulk Mapper-based PepRequest");
        PepRequest request = (PepRequestImpl) this.newPEPRequest(
        		PepRequestOperation.BULK_DECIDE);
        
        request.setAccessSubject(subjectObj);
        
        // This sets up the correlations
        request.setBulkResourceActions(resourceObjects, actionObjects);

        request.setEnvironment(environmentObj);
        
        if (log.isTraceEnabled()) log.trace(
        		"\n\tCompleted creation of Mapper-based PepRequest");
        return request;        
    }
    
    /**
     * Create a PepRequest using objects, where a list of n action-resource objects
     * are provided. A decision for each resource-action pair will be returned, 
     * when PepRequest.decide() is invoked.
     * @param subjectObj Object representing the Subject
     *  ex: javax.auth.security.Subject
     * @param actionResourceObjects a list of length n, of 
     *  actionResourceObjects representing the
     *  Action Resource pairs ex: String (read), String (file)
     * @param environmentObj Object representing the Environment
     *  ex: Map containing attributes name and values
     * @return a PepRequest populated with mapped Attributes
     * @throws PepException
     */
    public PepRequest newBulkPepRequest(
    						Object subjectObj, 
    						List actionResourceObjects, 
    						Object environmentObj) 
    		throws PepException {
        
        return this.newBulkPepRequest(
        				subjectObj,
        				actionResourceObjects,
        				actionResourceObjects,
        				environmentObj);
    }
    
    /**
     * Create a PepRequest using subject and environment objects, plus a "scope"
     * String that represents a PDP policy-specific resource representation.
     * 
     * When the PepRequest.decide() method is invoked, based on queryType it
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
     * @return a PepRequest populated with info mapper from the params
     * @throws PepException
     */
    public PepRequest newQueryPepRequest(
    						Object subjectObj, 
    						Object environmentObj, 
    						String scope, 
    						PepRequestQueryType queryType) 
    		throws PepException {
        
        PepRequest request = (PepRequestImpl) this.newPEPRequest(
        		queryType==PepRequestQueryType.VERBOSE?
        					PepRequestOperation.QUERY_VERBOSE:
        					PepRequestOperation.QUERY);
        request.setAccessSubject(subjectObj);
        request.setEnvironment(environmentObj);
        
        request.setScope(scope);
        
        if (queryType!=PepRequestQueryType.VERBOSE) {
            // Set to true if allowed results requested,
        	// otherwise false.
            request.setQueryReturnAllowed(
            	queryType.equals(
            		PepRequestQueryType.RETURN_ONLY_ALLOWED_RESULTS));
        }        
        return request;
    }
    
    /**
     * Internal factory method used by all the public methods
     * to call into the actual PepRequestImpl constructor.
     * <p>
     * Note to developers: This method makes the actual call
     * to {@link PepRequestImpl#PepRequestImpl(AzRequestContext, 
     * PepRequestFactory, PepRequestOperation)}.
     * <p> Note that the above constructor is where the PepApi
     * objects, which are contained in this PepRequestFactoryImpl
     * class are related to the AzApi objects thru the 
     * {@link AzRequestContext} handle obtained from the AzApi
     * {@link AzService} implementation.
     * <p>
     * The newPepRequest methods continue on return from obtaining
     * the underlying PDP AzApi AzRequestContext, and are able
     * to invoke the mappers to translate the local objects
     * to the AzApi objects.
     * <p>
     * Note also, that if this PepApi impl is used for guidance for
     * implementing a direct PepApi-PDP interface, that the modules
     * here that bridge to AzApi would likely have corresponding
     * modules to bridge to the specific PDP API.
     * <p>
     * Note also, that the long term goal of PepApi/AzApi is to use
     * AzApi as a common set of PDP request/response objects that
     * any PDP can map from and any PEP can map to, which will provide
     * a common interoperability layer.
     * <p>
     * Note also, that it is also recognized that direct links from
     * PepApi to PDP can provide improved performance, so, in conjunction
     * with proper configuration, it should be possible to provide
     * both full interoperability and high performance. For full flexibility
     * for any request to go to any pdp at any time, it is likely the
     * common interface will be required, however, if there are stable
     * configurations and expected policy processing sequences, then
     * configuration can likely expose sections where direct links can
     * be used with little or no sacrifice of interoperability.
     * @param operation
     * @return a PepRequest with initial AzEntity objects ready to be populated
     */
    protected PepRequest newPEPRequest(PepRequestOperation operation) {
        
        //Use AzApi AzService to create an AzRequestContext:
        AzService azHandle = AzServiceFactory.getAzService();        
        AzRequestContext azReqCtx = azHandle.createAzRequestContext();  // [a07]
        
        // Create a basic PepRequestImpl that can be further populated
        // on return to the caller
        PepRequest pepRequest = new PepRequestImpl(azReqCtx,this,operation);    
        
        return pepRequest;
    }
    
    protected void setAzService(AzService azService) {
        this.azService = azService;
    }

    public AzService getAzService() {
        return azService;
    }

    protected void setProviderClassName(String providerClassName) {
        this.providerClassName = providerClassName;
    }

    public String getProviderClassName() {
        return providerClassName;
    }

    protected void setContainerName(String name) {
        this.containerName = name;
    }

    public String getContainerName() {
        return containerName;
    }
    
    /**
     * Use @SuppressWarnings to allow the casts below:
     * This follows Joshua Bloch's book Effective Java
     * 2nd Edition Item 24: Eliminate unchecked warnings
     * where it is described how to minimize scope of
     * SuppressWarnings
     */
    public <T extends Enum<T> & AzCategoryId> 
    	RequestAttributesFactory<T> getRequestAttributesFactory(T t) {
    	
    	if (t.equals(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION)) {
    		@SuppressWarnings("unchecked")
    		RequestAttributesFactory<T> azWrapReqObjFac =
    			(RequestAttributesFactory<T>)getActionFactory();
    		return azWrapReqObjFac;
    	} else if (t.equals(AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE)) {
    		@SuppressWarnings("unchecked")
    		RequestAttributesFactory<T> azWrapReqObjFac =
    			(RequestAttributesFactory<T>)getResourceFactory();
    		return azWrapReqObjFac;
    	} else if (t.equals(
    			AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS)) {
    		@SuppressWarnings("unchecked")
    		RequestAttributesFactory<T> azWrapReqObjFac =
    			(RequestAttributesFactory<T>)getSubjectFactory();
    		return azWrapReqObjFac;
    	} else if (t.equals(AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT)) {
    		@SuppressWarnings("unchecked")
    		RequestAttributesFactory<T> azWrapReqObjFac =
    			(RequestAttributesFactory<T>)getEnvironmentFactory();
    		return azWrapReqObjFac;
    	}
    	return null;
    }
       
    protected void setSubjectFactory(SubjectFactory subjectFactory) {
        this.subjectFactory = subjectFactory;
    }

    protected void setResourceFactory(ResourceFactory resourceFactory) {
        this.resourceFactory = resourceFactory;
    }

    protected void setActionFactory(ActionFactory actionFactory) {
        this.actionFactory = actionFactory;
    }

    protected void setEnvironmentFactory(EnvironmentFactory environmentFactory) {
        this.environmentFactory = environmentFactory;
    }

    protected RequestAttributesFactory<? extends AzCategoryId> 
    			getEnvironmentFactory() {
        return environmentFactory;
    }

    protected RequestAttributesFactory<? extends AzCategoryId> 
    			getResourceFactory() {
        return resourceFactory;
    }

    protected RequestAttributesFactory<? extends AzCategoryId> 
    			getSubjectFactory() {
		return subjectFactory;
    }

    protected RequestAttributesFactory<? extends AzCategoryId> 
    			getActionFactory() {
        return actionFactory;
    }
    
    protected void setResponseFactory(PepResponseFactory responseFactory) {
        this.responseFactory = responseFactory;
    }

    /**
     * Helper method to return the PepResponseFactory that was
     * configured with this PepRequestFactory.
     * @return a PepResponseFactory object
     */
    public PepResponseFactory getResponseFactory() {
        return responseFactory;
    }
    
    /**
     * Return the List of PreDecisionHandlers that was set when
     * the PepRequestFactory was created.
     */
    public List<PreDecisionHandler> getPreDecisionHandlers() {
        return this.preDecisionHandlers;
    }

    /**
     * Return the DecisionHandler that was set when the PepRequestFactory
     * was created.
     */
    public DecisionHandler getDecisionHandler() {
        return this.decisionHandler;
    }

    /**
     * Return the List of PostDecisionHandlers that was set when
     * the PepRequestFactory was created.
     */
    public List<PostDecisionHandler> getPostDecisionHandlers() {
        return this.postDecisionHandlers;
    }
    
    /**
     * TBD: Some old helper methods below: probably should be
     * removed and replaced by clean logging capabilities.
     * @param azResult
     * @param log
     */
    static void printObligations(AzResult azResult, Log log){
            Iterator<AzEntity<AzCategoryIdObligation>> itOb = 
                    azResult.getAzObligations().iterator();
            log.debug("  TestAzAPI: itOb.hasNext() = " + 
                    itOb.hasNext());
            while (itOb.hasNext()){
                    AzEntity<AzCategoryIdObligation> azObligation = itOb.next();
                    Iterator<AzAttribute<?>> itAttr = 
                            azObligation.getAzAttributeMixedSet().iterator();
                    log.debug("  TestAzAPI: itAttr.hasNext() = " +
                            itAttr.hasNext());
                    while (itAttr.hasNext()){
                            printAttributeData(itAttr.next(), 
                                    "Obligation attribute: ",log);
                    }
            }               
    }
    static void printMissingAttributes(AzResult azResult, Log log){
            Iterator<AzAttribute<?>> itMAD = 
                azResult.getAzStatusDetail().getAzAttributeMixedSet().iterator();
            log.debug("  TestAzAPI: itMAD.hasNext() = " + itMAD.hasNext());
            while (itMAD.hasNext()){
                    AzAttribute<?> azMissingAttributeDetail = itMAD.next();
                    printAttributeData(azMissingAttributeDetail, 
                                    "MissingAttributeDetail attribute: ",log);
            }               
    }
    
    static void printResultData(AzResult azResult, Log log){
        log.debug(
            "\nTestAzAPI: " +
            "\n\t azResult.getAzResourceActionAssociation.getCorrelationId: " +
                    azResult.getAzResourceActionAssociation().getCorrelationId() +
                "\n\t azResult.getAzResourceActionAssociationId: " +
                    "\n\t\t " + azResult.getAzResourceActionAssociation() +
                "\n\t azResult.getAzDecision: " +
                    azResult.getAzDecision() +
                "\n\t azResult.getAzStatusCode: " +
                    azResult.getAzStatusCode() +
                "\n\t azResult.getStatusMessage: " +
                    "\n\t\t " + azResult.getStatusMessage());               
    }
    
    static <T extends Enum<T> & AzCategoryId> 
    	void printAttributeData(AzAttribute<T> azAttr, String info, Log log){
            String azCatIdClass = null;
            if (azAttr.getAzCategoryId() == null)
                    azCatIdClass = "Null - no category in this attribute.";
            else
                    azCatIdClass = azAttr.getAzCategoryId().getClass().getName();
            AzAttributeValue<?,?> azAttrVal = azAttr.getAzAttributeValue();
            log.debug(
                    "    TestAzAPI.printAttributeData: " + info +
                    "\n\t azAttr.getAttributeId: " + azAttr.getAttributeId() +
                    "\n\t azAttr.getClass: " + azAttr.getClass() +
                    "\n\t azAttr.getCat: " + azAttr.getAzCategoryId() +
                    "\n\t azAttr.getAzCategoryId.getClass: " + azCatIdClass +
                    "\n\t azAttr.getAttrVal.getType: " + azAttrVal.getType() +
                    "\n\t azAttr.getAttrVal.getValue: " + azAttrVal.getValue());
    }
    
}
