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
import org.openliberty.openaz.azapi.constants.AzDataTypeIdDnsName;

/**
 * AzAttributeValueDnsName contains Java String object 
 * that may be used to generate the XACML #dnsName DataType.
 * @author Rich
 */
public interface AzAttributeValueDnsName 
		extends AzAttributeValue<AzDataTypeIdDnsName, String> {
	
	/**
	 * Set the value of this AzAttributeValueDnsName object
	 * with a Java String object that can generate the 
	 * string that can be used for the value of a XACML 
	 * #dnsName DataType.
	 * 
	 * @param dnsName , a string that can be used to generate XACML 
	 * #dnsName DataType
	 */
	public void setValue(String dnsName);
	
	/**
	 * Return the Java String object that is used by this 
	 * AzAttributeValueDnsName object to represent the 
	 * XACML #dnsName DataType  
	 * 
	 * @return  a Java String object that corresponds to the 
	 * XACML #dnsName DataType
	 */
	public String getValue();
	
	/**
	 * Return the Java String that can be used as the XACML #dnsName
	 * DataType representation of the Java String object 
	 * contained in this AzAttributeValueDnsName object.
	 * 
	 * @return	a string that can be used as XACML #dnsName 
	 * DataType content
	 * 
	 */
	public String toXacmlString();
}
