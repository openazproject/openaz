package org.openliberty.openaz.pep;

import java.util.HashMap;

import org.openliberty.openaz.azapi.AzResponseContext;
import org.openliberty.openaz.azapi.AzResult;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzObligations;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;
import java.util.Iterator;

import java.util.Map;

import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepResponseFactory;
import org.openliberty.openaz.azapi.pep.Obligation;
import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.constants.PepRequestOperation;
import org.openliberty.openaz.azapi.constants.PepRequestQueryType;
import org.openliberty.openaz.azapi.constants.PepResponseBehavior;

import org.openliberty.openaz.azapi.AzResourceActionAssociation;

/**
 * PepResponse is the main entry point for processing the results
 * of a {@link PepRequestImpl#decide()} call.
 * <br>
 * It enables processing one result at a time for an AzResponseContext,
 * which may contain multiple decisions from one of the
 * <ul>
 * <li> 
 * {@link PepRequestFactoryImpl#newBulkPepRequest(Object, java.util.List, Object)} 
 * calls, 
 * </ul>
 * or a 
 * <ul>
 * <li>
 * {@link PepRequestFactory#newQueryPepRequest(Object, Object, String, PepRequestQueryType)}
 * call.
 * </ul>
 * It also provides a wrapper around the <code>AzResponseContext</code>
 * which enables access to the underlying low level AzApi funtions.
 * <p>
 * Please refer to {@link PepRequestFactoryImpl} for code example containing
 * processing of a PepResponse.
 */
public class PepResponseImpl implements PepResponse {
   
	private Log log = LogFactory.getLog(this.getClass());
   
	private AzResponseContext responseContext;
	private PepResponseFactory responseFactory;
   
	private PepRequestOperation operation;
	private PepRequest pepRequest;
	private Iterator resultIterator;
	private Object currentResult;
   
	private boolean queryAllowed;

    /**
     * Create an instance of the PepResponse using an AzResponseContext,
     * which is returned by an AzService.decide() or queryVerbose()
     * 
     * @param responseContext the actual <code>AzResponseContext</code> from
     * the underlying AzService
     * @param pepRequest the pepRequest associated with this pepResponse
     * @param responseFactory the pepResponseFactory used to 
     * create this pepResponse. 
     * This handle is used to get access to the <code>ObligationFactory</code> 
     * to create <code>Obligation</code> instances and access the 
     * <code>Behavior</code> for each indeterminate response type
     * @see ObligationFactoryImpl
     * @see ObligationImpl
     * @see PepResponseFactoryImpl
     * @param operation the operation that this pepResponse is responding to 
     */
    PepResponseImpl(AzResponseContext responseContext,
    				PepRequest pepRequest,
                    PepResponseFactory responseFactory,
                    PepRequestOperation operation) {
        super();
        this.responseContext = responseContext;
        this.responseFactory = responseFactory;
        this.pepRequest = pepRequest;
        this.resultIterator = responseContext.getResults().iterator();
        this.operation = operation;
                
        //Move the iterator to the first entry if it's a decide
        if (operation==PepRequestOperation.DECIDE  && 
        		this.resultIterator.hasNext()) {
        	if (log.isTraceEnabled()) log.trace(
        		"Setting currentResult. Operation = " + operation);
            currentResult = this.resultIterator.next();
        }
    }
    
    /**
     * Create a PepResponse from a Set<AzResourceActionAssociation>
     * which has been returned by an AzService.query().
     * @param actionResourceAssociations
     * @param pepRequest
     * @param responseFactory
     * @param queryAllowed
     */
    PepResponseImpl(
    		Set<AzResourceActionAssociation> actionResourceAssociations,
    		PepRequest pepRequest,
    		PepResponseFactory responseFactory, 
    		boolean queryAllowed ) {
        
        
        this.operation = PepRequestOperation.QUERY;        
        this.responseFactory = responseFactory;   
        this.pepRequest = pepRequest;
        this.resultIterator = actionResourceAssociations.iterator();        
        this.queryAllowed = queryAllowed;
        if (log.isTraceEnabled()) log.trace(
        	"Query ResponseImpl: " +
        		"\n\tresultIterator.hasNext() = " + 
        			resultIterator.hasNext() +
        		"\n\tqueryAllowed() = " + queryAllowed);
    }

