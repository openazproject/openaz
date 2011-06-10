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

public enum AzStatusCode {
	
	/** XACML Identifier: <b>urn:oasis:names:tc:xacml:1.0:status:ok</b>. This identifier indicates success. */
	AZ_OK("urn:oasis:names:tc:xacml:1.0:status:ok"),
	
	/** XACML Identifier: <b>urn:oasis:names:tc:xacml:1.0:status:missing-attribute</b>. This identifier indicates that all the attributes necessary to make a policy decision were not available (see Section 6.16).*/
	AZ_MISSING_ATTRIBUTE("urn:oasis:names:tc:xacml:1.0:status:missing-attribute"),
	
	/** XACML Identifier: <b>urn:oasis:names:tc:xacml:1.0:status:syntax-error</b>. This identifier indicates that some attribute value contained a syntax error, such as a letter in a numeric field.*/
	AZ_SYNTAX_ERROR("urn:oasis:names:tc:xacml:1.0:status:syntax-error"),
	
	/** XACML Identifier: <b>urn:oasis:names:tc:xacml:1.0:status:processing-error</b>. This identifier indicates that some attribute value contained a syntax error, such as a letter in a numeric field.*/
	AZ_PROCESSING_ERROR("urn:oasis:names:tc:xacml:1.0:status:processing-error");
	
	final String azStatusCode;
	
	AzStatusCode(String azStatusCode){
		this.azStatusCode = azStatusCode;
	}
	
	public String toString() {
		return azStatusCode;
	}
}
