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
//import org.openliberty.openaz.azapi.constants.AzDataTypeId;
import java.net.URI;

import org.openliberty.openaz.azapi.constants.AzDataTypeIdAnyURI;

/**
 * AzAttributeValueAnyURI contains Java URI object that may
 * be used to generate the XACML #anyURI DataType.
 * @author Rich
 */
public interface AzAttributeValueAnyURI 
		extends AzAttributeValue<AzDataTypeIdAnyURI, URI> {
	
	/**
	 * Return the AzDataTypeId for this AzAttributeValueAnyURI
	 * object.
	 * @return an enum that contains the XACML DataType identifier string
	 * @see org.openliberty.openaz.azapi.constants.AzDataTypeIdAnyURI
	 */
	public AzDataTypeIdAnyURI getType();
	
	/**
	 * Set the value of this AzAttributeValueAnyURI object
	 * with a Java URI object that can generate the string that
	 * can be used for the value of a XACML #anyURI DataType.
	 * 
	 * @param uri that can be used to generate XACML #anyURI DataType
	 */
	public void setValue(URI uri);
	
	/**
	 * Return the Java URI object that is used by this 
	 * AzAttributeValueAnyURI object to represent the 
	 * XACML #anyURI DataType  
	 * 
	 * @return  a Java URI object that corresponds to the 
	 * XACML #anyURI DataType
	 */
	public URI getValue();
	
	/**
	 * Return the Java String that can be used as the XACML #anyURI
	 * DataType representation of the Java URI object contained in
	 * this AzAttributeValueAnyURI object.
	 * 
	 * @return	a string that can be used as XACML #anyURI 
	 * DataType content
	 * 
	 */
	public String toXacmlString();
}