    /**
     * For bulk requests and query requests, move the iterator to
     * the next result.
     * <b>
     * For bulk requests and query requests, the iterator initially
     * points prior to the first result.
     * <b> 
     * For single newPepRequest, the iterator always points to the
     * first and only result and next() always returns false.
     * @return true if there is another result for bulk requests
     * and query requests, false otherwise.
     */
    public boolean next() 
    		throws PepException {
        
        if (this.operation == PepRequestOperation.DECIDE) {
            this.log.warn(
            	"The DECIDE operation only has a single result.  " + 
            	"Don't call PepResponse.next()");
            return false;
        }
        
        if (this.resultIterator.hasNext()) {
            this.currentResult = this.resultIterator.next();
            return true;
        } else {
            this.currentResult = null;
            return false;
        }
    }

    /**
    * Returns the decision associated with the current result.
    * @return true if the user was granted access to the resource, 
    * otherwise false
    * @throws PepException if the <code>Behavior</code> configured 
    * in the <code>PepResponseFactory</code>
    * indicates that for the specific AzDecision and AzStatus
    * that an exception should be thrown
    */
    public boolean allowed() throws PepException {
       
		if (!(this.currentResult instanceof AzResult)) {
			return this.queryAllowed;
		}
	   	
	   	AzResult azResult = (AzResult)this.currentResult;
       
       switch (azResult.getAzDecision()){
	       case AZ_PERMIT: 
	    	   return true;
	       case AZ_DENY:
	    	   return false;
	       case AZ_NOTAPPLICABLE:
	    	   return enforceBehavior(this.getResponseFactory().
	    			   getNotApplicableBehavior()); 
	       case AZ_INDETERMINATE:
	           switch (azResult.getAzStatusCode()) {
	           case AZ_SYNTAX_ERROR:
	        	   return enforceBehavior(this.getResponseFactory().
	        			   getSyntaxErrorBehavior());
	           case AZ_PROCESSING_ERROR:
	        	   return enforceBehavior(this.getResponseFactory().
	        			   getProcessingErrorBehavior());
	           case AZ_MISSING_ATTRIBUTE:
	        	   return enforceBehavior(this.getResponseFactory().
	        			   getMissingAttributeBehavior());
	           }
       } // end switch  
       
       throw new PepException(
   			"AzResult.getAzDecision did not match any of the known values");
	}
    /**
     * Return the action object associated with the current
     * result. The action object is the same object that was
     * used to create the PepRequest and may be used to 
     * correlate the PepResponse results with the action-resource
     * pairs that were used to create the PepRequest.
     * @return an object that was used as an action in the PepRequest
     * @throws PepException
     */
    public Object getAction() throws PepException {       
        return getPepRequest().getActionObject(
        		this.getAzResourceActionAssociation());
    }
    
    /**
     * Return the resource object associated with the current
     * result. The resource object is the same object that was
     * used to create the PepRequest and may be used to 
     * correlate the PepResponse results with the action-resource
     * pairs that were used to create the PepRequest.
     * @return an object that was used as a resource in the PepRequest
     * @throws PepException
     */
    public Object getResource() throws PepException {
        return getPepRequest().getResourceObject(
        		this.getAzResourceActionAssociation());
    }
    /**
     * Return the set of Obligations associated with the current result
     * indexed by ObligationId.
     * @return a Map of String, Obligation pairs
     * @throws PepException
     */
    public Map<String,Obligation> getObligations() throws PepException {
        
        assertValidResult();
        HashMap<String,Obligation> obligations = 
        	new HashMap<String,Obligation>();
        
        if (!(this.currentResult instanceof AzResult)) {
            this.log.warn(
        		"getObligations() method not supported on " + 
        		"PepRequest.OPERATION.QUERY");
            return obligations;
        }
    	
    	AzResult azResult = (AzResult)this.currentResult;
    	AzObligations azObligations = azResult.getAzObligations();
        
        if (azObligations!=null) {
            if (log.isTraceEnabled()) log.trace(
            		"process azObligations");
            Iterator<AzEntity<AzCategoryIdObligation>> obligationsIt =
                azObligations.iterator();
            while (obligationsIt.hasNext()) {
                AzEntity<AzCategoryIdObligation> azObligation =
                    obligationsIt.next();
                if (log.isTraceEnabled()) log.trace(
                		"read and wrap next azObligation");
                Obligation obligation = 
                	this.getResponseFactory().getObligationFactory().
                		createObject(azObligation);
                String name = obligation.getAzEntity().
                		getAzEntityId();
                if (log.isTraceEnabled()) log.trace(
                	"\n\tput wrapped Obligation: ObligationId = " + name);
                obligations.put(name,obligation);
            }
        }
        else {
        	if (log.isTraceEnabled()) log.trace(
        			"No Obligations returned in PepResponse");
        }
        return obligations;
    }

