/**
 * Copyright 2009 Oracle, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 * Authors:
 * 		Rich Levinson (Oracle)
 * Contributor:
 * 		Rich Levinson (Oracle)
 */
package org.openliberty.openaz.azapi;

import org.openliberty.openaz.azapi.constants.AzCategoryIdStatusDetail;
import org.openliberty.openaz.azapi.constants.AzDecision;
import org.openliberty.openaz.azapi.constants.AzStatusCode;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;

/**
 * @author rlevinson
 *
 */
public interface AzResult {
	/**
	 * 
	 * Returns a value drawn from the AzDecision type.
	 * 
	 * @return the AzDecision Enum associated with this AzResult
	 */
	public AzDecision getAzDecision();
	
	/**
	 * Returns one of the Enum OK, MISSINGATTRIBUTE or ERROR
	 * belonging to type {@link AzStatusCode}
	 * @return the AzStatusCode associated with this AzResult
	 */
	public AzStatusCode getAzStatusCode();
	
	/**
	 * Returns	an informational string message, if provided by 
	 * the PDP.
	 * 
	 * @return a String containing the status message
	 */
	public String getStatusMessage();
	
	/**
	 * 
	 * May be used to return an arbitrary object from the PDP.
	 * In the most well-defined XACML case, when a 
	 * MissingAttribute code is returned, it must take the form 
	 * of a Set<AzAttribute> contained in the "AzStatusDetail"
	 * entity (AzEntity<AzCategoryIdStatusDetail>).
	 * 
	 * @return the AzStatusDetail entity associated with this
	 * AzResult, if present, otherwise null.
	 */
	public AzEntity<AzCategoryIdStatusDetail> getAzStatusDetail();
	
	/**
	 * Returns the AzObligations object, which can contain 0 or
	 * more AzObligations (AzEntity<AzCategoryIdObligation>).
	 * @return the AzObligations object associated with this
	 * AzResult, otherwise null
	 */
	public AzObligations getAzObligations();
	
	/**
	 * Returns the optional XACML ResourceId attribute. 
	 * {@link AzXacmlStrings#X_ATTR_RESOURCE_ID} , if present, 
	 * that may be returned by the PDP in the XACML Result element. 
	 * The ResourceId is intended to identify the resource about 
	 * which the authorization decision is made. 
	 * <p>
	 * If not present, then it is assumed the decision refers to
	 * the resource-id specified in the Request.
	 * 
	 * @return a String containing the XACML resource-id attribute.
	 */
	public String getResourceId();

	
	/**
	 * A convenience method to return the combined AzResource,
	 * AzAction object references (AzResourceActionAssociation) for 
	 * this AzResult.
	 * <br>
	 * This object is guaranteed to be uniquely associated with the
	 * corresponding AzResourceActionAssociation that was submitted
	 * to the PDP based on a specific pair of AzResource and AzAction
	 * objects and which produced this AzResult.
	 * @return azResourceActionAssociation
	 * @see AzResourceActionAssociation#equals
	 */
	public AzResourceActionAssociation 
					getAzResourceActionAssociation();
}
