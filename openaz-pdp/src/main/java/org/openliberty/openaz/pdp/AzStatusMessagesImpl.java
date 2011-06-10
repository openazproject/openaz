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

public class AzStatusMessagesImpl {
	public static final String NO_RESOURCE_ACTION_ASSOCIATION =
		"No azResourceActionAssociation was found in AzRequestContext.";
	public static final String AZMSG_DENY = 
		"Deny: Authorization request has been denied";
	public static final String AZMSG_PERMIT =
		"Permit: Congratulations, Dummy Subject has been authorized to access Dummy Resource";
	public static final String AZMSG_NOTAPPLICABLE =
		"NotApplicable: No applicable policy found for this Subject and Resource, PEP behavior based on bias or undefined.";
	public static final String AZMSG_INDETERMINATE_MISSING_ATTR =
		"Indeterminate: Policy unable to be evaluated: check status code: Missing attribute";
	public static final String AZMSG_INDETERMINATE_SYNTAX_ERROR =
		"Indeterminate: Policy unable to be evaluated: check status code: Syntax error";
	public static final String AZMSG_INDETERMINATE_PROCESSING_ERROR =
		"Indeterminate: Policy unable to be evaluated: check status code: Processing error";

}
