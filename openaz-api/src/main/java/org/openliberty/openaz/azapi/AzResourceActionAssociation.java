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
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
/**
 * The AzResourceActionAssociation is a pairing of an AzResource 
 * entity (aka: AzEntity<AzCategoryIdResource>), and an AzAction 
 * entity (aka: AzEntity<AzCategoryIdAction>), for the purpose of 
 * submitting the pair as a specific resource/action pair for which 
 * an authorization decision can be obtained.
 * <p>
 * Every XACML PDP authorization decision request requires one Resource
 * element and one Action element. The AzResource and AzAction in
 * this AzResourceActionAssociation are in one to one correspondence
 * with the basis of construction of the XACML Resource and XACML Action 
 * elements that are submitted to the PDP when this AzResourceActionAssociation
 * is part of an AzRequestContext submitted to an AzService.decide() call.
 * <br>
 * In general, one AzService.decide() call can make multiple PDP 
 * authorization requests, but each individual request in the set of 
 * requests will have a unique AzResourceActionAssociation object 
 * that is the basis of that individual request.
 * <br>
 * When AzResponseContext is returned the AzResourceActionAssociation
 * object reference will be available for programmatic correlation
 * of multiple requests with multiple results.
 * <br> In addition, other correlation is also made available including
 * a user readable CorrelationID, and the resource-id and action-id 
 * associated with the AzResource and AzAction entities if available.
 * 
 * @author rlevinson
 *
 */
public interface AzResourceActionAssociation {
	
	/**
	 * Get the AzAction entity component of the association.
	 * @return the "AzAction" associated with this 
	 * AzResourceActionAssociation object
	 */
	public AzEntity<AzCategoryIdAction> getAzAction();
	
	/**
	 * Get the AzResource entity component of the association.
	 * @return the "AzResource" associated with this
	 * AzResourceActionAssociation object
	 */
	public AzEntity<AzCategoryIdResource> getAzResource();
	
	/**
	 * Get the helper object from the association that contains
	 * the resource-id and action-id if present.
	 * @return the AzResourceActionAssociationId helper object
	 */
	public AzResourceActionAssociationId getAzResourceActionAssociationId();
	
	/**
	 * Compare this association with another for equality based
	 * on object reference.
	 * @param azResourceActionAssociation
	 * @return a boolean true if the AzResourceActionAssociations are
	 * identical, otherwise false
	 */
	public boolean equals(AzResourceActionAssociation azResourceActionAssociation);
	
	/**
	 * An integer correlation id is created when the association is 
	 * created. This integer is intended for informal user-friendly
	 * correlation of results. It has no corresponding reference in
	 * XACML.
	 * @return an int that may be used to assist correlating the 
	 * AzResourceActionAssociations in the AzRequestContext with those
	 * found in the AzResponseContext
	 */
	public int getCorrelationId();
}
