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

import org.openliberty.openaz.azapi.constants.AzDataTypeId;

/**
 * AzAttributeValue is the parent interface of all the subinterfaces
 * that provide for the implementation of each XACML DataType as
 * enumerated by the enums that implement {@link AzDataTypeId}.
 * <p>
 * Subinterfaces of AzAttributeValue are implemented to meet the requirements
 * of each individual XACML DataType.
 * <p>
 * The basic design is that a Java object, which can handle the
 * requirements of the AzDataType be used for setValue(T). For
 * example, in the case of XACML #anyURI, the Java URI type is
 * used to meet those requirements.
 * <br>
 * In some cases there will be an obvious one to one mapping between
 * the XACML DataType identified by an AzDataType and a Java type
 * that meets the XACML DataType requirements.
 * <br>
 * In other cases there may be more complexity than a single available
 * Java class required to meet the requirements specified by the XACML
 * DataType. In those cases, custom interfaces may be implemented to
 * meet the requirements, as exampled by the AzData* interfaces that
 * are used for some interfaces.
 * <p>
 * Subclasses of AzAttributeValue are "type-safe" in the sense 
 * that they define a specific subtype of AzDataTypeId which
 * will be returned by {@link #getType()} and a specific Java 
 * type that will be returned by {@link #getValue()}, which
 * ensure that only the specific XACML DataType identifier 
 * (subtype of AzDataTypeId) that corresponds to the 
 * specific Java type are
 * kept together in an 
 * AzAttributeValue&lt;AzDataTypeId, SpecificJavaType&gt; 
 * subinterface of this class.
 * <p>
 * Note: the Sun XACML Implementation:
 * <pre>
 *    http://sunxacml.sourceforge.net/javadoc/index.html
 * </pre>
 * was used as the basis for defining the characteristics of many of
 * these datatypes, esp wrt to dates, times, durations, byte[], etc.
 * <p>
 * Note: it is an objective of the AzAPI to be able to "wrap" the
 * Sun XACML Client Implementation for the purpose of serializing
 * xml calls to the Sun XACML PDP. Note also in this regard that the
 * AzAPI is intended to be XACML 2.0 compliant, while the Sun
 * Implementation is a pre-XACML 2.0 implementation and lacks several
 * XACML 2.0 features. i.e. the AzAPI is intended to be "downward
 * compatible" to subset implementations.
 *
 * @param <U> An {@link AzDataTypeId} Enum representing a XACML DataType
 * @param <V> A Java type containing the value of the attribute or null
 * @author Rich
 *
 */
public interface AzAttributeValue<U extends Enum<U> & AzDataTypeId, V> {

	/**
	 * Return the XACML DataType of this attribute.
	 * <p>
	 * The XACML 2.0 specification defines the DataType as:
	 * <pre>
	 *   DataType [Required]
	 *     The data-type of the contents of the &lt;xacml-context:AttributeValue&gt; 
	 *     element. This SHALL be either a primitive type defined by the 
	 *     XACML 2.0 specification or a type (primitive or structured) defined 
	 *     in a namespace declared in the &lt;xacml-context&gt; element.
	 * </pre>
	 * 
	 * @return azDataTypeId a subtype of {@link AzDataTypeId}
	 */
	public U getType();
	
	/**
	 * Set the AzAttributeValueType, which contains an actual instance
	 * of a value for this attribute. The subclass implementations of
	 * the AzAttributeValueType will constrain the value data to
	 * conform with the requirements of the AzDataType.
	 * value 
	 * TODO: should this member be immutable? Or provide an option
	 * @param azVal
	 */
	public void setValue(V azVal);
	
	/**
	 * Return the value in the Java object used to represent the
	 * XACML DataType. This object will contain the value from
	 * which the XACML value is derived, but it is not necessarily
	 * the format necessary for XACML. The XACML value is obtained
	 * from the toXacmlString() method.
	 * 
	 * @return a value in the form of a Java object, or null, which
	 * corresponds to an empty XACML AttributeValue element.
	 */
	public V getValue();
	
	/**
	 * Return a String which can be used in an XML representation
	 * of the XACML Request.
	 * <p>
	 * Note: this method is useful to avoid being concerned about
	 * displaying null values, because in XACML a null value is
	 * equivalent to an empty AttributeValue xml element. Therefore,
	 * this method returns an empty String when the underlying
	 * Java object containing the value, V, is null.
	 * 
	 * @return the XACML String representation of the attribute's value
	 */
	public String toXacmlString();
	
	/**
	 * Validate the value stored in this AzAttributeValue instance
	 * against any internally specified constraints.
	 * <b>
	 * Subtype interfaces should override this method as it will 
	 * in general be implemented distinctively for each XACML
	 * DataType.
	 * <b>
	 * Default return value is true, if validate() not implemented.
	 * 
	 * @return true if valid; false if constraint violated
	 */
	public boolean validate();
}
