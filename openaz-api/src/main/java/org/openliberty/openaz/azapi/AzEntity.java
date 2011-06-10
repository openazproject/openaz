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

import java.util.Date;
import java.util.Set;
import java.net.URI;
import javax.security.auth.x500.X500Principal;

import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectCodebase;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectIntermediary;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectRecipient;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectRequestingMachine;
import org.openliberty.openaz.azapi.constants.AzCategoryIdStatusDetail;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;
import org.openliberty.openaz.azapi.constants.AzDataTypeId;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdAnyURI;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdBase64Binary;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdBoolean;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdDate;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdDateTime;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdDayTimeDuration;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdDnsName;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdDouble;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdHexBinary;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdInteger;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdIpAddress;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdRfc822Name;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdString;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdTime;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdX500Name;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdYearMonthDuration;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;

/**
 * The AzEntity Interface represents a XACML collection of
 * attributes. This is a generic interface that is used as the
 * basis for holding collections of attributes where they appear
 * in both the AzRequestContext and AzResponseContext.
 * <p>
 * The AzEntity Interface also contains factory methods for
 * creating AzAttributes that will be directly added the current
 * AzEntity object. It also contains factory methods for creating
 * the AzAttributeValue objects with the associated XACML AzDataTypeId
 * datatypes and the related Java or AzData* objects used to
 * create the XACML datatypes.
 * <p>
 * AzEntity is also is used to hold the collections of attributes
 * that are returned in the AzResponseContext (AzStatusDetail for
 * a collection of missing attributes reported by the PDP, and
 * AzObligation, which contains a set of AzAttributes that are
 * used to inform the PEP of responsibilities associated with
 * processing the Results of an authorization request.
 *<p>
 * Basically, in the XACML model, there are four "categories" of
 * entities defined that are part of a XACML Request,
 * each of which can contain a collection of attributes:
 * <ul>
 * <li> Subject: {@link AzEntity}<{@link AzCategoryIdSubjectAccess}>
 * <li> Resource: {@link AzEntity}<{@link AzCategoryIdResource}>
 * <li> Action: {@link AzEntity}<{@link AzCategoryIdAction}>
 * <li> Environment: {@link AzEntity}<{@link AzCategoryIdEnvironment}>
 * </ul>
 * In addition, the XACML model identifies four other kinds of
 * "Subject" entities that are represented each by their own
 * AzCategoryId Enum, which can appear in any request, and in
 * any number for multiple decision requests:
 * <ul>
 * <li> SubjectCodeBase: {@link AzEntity}<{@link AzCategoryIdSubjectCodebase}>
 * <li> SubjectIntermediary: {@link AzEntity}<{@link AzCategoryIdSubjectIntermediary}>
 * <li> SubjectRecipient: {@link AzEntity}<{@link AzCategoryIdSubjectRecipient}>
 * <li> SubjectRequestingMachine: {@link AzEntity}<{@link AzCategoryIdSubjectRequestingMachine}>
 * </ul>
 * Note: for the AzEntity objects with AzCategoryId Enum equal to one
 * of the above, only AzAttributes with that same AzCategoryId Enum
 * are allowed in the collection. i.e. the AzEntity and all the
 * contained AzAttributes have the same AzCategoryId Enum with no
 * exceptions. This collection may be accessed via the getAzAttributeSet()
 * method.
 * <p>
 * Note: for AzEntity objects with AzCategoryId Enum equal to one of
 * the following below, the collection may contain any AzAttribute with
 * any AzCategoryId Enum. This collection may be accessed via the
 * getAzAttributeMixedSet() method.
 * <p>
 * There are two "categories" of entities that contain a collection
 * of mixed AzAttributes that are returned by the PDP in a
 * XACML Response element:
 * <ul>
 * <li> StatusDetail: {@link AzEntity}<{@link AzCategoryIdStatusDetail}>
 * <li> Obligation:   {@link AzEntity}<{@link AzCategoryIdObligation}>
 * </ul>
 * TBD: there are other "categories" as well in XACML, for example in
 * the RBAC Profile and the Delegation profile for XACML 3.0. How and
 * whether to incorporate those categories to AzApi is an open issue.
 * <p>
 * TBD: should the createAzAttributeValue methods throw Exceptions
 * based on processing the input values, or can it be assumed the
 * object containing the input value has already handled any
 * exception conditions?
 */
