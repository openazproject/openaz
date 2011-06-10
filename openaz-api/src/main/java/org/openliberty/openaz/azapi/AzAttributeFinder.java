package org.openliberty.openaz.azapi;

import java.util.Collection;

import org.openliberty.openaz.azapi.AzService;
import org.openliberty.openaz.azapi.constants.AzCategoryId;

/**
 * This is a callback interface to enable users of azapi
 * to implement capabilities to obtain AzAttributes requested
 * by the PDP during evaluation of an 
 * {@link AzService#decide(AzRequestContext azReqCtx)}
 * method.
 * 
 * @author rlevinson
 *
 */
public interface AzAttributeFinder<T extends Enum<T> & AzCategoryId> {

	/**
	 * Finds one or more AzAttributes that match the criteris
	 * specified in the parameters.
	 * <p>
	 * When an {@link AzService#decide(AzRequestContext azReqCtx)} 
	 * call is invoked, if the underlying azapi implementation needs 
	 * to get more information from the calling environment, it has
	 * the option of invoking any registered {@link AzAttributeFinder} 
	 * callback to request more information.
	 * 
	 * @param azReqCtx a reference to the AzRequestContext that the 
	 * azapi is working on when the callback is invoked.
	 * @param azEntity the AzEntity within the AzRequestContext for
	 * which the additional attribute is being requested.
	 * @param azAttribute an AzAttribute with the metadata necessary
	 * to specify AttributeId, Issuer, etc., however, within
	 * the AzAttributeValue, only the AzDataType is set w corresponding
	 * value set to null.
	 * 
	 * @return a Collection<AzAttribute> containing one or more AzAttribute
	 * objects, each with the same metadata, but possibly multiple
	 * values, similar to a XACML Bag.
	 */
	public Collection<AzAttribute<T>> findAzAttribute(
				AzRequestContext azReqCtx,
				AzEntity<T> azEntity,
				AzAttribute<T> azAttribute);
}
