package org.openliberty.openaz.azapi.pep;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.pep.ResponseAttributes;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;
import org.openliberty.openaz.azapi.constants.AzCategoryIdStatusDetail;
/**
 * This is primarily an internal interface used to create a
 * {@link ResponseAttributes} Object with a specific 
 * AzCategoryId as indicated by the generic parameter &lt;T&gt;.
 * @param &lt;T&gt;
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public interface ResponseAttributesFactory<T extends Enum<T> & AzCategoryId> {

	/**
	 * Create a {@link ResponseAttributes} Object containing an
	 * {@link AzEntity} of type {@link AzCategoryId} generic 
	 * parameter &lt;T&gt;.
	 * This method is used to wrap response attribute
	 * collections of types {@link AzCategoryIdObligation}
	 * and {@link AzCategoryIdStatusDetail}
	 * <p>
	 * It is expected that implementers of post-decision handlers
	 * may do pre-processing of Obligation, handling of missing
	 * attributes, and handling of future enhancements to 
	 * obligations such as families and advice planned for 
	 * xacml 3.0.
	 * <p>
	 * @param entity an AzEntity attribute collection object
	 * @return a {@link ResponseAttributes} object
	 */
	public ResponseAttributes<T> 
			createObject(AzEntity<T> entity);

}
