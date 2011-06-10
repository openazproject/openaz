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
import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;

public class AzAttributeValueBooleanImpl
	extends AzAttributeValueImpl<AzDataTypeIdBoolean, Boolean>
		implements AzAttributeValueBoolean {
	Boolean b = false;
	public AzAttributeValueBooleanImpl(Boolean b) {
		super(AzDataTypeIdBoolean.AZ_DATATYPE_ID_BOOLEAN, b);
		this.b = b;
		if (log.isTraceEnabled()) log.trace(
				"\n   AzAttributeValueBooleanImpl constructor: " +
				"\n\tBoolean created with value = " + b);
	}
	//public AzDataType getType(){
	//	return AzDataType.X_BOOLEAN;
	//}
	public void setValue(Boolean b){
		this.b = b;
	}
	public Boolean getValue() {
		return b;
	}
	public String toXacmlString(){
		String bString = null;
		return bString;
	}
	/*
	//@Override
	public AzDataTypeIdBoolean getType() {
		// TODO Auto-generated method stub
		return null;
	}
	*/
	//@Override
	public boolean validate() {
		// TODO Auto-generated method stub
		return false;
	}

}
