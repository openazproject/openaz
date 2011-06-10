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

/**
 * Helper interface to collect the parameters needed to create
 * a XACML #dayTimeDuration DataType
 * @author rlevinson
 *
 */
public interface AzDataDayTimeDuration {
	
	/**
	 * Set boolean indicating whether duration is positive or
	 * negative
	 * @param b true means negative, false means positive
	 */
	public void setNegativeDuration(boolean b);
	
	/**
	 * Get boolean indicating whether duration is positive or
	 * negative
	 * @return a boolean true if negative, false if positive
	 */
	public boolean getNegativeDuration();

	/**
	 * Set long indicating number of days in duration
	 * @param intLong integer number of day in duration
	 */
	public void setDaysInDuration(long intLong);
	
	/**
	 * Get long indicating number of days in duration
	 * @return a long w integer specifying number of days
	 */
	public long getDaysInDuration();

	/**
	 * Set long indicating number of hours in duration
	 * @param intLong integer number of hours in duration
	 */
	public void setHoursInDuration(long intLong);
	
	/**
	 * Get long indicating number of hours in duration
	 * @return a long w integer specifying number of hours
	 */
	public long getHoursInDuration();
	
	/**
	 * Set long indicating number of minutes in duration
	 * @param intLong integer number of minutes in duration
	 */
	public void setMinutesInDuration(long intLong);
	
	/**
	 * Get long indicating number of minutes in duration
	 * @return a long w integer specifying number of minutes
	 */
	public long getMinutesInDuration();
	
	/**
	 * Set long indicating number of seconds in duration
	 * @param intLong integer number of seconds in duration
	 */
	public void setSecondsInDuration(long intLong);
	
	/**
	 * Get long indicating number of seconds in duration
	 * @return a long w integer specifying number of seconds
	 */
	public long getSecondsInDuration();
	
	/**
	 * Set long indicating number of nanoseconds in duration
	 * @param intInt integer number of nanoseconds in duration
	 */
	public void setNanoSecondsInDuration(int intInt);
	
	/**
	 * Get inr indicating number of nanoseconds in duration
	 * @return a long w integer specifying number of nanoseconds
	 */
	public int getNanoSecondsInDuration();
}
