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
import java.util.Date;

/**
 * Helper interface to collect the parameters necessary to create
 * the XACML #date, #time, #dateTime DataTypes.
 * <br>
 * Note: this interface is intended to replace the bare Date currently
 * used in the above XACML DataTypes, but has not yet been integrated.
 * @author rlevinson
 *
 */
public interface AzDataDateTime {
	
	/**
	 * Set the Date. 
	 * If not provided, then current date and time will be used and
	 * other parameters ignored.
	 * @param date
	 */
	public void setDate(Date date);
	
	/**
	 * Get the Date as is set in this object.
	 * 
	 */
	public Date getDate();
	
	/**
	 * Set the actual time zone that was used for the
	 * creation of the Date object as the offset to GMT in minutes.
	 * 
	 */
	public void setActualTimeZone(int timeZoneMinutes);
	
	/**
	 * Get the actual time zone offset in minutes
	 * 
	 */
	public int getActualTimeZone();
	
	/**
	 * Set the intended or expected time zone based on
	 * location where Date object was set.
	 * 
	 */
	public void setIntendedTimeZone(int timeZoneMinutes);
	
	/**
	 * Get the intended time zone
	 */
	public int getIntendedTimeZone();
	
	/**
	 * Set nanosecond offset
	 * 
	 */
	public void setNanoSecondOffset(int nanoSeconds);
	
	/**
	 * Get nanosecond offset
	 */
	public int getNanoSecondOffset();

}
