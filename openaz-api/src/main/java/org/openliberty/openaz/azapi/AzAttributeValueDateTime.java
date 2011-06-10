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
import org.openliberty.openaz.azapi.constants.AzDataTypeIdDateTime;

/**
 * AzAttributeValueDateTime contains an AzDataDateTime object that 
 * is intended to be used to generate the XACML #dateTime DataType.
 * @author Rich
 *
 */
public interface AzAttributeValueDateTime 
		extends AzAttributeValue<AzDataTypeIdDateTime, AzDataDateTime> {
	
	/**
	 * Set the value with AzDataDateTime object that will be used to provide the
	 * content for the Xacml DataType #dateTime
	 * 
	 * @see org.openliberty.openaz.azapi.constants.AzDataTypeIdDateTime
	 * @param date the AzDataDateTime object intended to be 
	 * converted to XACML #dateTime datatype
	 */
	public void setValue(AzDataDateTime date);
	
	/**
	 * Return the Date object that is used to produce the Xacml dateTime
	 * DataType content.
	 * 
	 * @return    the AzDataDateTime object that contains the Date             
	 */
	public AzDataDateTime getValue();
	
	/**
	 * Return a String containing the content to be used for a Xacml dateTime DataType.
	 * 
	 * @see       AzDataTypeIdDateTime
	 * @return    a String w content conforming to Xacml DataType: dateTime
	 */
	public String toXacmlString();
}
