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
import org.openliberty.openaz.azapi.constants.AzDataTypeIdBoolean;

/**
 * AzAttributeValueBoolean contains a Java Boolean object that may
 * be used to generate the XACML #boolean DataType.
 * @author Rich
 */
public interface AzAttributeValueBoolean 
		extends AzAttributeValue<AzDataTypeIdBoolean, Boolean>{
	
	/**
	 * Set the value of this AzAttributeValueBoolean object
	 * with a Java Boolean object that can generate the string that
	 * can be used for the value of a XACML #boolean DataType.
	 * 
	 * @param booleanObject that can be used to generate 
	 * XACML #boolean DataType
	 */
	public void setValue(Boolean booleanObject);
	
	/**
	 * Return the Java Boolean object that is used by this 
	 * AzAttributeValueBoolean object to represent the 
	 * XACML #boolean DataType  
	 * 
	 * @return  a Java Boolean object that corresponds to the 
	 * XACML #boolean DataType
	 */
	public Boolean getValue();
	
	/**
	 * Return the Java String that can be used as the XACML #boolean
	 * DataType representation of the Java Boolean object contained in
	 * this AzAttributeValueBoolean object.
	 * 
	 * @return	a string that can be used as XACML #boolean 
	 * DataType content
	 * 
	 */
	public String toXacmlString();
}
