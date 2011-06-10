package org.openliberty.openaz.azapi.constants;

import org.openliberty.openaz.azapi.AzResourceActionAssociation;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepRequestFactory;
import org.openliberty.openaz.azapi.pep.PepResponse;
/**
 * An enum that indicates the type of {@link PepRequestOperation}
 *  being issued.
 */
public enum PepRequestOperation {
	/** The {@link PepRequest} will return only a single decision 
	 * for a single resource action association 
	 * {@link AzResourceActionAssociation}
	 */
	DECIDE,			// For single decisions
	/** The {@link PepRequest} will return one or more decisions on
	 * a set of resource action associations 
	 */
	BULK_DECIDE,	// For multiple decisions
	/** The {@link PepRequest} will return one of more decisions
	 * for a set of allowed resource action associations or
	 * a set of denied resource action associations depending
	 * on the value of the (@link PepRequestQueryType} submitted
	 * with the {@link PepRequestFactory#newQueryPepRequest}
	 */
	QUERY,
	/** The {@link PepRequest} will return a {@link PepResponse} 
	 * with a full result (including all status and obligations)
	 * for each resource action association 
	 * {@link AzResourceActionAssociation} requested.
	 */
	QUERY_VERBOSE
}
