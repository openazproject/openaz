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
package org.openliberty.openaz.pdp;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;

public class AzAttributeValueStringImpl
		extends AzAttributeValueImpl<AzDataTypeIdString, String> 
		implements AzAttributeValueString {
	String value;
	Log log = LogFactory.getLog(this.getClass()); 
	
	public AzAttributeValueStringImpl(String s){
		super(AzDataTypeIdString.AZ_DATATYPE_ID_STRING, s);
		this.value = s;
		if (log.isTraceEnabled()) log.trace(
			"\n   AzAttributeValueString constructor: " +
			"\n\tString created = " + value);
	}
	
	public void setValue(String s){
		this.value = s;
	}
	public String getValue() {
		return value;
	}
	public String toXacmlString() {
		return value;
	}
}
