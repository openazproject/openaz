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

import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;


public class AzAttributeValueDateTimeImpl
		extends AzAttributeValueImpl<AzDataTypeIdDateTime, AzDataDateTime> 
		implements AzAttributeValueDateTime {
	private AzDataDateTime date;
	Log log = LogFactory.getLog(this.getClass()); 
	
	public AzAttributeValueDateTimeImpl(){
		super(AzDataTypeIdDateTime.AZ_DATATYPE_ID_DATETIME, 
				new AzDataDateTimeImpl());
		this.date = super.getValue();
		if (log.isTraceEnabled()) log.trace(
			"    TestAzAttributeValueDateTime: " +
			" Date Created = " + date.getDate());
	}
	
	public AzAttributeValueDateTimeImpl(
			Date date, int nanoseconds, 
			int timeZone, int defaultedTimeZone){
		super(AzDataTypeIdDateTime.AZ_DATATYPE_ID_DATETIME, 
				new AzDataDateTimeImpl(
					date,timeZone,defaultedTimeZone,nanoseconds));
		this.date = super.getValue();
	}
	public AzAttributeValueDateTimeImpl(AzDataDateTime date){
		super(AzDataTypeIdDateTime.AZ_DATATYPE_ID_DATETIME, date);
		this.date = date;
	}
	
	public void setValue(AzDataDateTime azVal){
		this.date = azVal;
	}
	public AzDataDateTime getValue(){
		if (log.isTraceEnabled()) log.trace(
			"    TestAzAttributeValueDateTime.getValue: " +
				"\n\t Date = " + date.getDate());
		return date;
	}
	public String toXacmlString(){
		return date.toString();
	}
}
