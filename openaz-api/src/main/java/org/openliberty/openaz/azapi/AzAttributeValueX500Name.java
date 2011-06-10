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
import javax.security.auth.x500.X500Principal;

import org.openliberty.openaz.azapi.constants.AzDataTypeIdX500Name;
/**
 * AzAttributeValueX500Name contains Java X500Principal object 
 * that may be used to generate the XACML #x500Name DataType.
 * @author Rich
 */
public interface AzAttributeValueX500Name 
		extends AzAttributeValue<AzDataTypeIdX500Name, X500Principal> {
	
	/**
	 * Set the value of this AzAttributeValueX500Name object
	 * with a Java X500Principal object that can generate the 
	 * string that can be used for the value of a XACML 
	 * #x500Name DataType.
	 * 
	 * @param x500Principal that can be used to generate 
	 * XACML #x500Name DataType
	 */
	public void setValue(X500Principal x500Principal);
	
	/**
	 * Return the Java X500Principal object that is used by this 
	 * AzAttributeValueX500Name object to represent the 
	 * XACML #x500Name DataType  
	 * 
	 * @return  a Java X500Principal object that corresponds to the 
	 * XACML #x500Name DataType
	 */
	public X500Principal getValue();
	
	/**
	 * Return the Java String that can be used as the XACML #x500Name
	 * DataType representation of the Java X500Prinicipal object 
	 * contained in this AzAttributeValueAnyURI object.
	 * 
	 * @return	a string that can be used as XACML #x500Name 
	 * DataType content
	 * 
	 */
	public String toXacmlString();
}
