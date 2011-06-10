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


//public abstract class 
public  class 
	AzAttributeValueImpl<U extends Enum<U> & AzDataTypeId, V> 
		implements AzAttributeValue<U,V> {
	U type;
	V value;
	
	Log log = LogFactory.getLog(this.getClass());

	AzAttributeValueImpl(){}
	
	public AzAttributeValueImpl(U type, V value){
		this.type = type;
		this.value = value;
		if (log.isTraceEnabled()) log.trace(
			"\n  AzAttributeValue(superclass): constructor: " +
				"\n\t type = " + type +
				"\n\t value = " + value);
	}
	public void setType(U type){
		this.type = type;
	}
	public U getType(){
		return type;
	}
	public void setValue(V azVal){
		this.value = azVal;
	}
	public V getValue(){
		return value;
	}
	public boolean validate(){
		boolean result = true;
		return result;
	}
	public String toXacmlString(){
		//String xacmlString = "To Be Implemented";
		String strDataType = this.type.toString();
		String xacmlString = ""; // a null value is empty string
		if ( ! ( this.value == null ) ) {
			xacmlString = this.value.toString();
		}
		if (log.isTraceEnabled()) log.trace(
			"\n\tAttributeValue DataType: " + strDataType +
				 "\n\tAttributeValue Value:    " + xacmlString);
		return xacmlString;
	}
	public String toString(){
		String strDataType = this.type.toString();
		String strValue = ""; // a null value is empty string
		if ( ! ( this.value == null ) ) {
			strValue = this.value.toString();
		}
		if (log.isTraceEnabled()) log.trace(
			"\n\tAttributeValue DataType: " + strDataType +
				 "\n\tAttributeValue Value:    " + strValue);
		return strValue;
	}
}
