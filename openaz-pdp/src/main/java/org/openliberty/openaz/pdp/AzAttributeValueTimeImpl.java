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


import java.util.Date;

public class AzAttributeValueTimeImpl 
	extends AzAttributeValueImpl<AzDataTypeIdTime, AzDataDateTime> 
	implements AzAttributeValueTime{
	private AzDataDateTime date;
//	private int timeZone;
//	private int defaultedTimeZone;
	public AzAttributeValueTimeImpl(){
		super(AzDataTypeIdTime.AZ_DATATYPE_ID_TIME, null);
		date = new AzDataDateTimeImpl();
	}
	public AzAttributeValueTimeImpl(
			Date date, int nanoSeconds, int timeZone, 
			int defaultedTimeZone){
		super(AzDataTypeIdTime.AZ_DATATYPE_ID_TIME, null);
		this.date = new AzDataDateTimeImpl(date,timeZone,defaultedTimeZone,nanoSeconds);
	}
	public AzAttributeValueTimeImpl(AzDataDateTime date){
		this.date = date;
	}
	public void setValue(Date date){
	}
	public AzDataDateTime getValue() {
		return date;
	}
	public String toXacmlString(){
		return date.toString();
	}
}
