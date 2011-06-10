package org.openliberty.openaz.azapi.pep;

import java.util.Set;

import org.openliberty.openaz.azapi.constants.PepRequestOperation;
import org.openliberty.openaz.azapi.constants.PepResponseBehavior;
import org.openliberty.openaz.azapi.constants.AzStatusCode;
import org.openliberty.openaz.azapi.constants.AzDecision;
import org.openliberty.openaz.azapi.pep.ObligationFactory;
import org.openliberty.openaz.azapi.AzResourceActionAssociation;
import org.openliberty.openaz.azapi.AzResponseContext;

/**
 * Factory for creating and configuring <code>PepResponse</code>.
 * <br>  
 * This class creates {@link PepResponse} objects and configures
 * the behavior of how the <code>PepResponse</code> interprets the 
 * results from the AzService.  
 * <br>
 * The {@link PepResponseBehavior} can either be to 
 * <ul>
 * <li>
 * return true (PERMIT: {@link PepResponseBehavior#RETURN_YES}), 
 * <li>
 * return false (DENY: {@link PepResponseBehavior#RETURN_NO}), 
 * <li>
 * or throw an exception (DENY: {@link PepResponseBehavior#THROW_EXCEPTION}).
 * </ul>
 * <p>
 * In general, a Permit returns true, and a Deny returns false, 
 * but there are also other types of returns, including 
 * NotApplicable and Indeterminate. The configuration is to 
 * specify for each of the 4 xacml-defined conditions, what 
 * the behavior will be. i.e. for each of the "special" 
 * conditions there is a choice to return either true (Permit), 
 * false (Deny), or throw an Exception.
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public interface PepResponseFactory {
	
	/**
	 * Create a {@link PepResponse} based on the 
	 * {@link AzResponseContext}	
	 * @param responseContext
	 * @param pepRequest
	 * @param operation
	 * @return a {@link PepResponse} object
	 */
	public PepResponse createPepResponse(
		   		AzResponseContext responseContext,
				PepRequest pepRequest,
				PepRequestOperation operation);
	
	/**
	 * Create a {@link PepResponse} based on the set of
	 * returned {@link AzResourceActionAssociation} objects.
	 * @param actionResourceAssociations
	 * @param pepRequest
	 * @param queryAllowed
	 * @return a {@link PepResponse} object
	 */
	public PepResponse createPepResponse(
		    Set<AzResourceActionAssociation> actionResourceAssociations,
		    PepRequest pepRequest,
	        boolean queryAllowed);
	
	/**
	 * Get the {@link PepResponseBehavior} that has been
	 * set to be applied when an indeterminate due to
	 * missing attributes is returned.
	 * <br>
	 * i.e. when the underlying {@link AzDecision} is
	 * {@link AzDecision#AZ_INDETERMINATE} 
	 * <br>
	 * and the underlying {@link AzStatusCode}
	 * is {@link AzStatusCode#AZ_MISSING_ATTRIBUTE}.
	 * @return a {@link PepResponseBehavior} enum value
	 */
	public PepResponseBehavior getMissingAttributeBehavior();
	
	/**
	 * Get the {@link PepResponseBehavior} that has been
	 * set to be applied when not-applicable response 
	 * is returned.
	 * <br>
	 * i.e. when the underlying {@link AzDecision} is
	 * {@link AzDecision#AZ_NOTAPPLICABLE} 
	 * @return a {@link PepResponseBehavior} enum value
	 */
	public PepResponseBehavior getNotApplicableBehavior();
	
	/**
	 * Get the {@link PepResponseBehavior} that has been
	 * set to be applied when an indeterminate due to
	 * processing error is returned.
	 * <br>
	 * i.e. when the underlying {@link AzDecision} is
	 * {@link AzDecision#AZ_INDETERMINATE} 
	 * <br>
	 * and the underlying {@link AzStatusCode}
	 * is {@link AzStatusCode#AZ_PROCESSING_ERROR}.
	 * @return a {@link PepResponseBehavior} enum value
	 */
	public PepResponseBehavior getProcessingErrorBehavior();
	
	/**
	 * Get the {@link PepResponseBehavior} that has been
	 * set to be applied when an indeterminate due to
	 * syntax error is returned.
	 * <br>
	 * i.e. when the underlying {@link AzDecision} is
	 * {@link AzDecision#AZ_INDETERMINATE} 
	 * <br>
	 * and the underlying {@link AzStatusCode}
	 * is {@link AzStatusCode#AZ_SYNTAX_ERROR}.
	 * @return a {@link PepResponseBehavior} enum value 
	 */
	public PepResponseBehavior getSyntaxErrorBehavior();
	
	/**
	 * Set the behavior to one of those described in 
	 * {@link PepResponseBehavior} when
	 * an (@link AzStatusCode#AZ_MISSING_ATTRIBUTE}
	 * decision is returned from the 
	 * underlying AzApi implementation. 
	 * @param missingAttributeBehavior
	 */
	public void setMissingAttributeBehavior(PepResponseBehavior missingAttributeBehavior);
	
	/**
	 * Set the behavior to one of those described in 
	 * {@link PepResponseBehavior} when
	 * an {@link AzDecision#AZ_NOTAPPLICABLE} 
	 * decision is returned from the 
	 * underlying AzApi implementation. 
	 * @param notApplicableBehavior
	 */
	public void setNotApplicableBehavior(PepResponseBehavior notApplicableBehavior);
	
	/**
	 * Set the behavior to one of those described in 
	 * {@link PepResponseBehavior} when
	 * an (@link AzStatusCode#AZ_PROCESSING_ERROR}
	 * decision is returned from the 
	 * underlying AzApi implementation. 
	 * @param processingErrorBehavior
	 */
	public void setProcessingErrorBehavior(
			PepResponseBehavior processingErrorBehavior);

	/**
	 * Set the behavior to one of those described in 
	 * {@link PepResponseBehavior} when
	 * an (@link AzStatusCode#AZ_SYNTAX_ERROR}
	 * decision is returned from the 
	 * underlying AzApi implementation. 
	 * @param syntaxErrorBehavior
	 */
	public void setSyntaxErrorBehavior(
			PepResponseBehavior syntaxErrorBehavior);

   	/** 
   	 * Get an {@link ObligationFactory} that can be used
   	 * to create Obligations for this response.
   	 * @return an {@link ObligationFactory} object
   	 */
    public ObligationFactory getObligationFactory();
}
