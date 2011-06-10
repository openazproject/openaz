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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;



public class AzAttributeImpl<T extends Enum<T> & AzCategoryId,
							 U extends Enum<U> & AzDataTypeId,
							 V> 
								implements AzAttribute<T> {
	T azCategoryId;
	String issuer;
	String attributeId;
	AzAttributeValue<U,V> attributeValue;
	Log log = LogFactory.getLog(this.getClass());

	public AzAttributeImpl(
			T azCategoryId,
			String issuer, String attributeId,
			AzAttributeValue<U,V> attributeValue){
		this.azCategoryId = azCategoryId;
		if (log.isTraceEnabled()) log.trace(
			"\n    AzAttributeImpl: constructor: " +
				"\n\t getClass() = " + this.getClass() +
				"\n\t issuer = " + issuer +
				"\n\t attributeId = " + attributeId);
		this.attributeId = attributeId;
		this.issuer = issuer;
		this.attributeValue = attributeValue;
	}
	public  T getAzCategoryId(){
		return azCategoryId;
	}
	public void setAzCategoryId(T c){
		this.azCategoryId = c;
	}

	public String getAttributeId(){
		return attributeId;
	}
	public void setAttributeId(String attrId){
		this.attributeId = attrId;
	}

	public String getAttributeIssuer(){
		return issuer;
	}
	public void setAttributeIssuer(String issuer){
		this.issuer = issuer;
	}

	public AzAttributeValue<U,V> getAzAttributeValue(){
		return attributeValue;
	}
	public void setAzAttributeValue(AzAttributeValue<U,V> azVal){
		this.attributeValue = azVal;
	}
	
	public String toString() {
		return "AttributeId: " + attributeId + 
			   "  AttributeValue: " + attributeValue.toXacmlString();
	}

}