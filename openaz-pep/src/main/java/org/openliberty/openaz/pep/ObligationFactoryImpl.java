package org.openliberty.openaz.pep;

import org.openliberty.openaz.azapi.pep.ObligationFactory;
import org.openliberty.openaz.azapi.pep.Obligation;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;

/**
 * ObligationFactory creates an instance of 
 * {@link AzEntity}<{@link AzCategoryIdObligation}>, which is 
 * an AzApi collection that can contain attributes to
 * represent the Obligation entity in a Xacml Response.
 * <p>
 * @author rlevinson,jbregman,pmishra
 *
 */
public class ObligationFactoryImpl 
	extends ResponseAttributesFactoryImpl<AzCategoryIdObligation>
	implements ObligationFactory{

    public ObligationFactoryImpl() {
        super();
    }

    /** 
     * Creates an {@link Obligation} object based on the 
     * underlying {@link AzEntity}.
     */
    public Obligation createObject(AzEntity<AzCategoryIdObligation> entity) {
        return (Obligation) new ObligationImpl(entity);
    }
}
