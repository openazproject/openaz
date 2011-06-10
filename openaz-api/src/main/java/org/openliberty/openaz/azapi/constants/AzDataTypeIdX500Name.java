/**
 * Copyright 2009,2010 Oracle, Inc.
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
package org.openliberty.openaz.azapi.constants;

/** 
 * <b>XACML DataType:</b>  urn:oasis:names:tc:xacml:1.0:data-type:x500Name.
*/
public enum AzDataTypeIdX500Name implements AzDataTypeId {
	/** XACML DataType:  urn:oasis:names:tc:xacml:1.0:data-type:x500Name.
	 * <br>The "urn:oasis:names:tc:xacml:1.0:data-type:x500Name" 
	 * primitive type represents an ITU-T Rec. X.520 Distinguished 
	 * Name. 
	 * <br>The valid syntax for such a name is described in IETF 
	 * RFC 2253 "Lightweight Directory Access Protocol (v3): 
	 * UTF-8 String Representation of Distinguished Names"
	 * <p>
	 * For example the general nature of this syntax is described in
	 * the constructor for javax.security.auth.x500.X500Principal(String name): Creates an X500Principal 
	 * from a string representation of an X.500 distinguished name 
	 * <br>(ex: "CN=Duke, OU=JavaSoft, O=Sun Microsystems, C=US"). 
	 * <br>The distinguished name must be specified using the grammar defined 
	 * in RFC 1779 or RFC 2253 (either format is acceptable). 
	 */ 
	AZ_DATATYPE_ID_X500NAME("urn:oasis:names:tc:xacml:1.0:data-type:x500Name");
	private final String azDataTypeId;

	AzDataTypeIdX500Name(String azDataTypeId) {
		this.azDataTypeId = azDataTypeId;
	}

	@Override
	public String toString() {
		return azDataTypeId;
	}
}