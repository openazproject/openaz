package org.openliberty.openaz.pdp.provider;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.AzAttributeFinder;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.AzResourceActionAssociation;
import org.openliberty.openaz.azapi.AzResourceActionAssociationId;
import org.openliberty.openaz.azapi.AzResponseContext;
import org.openliberty.openaz.azapi.AzResult;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;
import org.openliberty.openaz.azapi.constants.AzCategoryIdStatusDetail;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdString;
import org.openliberty.openaz.azapi.constants.AzDecision;
import org.openliberty.openaz.azapi.constants.AzStatusCode;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;
import org.openliberty.openaz.pdp.AbstractService;
import org.openliberty.openaz.pdp.AzEntityImpl;
import org.openliberty.openaz.pdp.AzStatusMessagesImpl;

import org.openliberty.openaz.pdp.resources.OpenAzResourceDirectory;
import org.openliberty.openaz.pdp.resources.OpenAzResourceQueryBuilder;
import org.openliberty.openaz.pdp.resources.OpenAzTestResourceCollection;

public class SimpleConcreteDummyService extends AbstractService {
	Log log = LogFactory.getLog(this.getClass());
	
	// dummy defns of resource type identifier and value
	public static String OPENAZ_ATTR_RESOURCE_TYPE =
		"urn:openaz:names:xacml:1.0:resource:resource-type";
	 
	public <T extends Enum<T> & AzCategoryId>
		void registerAzAttributeFinder(
			AzAttributeFinder<T> azAttributeFinder){
	
	}

