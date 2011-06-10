package org.openliberty.openaz.azapi.constants;

import org.openliberty.openaz.azapi.pep.PepResponseFactory;
import org.openliberty.openaz.azapi.pep.PepResponse;
/**
 * This enum provides the options that can be set using the
 * {@link PepResponseFactory} to determine the behavior when
 * {@link PepResponse#allowed()} is called AND the 
 * {@link AzDecision} is either Indeterminate or NotApplicable. 
 * <p>
 * The different conditions for which behavior is specified
 * and which ones can be configured are the following:
 * <ul>
 * <li> {@link AzDecision#AZ_PERMIT} allowed() always returns true
 * <li> {@link AzDecision#AZ_DENY} allowed() always returns false
 * <li> {@link AzDecision#AZ_NOTAPPLICABLE} allowed() is 
 * configurable with default behavior being {@link PepResponseBehavior#RETURN_NO}
 * <li> {@link AzDecision#AZ_INDETERMINATE} has specific 
 * AzStatusCode values, each of which is configurable as follows:
 * <ul>
 * <li> {@link AzStatusCode#AZ_MISSING_ATTRIBUTE} allowed() has
 * default behavior: {@link PepResponseBehavior#RETURN_NO}
 * <li> {@link AzStatusCode#AZ_PROCESSING_ERROR} allowed() has
 * default behavior: {@link PepResponseBehavior#THROW_EXCEPTION}
 * <li> {@link AzStatusCode#AZ_SYNTAX_ERROR} allowed() has
 * default behavior: {@link PepResponseBehavior#THROW_EXCEPTION}
 * </ul>
 * </ul>
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 */
public enum PepResponseBehavior {
	
	/** The behavior is to allow (Permit) access by returning true when the condition for which this behavior is assigned occurs  */
    RETURN_YES,
    
	/** The behavior is to disallow (Deny) access by returning false when the condition for which this behavior is assigned occurs  */
    RETURN_NO,
    
	/** The behavior is to disallow (Deny) access by throwing a PepException when the condition for which this behavior is assigned occurs  */
    THROW_EXCEPTION
}
