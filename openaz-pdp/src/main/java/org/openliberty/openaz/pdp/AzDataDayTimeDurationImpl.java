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
//import org.openliberty.openaz.azapi.constants.*;


public class AzDataDayTimeDurationImpl 
	implements AzDataDayTimeDuration{
	boolean isNegativeDuration = false;
	long days = 0;
	long hours = 0;
	long minutes = 0;
	long seconds = 0;
	int nanoSeconds = 0;
public AzDataDayTimeDurationImpl(
			boolean isNegativeDuration,
			long days,
			long hours,
			long minutes,
			long seconds,
			int nanoSeconds){
		this.isNegativeDuration = isNegativeDuration;
		this.days = days;
		this.hours = hours;
		this.minutes = minutes;
		this.seconds = seconds;
		this.nanoSeconds = nanoSeconds;
	}
	public void setNegativeDuration(boolean b){
		
	}
	
	/**
	 * Get boolean indicating whether duration is positive or
	 * negative
	 * @return a boolean true if negative, false if positive
	 */
	public boolean getNegativeDuration(){
		boolean b = false;
		return b;
	}

	/**
	 * Set long indicating number of days in duration
	 * @param intLong integer number of day in duration
	 */
	public void setDaysInDuration(long intLong){
		this.days = intLong;
	}
	
	/**
	 * Get long indicating number of days in duration
	 * @return a long w integer specifying number of days
	 */
	public long getDaysInDuration(){
		return days;
	}

	/**
	 * Set long indicating number of hours in duration
	 * @param intLong integer number of hours in duration
	 */
	public void setHoursInDuration(long intLong){
		this.hours = intLong;
	}
	
	/**
	 * Get long indicating number of hours in duration
	 * @return a long w integer specifying number of hours
	 */
	public long getHoursInDuration(){
		return hours;
	}
	
	/**
	 * Set long indicating number of minutes in duration
	 * @param intLong integer number of minutes in duration
	 */
	public void setMinutesInDuration(long intLong){
		this.minutes = intLong;
	}
	
	/**
	 * Get long indicating number of minutes in duration
	 * @return a long w integer specifying number of minutes
	 */
	public long getMinutesInDuration(){
		return minutes;
	}
	
	/**
	 * Set long indicating number of seconds in duration
	 * @param intLong integer number of seconds in duration
	 */
	public void setSecondsInDuration(long intLong){
		this.seconds = intLong;
	}
	
	/**
	 * Get long indicating number of seconds in duration
	 * @return a long w integer specifying number of seconds
	 */
	public long getSecondsInDuration(){
		return seconds;
	}
	
	/**
	 * Set long indicating number of nanoseconds in duration
	 * @param intInt integer number of nanoseconds in duration
	 */
	public void setNanoSecondsInDuration(int intInt){
		this.nanoSeconds = intInt;
	}
	
	/**
	 * Get inr indicating number of nanoseconds in duration
	 * @return a long w integer specifying number of nanoseconds
	 */
	public int getNanoSecondsInDuration(){
		return nanoSeconds;
	}
}
