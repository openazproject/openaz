package org.openliberty.openaz.azapi.pep;

import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepResponse;

/**
 * An interface that may be implemented to process the 
 * PepResponse that is returned from the main decide()
 * call before the final results are returned to the user.
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public interface PostDecisionHandler {
    
	/** 
	 * This method is used to apply post-decision custom
	 * processing to the {@link PepResponse} after it has
	 * been returned from the {@link PepRequest#decide()}
	 * method.
	 * @param request
	 * @throws PepException
	 */
    public void postDecide(PepRequest request, PepResponse response) 
    	throws PepException;
    
}
