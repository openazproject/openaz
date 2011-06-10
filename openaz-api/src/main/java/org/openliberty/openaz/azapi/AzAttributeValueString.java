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
import org.openliberty.openaz.azapi.constants.AzDataTypeIdString;

/**
 * AzAttributeValueString contains a Java String object that may
 * be used to generate the XACML #string DataType.
 * @author Rich
 */
public interface AzAttributeValueString 
		extends AzAttributeValue<AzDataTypeIdString, String> {
	
	/**
	 * Set the value of this AzAttributeValueString object
	 * with a Java String object that can generate the string that
	 * can be used for the value of a XACML #string DataType.
	 * 
	 * @param azVal a string that can be used to generate XACML #string DataType
	 */
	public void setValue(String azVal);
	
	/**
	 * Return the Java String object that is used by this 
	 * AzAttributeValueString object to represent the 
	 * XACML #string DataType  
	 * 
	 * @return  a Java String object that corresponds to the 
	 * XACML #string DataType
	 */
	public String getValue();
	
	/**
	 * Return the Java String that can be used as the XACML #string
	 * DataType representation of the Java String object contained in
	 * this AzAttributeValueString object.
	 * 
	 * @return	a string that can be used as XACML #string 
	 * DataType content
	 */
	public String toXacmlString();
}
