package org.openliberty.openaz.azapi.pep;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;

/**
 * A factory that can be used to create Obligation objects
 * that wrap {@link AzEntity}<{@link AzCategoryIdObligation}>
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public interface ObligationFactory{

    /** 
     * Creates an {@link Obligation} object based on the 
     * underlying {@link AzEntity}.
     */
	public Obligation createObject(AzEntity<AzCategoryIdObligation> entity);
}
