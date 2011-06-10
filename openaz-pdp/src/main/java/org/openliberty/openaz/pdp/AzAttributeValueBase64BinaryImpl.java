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


public class AzAttributeValueBase64BinaryImpl 
	implements AzAttributeValueBase64Binary{
	AzDataByteArray byteArray = null;
	public AzAttributeValueBase64BinaryImpl(
			AzDataByteArray byteArray){
		this.byteArray = byteArray;
	}
	//public AzDataType getType(){
	//	return AzDataType.X_BASE64BINARY;
	//}
	public void setValue(AzDataByteArray byteArray){
		this.byteArray = byteArray;
	}
	public AzDataByteArray getValue() {
		return byteArray;
	}
	public String toXacmlString(){
		String intString = null;
		return intString;
	}
	public boolean validate(){
		return true;
	}
	//@Override
	public AzDataTypeIdBase64Binary getType() {
		// TODO Auto-generated method stub
		return null;
	}
}