public interface AzEntity<T extends Enum<T> & AzCategoryId> {
	
	/**
	 * Returns the AzCategoryId of this AzEntity. Depending on
	 * the specific subclass, this AzCategoryId may or may not 
	 * govern the AzCategoryId of the member attributes. 
	 * <p>
	 * Generally, for the AzRequestContext collections, this 
	 * AzCategoryId specifies the allowed AzCategoryId for the 
	 * member attributes. For the AzResponseContext collections
	 * (AzObligation, AZStatusDetail) this AzCategoryId only 
	 * indicates the type of collection, and the member attributes
	 * can have any AzCategoryId. 
	 * 
	 * @return an Enum that implements {@link AzCategoryId}
	 */
	public T getAzCategoryId();
		
	/**
	 * Returns the XACML ObligationId xml attribute for 
	 * an AzEntity&lt;AzCategoryIdObligation&gt;.
	 * Returns an empty string otherwise.
	 * @return a non-empty string only for Obligations
	 */
	public String getAzEntityId();
	
	/**
	 * Sets the XACML ObligationId xml attribute for
	 * an AzEntity&lt;AzCategoryIdObligation&gt;, 
	 * If this AzEntity is of another AzCategoryId, then
	 * the AzEntityId has no meaning in a XACML sense
	 * and is not propagated when the AzEntity is serialized.
	 * @param azEntityId a URI string for a XACML Obligation 
	 */
	public void setAzEntityId(String azEntityId);
	
	/**
	 * Get a local non-XACML implementation-specific id that
	 * can be used to distinguish this AzEntity object from
	 * other AzEntity objects. This is primarily intended to
	 * be an id used for operations personnel to assist in
	 * identifying AzEntity objects.
	 * <p>
	 * An alternative approach might be to have specific "official"
	 * AzAttributes used for such purposes. XACML provides ids,
	 * such as subject-id, resource-id, and action-id, however,
	 * these ids are tied somewhat to XACML semantics, and
	 * enterprises may want to define a specific id which could
	 * be applied in any category, possibly category-specific,
	 * which could be used to correlate the entities in the
	 * authorization requests for administrative purposes.
	 * The id provided by this method could be used to access
	 * the "special id" if it existed within the attribute collection
	 * or it could be implemented independently of the attributes
	 * as well.
	 * @return a String containing an identifier associated with
	 * this AzEntity object.
	 * @see AzXacmlStrings#X_ATTR_ACTION_ID
	 */
	public String getId();
	
	/**
	 * Creates a general AzAttribute with the AzCategoryId Enum, T, 
	 * specified for this AzEntity object, and add it to the 
	 * Set<AzAttribute<T>> managed by this AzEntity object.
	 * <p>
	 * The AzApi caller must provide an optional issuer, a required
	 * AttributeId, and a required AzAttributeValue containing 
	 * an AzDataTypeId Enum identifying the XACML datatype of this
	 * attribute, and a value for this attribute in an appropriate
	 * object, determined by the createAzAttributeValue(U,V) signature,
	 * where U is the XACML datatype, and V is the corresponding 
	 * object type required to provide the value for an attribute
	 * of that XACML datatype.
	 * <p>
	 * When an attribute is created within an AzEntity object, it
	 * inherits the AzCategoryId Enum from the AzEntity object.
	 * <p>
	 * @param issuer
	 * @param attributeId a URI string
	 * @param attributeValue
	 * @return a new AzAttribute representing category, issuer, 
	 * attributeId, and attributeValue provided
	 */
	public <U extends Enum<U> & AzDataTypeId, V> 
	 		AzAttribute<T> createAzAttribute(
	 				String issuer,
	 				String attributeId, 
	 				AzAttributeValue<U, V> attributeValue);
	
