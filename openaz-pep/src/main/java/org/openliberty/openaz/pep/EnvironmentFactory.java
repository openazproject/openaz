package org.openliberty.openaz.pep;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;
import org.openliberty.openaz.azapi.pep.PepRequest;

/**
 * EnvironmentFactory creates an instance of 
 * {@link AzEntity}<{@link AzCategoryIdEnvironment}>, which is 
 * an AzApi collection that can contain attributes to
 * represent the Environment entity in a Xacml Request.
 * 
 * @author rlevinson,jbregman,pmishra
 *
 */
public class EnvironmentFactory 
	extends RequestAttributesFactoryImpl<AzCategoryIdEnvironment> {

    @Override
    public Environment createObject(PepRequest pepRequestContext) {
        
        //Some Comment asjdlkasd
        AzRequestContext ctx = pepRequestContext.getAzRequestContext();
        
        AzEntity<AzCategoryIdEnvironment> env = 
        	ctx.createAzEntity(
        		AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT);
        ctx.addAzEntity(env);     
        return new Environment(env,pepRequestContext,this);        
    }
}