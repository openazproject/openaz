package org.openliberty.openaz.pep;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.RequestAttributesFactory;

/**
 * This class is a non-generic facade for 
 * AzEntity<AzCategoryIdResources> 
 * @author rlevinson,jbregman,pmishra
 *
 */
public class Resource extends 
		RequestAttributesImpl<AzCategoryIdResource>{
    
    public Resource(
    		AzEntity<AzCategoryIdResource> wrapperObject, 
    		PepRequest ctx, 
    		RequestAttributesFactory<AzCategoryIdResource> factory) {
        super(wrapperObject,ctx,factory);
    }
    
    
}
