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

/** <b>XACML DataType:</b>  urn:oasis:names:tc:xacml:2.0:data-type:dnsName.*/ 
public enum AzDataTypeIdDnsName implements AzDataTypeId {
	/** XACML DataType:  urn:oasis:names:tc:xacml:2.0:data-type:dnsName. 
	 * <br>The "urn:oasis:names:tc:xacml:2.0:data-type:dnsName" primitive 
	 * type represents a Domain Name Service (DNS) host name, with optional 
	 * port or port range. The syntax SHALL be:
	 * <pre>
	 *     dnsName = hostname [ ":" portrange ]
	 * </pre>
	 * The hostname is formatted in accordance with IETF RFC 2396 
	 * "Uniform Resource Identifiers (URI): Generic Syntax", section 3.2, 
	 * except that a wildcard "*" may be used in the left-most component 
	 * of the hostname to indicate "any subdomain" under the domain 
	 * specified to its right.
	 * <p>For both the "urn:oasis:names:tc:xacml:2.0:data-type:ipAddress" and 
	 * "urn:oasis:names:tc:xacml:2.0:data-type:dnsName" data-types, the port 
	 * or port range syntax SHALL be
	 * <pre>     portrange = portnumber | "-"portnumber | portnumber"-"[portnumber]
	 * </pre>where "portnumber" is a decimal port number. 
	 * <br>If the port number is of the form "-x", where "x" is a port 
	 * number, then the range is all ports numbered "x" and below. 
	 * <br>If the port number is of the form"x-", then the range is 
	 * all ports numbered "x" and above. 
	 * <br>[This syntax is taken from the Java SocketPermission.]
	 */ 
	AZ_DATATYPE_ID_DNSNAME("urn:oasis:names:tc:xacml:2.0:data-type:dnsName");
	private final String azDataTypeId;

	AzDataTypeIdDnsName(String azDataTypeId) {
		this.azDataTypeId = azDataTypeId;
	}

	@Override
	public String toString() {
		return azDataTypeId;
	}
}