	/**
	 * Get any attribute in the current collection by specifying
	 * its AttributeId.
	 * <p>
	 * Note: In the case where an attribute is multi-valued, this
	 * method returns only one of the values, and which value may
	 * vary if the method is called multiple times. Therefore, if
	 * the AzApi caller needs information whether there are 
	 * additional values, and what those values are, then the
	 * method getAzAttributeSetByAttribId(String s) should be
	 * used, which returns all the values with a specific XACML
	 * AttributeId.
	 * 
	 * @param s a String containing a XACML AttributeId
	 * @return any AzAttribute from this AzEntity object that
	 * matches the current XACML AttributeId; if none match,
	 * returns null.
	 * @see #getAzAttributeSetByAttribId(String)
	 */
	public AzAttribute<T> getAttributeByAttribId(String s);
	
	/**
	 * Get the Set of attributes in this collection that match
	 * the provided XACML AttributeId.
	 * <p>
	 * In general, XACML attributes may be multi-valued. The way
	 * this is represented by AzApi is that a separate AzAttribute
	 * instance exists for each value.
	 * 
	 * @param s a String containing the XACML AttributeId being requested
	 * @return the Set of AzAttributes that have an AttributeId 
	 * that matches the String provided.
	 */
	public Set<AzAttribute<T>> getAzAttributeSetByAttribId(String s);
	
	/**
	 * Get the Set that holds the attributes in this collection
	 * <p>
	 * Note: this method returns a Set&lt;AzAttribute&lt;T&gt;&gt;
	 * that contains only AzAttributes with the AzCategoryId 
	 * Enum designated for this AzEntity object.
	 * 
	 * @return a SetAzAttribute&lt;T&gt; which may be empty or 
	 * contain one or more AzAttributes&lt;T&gt;; null if this 
	 * AzEntity has only mixed attributes
	 */
	public Set<AzAttribute<T>> getAzAttributeSet();
	
	/**
	 * Get the Set that holds the attributes in this collection
	 * <p>
	 * Note: this method returns a Set&lt;AzAttribute&lt;?&gt;&gt; 
	 * that contains AzAttributes that may individually have 
	 * any AzCategoryId Enum, 
	 * which may be the same as or different from the AzCategory 
	 * Enum designated for this AzEntity object.
	 * <p>
	 * Note: A specific AzEntity may have AzAttributes either in the
	 * mixed attribute collection or the uniform attribute collection
	 * but not both. The "other" collection will always be null.
	 * 
	 * @return a SetAzAttribute&lt;?&gt; which may be empty or 
	 * contain one or more AzAttributes&lt;?&gt;; null if this 
	 * AzEntity has only uniform attributes
	 */
	public Set<AzAttribute<?>> getAzAttributeMixedSet();
	
	
	/**
	 * Add an existing AzAttribute to the current collection of 
	 * AzAttributes in this AzEntity object. 
	 * <p>
	 * For most AzEntity objects the AzCategoryId Enum, V, must match
	 * that of the AzEntity.getAzCategoryId(), T. However, for 
	 * AzEntity&lt;AzCategoryIdStatusDetail&gt; 
	 * and AzEntity&lt;AzCategoryIdObligation&gt;, 
	 * AzAttributes of any category are allowed in the collection.
	 * @param <V> the AzCategoryId Enum of azAttribute
	 * @param azAttribute
	 * @return a boolean true if azAttribute added, false if not
	 * <dt><b>See Also:</b>
	 * <dd> {@link AzEntity}<{@link AzCategoryIdObligation}>
	 * <dd> {@link AzEntity}<{@link AzCategoryIdStatusDetail}>
	 * 
	 */
	public <V extends Enum<V> & AzCategoryId>
		boolean addAzAttribute(AzAttribute<V> azAttribute);
	
