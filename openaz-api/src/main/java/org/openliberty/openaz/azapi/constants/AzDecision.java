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
package org.openliberty.openaz.azapi.constants;

/** AzDecision enum values correspond to the values defined in XACML 2.0 Section 6.11: (http://docs.oasis-open.org/xacml/2.0/access_control-xacml-2.0-core-spec-os.pdf) */
public enum AzDecision {
	
	/** "Permit": the requested access is permitted. */
	AZ_PERMIT("Permit"),
	
	/** "Deny": the requested access is denied. */
	AZ_DENY("Deny"),
	
	/** "Indeterminate": the PDP is unable to evaluate the requested access. Reasons for such inability include: missing attributes, network errors while retrieving policies, division by zero during policy evaluation, syntax errors in the decision request or in the policy, etc. The 
	 * most "interesting" of these is MissingAttributes, which returns information that the caller can take action on to submit another request if that is appropriate or desired. */
	AZ_INDETERMINATE("Indeterminate"),
	
	/** "NotApplicable": the PDP does not have any policy that applies to this decision request. In general, Xacml Policies and Rules have a Target
	 * element that is used to determine if the Policy or Rule should be applied to the Request. If not, then the Target result causes the Policy
	 * or Rule to evaluate to NotApplicable. After all Policy evaluation is complete, if result is NotApplicable, then that is Decision value that is returned.*/
	AZ_NOTAPPLICABLE("NotApplicable");
	
	final String azDecision;
	
	AzDecision(String azDecision){
		this.azDecision = azDecision;
	}
}
