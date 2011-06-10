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

/**
 * This CategoryId indicates that the associated AzEntity or
 * AzAttribute is in the XACML Resource Category, 
 * {@link AzCategoryIdResource#AZ_CATEGORY_ID_RESOURCE}
 */
public enum AzCategoryIdResource implements AzCategoryId {
	/** XACML Category:  <b>urn:oasis:names:tc:xacml:3.0:attribute-category:resource</b> */ 
	AZ_CATEGORY_ID_RESOURCE(
	"urn:oasis:names:tc:xacml:3.0:attribute-category:resource");
	private final String azCategoryId;
	AzCategoryIdResource(String azCategoryId){
		this.azCategoryId = azCategoryId;
	}
	@Override public String toString() {
		return azCategoryId;
	}
}