   public AzResponseContext decide(AzRequestContext azReqCtx)
    {
        System.out.println(this.getClass().getName() + ".decide(): ");
        // Create a Response Context to store the results
        AzResponseContextImpl rspCtx = new AzResponseContextImpl();
        
        // Create the first AzResult which must be prepared for
        // return in case there are no AzResourceActionAssociations
        // to process.
        AzResultImpl azResult = new AzResultImpl();  
        
        AzResourceActionAssociation azAssoc = null;
        AzResourceActionAssociationId azAssocId = null;
        Iterator<AzResourceActionAssociation> it = 
        	azReqCtx.getAssociations().iterator();
        if (!it.hasNext()){
            // Set the null AssociationId case to be processing error
            azResult.setAzDecision(AzDecision.AZ_INDETERMINATE);
            azResult.setAzStatusCode(AzStatusCode.AZ_SYNTAX_ERROR);
            azResult.setStatusMessage(
        		AzStatusMessagesImpl.NO_RESOURCE_ACTION_ASSOCIATION);
            rspCtx.addResult(azResult);
        }
        while (it.hasNext()){
            azAssoc = it.next();
            azAssocId = azAssoc.getAzResourceActionAssociationId();
            System.out.println("\t ResourceId: " + azAssocId.getResourceId() +
                            "\t  ActionId: " + azAssocId.getActionId());
            azResult = new AzResultImpl();
            azResult.setAzResourceActionAssociation(azAssoc);
            azResult.setAzDecision(getAzDecisionValue());
            azResult.setAzStatusCode(getStatusCode());
            azResult.setStatusMessage(getStatusMessage());
            System.out.println("    TestAzService.decide: " +
                            "\n\t getStatusMessage: " + getStatusMessage());
            switch (azResult.getAzDecision()) {
                case AZ_PERMIT:
                    AzObligationsImpl azObligations = new AzObligationsImpl();
                    AzEntity<AzCategoryIdObligation> azObligation = 
                        azReqCtx.createNewAzEntity(
                          AzCategoryIdObligation.AZ_CATEGORY_ID_OBLIGATION);
                    // As the AzService provider, cast to local impl 
                    // to set ObligationId
                    ((AzEntityImpl<AzCategoryIdObligation>)azObligation).
                    	setAzEntityId("This.is.an.ObligationId");
                    azObligation.createAzAttribute(
                        "PDP-Issuer",            // issuer
                        "Obl-attr-id-test-obl",  // attr-id
                        azObligation.createAzAttributeValue(
                            AzDataTypeIdString.AZ_DATATYPE_ID_STRING,
                            "PEP is obligated to roll out " +
                            "the red carpet"));
                    azObligation.createAzAttribute(
                        "PDP-Issuer",              // issuer
                        "Obl-attr-id-test-obl-2",  // attr-id
                        azObligation.createAzAttributeValue(
                            AzDataTypeIdString.AZ_DATATYPE_ID_STRING,
                        	"PEP is obligated to roll out " +
                        	"the green carpet, too"));
                    azObligations.addAzObligation(azObligation);
                    azResult.setAzObligations(azObligations);
                    Iterator<AzEntity<AzCategoryIdObligation>> itOb = 
                    	azResult.getAzObligations().iterator();
                    System.out.println("TestAzService.decide: " +
                    		"check itOb.hasNext() = " + itOb.hasNext());
                    break;
                case AZ_DENY:
                    //TBD: put a test case for Deny here.
                    break;
                case AZ_NOTAPPLICABLE:
                    //TBD: put a test case for NotApplicable here.
                    break;
                case AZ_INDETERMINATE:
                    switch (azResult.getAzStatusCode()){
                        case AZ_SYNTAX_ERROR:
                            // TODO: put a test case for 
                        	// Indeterminate.SyntaxError here.
                            break;
                        case AZ_PROCESSING_ERROR:
                            //TODO: put a test case for 
                        	// Indeterminate.ProcessingError here.
                            break;
                        case AZ_MISSING_ATTRIBUTE:
                            AzEntity<AzCategoryIdStatusDetail> 
                              azStatusDetail = azReqCtx.createNewAzEntity(
                                AzCategoryIdStatusDetail.
                                	AZ_CATEGORY_ID_STATUSDETAIL);
                            azStatusDetail.createAzAttribute(
                              "PDP-issuer",
                              "MissingAttribute-01-AttributeId",
                              azStatusDetail.createAzAttributeValue(
                                AzDataTypeIdString.AZ_DATATYPE_ID_STRING,
                                "This is the value needed for this " +
                                "missing attribute"));
                            azResult.setAzStatusDetail(azStatusDetail);
                            break;
                    } // end inner switch                                       
            } // end outer switch                       
            rspCtx.addResult(azResult);
        }
        return rspCtx;
    }
    
    /**
     * Dummy PDP to return a mix of AzDecisions.
     * No need to make public.
     */
    int counter = 0;
    AzDecision getAzDecisionValue(){
        counter++;
        System.out.println("TestAzService: counter = " + counter);
        int modCtr = counter % 6;
        switch (modCtr){
            case(1): return AzDecision.AZ_PERMIT;
            case(2): return AzDecision.AZ_NOTAPPLICABLE;
            case(3): return AzDecision.AZ_INDETERMINATE;
            case(4): return AzDecision.AZ_INDETERMINATE;
            case(5): return AzDecision.AZ_INDETERMINATE;
        }
        return AzDecision.AZ_DENY;
    }
    String getStatusMessage() {
        String statMsg = null;
        int modCtr = counter % 6;
        switch (modCtr){
            case(0): return statMsg = AzStatusMessagesImpl.AZMSG_DENY; 
            case(1): return statMsg = AzStatusMessagesImpl.AZMSG_PERMIT; 
            case(2): return statMsg = AzStatusMessagesImpl.AZMSG_NOTAPPLICABLE;
            case(3): return statMsg = AzStatusMessagesImpl.AZMSG_INDETERMINATE_MISSING_ATTR;
            case(4): return statMsg = AzStatusMessagesImpl.AZMSG_INDETERMINATE_SYNTAX_ERROR;
            case(5): return statMsg = AzStatusMessagesImpl.AZMSG_INDETERMINATE_PROCESSING_ERROR;
        }
        return statMsg;
    }
    AzStatusCode getStatusCode() {
            int modCtr = counter % 6;
            switch (modCtr){
                    case(1): return AzStatusCode.AZ_OK;
                    case(2): return AzStatusCode.AZ_OK;
                    case(3): return AzStatusCode.AZ_MISSING_ATTRIBUTE;
                    case(4): return AzStatusCode.AZ_PROCESSING_ERROR;
                    case(5): return AzStatusCode.AZ_SYNTAX_ERROR;           
            }
            return AzStatusCode.AZ_OK;
    }
        
