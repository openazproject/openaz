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

/** The AzCategoryIdSubjectCodebase identifier indicates a 
 * system entity associated with a local or remote codebase 
 * that generated the request. Corresponding subject attributes 
 * might include the URL from which it was loaded and/or the 
 * identity of the code-signer. There may be more than one. 
 * No means is provided to specify the order in which they 
 * processed the request. 
 */
public enum AzCategoryIdSubjectCodebase implements AzCategoryId {
	/** XACML Category: <b>urn:oasis:names:tc:xacml:1.0:subject-category:codebase</b>. */
	AZ_CATEGORY_ID_SUBJECT_CODEBASE(
			"urn:oasis:names:tc:xacml:1.0:subject-category:codebase");
	private final String azCategoryId;

	AzCategoryIdSubjectCodebase(String azCategoryId) {
		this.azCategoryId = azCategoryId;
	}

	@Override
	public String toString() {
		return azCategoryId;
	}
}
