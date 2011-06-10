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

//import org.openliberty.openaz.azapi.constants.AzDataType;
import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;

public class AzAttributeValueIntegerImpl 
	extends AzAttributeValueImpl<AzDataTypeIdInteger, Long>
		implements AzAttributeValueInteger {
	Long i = null;
	public AzAttributeValueIntegerImpl(Long i) {
		super(AzDataTypeIdInteger.AZ_DATATYPE_ID_INTEGER, i);
		this.i = i;
		if (log.isTraceEnabled()) log.trace(
				"\n   AzAttributeValueIntegerImpl constructor: " +
				"\n\tLong created with value = " + i);
	}
	public AzDataTypeIdInteger getType(){
		return AzDataTypeIdInteger.AZ_DATATYPE_ID_INTEGER;
	}
	public void setValue(Long i){
		this.i = i;
	}
	public Long getValue() {
		return i;
	}
	public String toXacmlString(){
		String intString = i.toString();
		return intString;
	}
	//@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}
}
