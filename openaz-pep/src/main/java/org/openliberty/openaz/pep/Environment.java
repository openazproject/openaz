package org.openliberty.openaz.pep;

import java.util.Date;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.RequestAttributesFactory;

/**
 * This class is a non-generic facade for 
 * AzEntity<AzCategoryIdEnvironment> 
 * @author rlevinson,jbregman,pmishra
 *
 */
public class Environment extends 
			RequestAttributesImpl<AzCategoryIdEnvironment> {
    public Environment(
    		AzEntity<AzCategoryIdEnvironment> env, 
    		PepRequest ctx, 
    		RequestAttributesFactory<AzCategoryIdEnvironment> factory) {
        super(env,ctx,factory);
    }
    
    public void setCurrentDateTime(Date d) {
        this.setAttribute(
        		AzXacmlStrings.X_ATTR_ENV_CURRENT_DATE_TIME,d);
    }
    

       
}
