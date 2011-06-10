package org.openliberty.openaz.pep;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;
import org.openliberty.openaz.azapi.pep.PepRequest;

/**
 * ResourceFactory creates an instance of 
 * {@link AzEntity}<{@link AzCategoryIdResource}>}, which is 
 * an AzApi collection that can contain attributes to
 * represent the Resource entity in a Xacml Request.
 * 
 * @author rlevinson,jbregman,pmishra
 *
 */
public class ResourceFactory  
			extends RequestAttributesFactoryImpl<AzCategoryIdResource> {

	public ResourceFactory() {
        super();
    }

    public Resource createObject(PepRequest pepRequestContext) {
        
        AzRequestContext ctx = pepRequestContext.getAzRequestContext();
        // Get a Resource Category and AzEntity
        AzEntity<AzCategoryIdResource> azResource = 
        	ctx.createAzEntity(AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE); 
        
        ctx.addAzEntity(azResource);
       
        return new Resource(azResource,pepRequestContext,this);
        
    }
}
