package org.openliberty.openaz.pdp.resources;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdString;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;
import org.openliberty.openaz.pdp.provider.AzServiceFactory;

public class OpenAzResourceQueryBuilder {
	
	static Log log = LogFactory.getLog(OpenAzResourceQueryBuilder.class);

	// dummy defns of resource type identifier and value;
	// this goes along with the TestResourceCollection
	public static String OPENAZ_ATTR_RESOURCE_TYPE =
		"urn:openaz:names:xacml:1.0:resource:resource-type";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		OpenAzResourceQueryBuilder rqb =
			new OpenAzResourceQueryBuilder();
		if (log.isTraceEnabled()) log.trace(
				"Class = " + OpenAzResourceQueryBuilder.class);
		if (log.isTraceEnabled()) log.trace(
				"What is printed?");
		
	}
    /**
     * Local method used by query and queryVerbose to set up
     * the AzRequestContext based on the scope of resources,
     * by adding an AzEntity<AzCategoryIdResource> and an
     * AzEntity<AzCategoryIdAction> for each resource-action
     * pair returned by the configured resource directory.
     * In addition, for Resource, both a resource-type AzAttribute
     * and a resource-id AzAttribute are added the AzEntity. For
     * the Action, only an action-id attribute is added. This
     * behavior of the specific attributes added is specific to
     * this implementation, but it is expected most implementations
     * will want to do something similar. It is an example of where
     * coordination is needed between the policies that are developed
     * and the requests that are expected to be submitted.
     * @param scope
     * @param azRequestContext
     * @return the input AzRequestContext updated with scoped pairs
     */
	public static AzRequestContext getScopedAzRequestContext(
			String scope, 
			AzRequestContext azRequestContext){

    	// TODO: this is placeholder for now; when multi component
		// query string support is added in a more substantive
		// manner, then it will need to be configurable,
		// probably by resource type.
		String scopeSeparatorChar = ",";

		// Get the configured AzResourceDirectory
    	OpenAzResourceDirectory azResourceDirectory =
    		AzServiceFactory.azResourceDirectory;

    	// Create a list to collect the resource-action pairs
    	// to be obtained from the AzResourceDirectory
    	List<List<String>> azResourceActionList = null;
    	
    	// Parse the scope into parts based on separator char.
    	// for now this should only be one part
    	if (log.isTraceEnabled()) log.trace("scope = " + scope);
    	String[] scopeArray = scope.split(scopeSeparatorChar);
    	List<String> scopeParts = new ArrayList<String>();
    	
    	// It is assumed that the first "part" of the scope
    	// will be the resource type, and if there is only
    	// one part, then that is the resource type and is
    	// equivalent to a query for all resources of that type.
    	String scopeResourceType = null;
    	if ( !(scopeArray == null) ) {
    		// get rid of whitespace at front and end of the "part"
    		// but a "part" may have enclosed white space
        	for (String scopePart : scopeArray){
        		scopeParts.add(scopePart.trim());
        	}
        	if (log.isTraceEnabled()) log.trace(
        			"scopeParts[len=" + scopeArray.length + 
        			"] = " + scopeParts);
        	// if any parts were found
        	if ( (scopeArray.length > 0 ) ) {
        		scopeResourceType = scopeParts.get(0);
        		// if scope is only the resource type
        		if ( (scopeArray.length==1) ) {
        			// get list of all resources of that type from
        			// the AzResourceDirectory
        			azResourceActionList = 
        				azResourceDirectory.getListByType(
        					scopeResourceType);
        			if (log.isDebugEnabled()) log.debug(
        				"azResourceActionList.size() = " +
        					azResourceActionList.size());
    			/*
    			// TODO: decide how to handle more detailed scope
    			// than just get all resources of specific resource=type
        		} else if (scopeArray.length==2) {
        			azResourceActionList =
        				azResourceDirectory.getListByInstance(
        					scopeParts.get(0),
        					new Integer(
        						scopeParts.get(1)).intValue());
				*/
        		} else {
        			log.info("unknown parameters in scope argument" +
        				"(limit is 2 parameters): " + scopeParts);
        			return null;
        		}
        		
        	}
    		
    	} else {
    		log.info("scope returned null resources");
    		return null;
    	}
    	if (log.isTraceEnabled()) log.trace(
    			"azResourceActionList = " + 
    			azResourceActionList);
    	
    	// Create variables for Resource and Action AzEntity's
    	AzEntity<AzCategoryIdResource> azResource = null;
    	AzEntity<AzCategoryIdAction> azAction = null;
    	for ( List<String> azResourceActionPair : azResourceActionList ) {
    		// create an AzEntity<AzCategoryIdResource> with a
    		// resource-id AzAttribute and a
    		// resource-type AzAttribute
    		azResource = azRequestContext.createAzEntity(
    				AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE);
    		azResource.createAzAttribute(
				"org.openliberty.openaz.pdp.resources.TestResourceCollection",
				AzXacmlStrings.X_ATTR_RESOURCE_ID,
				azResource.createAzAttributeValue(
    				AzDataTypeIdString.AZ_DATATYPE_ID_STRING, 
    				azResourceActionPair.get(0)));
    		azResource.createAzAttribute(
				"org.openliberty.openaz.pdp.resources.TestResourceCollection",
				OPENAZ_ATTR_RESOURCE_TYPE, 
				azResource.createAzAttributeValue(
    				AzDataTypeIdString.AZ_DATATYPE_ID_STRING, 
    				scopeResourceType));
    		// create an AzEntity<AzCategoryIdAction> with an
    		// action-id AzAttribute
    		azAction = azRequestContext.createAzEntity(
    				AzCategoryIdAction.AZ_CATEGORY_ID_ACTION);
    		azAction.createAzAttribute(
				"org.openliberty.openaz.pdp.resources.TestResourceCollection",
				AzXacmlStrings.X_ATTR_ACTION_ID,
				azAction.createAzAttributeValue(
					AzDataTypeIdString.AZ_DATATYPE_ID_STRING, 
					azResourceActionPair.get(1)));
    		// add this resource action association to context
    		azRequestContext.createAndAddResourceActionAssociation(
    				azResource, azAction);
    	}
		return azRequestContext;
	}

}