	/**
	 * Add a new AzAttribute to the current collection of 
	 * AzAttributes in this AzEntity object. 
	 * <p>
	 * For most AzEntity objects the AzCategoryId Enum, W, must match
	 * that of the AzEntity.getAzCategoryId(). However, for 
	 * AzEntity&lt;AzCategoryIdStatusDetail&gt; 
	 * and AzEntity&lt;AzCategoryIdObligation&gt;, 
	 * AzAttributes of any category are allowed.
	 * @param <U> A Java type containing the value of the attribute
	 * @param <V> An AzDataTypeId Enum representing a XACML DataType
	 * @param <W> An AzCategoryId Enum representing a XACML Category
	 * @param w an instance of W
	 * @param issuer a String identifying the Issuer of this attribute
	 * @param attributeId
	 * @param attributeValue an AzAttributeValue
	 * @return boolean true if attribute added, false if not
	 */
	public <U extends Enum<U> & AzDataTypeId, 
	V, W extends Enum<W> & AzCategoryId> 
		boolean addAzAttribute(
				W w,
				String issuer,
				String attributeId, 
				AzAttributeValue<U, V> attributeValue);

	/**
	 * Factory method to create an AzAttributeValue<S,U>
	 * <p>
	 *  define S, a Java impl type for AzAttributeValues,
	 *  and V, an AzDataTypeId, which are both input parameters,
	 *  and U, a return type AzAttributeValue
	 */
	//public <U extends Enum<U> & AzDataTypeId, V> 
	//	AzAttributeValue<U, V> createAzAttributeValue(U u, V v);
		
	
	/**
	 * Generic version of createAzAttributeValue. This method
	 * will be called if the values of U and V are not a
	 * pair that has an explicit factory signature defined
	 * in the specific 
	 * <code>createAzAttributeValue(AzDataTypeIdSubtype, SpecificJavaType)</code>
	 * signatures in this AzEntity interface.
	 * <p>
	 * Implementations must return a null value if the
	 * requested subtypes are not supported. 
	 * <p>
	 * Implementations MAY return custom subtypes specifically 
	 * supported by the implementation that are additional
	 * alternatives to the subtypes defined by the signatures
	 * in AzEntity.
	 * @param u a subtype of AzDataTypeId or a custom subtype 
	 * extension supported by the implementation
	 * @param v a Java type associated with the AzDataTypeId,
	 * which generally will be that associated with one of the
	 * AzEntity.createAttributeValue(u,v) signatures or MAY be
	 * a custom pair defined by a specific implementation.
	 * 
	 * @return an AzAttributeValue&lt;U,V&gt; containing the
	 * AzDataType and Java object value supplied in the params.
	 */
	public <U extends Enum<U> & AzDataTypeId, V>
		AzAttributeValue<U,V> createAzAttributeValue(U u, V v);

	
	/**
	 * Returns an AzAttributeValue object that holds a #anyURI type 
	 * and value, that can be used with an AzAttribute of 
	 * any AzCategoryId.
	 * 
	 * @param u an AzDataTypeIdAnyURI Enum
	 * @param v a URI providing a value for this AzAttributeValue object
	 * @return
	 * <dt><b>See Also:</b>
	 * <dd> {@link AzAttributeValueAnyURI}
	 * <dd> {@link AzAttributeValue}
	 * @see #createAzAttribute createAzAttribute(String, String, AzAttributeValue)
	 */
	public	AzAttributeValueAnyURI createAzAttributeValue(
			AzDataTypeIdAnyURI u, URI v);

	
	/**
	 * Returns an AzAttributeValue object that holds a AzDataByteArray type 
	 * and value, that can be used with an AzAttribute of any 
	 * AzCategoryId.
	 * 
	 * @param u an AzDataTypeIdBase64Binary Enum
	 * @param v an AzDataByteArray containing the Base64 binary data
	 * @return a new AzAttributeValueBase64Binary representing the
	 * Base64 binary data provided in the AzDataByteArray
	 * @see AzAttributeValueBase64Binary
	 * @see AzAttributeValue
	 */
	public AzAttributeValueBase64Binary createAzAttributeValue(
			AzDataTypeIdBase64Binary u, AzDataByteArray v);
	//AzAttributeValueBase64Binary createAzAttributeValueBase64Binary(
	//		AzDataByteArray azDataByteArray);

	
	/**
	 * Returns an AzAttributeValue object that holds a Boolean type 
	 * and value, that can be used with an AzAttribute of any 
	 * AzCategoryId.
	 * 
	 * @param u an AzDataTypeIdBoolean Enum
	 * @param v a Boolean with the value that will be set for this attribute 
	 * @return a new AzAttributeValueBoolean representing the 
	 * Boolean value provided
	 * @see AzAttributeValueBoolean
	 * @see AzAttributeValue
	 */
	public AzAttributeValueBoolean createAzAttributeValue(
			AzDataTypeIdBoolean u, Boolean v);
	
