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
 * Helper interface to store the parameters needed to create
 * a XACML #yearMonthDuration DataType
 * @author rlevinson
 *
 */
public interface AzDataYearMonthDuration {

	
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
	public void setYearsInDuration(long intLong);
	
	/**
	 * Get long indicating number of years in duration
	 * @return a long w integer specifying number of years
	 */
	public long getYearsInDuration();

	/**
	 * Set long indicating number of years in duration
	 * @param intLong integer number of years in duration
	 */
	public void setMonthsInDuration(long intLong);
	
	/**
	 * Get long indicating number of months in duration
	 * @return a long w integer specifying number of months
	 */
	public long getMonthsInDuration();
	}
