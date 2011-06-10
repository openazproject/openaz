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
package org.openliberty.openaz.pdp;
//import org.openliberty.openaz.azapi.AzAction;
//import org.openliberty.openaz.azapi.AzResource;

import java.util.Iterator;
import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;


public class AzResourceActionAssociationIdImpl 
	implements AzResourceActionAssociationId {
	String azResourceId = null;
	String azActionId = null;
	//public TestAzResourceActionAssociationId(
	//		AzResource azResource, AzAction azAction){
	public AzResourceActionAssociationIdImpl(
			AzEntity<AzCategoryIdResource> azResource, 
			AzEntity<AzCategoryIdAction> azAction){
		//this.azResourceId = azResource.getId();
		//this.azActionId = azAction.getId();
		Iterator<AzAttribute<AzCategoryIdResource>> it = 
			azResource.getAzAttributeSetByAttribId(
				AzXacmlStrings.X_ATTR_RESOURCE_ID).iterator();
		if (it.hasNext())
			this.azResourceId = 
				it.next().getAzAttributeValue().getValue().toString();
		else
			this.azResourceId = "no resource-id attribute found";
		Iterator<AzAttribute<AzCategoryIdAction>> itAction = 
			azAction.getAzAttributeSetByAttribId(
				AzXacmlStrings.X_ATTR_ACTION_ID).iterator();
		if (itAction.hasNext())
			this.azActionId = 
				itAction.next().getAzAttributeValue().getValue().toString();
		else
			this.azActionId = "no action-id attribute found";
	}
	public String getResourceId(){
		return azResourceId;
	}
	public String getActionId(){
		return azActionId;
	}
	public boolean equals(AzResourceActionAssociationId azResourceActionAssociationId){
		// test for nulls: if one of these is null, but not both return false
		if ((this.azResourceId == null || 
				azResourceActionAssociationId.getResourceId() == null) &&
			!(this.azResourceId == null && 
				azResourceActionAssociationId.getResourceId() == null) ) {
			System.out.println(
				"  TestAzResourceActionAssociationId.equals: " +
				"one null, but not other: \n\t this.azResourceId = " + 
				this.azResourceId +
				"\n\t azResourceActionAssociationId.getResourceId() = " +
				azResourceActionAssociationId.getResourceId());
			return false;
		}
		// if not both null, then since one not null from above,
		// then both not null and can test values; and return if no match
		if (!(this.azResourceId == null && 
				azResourceActionAssociationId.getResourceId() == null)) {
			
			if ( ! (azResourceActionAssociationId.getResourceId().equals(
					this.azResourceId) ) ) {
				System.out.println(
					"  TestAzResourceActionAssociationId.equals: " +
					"both not null, but not equal: \n\t this.azResourceId = " + 
					this.azResourceId +
					"\n\t azResourceActionAssociationId.getResourceId() = " +
					azResourceActionAssociationId.getResourceId());
				return false;
			}
		}
		// test for nulls: if one of these is null, but not both return false
		if ((this.azActionId == null || 
				azResourceActionAssociationId.getActionId() == null) &&
			!(this.azActionId == null && 
				azResourceActionAssociationId.getActionId() == null) ) {
			System.out.println(
				"  TestAzResourceActionAssociationId.equals: " +
				"one null, but not other: \n\t this.getActionId = " + 
				this.azActionId +
				"\n\t azResourceActionAssociationId.getActionId() = " +
				azResourceActionAssociationId.getActionId());
			return false;
		}
		// if not both null, then since one not null from above,
		// then both not null and can test values; and return if no match
		if (!(this.azActionId == null && 
				azResourceActionAssociationId.getActionId() == null)) {
			
			if ( ! (azResourceActionAssociationId.getActionId().equals(
					this.azActionId) ) ) {
				System.out.println(
					"  TestAzResourceActionAssociationId.equals: " +
					"both not null, but not equal: \n\t this.getActionId = " + 
					this.azActionId +
					"\n\t azResourceActionAssociationId.getActionId() = " +
					azResourceActionAssociationId.getActionId());
				return false;
			}
		}
		System.out.println(
				"  TestAzResourceActionAssociationId.equals: " +
				" should be equal: \n\t this.azResourceId = " + 
				this.azResourceId +
				"\n\t azResourceActionAssociationId.getResourceId() = " +
				azResourceActionAssociationId.getResourceId());
		System.out.println(
				"  TestAzResourceActionAssociationId.equals: " +
				"  should be equal: \n\t this.getActionId = " + 
				this.azActionId +
				"\n\t azResourceActionAssociationId.getActionId() = " +
				azResourceActionAssociationId.getActionId());
		return true;
	}
	public String toString(){
		return "\n\t TestAzResourceActionAssociationId.toString() = " +
			"\n\t\t this.azResourceId = " + this.azResourceId +
			"\n\t\t this.getActionId = " + this.azActionId;				
	}
}