    /**
     * The handle to the actual <code>AzResponseContext</code>.  
     * @return the AzResponseContext returned by AzApi
     */
    public AzResponseContext getAzResponseContext() 
    		throws PepException {
        return this.responseContext;
    }
    /**
     * Return the PepRequest object associated with this
     * PepResponse.
     * @return the PepRequest object that was issued in 
     * association with this PepResponse.
     */
    public PepRequest getPepRequest(){
    	return pepRequest;
    }
    
    protected PepResponseFactory getResponseFactory() {
        return this.responseFactory;
    }

    AzResourceActionAssociation getAzResourceActionAssociation() 
    		throws PepException{
        
        assertValidResult();
        if (this.currentResult instanceof AzResult) {
            return ((AzResult)this.currentResult).
            	getAzResourceActionAssociation();
        }  else {
            return ((AzResourceActionAssociation)this.currentResult);
        }
    }
    
    /**
     * Return true, false, or throw an Exception based on the
     * configured PepResponseBehavior that is passed here to enforce
     * @param behavior
     * @return
     * @throws PepException
     */
    private boolean enforceBehavior(PepResponseBehavior behavior) 
    		throws PepException{
        if (behavior==PepResponseBehavior.RETURN_YES) {
            return true;    
        } else if (behavior==PepResponseBehavior.RETURN_NO) {
            return false;
        } else {
            this.log.error(
        		"Enforce behavior is throwing an exception.  ");
            	// rich: not sure why the following message was here
            	//  so I commented it out. It may have made sense for
            	//  an earlier version but no longer appears relevant:
            	// + "This means that the allowed() method was called " + 
                //"on an instance of the PepResponse that was not " + 
                //"created by the PepRequest.");
            throw new PepException(
        		"This Excecption is being thrown based on configured " +
        		"PepResponseBehavior for AzResult containing AzDecision " +
        		"with value of NotApplicable or Indeterminate");
        }
    }
    
    private void assertValidResult() throws PepException {
        if (this.currentResult==null) {
            throw new PepException(
        		"The current AzResult in this PepResponse is null.");
        }
    }
}

/**
 * Returns the information contained in the Obligations of this PepResponse
 * @return the key to the map is the <code>ObligationId</code>.  The value is another map that contains the
 * name of the attribute and the value of the attribute contained in that <code>Obligation</code>
 * @throws PepException
 */
/*
public Map<String,Map<String,String>> getObligations() throws PepException {
    
    assertValidResult();
        
    HashMap<String,Map<String,String>> obligations = 
    	new HashMap<String,Map<String,String>>();
    
    if (!(this.currentResult instanceof AzResult)) {
        
        this.log.warn("getObligations() method not supported on PepRequest.OPERATION.QUERY");
        return obligations;
    }
	
	AzResult azResult = (AzResult)this.currentResult;
	AzObligations azObligations = azResult.getAzObligations();
    
    if (azObligations!=null) {
        log.info("process obligations");
        Iterator<AzEntity<AzCategoryIdObligation>> obligationsIt =
            azObligations.iterator();
        while (obligationsIt.hasNext()) {
            AzEntity<AzCategoryIdObligation> azObligation =
                obligationsIt.next();
            log.info("read next obligation");
            Obligation obligation = 
            	this.getResponseFactory().getObligationFactory().
            		createObject(azObligation);
            String name = obligation.getWrappedObject().
            		getAzEntityId();
            log.info("ObligationId = " + name);
            Map<String,String> values = 
            	obligation.getStringValues();
            obligations.put(name,values);
        }
    }
    else {
    	log.info("No Obligations returned in PepResponse");
    }
     
    return obligations;
}
*/
