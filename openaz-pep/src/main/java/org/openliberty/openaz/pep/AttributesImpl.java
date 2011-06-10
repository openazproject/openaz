package org.openliberty.openaz.pep;

import org.openliberty.openaz.azapi.pep.Attributes;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;

/**
 * Abstract wrapper for the attributes collections for each
 * entity or response type (Subject, Resource, Action, Environment,
 * Obligation)
 * @author rlevinson, jbregman, pmishra
 *
 * @param <T> an entity or response identifier: one of: 
 * {@link AzCategoryIdAction}, {@link AzCategoryIdEnvironment}, 
 * {@link AzCategoryIdResource}, {@link AzCategoryIdSubjectAccess}, 
 * {@link AzCategoryIdObligation}
 */
public abstract class AttributesImpl<T extends Enum<T> & AzCategoryId> 
		implements Attributes<T> {
    
    public AzEntity<T> wrappedObject; // for external mappers
    
    public AttributesImpl(AzEntity<T> wrappedObject) {
        super();
        this.wrappedObject = wrappedObject;
    }

    /**
     * Return the underlying {@link AzEntity} attribute 
     * collection that is contained by this object wrapper.
     */
    public AzEntity<T> getAzEntity() {        
        return this.wrappedObject;
    }

}


