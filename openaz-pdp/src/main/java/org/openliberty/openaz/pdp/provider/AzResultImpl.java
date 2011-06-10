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
package org.openliberty.openaz.pdp.provider;

//import org.openliberty.openaz.azapi.AzAction;
//import org.openliberty.openaz.azapi.AzResource;
//import org.openliberty.openaz.azapi.AzStatusDetail;

import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;

public class AzResultImpl implements AzResult{
	
	AzDecision azDecision = null;
	AzObligations azObligations = null;
	//AzStatusDetail azStatusDetail = null;
	AzEntity<AzCategoryIdStatusDetail> azStatusDetail = null;
	String statusMessage = null;
	AzStatusCode azStatusCode = null;
	AzResourceActionAssociation azResourceActionAssociation = null;
	
	public AzResultImpl(){}
	
	public AzDecision getAzDecision(){
		return azDecision;
	}
	void setAzDecision(AzDecision azDecision){
		this.azDecision = azDecision;
	}
	
	public AzObligations getAzObligations(){		
		return azObligations;
	}
	void setAzObligations(AzObligations azObligations){
		this.azObligations = azObligations;
	}
	
	
	//public AzStatusDetail getAzStatusDetail(){
	public AzEntity<AzCategoryIdStatusDetail> getAzStatusDetail(){
		return azStatusDetail;
	}
	//void setAzStatusDetail(AzStatusDetail azStatusDetail){
	void setAzStatusDetail(AzEntity<AzCategoryIdStatusDetail> azStatusDetail){
		this.azStatusDetail = azStatusDetail;
	}
	
	public String getStatusMessage(){
		return statusMessage;
	}
	void setStatusMessage(String statusMessage){
		this.statusMessage = statusMessage;
	}
	
	public AzStatusCode getAzStatusCode(){
		return azStatusCode;
	}
	void setAzStatusCode(AzStatusCode azStatusCode){
		this.azStatusCode = azStatusCode;
	}
	
	public AzResourceActionAssociation getAzResourceActionAssociation(){
		return azResourceActionAssociation;
	}
	void setAzResourceActionAssociation(AzResourceActionAssociation azResourceActionAssociation) {
		this.azResourceActionAssociation = azResourceActionAssociation;
	}
	public AzResourceActionAssociationId getAzResourceActionAssociationId(){
		if (azResourceActionAssociation == null) return null;
		return azResourceActionAssociation.getAzResourceActionAssociationId();
	}
	//public AzAction getAzAction(){
	public AzEntity<AzCategoryIdAction> getAzAction(){
		if (azResourceActionAssociation == null) return null;
			return azResourceActionAssociation.getAzAction();
	}
	//public AzResource getAzResource(){
	public AzEntity<AzCategoryIdResource> getAzResource(){
		if (azResourceActionAssociation == null) return null;
			return azResourceActionAssociation.getAzResource();
	}
	public String getResourceId(){
		String resourceId = null;
		return resourceId;
	}

}
