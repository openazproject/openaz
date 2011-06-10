package org.openliberty.openaz.azapi.constants;

/**
 * An enum that indicates the type of {@link PepRequestQueryType}
 *  being issued.
 */
public enum PepRequestQueryType {
	/** The PepResponse returned for this query type will contain
	 * all the full results for each resource action association
	 * requested
	 */
	VERBOSE, 
	/** The PepResponse returned for this query type will contain
	 * only the list of resource action associations that are
	 * allowed.
	 */
	RETURN_ONLY_ALLOWED_RESULTS, 
	/**
	 * The PepResponse returned for this query type will contain
	 * only the list of resource action associations that are
	 * denied.
	 */
	RETURN_ONLY_DENIED_RESULTS;
}
