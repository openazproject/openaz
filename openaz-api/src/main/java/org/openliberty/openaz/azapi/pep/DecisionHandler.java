package org.openliberty.openaz.azapi.pep;

import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.openliberty.openaz.azapi.constants.PepRequestOperation;

/**
 * Interface for the main decide method that takes request objects
 * submitted as collections of attributes and returns response
 * objects from the returned decisions and obligation attributes.
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public interface DecisionHandler {

	/**
	 * Returns a PepResponse containing either a single or
	 * multiple decisions based on the {@link PepRequestOperation}
	 * that was used to create the {@link PepRequest}
	 * by the {@link PepRequestFactory}
	 * @param request
	 * @return a {@link PepResponse} object
	 * @throws PepException
	 */
    public PepResponse decide(PepRequest request) 
    	throws PepException;
    

}
