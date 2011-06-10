package org.openliberty.openaz.azapi.pep;

import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.pep.PepRequest;

/**
 * An interface that can be used for preliminary processing
 * of a PepRequest before it is actually submitted to the 
 * main decide() method.
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public interface PreDecisionHandler {
    
	/** 
	 * This method is used to apply preliminary custom
	 * processing to the {@link PepRequest} prior to its
	 * being submitted with the {@link PepRequest#decide()}
	 * method.
	 * @param request
	 * @throws PepException
	 */
	public void preDecide(PepRequest request) 
    	throws PepException;
}
