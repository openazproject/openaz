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
import org.openliberty.openaz.azapi.constants.AzDataTypeIdRfc822Name;

/**
 * AzAttributeValueRfc822Name contains Java String object 
 * that may be used to generate the XACML #rfc822Name DataType.
 * @author Rich
 */
public interface AzAttributeValueRfc822Name 
		extends AzAttributeValue<AzDataTypeIdRfc822Name, String> {
	
	/**
	 * Set the value of this AzAttributeValueRfc822Name object
	 * with a Java String object that can generate the 
	 * string that can be used for the value of a XACML 
	 * #rfc822Name DataType.
	 * 
	 * @param rfc822Name a string that can be used to generate XACML 
	 * #rfc822Name DataType
	 */
	public void setValue(String rfc822Name);
	
	/**
	 * Return the Java String object that is used by this 
	 * AzAttributeValueRfc822Name object to represent the 
	 * XACML #rfc822Name DataType  
	 * 
	 * @return  a Java String object that corresponds to the 
	 * XACML #rfc822Name DataType
	 */
	public String getValue();
	
	/**
	 * Return the Java String that can be used as the XACML #rfc822Name
	 * DataType representation of the Java String object 
	 * contained in this AzAttributeValueRfc822Name object.
	 * 
	 * @return	a string that can be used as XACML #rfc822Name 
	 * DataType content
	 * 
	 */
	public String toXacmlString();
}
