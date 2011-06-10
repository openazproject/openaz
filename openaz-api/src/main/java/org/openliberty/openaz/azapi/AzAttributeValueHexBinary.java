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
import org.openliberty.openaz.azapi.constants.AzDataTypeIdHexBinary;

/**
 * AzAttributeValueHexBinary contains a Java byte[] object that may
 * be used to generate the XACML #hexBinary DataType.
 * @author Rich
 */
public interface AzAttributeValueHexBinary 
		extends AzAttributeValue<AzDataTypeIdHexBinary, AzDataByteArray>{
	
	/**
	 * Set the value of this AzAttributeValueHexBinary object
	 * with a Java AzDataByteArray object that can generate the 
	 * string that can be used for the value of a XACML 
	 * #hexBinary DataType.
	 * 
	 * @param azVal an azDataByteArray that can be used to generate 
	 * XACML #hexBinary DataType
	 */
	public void setValue(AzDataByteArray azVal);
	
	/**
	 * Return the Java AzDataByteArray object that contains the Java
	 * byte[] array that is used by this AzAttributeValueHexBinary 
	 * object to represent the XACML #hexBinary DataType  
	 * 
	 * @return  a Java azDataByteArray object that corresponds to the 
	 * XACML #hexBinary DataType
	 */
	public AzDataByteArray getValue();
	
	/**
	 * Return the Java String that can be used as the XACML #hexBinary
	 * DataType representation of the Java byte[] array, contained in
	 * the AzDataByteArray of this AzAttributeValueHexBinary object.
	 * 
	 * @return	a string that can be used as XACML #hexBinary 
	 * DataType content
	 * @see AzDataByteArray
	 */
	public String toXacmlString();
}

