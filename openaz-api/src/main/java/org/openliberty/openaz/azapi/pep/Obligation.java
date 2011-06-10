package org.openliberty.openaz.azapi.pep;

import java.util.Map;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;

/**
 * The Obligation interface provides access to an Obligation
 * object implementation that contains a set of zero or more
 * Attributes. The Obligation has an id: ObligationId. 
 * Each attribute has an id, as well, which is the first String 
 * of each String pair that represents each attribute in the 
 * Map that contains the attributes.
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public interface Obligation
	extends	ResponseAttributes<AzCategoryIdObligation>{
    /**
     * Return the ObligationId for this Obligation.
     * <p>
     * Note: The ObligationId is obtained from the underlying 
     * {@link AzEntity#getAzEntityId()}
     * which, for the {@link AzCategoryIdObligation} 
     * category, is used to hold the ObligationId.
     * @see Attributes for more info on AzEntity
     * @return a string containing the ObligationId of this Obligation
     */
    public String getObligationId();   
    /**
     * Returns a Set of Obligation Attribute name,value pairs,
     * indexed by name.
     * @return a map of Obligation Attribute name,value pairs
     */
    public Map<String,String> getStringValues();
}