	/**
	 * Returns a general AzAttributeValueDate object that holds a 
	 * #date type and value, that can be used with an AzAttribute of 
	 * any AzCategoryId.
	 * @return 		an AzAttributeValueDate instance
	 * @see AzAttributeValueDate
	 */
	//TBD: replace public AzAttributeValueDate createAzAttributeValueDate();
	 
	/**
	 * Returns a general AzAttributeValueDate object that holds a 
	 * #date type and value, that can be used with an AzAttribute of 
	 * any AzCategoryId.
	 * @param u an AzDataTypeIdDate Enum
	 * @param v an AzDataDateTime object containing the value 
	 * provided for this attribute
	 * @return 		an AzAttributeValueDate instance representing the 
	 * value provided for this attribute
	 * @see AzAttributeValueDate
	 */
	public AzAttributeValueDate createAzAttributeValue(
			AzDataTypeIdDate u, AzDataDateTime v);
	
	
	/**
	 * Returns a general AzAttributeValueDateTime object that holds a 
	 * #dateTime type and value, that contains the date and time
	 * specified in the input parameters. This object can be used 
	 * with an AzAttribute of any AzCategoryId.
	 * @return 		an AzAttributeValueDateTime instance
	 * @see AzAttributeValueDateTime
	 */
	//TBD: replace: AzAttributeValueDateTime createAzAttributeValueDateTime();
	
	/**
	 * Returns a general AzAttributeValueDateTime object that holds a 
	 * #dateTime type and value, that can be used with an AzAttribute of 
	 * any AzCategoryId.
	 * @param u an AzDataTypeIdDateTime Enum
	 * @param v an AzDataDateTime object containing the value provided
	 * @return 	an AzAttributeValueDateTime instance representing the 
	 * value provided
	 * @see AzAttributeValueDateTime
	 */
	public AzAttributeValueDateTime createAzAttributeValue(
			AzDataTypeIdDateTime u, AzDataDateTime v);
	

