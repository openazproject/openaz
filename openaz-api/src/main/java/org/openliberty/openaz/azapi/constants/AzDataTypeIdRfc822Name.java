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

/** <b>XACML DataType:</b>  urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name. */ 
public enum AzDataTypeIdRfc822Name implements AzDataTypeId {
	/** XACML DataType:  urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name. 
	 * <br>The "urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name" primitive type 
	 * represents an electronic mail address. The valid syntax for such a 
	 * name is described in IETF RFC 2821, Section 4.1.2, Command Argument 
	 * Syntax, under the term "Mailbox". 
	 * From rfc 2821:
	 * <pre>
	 *   Domain = (sub-domain 1*("." sub-domain)) / address-literal
	 *   sub-domain = Let-dig [Ldh-str]
	 *   address-literal = "[" IPv4-address-literal /
	 *                         IPv6-address-literal /
	 *                         General-address-literal "]"
	 *   Mailbox = Local-part "@" Domain
	 *   Local-part = Dot-string / Quoted-string
	 *   Dot-string = Atom *("." Atom)
	 *   Atom = 1*atext
	 *   Quoted-string = DQUOTE *qcontent DQUOTE
	 *   String = Atom / Quoted-string
	 * </pre>
	 * */ 
	AZ_DATATYPE_ID_RFC822NAME("urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name");
	private final String azDataTypeId;

	AzDataTypeIdRfc822Name(String azDataTypeId) {
		this.azDataTypeId = azDataTypeId;
	}

	@Override
	public String toString() {
		return azDataTypeId;
	}
}