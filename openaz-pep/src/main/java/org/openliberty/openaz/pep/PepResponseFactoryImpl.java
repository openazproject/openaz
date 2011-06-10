package org.openliberty.openaz.pep;

import java.util.Set;

import org.openliberty.openaz.azapi.pep.PepResponseFactory;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.ObligationFactory;
import org.openliberty.openaz.azapi.constants.PepRequestOperation;
import org.openliberty.openaz.azapi.constants.PepResponseBehavior;
import org.openliberty.openaz.azapi.AzResourceActionAssociation;
import org.openliberty.openaz.azapi.AzResponseContext;

/**
 * Factory for creating and configuring <code>PepResponse</code>.
 * <br>  
 * This class creates <code>PepResponse</code> objects and configures
 * the behavior of how the <code>PepResponse</code> interprets the 
 * results from the AzService.  
 * <br>
 * The <code>Behavior</code> can either be to 
 * <ul>
 * <li>
 * return true (PERMIT), 
 * <li>
 * return false (DENY) 
 * <li>
 * or throw an exception (DENY).
 * </ul>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public class PepResponseFactoryImpl 
		implements PepResponseFactory {
    
	private ObligationFactoryImpl obligationFactory = 
		new ObligationFactoryImpl();
   
	private PepResponseBehavior 
   				notApplicableBehavior = 
   					PepResponseBehavior.RETURN_NO;
	private PepResponseBehavior 
				missingAttributeBehavior = 
					PepResponseBehavior.RETURN_NO;
	private PepResponseBehavior 
   				syntaxErrorBehavior = 
   					PepResponseBehavior.THROW_EXCEPTION;
	private PepResponseBehavior 
   				processingErrorBehavior = 
   					PepResponseBehavior.THROW_EXCEPTION;
     
	/**
	 * Create a PepResponse from an AzResponseContext
	 */
    public PepResponse createPepResponse(
					   AzResponseContext responseContext,
					   PepRequest pepRequest,
					   PepRequestOperation operation) {       
        return new PepResponseImpl(
    		   		responseContext,
    		   		pepRequest, 
    		   		this,
    		   		operation);       
    }

    /**
     * Create a PepResponse for a query that has returned
     * a Set<AzResourceActionAssociation> as opposed to
     * decide() or queryVerbose() which return AzResponseContext
     */
    public PepResponse createPepResponse(
			Set<AzResourceActionAssociation> actionResourceAssociations,
			PepRequest pepRequest,
			boolean queryAllowed) {
	    return new PepResponseImpl(
					actionResourceAssociations,
					pepRequest, 
					this,
					queryAllowed);
    }

    protected void setObligationFactory(
    		ObligationFactoryImpl obligationFactory) {
        this.obligationFactory = obligationFactory;
    }

   	/** 
   	 * Get an {@link ObligationFactory} that can be used
   	 * to create Obligations for this response.
   	 * @return an ObligationFactory
   	 */
    public ObligationFactory getObligationFactory() {
        return obligationFactory;
    }
    
    /**
     * Set the behavior to one of those described in 
     * {@link PepResponseBehavior} when
     * a NotApplicable decision 
     * is returned from the underlying AzApi implementation. 
     * @param notApplicableBehavior
     */
    public void setNotApplicableBehavior(
    		PepResponseBehavior notApplicableBehavior) {
        this.notApplicableBehavior = notApplicableBehavior;
    }

    public PepResponseBehavior getNotApplicableBehavior() {
        return notApplicableBehavior;
    }

    /**
     * Set the behavior to one of those described in 
     * {@link PepResponseBehavior} when
     * an Indeterminate decision 
     * with a SyntaxError status code 
     * is returned from the underlying AzApi implementation. 
     * @param syntaxErrorBehavior
     */
    public void setSyntaxErrorBehavior(
    		PepResponseBehavior syntaxErrorBehavior) {
        this.syntaxErrorBehavior = syntaxErrorBehavior;
    }

    public PepResponseBehavior getSyntaxErrorBehavior() {
        return syntaxErrorBehavior;
    }

    /**
     * Set the behavior to one of those described in 
     * {@link PepResponseBehavior} when
     * an Indeterminate decision 
     * with a ProcessingError status code
     * is returned from the underlying AzApi implementation. 
     * @param processingErrorBehavior
     */
    public void setProcessingErrorBehavior(
    		PepResponseBehavior processingErrorBehavior) {
        this.processingErrorBehavior = processingErrorBehavior;
    }

    public PepResponseBehavior getProcessingErrorBehavior() {
        return processingErrorBehavior;
    }
    
    /**
     * Set the behavior to one of those described in 
     * {@link PepResponseBehavior} when 
     * an Indeterminate decision 
     * with a MissingAttribute status code
     * is returned from the underlying AzApi implementation. 
     * @param missingAttributeBehavior
     */
    public void setMissingAttributeBehavior(
    			PepResponseBehavior missingAttributeBehavior) {
        this.missingAttributeBehavior = missingAttributeBehavior;
    }

    public PepResponseBehavior getMissingAttributeBehavior() {
        return missingAttributeBehavior;
    }


}