	/**
	 * Returns an AzAttributeValueDayTimeDuration object that
	 * holds a XACML #dayTimeDuration DataType.
	 * @param u an AzDataTypeIdDayTimeDuration Enum
	 * @param v an AzDataDayTimeDuration object providing a value
	 * @return	an AzAttributeValueDayTimeDuration representing
	 * the provided value.
	 */
	public AzAttributeValueDayTimeDuration createAzAttributeValue(
			AzDataTypeIdDayTimeDuration u, AzDataDayTimeDuration v);
	
	
	/**
	 * Returns an AzAttributeValue object that holds a String type 
	 * and value, that can be used with an AzAttribute of any 
	 * AzCategoryId.
	 * 
	 * @param u an AzDataTypeIdDnsName Enum
	 * @param v a String providing the value for this AzAttributeValue object
	 * @return an AzAttributeValueDnsName representing the value
	 * provided
	 * @see AzAttributeValueDnsName
	 * @see AzAttributeValue
	 */
	public AzAttributeValueDnsName createAzAttributeValue(
			AzDataTypeIdDnsName u, String v);
	
	
	/**
	 * Returns an AzAttributeValue object that holds a Double type 
	 * and value, that can be used with an AzAttribute of any 
	 * AzCategoryId.
	 * 
	 * @param u an AzDataTypeIdDouble Enum
	 * @param v a Double providing the value for this 
	 * AzAttributeValue object
	 * @return an AzAttributeValueDouble representing the value
	 * provided
	 * @see AzAttributeValueDouble
	 * @see AzAttributeValue
	 */
	public AzAttributeValueDouble createAzAttributeValue(
			AzDataTypeIdDouble u, Double v);

	
	/**
	 * Returns an AzAttributeValue object that holds a AzDataByteArray type 
	 * and value, that can be used with an AzAttribute of any 
	 * AzCategoryId.
	 * 
	 * @param u an AzDataTypeIdHexBinary Enum
	 * @param v an AzDataByteArray containing binary data
	 * @return a new AzAttributeValueHexBinary representing the 
	 * binary data provided in the AzDataByteArray 
	 * @see AzAttributeValueHexBinary
	 * @see AzAttributeValue
	 */
	public AzAttributeValueHexBinary createAzAttributeValue(
			AzDataTypeIdHexBinary u, AzDataByteArray v);

	
	/**
	 * Returns an AzAttributeValue object that holds an Integer type 
	 * and value, that can be used with an AzAttribute of any 
	 * AzCategoryId.
	 * 
	 * @param u an AzDataTypeIdInteger Enum
	 * @param v a Long containing an integer
	 * @return a new AzAttributeValueInteger representing the
	 * value of the integer provided.
	 * @see AzAttributeValueInteger
	 * @see AzAttributeValue
	 */
	public AzAttributeValueInteger createAzAttributeValue(
			AzDataTypeIdInteger u, Long v);
	
	
	/**
	 * Returns an AzAttributeValue object that holds a String type 
	 * and value, that can be used with an AzAttribute of any 
	 * AzCategoryId.
	 * 
	 * @param u an {@link AzDataTypeIdIpAddress} Enum
	 * @param v a String value provided to be used to create the IpAddress
	 * @return a new AzAttributeValueIpAddress representing the
	 * value of the String provided
	 * @see AzAttributeValueIpAddress
	 * @see AzAttributeValue
	 */
	public AzAttributeValueIpAddress createAzAttributeValue(
			AzDataTypeIdIpAddress u, String v);
	//AzAttributeValueIpAddress createAzAttributeValueIpAddress(String s);
	
	
	/**
	 * Returns an AzAttributeValue object that holds a String type 
	 * and value, that can be used with an AzAttribute of any 
	 * AzCategoryId.
	 * 
	 * @param u an AzDataTypeIdRfc822Name Enum
	 * @param v a String value representing an RFC822 name
	 * @return a new AzAttributeValueRfc822Name representing the
	 * String value provided
	 * @see AzAttributeValueRfc822Name
	 * @see AzAttributeValue
	 */
	public AzAttributeValueRfc822Name createAzAttributeValue(
			AzDataTypeIdRfc822Name u, String v);
	//AzAttributeValueRfc822Name createAzAttributeValueRfc822Name(String s);
	
	
	/**
	 * Returns an AzAttributeValue object that holds a String type 
	 * and value, that can be used with an AzAttribute of any 
	 * AzCategoryId.
	 * 
	 * @param u an AzDateTypeIdString Enum
	 * @param v a String value
	 * @return a new AzAttributeValueString object representing the
	 * String value provided.
	 * @see AzAttributeValueString
	 * @see AzAttributeValue
	 */
	public AzAttributeValueString createAzAttributeValue(
			AzDataTypeIdString u, String v);
	//AzAttributeValueString createAzAttributeValueString(String s);
	
	
	/**
	 * Returns an AzAttributeValueTime object that holds a 
	 * #time type and the value of the current time, that can be 
	 * used with an AzAttribute of any AzCategoryId.  
	 * @return 		an AzAttributeValueTime instance
	 * @see AzAttributeValueTime
	 */
	//AzAttributeValueTime createAzAttributeValueTime();
	 
