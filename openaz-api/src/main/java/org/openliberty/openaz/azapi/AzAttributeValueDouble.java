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
import org.openliberty.openaz.azapi.constants.AzDataTypeIdDouble;

/**
 * AzAttributeValueDouble contains a Java Double object that may
 * be used to generate the XACML #double DataType.
 * <br>
 * Basically, the xs:double data type: 
 * <pre>
 *   http://www.w3.org/TR/xmlschema-2/#double
 * </pre>
 * is a double precision floating point number. 
 * Therefore, Java Double is the corresponding class best
 * matching this requirement.
 * @author Rich
 */
public interface AzAttributeValueDouble
		extends AzAttributeValue<AzDataTypeIdDouble, Double>{

	/**
	 * Set the value of this AzAttributeValueDouble object
	 * with a Java Double object that can generate the string that
	 * can be used for the value of a XACML #double DataType.
	 * 
	 * @param dDouble that can be used to generate XACML #double DataType
	 */
	public void setValue(Double dDouble);
	
	/**
	 * Return the Java Double object that is used by this 
	 * AzAttributeValueDouble object to represent the 
	 * XACML #double DataType  
	 * 
	 * @return  a Java Double object that corresponds to the 
	 * XACML #double DataType
	 */
	public Double getValue();
	
	/**
	 * Return the Java Double that can be used as the XACML #double
	 * DataType representation of the Java Double object contained in
	 * this AzAttributeValueDouble object.
	 * 
	 * @return	a string that can be used as XACML #double 
	 * DataType content
	 * 
	 */
	public String toXacmlString();
}
