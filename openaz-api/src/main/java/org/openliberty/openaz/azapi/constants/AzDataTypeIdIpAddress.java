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
package org.openliberty.openaz.azapi.constants;

/** <b>XACML DataType:</b>  urn:oasis:names:tc:xacml:2.0:data-type:ipAddress. */
public enum AzDataTypeIdIpAddress implements AzDataTypeId {
	/** XACML DataType:  urn:oasis:names:tc:xacml:2.0:data-type:ipAddress. 
	 * <br>The "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress" primitive 
	 * type represents an IPv4 or IPv6 network address, with optional mask 
	 * and optional port or port range. The syntax SHALL be:
	 * <pre>
	 *     ipAddress = address [ "/" mask ] [ ":" [ portrange ] ]
	 * </pre>
	 * For an IPv4 address, the address and mask are formatted in accordance 
	 * with the syntax for a "host" in IETF RFC 2396 "Uniform Resource Identifiers 
	 * (URI): Generic Syntax", section 3.2. 
	 * <p>
	 * For an IPv6 address, the address and mask are formatted in accordance with 
	 * the syntax for an "ipv6reference" in IETF RFC 2732 "Format for Literal IPv6 
	 * Addresses in URL's". (Note that an IPv6 address or mask, in this syntax, 
	 * is enclosed in literal "[" "]" brackets.) 
	 */ 
	AZ_DATATYPE_ID_IPADDRESS("urn:oasis:names:tc:xacml:2.0:data-type:ipAddress");
	private final String azDataTypeId;

	AzDataTypeIdIpAddress(String azDataTypeId) {
		this.azDataTypeId = azDataTypeId;
	}

	@Override
	public String toString() {
		return azDataTypeId;
	}
}