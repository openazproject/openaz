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
package org.openliberty.openaz.azapi;
//import java.util.Date;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdTime;

/**
 * AzAttributeValueDate contains the Java Date object within an
 * AzDataDateTime object that contains the information to
 * be used to generate the XACML #date DataType.
 * @author Rich
 *
 */
public interface AzAttributeValueTime 
		extends AzAttributeValue<AzDataTypeIdTime, AzDataDateTime> {
	
	/**
	 * Set the value with an AzDataDateTime object that will be used to provide the
	 * content for the Xacml DataType: #time
	 * 
	 * @see org.openliberty.openaz.azapi.constants.AzDataTypeIdTime
	 * @param azDataDateTime an object containing the Date and other 
	 * info to be converted to Xacml #time
	 */
	public void setValue(AzDataDateTime azDataDateTime);
	
	/**
	 * Return the Date object that is used to produce the Xacml date
	 * DataType content.
	 * 
	 * @return    the Date object             
	 */
	public AzDataDateTime getValue();
	
	/**
	 * Return a String containing the content to be used for a 
	 * Xacml #time DataType.
	 * 
	 * @see       org.openliberty.openaz.azapi.constants.AzDataTypeIdTime
	 * @return    a String w content conforming to Xacml 
	 * DataType: #time
	 */
	public String toXacmlString();}
