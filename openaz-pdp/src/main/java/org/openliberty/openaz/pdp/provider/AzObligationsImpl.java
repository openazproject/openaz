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
package org.openliberty.openaz.pdp.provider;
//import org.openliberty.openaz.azapi.AzObligation;
import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

public class AzObligationsImpl implements AzObligations{
	
	//Set<AzObligation> azObligations = null;
	Set<AzEntity<AzCategoryIdObligation>> azObligations = null;
	
	AzObligationsImpl(){
		azObligations = new HashSet<AzEntity<AzCategoryIdObligation>>();
	}
	
	//void addAzObligation(AzObligation azObligation){
	public void addAzObligation(AzEntity<AzCategoryIdObligation> azObligation){
		azObligations.add(azObligation);
	}
	
	//public Iterator<AzObligation> iterator(){
	public Iterator<AzEntity<AzCategoryIdObligation>> iterator(){
		return azObligations.iterator();
	}
	
	public int size(){
		return azObligations.size();
	}

}
