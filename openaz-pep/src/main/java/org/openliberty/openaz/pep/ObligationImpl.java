package org.openliberty.openaz.pep;

import java.util.HashMap;
import java.util.Map;
import java.util.Iterator;

import org.openliberty.openaz.azapi.pep.Obligation;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzAttribute;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;

/**
 * The Obligation object contains a set of zero or more
 * Attributes. The Obligation has an id: ObligationId, and
 * each attribute has an id, as well, which is the first String of
 * each String pair in the set of attributes.
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public class ObligationImpl 
	extends	ResponseAttributesImpl<AzCategoryIdObligation> 
	implements Obligation {  

	/**
	 * Obligation constructor: super's up to AttributesImpl, which
	 * holds reference to the AzEntity parameter.
	 * 
	 * @param obligation
	 */
    ObligationImpl(AzEntity<AzCategoryIdObligation> obligation) {
        super(obligation);
    }  
    
    /**
     * Return the ObligationId for this Obligation.
     * This is from the underlying AzEntity.getAzEntityId()
     * which is used to store the ObligationId.
     * @return a string containing the ObligationId of this Obligation
     */
    public String getObligationId(){
    	return this.getAzEntity().getAzEntityId();
    }
    
    /**
     * Returns a Set of Obligation Attribute name,value pairs,
     * indexed by name.
     * @return a map of Obligation Attribute name,value pairs
     */
    public Map<String,String> getStringValues() {
     
        //TODO: Implement this 
    	HashMap<String,String> obligationAttrs = 
    		new HashMap<String,String>();
    	AzEntity<AzCategoryIdObligation> azObligation =
    		this.getAzEntity();
    	Iterator<AzAttribute<?>> it = 
    		azObligation.getAzAttributeMixedSet().iterator();
    	while (it.hasNext()) {
    		AzAttribute<?> azAttr = it.next();
    		obligationAttrs.put(azAttr.getAttributeId(), 
    			azAttr.getAzAttributeValue().getValue().toString());
    	}
        return obligationAttrs;   
    }
}
