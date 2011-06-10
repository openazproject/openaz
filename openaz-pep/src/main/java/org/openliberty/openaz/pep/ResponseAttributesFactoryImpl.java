package org.openliberty.openaz.pep;

import org.openliberty.openaz.azapi.pep.RequestAttributes;
import org.openliberty.openaz.azapi.pep.ResponseAttributesFactory;
import org.openliberty.openaz.azapi.pep.ResponseAttributes;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;

/**
 * A factory to use to create an {@link ResponseAttributes} that
 * contains the underlying AzEntity obligation attribute collections
 * that are contained in the PepRequest.
 * 
 * @author rlevinson, jbregman, pmishra
 *
 * @param <T> an entity response identifier:  {@link AzCategoryIdObligation} 
 */
public abstract class ResponseAttributesFactoryImpl
		<T extends Enum<T> & AzCategoryId> 
		implements ResponseAttributesFactory<T> {
    
    public abstract ResponseAttributes<T> 
    			createObject(AzEntity<T> entity);
   
}
