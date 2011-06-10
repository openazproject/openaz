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
import org.openliberty.openaz.azapi.*;

import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;

//import org.openliberty.openaz.azapi.*;



public class AzResponseContextImpl implements AzResponseContext {
	
	Set<AzResult> azResults;
	Iterator<AzResult> azResultIterator = null;
	
	public AzResponseContextImpl(){
		azResults = new HashSet<AzResult>();
		azResultIterator = azResults.iterator();
	}
	
	void remove() {
		azResultIterator.remove();
	}
	
	public AzResult next() {
		return azResultIterator.next();
	}
	
	public boolean hasNext() {
		//azResultIterator = azResults.iterator();
		return azResultIterator.hasNext();
	}
	
	//public Iterator<AzResult> iterator(){
	//	return azResults.iterator();
	//}
	
	public AzResult getResult(AzResourceActionAssociation azResourceActionAssociation){
		AzResult azResult = null;
		Iterator<AzResult> it = azResults.iterator();
		while (it.hasNext()){
			azResult = next();
			if (azResourceActionAssociation.equals(
					azResult.getAzResourceActionAssociation())){
				System.out.println("TestAzResponseContext.getResults(azResourceActionAssociation): " +
						" found match, return result: " + azResourceActionAssociation);
				return azResult;
			}
		}
		return azResult;
	}
	public Set<AzResult> getResults() {
		return azResults;
	}
	public Set<AzResult> getResults(AzResourceActionAssociationId azResourceActionAssociationId){
		Set<AzResult> azResultSet = null;
		AzResult azResult = null;
		Iterator<AzResult> it = azResults.iterator();
		while (it.hasNext()){
			azResult = next();
			if (azResourceActionAssociationId.equals(
					azResult.getAzResourceActionAssociation())){
				System.out.println("TestAzResponseContext.getResults(azResourceActionAssociationId): " +
						" found match, adding to return result set: " + azResourceActionAssociationId);
				if (azResultSet == null){
					azResultSet = new HashSet<AzResult>();					
				}
				azResultSet.add(azResult);
			}
		}
		return azResultSet;
	}
	boolean addResult(AzResult azResult){
		boolean result = false;
		result = azResults.add(azResult);
		azResultIterator = azResults.iterator();
		return result;
	}

}
