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

//import org.openliberty.openaz.azapi.AzAttributeValueString;
//import org.openliberty.openaz.azapi.AzAttributeValueTypeString;
//import org.openliberty.openaz.azapi.constants.AzDataType;
import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;


public class AzAttributeValueIpAddressImpl 
	extends AzAttributeValueImpl<AzDataTypeIdIpAddress, String> 
	implements AzAttributeValueIpAddress {
	String value;
	public AzAttributeValueIpAddressImpl(String s){
		super(AzDataTypeIdIpAddress.AZ_DATATYPE_ID_IPADDRESS, null);
		this.value = s;
		System.out.println(
			"TestAzAttributeValueRfc822Name: String Created = " + value);
	}
	public AzDataTypeIdIpAddress getType(){
		return AzDataTypeIdIpAddress.AZ_DATATYPE_ID_IPADDRESS
		;
	}
	public void setValue(String s){
		this.value = s;
	}
	public String getValue() {
		return value;
	}
	public String toXacmlString() {
		return value;
	}
}
