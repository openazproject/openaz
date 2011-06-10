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

public class AzDataDateTimeImpl 
	implements AzDataDateTime{
	Date date = null;
	int actualTimeZone = 0;
	int intendedTimeZone = 0;
	int nanoSeconds = 0;
	Log log = LogFactory.getLog(this.getClass()); 

	public AzDataDateTimeImpl(
				Date date,
				int actualTimeZone,
				int intendedTimeZone,
				int nanoSeconds){
			this.date = date;
			this.actualTimeZone = actualTimeZone;
			this.intendedTimeZone = intendedTimeZone;
			this.nanoSeconds = nanoSeconds;
			if (log.isTraceEnabled()) log.trace(
				"\n    Constructor creating new AzDataDateTime: " + 
					"\n\t with date set to: " + this.date);
		}
	public AzDataDateTimeImpl(){
		this.date = new Date();
	}
	/**
	 * Set the Date. 
	 * If not provided, then current date and time will be used and
	 * other parameters ignored.
	 * @param date
	 */
	public void setDate(Date date){
		this.date = date;
	}
	
	
	/**
	 * Get the Date as is set in this object.
	 * 
	 */
	public Date getDate(){
		return date;
	}
	
	/**
	 * Set the actual time zone that was used for the
	 * creation of the Date object as the offset to GMT in minutes.
	 * 
	 */
	public void setActualTimeZone(int timeZoneMinutes){
		this.actualTimeZone = timeZoneMinutes;
	}
	
	/**
	 * Get the actual time zone offset in minutes
	 * 
	 */
	public int getActualTimeZone(){
		return actualTimeZone;
	}
	
	/**
	 * Set the intended or expected time zone based on
	 * location where Date object was set.
	 * 
	 */
	public void setIntendedTimeZone(int timeZoneMinutes){
		this.intendedTimeZone = timeZoneMinutes;
	}
	
	/**
	 * Get the intended time zone
	 */
	public int getIntendedTimeZone(){
		return intendedTimeZone;
	}
	
	/**
	 * Set nanosecond offset
	 * 
	 */
	public void setNanoSecondOffset(int nanoSeconds){
		this.nanoSeconds = nanoSeconds;
	}
	
	/**
	 * Get nanosecond offset
	 */
	public int getNanoSecondOffset(){
		return nanoSeconds;
	}
	public String toString(){
		String s = null;
		s = this.date.toString();
		return s;
	}
}
