package org.openliberty.openaz.pep;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.pep.PepRequest;

/**
 * ActionFactory creates an instance of 
 * {@link AzEntity}<{@link AzCategoryIdAction}>, which is 
 * an AzApi collection that can contain attributes to
 * represent the Action entity in a Xacml Request.
 * 
 * @author rlevinson,jbregman,pmishra
 *
 */
public class ActionFactory 
	extends RequestAttributesFactoryImpl<AzCategoryIdAction> {

    public Action createObject(PepRequest pepRequestContext) {
        
        AzRequestContext ctx = pepRequestContext.getAzRequestContext();
        // Get a Resource Category and AzEntity
        AzEntity<AzCategoryIdAction> azAction = 
        	ctx.createAzEntity(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION); // [a31]
        
        ctx.addAzEntity(azAction);
        
        return  new Action(azAction,pepRequestContext,this);
    }
}
