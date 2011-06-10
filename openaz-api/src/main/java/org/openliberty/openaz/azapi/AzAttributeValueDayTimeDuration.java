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
import org.openliberty.openaz.azapi.constants.AzDataTypeIdDayTimeDuration;

/**
 * AzAttributeValueDayTimeDuration contains AzDataDayTimeDuration
 * object that may be used to generate the XACML 
 * #dayTimeDuration DataType.
 * @author Rich
 */
public interface AzAttributeValueDayTimeDuration 
		extends AzAttributeValue<AzDataTypeIdDayTimeDuration, AzDataDayTimeDuration> {
	
	/**
	 * Set the value of this AzAttributeValueDayTimeDuration object
	 * with an AzDataDayTimeDuration object that can generate the 
	 * string that can be used for the value of a XACML 
	 * #dayTimeDuration DataType.
	 * 
	 * @param azDayTimeDuration that can be used to generate XACML 
	 * #dayTimeDuration DataType
	 */
	public void setValue(AzDataDayTimeDuration azDayTimeDuration);
	
	/**
	 * Return the AzDataDayTimeDuration object that is used by this 
	 * AzAttributeValueDayTimeDuration object to represent the 
	 * XACML #dayTimeDuration DataType  
	 * 
	 * @return  an azDataDayTimeDuration object that corresponds to 
	 * the XACML #dayTimeDuration DataType
	 */
	public AzDataDayTimeDuration getValue();
	
	/**
	 * Return the Java String that can be used as the XACML 
	 * #dayTimeDuration DataType representation of the 
	 * AzDataDayTimeDuration object contained in
	 * this AzAttributeValueDayTimeDuration object.
	 * 
	 * @return	a string that can be used as XACML #dayTimeDuration 
	 * DataType content
	 * 
	 */
	public String toXacmlString();
}