	/**
	 * Returns an AzAttributeValueTime object that holds a 
	 * #time type and value, where that value was established by
	 * the parameters passed in this method. that can be used 
	 * with an AzAttribute of any AzCategoryId.
	 * @param u an AzDateTypeIdTime Enum
	 * @param v an AzDataDateTime object
	 * @return 		an AzAttributeValueTime instance representing
	 * the parameters provided in the AzDataDateTime object provided
	 * @see AzAttributeValueTime
	 */
	public AzAttributeValueTime createAzAttributeValue(
			AzDataTypeIdTime u, AzDataDateTime v);
	//AzAttributeValueTime createAzAttributeValueTime(
	//		Date date, int nanoseconds, int timeZone, 
	//		int defaultedTimeZone);
	 
	
	/**
	 * Returns an AzAttributeValue object that holds a #x500Name type 
	 * and value, that can be used with an AzAttribute of 
	 * any AzCategoryId.
	 * @param u an AzDataTypeIdX500Name Enum
	 * @param v an X500Principal object
	 * @return anew AzAttributeValueX500Name representing the 
	 * X500Principal provided
	 * @see AzAttributeValueX500Name
	 * @see AzAttributeValue
	 */
	public AzAttributeValueX500Name createAzAttributeValue(
			AzDataTypeIdX500Name u, X500Principal v);
	//AzAttributeValueX500Name createAzAttributeValueX500Name(
	//		X500Principal x500Principal);
	
	
	/**
	 * Returns an AzAttributeValueYearMonthDuration object that
	 * holds a XACML #yearMonthDuration DataType.
	 * 
	 * @param u an AzDataTypeIdYearMonthDuration Enum
	 * @param v an AzDataYearMonthDuration object providing the duration data 
	 * to be used for the value
	 * @return	an AzAttributeValueYearMonthDuration
	 */
	public AzAttributeValueYearMonthDuration createAzAttributeValue(
			AzDataTypeIdYearMonthDuration u, AzDataYearMonthDuration v);
	//AzAttributeValueYearMonthDuration createAzAttributeValueYearMonthDuration(
	//		AzDataYearMonthDuration azDataYearMonthDuration);
	

	/**
	 * Returns a helper data container for XACML #date,
	 * #time, and #date-time datatypes
	 * 
	 * @param date
	 * @param actualTimeZone
	 * @param intendedTimeZone
	 * @param nanoSeconds
	 * @return an AzDataDateTime that can be used as input
	 * to creating Date and Time AzAttributeValues
	 * @see AzAttributeValueDate
	 * @see AzAttributeValueDateTime
	 * @see AzAttributeValueTime
	 * 
	 */
	AzDataDateTime createAzDataDateTime(
			Date date,
			int actualTimeZone,
			int intendedTimeZone,
			int nanoSeconds);
	
	/**
	 * Returns a helper data container for XACML #dayTimeDuration
	 * DataType
	 * 
	 */
	AzDataDayTimeDuration createAzDataDayTimeDuration(
			boolean isNegativeDuration,
			long days,
			long hours,
			long minutes,
			long seconds,
			int nanoSeconds);
	/**
	 * Returns a helper data container for Xacml #yearMonthDuration
	 */
	AzDataYearMonthDuration createAzDataYearMonthDuration(
			boolean isNegativeDuration,
			long years,
			long months);
	/**
	 * Returns a helper data container for boxed Java byte[] arrays
	 */
	AzDataByteArray createAzDataByteArray(
			byte[] byteArray);
	
}
