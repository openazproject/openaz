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
import java.util.Set;

/**
 * The AzResponseContext which is returned by an AzService.decide 
 * call corresponds to the XML Response Context Defined by XACML.
 * <p>
 * Each response can be correlated to the specific request
 * based on the values of the AzResourceActionAssociation associated
 * with each AzResult object in the AzResponseContext. 
 * <p>
 * @author rlevinson
 *
 */
public interface AzResponseContext {
	
	/**
	 * Returns the AzResult object which is associated with a specific
	 * AzResourceAssociation object.
	 * <br>
	 * The AzResourceAssociation object must be from the Set of 
	 * AzResourceAssociation objects that were in the AzRequestContext 
	 * object that was the basis for this AzResponseContext object. 
	 * 
	 * @param azAssociation an AzResourceActionAssociation object, generally
	 * from the associated AzRequestContext
	 * @return the AzResult object associated with the requesting
	 * AzResourceActionAssociation object parameter
	 */
	public AzResult getResult(AzResourceActionAssociation azAssociation);

	/**
	 * Returns a Set of zero to n AzResult objects that are contained
	 * in the Response.
	 * <p>
	 * This method returns all the AzResult objects in the Response.
	 * The caller can then use the java.util.Set to iterate through
	 * the AzResult objects.
	 * <p>
	 * Correlation of output AzResults with input AzResourceAssociation
	 * may be managed using the AzResourceActionAssociation object
	 * reference by correlating the object returned in the AzResult
	 * with one of the objects submitted in the AzRequestContext.
	 * <p>
	 * Alternative correlation mechanisms include using the locally
	 * defined 
	 * {@link AzResourceActionAssociation#getCorrelationId} identifier,
	 * which may be used to provide more user-friendly identifiers.
	 * In addition, one may use the standard XACML 2.0 ResourceId 
	 * attribute (which may not produce unambiguous results depending
	 * on the set of requests where duplicates or omissions may exist).
	 * Finally, it is possible that there will emerge xml:id attributes
	 * that the ContextHandler may apply in proprietary profiles or 
	 * the currently planned XACML 3.0 multiple resource profile.
	 * 
	 * @return the Set<AzResult> containing the AzResults in this
	 * AzResponseContext
	 */
	public Set<AzResult> getResults();
	
}
