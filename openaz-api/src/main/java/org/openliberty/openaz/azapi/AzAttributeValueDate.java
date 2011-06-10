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
import org.openliberty.openaz.azapi.constants.AzDataTypeIdDate;

/**
 * AzAttributeValueDate contains the Java Date object that is intended
 * be used to generate the XACML #date DataType.
 * @author Rich
 *
 */
public interface AzAttributeValueDate 
		extends AzAttributeValue<AzDataTypeIdDate, AzDataDateTime> {
	
	/**
	 * Set the value with Java Date object that will be used to provide the
	 * content for the Xacml DataType: #date
	 * 
	 * @see org.openliberty.openaz.azapi.constants.AzDataTypeIdDate
	 * @param date		the Date object intended to be converted to Xacml date
	 */
	public void setValue(AzDataDateTime date);
	
	/**
	 * Return the Date object that is used to produce the Xacml date
	 * DataType content.
	 * 
	 * @return    the Date object             
	 */
	public AzDataDateTime getValue();
	
	/**
	 * Return a String containing the content to be used for a Xacml date DataType.
	 * 
	 * @see       org.openliberty.openaz.azapi.constants.AzDataTypeIdDate
	 * @return    a String w content conforming to Xacml DataType: date
	 */
	public String toXacmlString();}