    /*
     * Returns the set of AzResourceActionAssociations that are allowed 
     * on the specified input AzResource.
     * 
     * @param azResource
     * @param azRequestContext only the AzEnvironment and AzSubject 
     * entities will be considered in the evaluation 
     * @return
     */
    //public Set<AzResourceActionAssociation> whatIsAllowed(
	//			AzResource azResource, 
	//			AzRequestContext azRequestContext){
    //      Set<AzResourceActionAssociation> azResActAssoc = null;
    //      return azResActAssoc;
    //}
    
    
    /*
     * Returns the set of AzResourceActionAssociations that are either
     * allowed or denied based on the scope parameter and the 
     * allowedNotAllowed parameter.
     * <p>
     * Basically, what is required here is that the "scope" get resolved
     * into a set of AzResourecActionAssociation pairs and then each
     * is sent to the pdp for an individual request,
     * <p>
     * or the scope and user/environment info in the request can be
     * sent to the pdp and if policies are set up in a pre-defined
     * fashion to return scope info, then a minimal scope for the 
     * user can be returned which can then be used to query the
     * resource catalog for resource-action associations.
     * <p>
     * This class uses a test resource catalog, 
     * org.openliberty.openaz.pdp.resources.TestResourceCollection
     * 
     */
    public Set<AzResourceActionAssociation> query(
                    String scope, 
                    AzRequestContext azRequestContext,
                    boolean allowedNotAllowed){
    	
    	azRequestContext = 
    		OpenAzResourceQueryBuilder.getScopedAzRequestContext(
    			scope, azRequestContext);
    	
    	// Submit the request and process the results !?
    	AzResponseContext azResponseContext =
    		decide(azRequestContext);
    	//log.info("query not implemented yet - returns null");
    	// Pull the ResourceAction associations out of the response
    	Set<AzResourceActionAssociation> azResActAssoc = null;
    	Set<AzResult> azResults = null;
    	if ( ! (azResponseContext == null) ) {
    		azResults = azResponseContext.getResults();
    		if ( ! ( azResults == null ) ) {
    			azResActAssoc = 
    				new HashSet<AzResourceActionAssociation>();
    			for ( AzResult azResult : azResults ) {
    				// if want only permits only add permits to set
    				if (allowedNotAllowed) {
	    				if ( azResult.getAzDecision() == 
	    						AzDecision.AZ_PERMIT) {
	    					azResActAssoc.add(azResult.
	    						getAzResourceActionAssociation());	    					
	    				}
	    			// otherwise only add denies to the set
    				} else {
    					if ( azResult.getAzDecision() == 
    							AzDecision.AZ_DENY) {
    						azResActAssoc.add(azResult.
    							getAzResourceActionAssociation());
    					}
    				}
    			}
    		}
    	}
        return azResActAssoc;
    }

    public AzResponseContext queryVerbose(
                    String scope, AzRequestContext azRequestContext){
    	log.info("queryVerbose - returns all results");
    	azRequestContext = 
    		OpenAzResourceQueryBuilder.getScopedAzRequestContext(
    			scope, azRequestContext);
    	// Submit the request and process the results !?
    	AzResponseContext azResponseContext =
    		decide(azRequestContext);
        return azResponseContext;
    }   
}
