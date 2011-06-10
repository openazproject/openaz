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
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzDataTypeId;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;
/**
 * General interface for access to all the information contained
 * in a specific AzAttribute.
 * 
 * @author rlevinson
 *
 * @param <T> the {@link AzCategoryId} subtype of this AzAttribute
 */
public interface AzAttribute<T extends Enum<T> & AzCategoryId> {
	/**
	 * Returns the AzCategoryId subtype that defines the usage
	 * of this attribute within an AzRequestContext
	 * @return the azCategoryId of this attribute
	 */
	public T getAzCategoryId(); 
	
	/**
	 * Returns the XACML AttributeId for this AzAttribute.
	 * <p>
	 * The AttributeId is defined in XACML 2.0 as follows:
	 * <pre>
	 *     AttributeId [Required] 
	 *         The Attribute identifier. A number of identifiers 
	 *         are reserved by XACML to denote commonly used 
	 *         attributes.
	 * </pre>
	 * The reserved XACML identifiers are available in
	 * {@link AzXacmlStrings}
	 * @return the xacml AttributeId for this AzAttribute
	 */
	public String getAttributeId();
	
	/**
	 * Returns the name of the issuer of this attribute.
	 * <p>
	 * The Issuer is defined in XACML 2.0 as:
	 * <pre>
	 *     Issuer [Optional] 
	 *         The Attribute issuer. For example, this attribute value 
	 *         MAY be an x500Name that binds to a public key, or it may 
	 *         be some other identifier exchanged out-of-band by issuing 
	 *         and relying parties.
	 * </pre>
	 * @return a string containing the identifier of the issuer
	 * of the attribute
	 */
	public String getAttributeIssuer();
	
	/**
	 * Returns the {@link AzAttributeValue} subtype that contains
	 * the {@link AzDataTypeId} and associated Java type that
	 * is used to hold the value for this AzAttribute.
	 * 
	 * @return an {@link AzAttributeValue} subtype that has
	 * been associated with this AzAttribute.
	 */
	public AzAttributeValue<?,?> getAzAttributeValue();
}
