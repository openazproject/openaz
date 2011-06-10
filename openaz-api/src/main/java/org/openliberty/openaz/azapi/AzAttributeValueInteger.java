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
import org.openliberty.openaz.azapi.constants.AzDataTypeIdInteger;

/**
 * AzAttributeValueInteger contains a Java Long object that may
 * be used to generate the XACML #integer DataType.
 * <br>
 * Basically, the xs:integer data type: 
 * <pre>
 *   http://www.w3.org/TR/xmlschema-2/#integer
 * </pre>
 * is an integer that runs from minus to plus infinity. Therefore,
 * to best approximate this impossible objective, the largest Java
 * integer, long, is chosen to provide the capability.
 * @author Rich
 */
public interface AzAttributeValueInteger 
		extends AzAttributeValue<AzDataTypeIdInteger, Long>{

	/**
	 * Set the value of this AzAttributeValueInteger object
	 * with a Java Long object that can generate the string that
	 * can be used for the value of a XACML #integer DataType.
	 * 
	 * @param intLong that can be used to generate XACML #integer DataType
	 */
	public void setValue(Long intLong);
	
	/**
	 * Return the Java Long object that is used by this 
	 * AzAttributeValueInteger object to represent the 
	 * XACML #integer DataType  
	 * 
	 * @return  a Java Long object that corresponds to the 
	 * XACML #integer DataType
	 */
	public Long getValue();
	
	/**
	 * Return the Java String that can be used as the XACML #integer
	 * DataType representation of the Java Long object contained in
	 * this AzAttributeValueInteger object.
	 * 
	 * @return	a string that can be used as XACML #integer 
	 * DataType content
	 * 
	 */
	public String toXacmlString();
}
