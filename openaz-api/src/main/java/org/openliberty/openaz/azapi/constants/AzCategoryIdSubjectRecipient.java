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

/** The AzCategoryIdSubjectRecipient identifier indicates the system
 * entity that will receive the results of the request (used when it 
 * is distinct from the access-subject). 
 */
public enum AzCategoryIdSubjectRecipient implements AzCategoryId {
	/** XACML Category: <b>urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject</b>. */
	AZ_CATEGORY_ID_SUBJECT_RECIPIENT(
			"urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject");
	private final String azCategoryId;

	AzCategoryIdSubjectRecipient(String azCategoryId) {
		this.azCategoryId = azCategoryId;
	}

	@Override
	public String toString() {
		return azCategoryId;
	}
}