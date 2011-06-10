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


/** <b>XACML DataType:</b>  urn:oasis:names:tc:xacml:2.0:data-type:yearMonthDuration */
public enum AzDataTypeIdYearMonthDuration implements AzDataTypeId {
	/** XACML DataType:  urn:oasis:names:tc:xacml:2.0:data-type:yearMonthDuration */
	AZ_DATATYPE_ID_YEARMONTHDURATION("urn:oasis:names:tc:xacml:2.0:data-type:yearMonthDuration");
	private final String azDataTypeId;

	AzDataTypeIdYearMonthDuration(String azDataTypeId) {
		this.azDataTypeId = azDataTypeId;
	}

	@Override
	public String toString() {
		return azDataTypeId;
	}
}