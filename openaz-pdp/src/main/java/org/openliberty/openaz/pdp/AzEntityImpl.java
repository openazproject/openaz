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

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.HashSet;
import java.net.URI;
import javax.security.auth.x500.X500Principal;

import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;

public class AzEntityImpl<T extends Enum<T> & AzCategoryId> 
		implements AzEntity<T> {
	
	Log log = LogFactory.getLog(this.getClass());
	static int attributeCounter = 0; // use for debugging
	
	String azEntityId = ""; // empty unless set for Obligation	
	int idEntityCounter;	// caller-specified identifier:
	String id;				// string value of idEntityCounter
	T t;					// CategoryId
	
	// use attributes for action, resource, subject, env
	Set<AzAttribute<T>> attributes = null;	
	// use mixedAttributes for obligations and status detail
	Set<AzAttribute<?>> mixedAttributes = null;

	/**
	 * Constructor:
	 * @param t the AzCategoryId for this AzEntity
	 * @param idEntityCounter a provider-specific counter to aid tracing
	 */
	public AzEntityImpl(T t, int idEntityCounter) {
		this.t = t;
		this.idEntityCounter = idEntityCounter;
		// create a String identifier that may be used to reference
		// this instance of the object specifying that this is an
		// AzEntity, of a specific AzCategoryId, with a counter to
		// diffentiate the identifier
		this.id = "AzEntity(" + 
				   t.getClass().getSimpleName() + 
				  ")-" + 
				  new Integer(idEntityCounter).toString();
		if (log.isTraceEnabled()) log.trace(
			"\n============================================" + 
			"=============================================" +
			"\n   Constructor executed and " +
			"created new AzEntity<" + t.getClass().getSimpleName() + "> with: " +
			"\n\tAzCategoryId type:  " + t +
			"\n\tAzCategoryId class: " + t.getClass() +
			"\n\tAzEntity class:     " + this.getClass().getSimpleName() +
			"<" + t.getClass().getSimpleName() + ">" +
			"\n\tAzEntity.getId():   " + this.getId() +
			"\n\tidEntityCounter:    " + idEntityCounter +
			"\n============================================" + 
			  "=============================================\n");
		if (t.equals(AzCategoryIdObligation.AZ_CATEGORY_ID_OBLIGATION) || 
			t.equals(AzCategoryIdStatusDetail.AZ_CATEGORY_ID_STATUSDETAIL) ) {
			mixedAttributes = new HashSet<AzAttribute<?>>();
		}
		else {
			attributes = new HashSet<AzAttribute<T>>();
		}
	}
	
	public T getAzCategoryId() {
			return t;
	}
	
	public String getAzEntityId() {
		return azEntityId;
	}
	
	/**
	 * Provider implementation to allow setting ObligationId
	 * for current AzEntity only if AzCategoryId is Obligation.
	 * Does not allow setting for other AzCategoryId values.
	 * 
	 * Note: It's a hack, but any generalization appears to be 
	 * more work than reasonable. We may want to consider
	 * an enhanced AzEntity interface that allows decorating
	 * AzEntity<AzCategoryId ??? > for only specific categories.
	 * Right now there are slightly different semantics around the 
	 * AzEntity as a function of AzCategoryId. There is a tradeoff
	 * between generalization and specialization to analyze.
	 * i.e. do we define separate objects for the slight differences,
	 * and how does that work around generics. It's done w the
	 * different AzDataTypes, but this one requires a new
	 * method, and input and can't be buried in the impl.
	 * @param azObligationId
	 */
	public void setAzEntityId(String azObligationId){
		if (getAzCategoryId().equals(
				AzCategoryIdObligation.AZ_CATEGORY_ID_OBLIGATION)) {
			this.azEntityId = azObligationId;
		}
	}
	public void setId(String id){
		this.id = id;
	}
	public String getId(){
		return id;
	}
	public int getIdEntityCounter(){
		return idEntityCounter;
	}
	public Set<AzAttribute<T>> getAzAttributeSet(){
		return attributes;
	}
	public Set<AzAttribute<?>> getAzAttributeMixedSet(){
		return mixedAttributes;
	}
	
	public <U extends Enum<U> & AzDataTypeId, V>
		AzAttribute<T> createAzAttribute(
			String issuer, 
			String attributeId,  
			AzAttributeValue<U,V> attributeValue){
		if (log.isTraceEnabled()) log.trace(
			"\n  AzEntityImpl.createAzAttribute: creating attribute: " +
				"\n\t attributeId: " + attributeId +
				"\n\t categoryId: " + this.t +
				"\n\t issuer: " + issuer +
				"\n\t attributeValueType: " + attributeValue.getType() +
				"\n\t attributeValueXacmlString: " + attributeValue.toXacmlString());
		
		AzAttribute<T> testAzAttribute = new AzAttributeImpl<T,U,V> (
				getAzCategoryId(),
				issuer, attributeId, attributeValue);
		if (t.equals(AzCategoryIdObligation.AZ_CATEGORY_ID_OBLIGATION) || 
			t.equals(AzCategoryIdStatusDetail.AZ_CATEGORY_ID_STATUSDETAIL) ) {
			mixedAttributes.add(testAzAttribute);
		}
		else {
			attributes.add(testAzAttribute);
		}
		attributeCounter++;
		if (log.isTraceEnabled()) log.trace(
				"\n--------------------------------------------" + 
				"---------------------------------------------" +
				"\n   Created and added AzAttribute: " + 
				"\n\tto AzEntity with id:   " +
					this.getId() +
				"\n\tAttribute category:    " + 
					testAzAttribute.getAzCategoryId() +
				"\n\tAttribute issuer:      " + 
					testAzAttribute.getAttributeIssuer() +
				"\n\tAttributeValue Type:   " + 
					testAzAttribute.getAzAttributeValue().getType() +
				"\n\tAttribute attributeId: " + 
					testAzAttribute.getAttributeId() + 
				"\n\tAttributeValue Value:  " +
					testAzAttribute.getAzAttributeValue().toXacmlString() +
				"\n\t attributeCounter:     " + attributeCounter +
				"\n--------------------------------------------" + 
				"---------------------------------------------\n");
		return testAzAttribute;
	}
	
	// TODO: following calls apparently have extraneous parameter, u,
	// which appears not to be needed. This was probably artifact of
	// refactoring several releases back. Need to get to essence of issue.
	// It appears to be redundant with the return type.
	
	public	AzAttributeValueAnyURI createAzAttributeValue(
			AzDataTypeIdAnyURI u, URI v){
		AzAttributeValueAnyURI azAttrValAnyURI = 
			new AzAttributeValueAnyURIImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(AzDataTypeIdAnyURI u, URI v) " +
			"\n\treturning AzAttributeValueAnyURI");
		return azAttrValAnyURI;
	}

	public AzAttributeValueBase64Binary createAzAttributeValue(
		AzDataTypeIdBase64Binary u, AzDataByteArray v){
		AzAttributeValueBase64Binary azAttrValBase64Binary = 
			new AzAttributeValueBase64BinaryImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(" + 
			"AzDataTypeIdBase64Binary u, AzDataByteArray v) " + 
			"\n\treturning AzAttributeValueBase64Binary");
		return azAttrValBase64Binary;
	}
	
	public AzAttributeValueBoolean createAzAttributeValue(
			AzDataTypeIdBoolean u, Boolean v){
		AzAttributeValueBoolean azAttrValBoolean = 
			new AzAttributeValueBooleanImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(AzDataTypeIdBoolean u, Boolean v) " +
			"\n\t  azAttrValBoolean.getType() = " + azAttrValBoolean.getType() +
			"\n\t  azAttrValBoolean.getValue() = " + azAttrValBoolean.getValue() +
			"\n\treturning AzAttributeValueBoolean");
		return azAttrValBoolean;
	}
	
	public AzAttributeValueDate createAzAttributeValue(
		AzDataTypeIdDate u, AzDataDateTime v){
		AzAttributeValueDate azAttrValDate = 
			new AzAttributeValueDateImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(AzDataTypeIdDate u, AzDataDateTime v) " + 
			"\n\treturning AzAttributeValueDate");
		return azAttrValDate;
	}
	
	public AzAttributeValueDateTime createAzAttributeValue(
		AzDataTypeIdDateTime u, AzDataDateTime v){
		AzAttributeValueDateTime azAttrValDateTime = 
			new AzAttributeValueDateTimeImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" +
			"\n\t%%%%%%%%%%%%%   using typesafe method   %%%%%%%%%%%%" +
			"\n\t% AzAttributeValueDateTime createAzAttributeValue( %" +
			"\n\t%%%  AzDataTypeIdDateTime u, AzDataDateTime v); %%%%" +
			"\n\t%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		if (log.isTraceEnabled()) log.trace(
			"\n\treturning AzAttributeValueDateTime");
		return azAttrValDateTime;
	}
	
	public AzAttributeValueDayTimeDuration createAzAttributeValue(
		AzDataTypeIdDayTimeDuration u, AzDataDayTimeDuration v){
		AzAttributeValueDayTimeDuration azAttrValDayTimeDuration = 
			new AzAttributeValueDayTimeDurationImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(" + 
			"AzDataTypeIdDayTimeDuration u, AzDataDayTimeDuration v) " + 
			"\n\treturning AzAttributeValueDayTimeDuration");
		return azAttrValDayTimeDuration;
	}
	
	public AzAttributeValueDnsName createAzAttributeValue(
		AzDataTypeIdDnsName u, String v){
		AzAttributeValueDnsName azAttrValDnsName = 
			new AzAttributeValueDnsNameImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(AzDataTypeIdDnsName u, String v) " + 
			"\n\treturning AzAttributeValueDnsName");
		return azAttrValDnsName;
	}
	
	public AzAttributeValueDouble createAzAttributeValue(
		AzDataTypeIdDouble u, Double v){
		AzAttributeValueDouble azAttrValDouble = 
			new AzAttributeValueDoubleImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(AzDataTypeIdDouble u, Double v) " + 
			"\n\treturning AzAttributeValueDouble");
		return azAttrValDouble;
	}
	
	public AzAttributeValueHexBinary createAzAttributeValue(
		AzDataTypeIdHexBinary u, AzDataByteArray v){
		AzAttributeValueHexBinary azAttrValHexBinary = 
			new AzAttributeValueHexBinaryImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(" + 
			"AzDataTypeIdHexBinary u, AzDataByteArray v) " + 
			"\n\treturning AzAttributeValueHexBinary");
		return azAttrValHexBinary;
	}
	
	public AzAttributeValueInteger createAzAttributeValue(
		AzDataTypeIdInteger u, Long v){
		AzAttributeValueInteger azAttrValInteger = 
			new AzAttributeValueIntegerImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(AzDataTypeIdInteger u, Long v) " + 
			"\n\treturning AzAttributeValueInteger");
		return azAttrValInteger;
	}
	
	public AzAttributeValueIpAddress createAzAttributeValue(
		AzDataTypeIdIpAddress u, String v){
		AzAttributeValueIpAddress azAttrValIpAddress = 
			new AzAttributeValueIpAddressImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(AzDataTypeIdIpAddress u, String v) " + 
			"\n\treturning AzAttributeValueIpAddress");
		return azAttrValIpAddress;
	}
	
	public AzAttributeValueRfc822Name createAzAttributeValue(
		AzDataTypeIdRfc822Name u, String v){
		AzAttributeValueRfc822Name azAttrValRfc822Name = 
			new AzAttributeValueRfc822NameImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(AzDataTypeIdRfc822Name u, String v) " + 
			"\n\treturning AzAttributeValueRfc822Name");
		return azAttrValRfc822Name;
	}
	
	public AzAttributeValueString createAzAttributeValue(
			AzDataTypeIdString u, String v){
		AzAttributeValueString azAttrValString =
			new AzAttributeValueStringImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%" +
			"\n\t%%%%%%%%%%%%%   using typesafe method   %%%%%%%%%%" +
			"\n\t% AzAttributeValueString createAzAttributeValue( %" +
			"\n\t%%%%    AzDataTypeIdString u, String v);     %%%%%" +
			"\n\t%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%");
		if (log.isTraceEnabled()) log.trace(
			"\n\treturning AzAttributeValueString: " + v);
		return azAttrValString;
	}
	
	public AzAttributeValueTime createAzAttributeValue(
		AzDataTypeIdTime u, AzDataDateTime v){
		AzAttributeValueTime azAttrValTime = 
			new AzAttributeValueTimeImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(AzDataTypeIdTime u, AzDataDateTime v) " + 
			"\n\treturning AzAttributeValueTime");
		return azAttrValTime;
	}
	
	public AzAttributeValueX500Name createAzAttributeValue(
		AzDataTypeIdX500Name u, X500Principal v){
		AzAttributeValueX500Name azAttrValX500Name = 
			new AzAttributeValueX500NameImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(AzDataTypeIdX500Name u, X500Principal v) " + 
			"\n\treturning AzAttributeValueX500Name");
		return azAttrValX500Name;
	}
	
	public AzAttributeValueYearMonthDuration createAzAttributeValue(
		AzDataTypeIdYearMonthDuration u, AzDataYearMonthDuration v){
		AzAttributeValueYearMonthDuration azAttrValYearMonthDuration = 
			new AzAttributeValueYearMonthDurationImpl(v);
		if (log.isTraceEnabled()) log.trace(
			"\n\t.createAzAttributeValue(" + 
			"AzDataTypeIdYearMonthDuration u, AzDataYearMonthDuration v) " + 
			"\n\treturning AzAttributeValueYearMonthDuration");
		return azAttrValYearMonthDuration;
	}

	/**
	 * This looks like it should work. Only question is if it is worth
	 * the aggravation to replace all the specific signatures. It may
	 * well be, because it will be able to catch stuff that is passed
	 * that does not have supported sigs.
	 * <p>
	 * Also, and maybe more important, it will be extensible by
	 * providers to allow different impl objects.
	 * <p>
	 * Note: it appears that this method will be called in
	 * 	precedence over the explicit signature methods, unless
	 * the args to the explicit sig methods are totally
	 * unambiguous. i.e. if you have an unknown object coming
	 * in and you test its class using instanceof, then even
	 * if the class is correct, this generic will be called.
	 * To do instanceof you need to recast the variable
	 * when you call createAzAttribute( azDataTypeId, (Cast) object)
	 * @param <U>
	 * @param <V>
	 * @param u
	 * @param v
	 * @return an AzAttributeValue<U,V>, where U is Xacml DataType,
	 * 	and V is a corresponding Java object type
	 */
	//@SuppressWarnings("unchecked")
	public <U extends Enum<U> & AzDataTypeId, V>
			AzAttributeValue<U,V> createAzAttributeValue(U u, V v){
		AzAttributeValue<U,V> azAttributeValue = null;
		if (log.isTraceEnabled()) log.trace(
			"\n\t&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&" +
			"\n\t&&&    using extraneous method       &&&" +
			"\n\t&&& createAzAttributeValue(U u, V v) &&&" +
			"\n\t&&& u = " + u.getClass().getName() + " &&&" +
			"\n\t&&& v = " + v.getClass().getName() + " &&&" +
			"\n\t&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&");
		if (u.equals(
				AzDataTypeIdYearMonthDuration.AZ_DATATYPE_ID_YEARMONTHDURATION) 
			&& (v instanceof AzDataYearMonthDuration) ) {
				if (log.isTraceEnabled()) log.trace("Test 1");
				AzAttributeValueYearMonthDuration azAttrValYearMonthDuration = 
					new AzAttributeValueYearMonthDurationImpl(
						(AzDataYearMonthDuration) v);
				//@SuppressWarnings("unchecked")
				//AzAttributeValue<U,V> azAttributeValueTemp = 
				//	(AzAttributeValue<U,V>) azAttrValYearMonthDuration;
				//azAttributeValue = azAttributeValueTemp;
				if (log.isTraceEnabled()) log.trace(
					"Test 1: \n\t AttributeValue Type = " + 
						azAttrValYearMonthDuration.getType() +
					"\n\t   Value = " + 
						azAttrValYearMonthDuration.getValue() + 
					"\n\t   toXacmlString = " + 
						azAttrValYearMonthDuration.toXacmlString());
			
		} else if (u.equals(AzDataTypeIdString.AZ_DATATYPE_ID_STRING)
			&& (v instanceof String) ) {
				if (log.isTraceEnabled()) log.trace("Test 2");
				if (log.isTraceEnabled()) log.trace(
						"Test 2: Should not come thru here since this is " +
						 "explicit supported type combo: " + 
						 "\n\t u = " + u.getClass().getName() +
						 "\n\t v = " + v.getClass().getName());
			
		} else if (u.equals(AzDataTypeIdString.AZ_DATATYPE_ID_STRING)
			&& (v instanceof URI) ) {
				if (log.isTraceEnabled()) log.trace("Test 3");
				if (log.isTraceEnabled()) log.trace(
					"Test 3: Example of specific non-supported type combo: " +
					 "\n\t u = " + u.getClass().getName() +
					 "\n\t v = " + v.getClass().getName());
			
		} else {
			if (log.isTraceEnabled()) log.trace(
				"Unsupported Attribute value type:" +
					"\n\t Type class u: " + u.getClass().getName() +
					"\n\t\t Type: " + u +
					"\n\t Value class v: " + v.getClass().getName() + 
					"\n\t\t Value: " + v);
		}
		if (log.isTraceEnabled()) log.trace(
				"Exiting: createAzAttributeValue(U u, V v)");
		return azAttributeValue;
	}

	
	public <V extends Enum<V> & AzCategoryId>
	boolean addAzAttribute(AzAttribute<V> azAttribute){
		boolean result = false;
		if (log.isTraceEnabled()) log.trace(
			"Adding pre-existing attribute of any category, V, " + 
				"to Obligation or StatusDetail: " +
				"\n\t azCategoryId: " + azAttribute.getAzCategoryId() +
				"\n\t issuer: " + azAttribute.getAttributeIssuer() +
				"\n\t attributeId: " + azAttribute.getAttributeId());
		
		//TODO: this doesn't look right, should be looking at
		// categoryId of the current AzEntity, to see if we
		// can add the attribute of pre-defined category-id.
		V categoryId = azAttribute.getAzCategoryId();
                
        if (categoryId.equals(
	        		AzCategoryIdObligation.AZ_CATEGORY_ID_OBLIGATION) ||
	            categoryId.equals(
	            	AzCategoryIdStatusDetail.AZ_CATEGORY_ID_STATUSDETAIL) ) {
			mixedAttributes.add(azAttribute);
			result = true;
        } else {
        	if (log.isTraceEnabled()) log.trace(
        		"Cannot add attribute w pre-existing category" +
        			"to any other than obligation or statusdetail");
		}
                
		return result;
	}
	public <U extends Enum<U> & AzDataTypeId, V, W extends Enum<W> & AzCategoryId> 
	boolean addAzAttribute(
			W w,
			String issuer,
			String attributeId, 
			AzAttributeValue<U, V> attributeValue){
		boolean result = false;
		if (log.isTraceEnabled()) log.trace(
			"  TestAzEntity.addAzAttribute: adding attribute: " +
				"\n\t azCategoryId: " + w +
				"\n\t issuer: " + issuer +
				"\n\t attributeId: " + attributeId);
		if (w.equals(AzCategoryIdObligation.AZ_CATEGORY_ID_OBLIGATION) || 
			w.equals(AzCategoryIdStatusDetail.AZ_CATEGORY_ID_STATUSDETAIL) ) {
			createAzAttribute(issuer, attributeId, attributeValue);
			result = true;
		}
		return result;
	}	
	
	public AzAttribute<T> getAttributeByAttribId(String attributeId){
		Set<AzAttribute<T>> attrSet = getAzAttributeSetByAttribId(attributeId);
		AzAttribute<T> azAttr = null;
		for (AzAttribute<T> azNext: attrSet){
			azAttr = azNext;
		}
		return azAttr;
	}
	
	public Set<AzAttribute<T>> getAzAttributeSetByAttribId(String attributeId){
		return getAttributeByAttribId(attributeId, attributes);
	}
	public Set<AzAttribute<T>> getAttributeByAttribId(String attributeId,
			Set<? extends AzAttribute<T>> attributeSet){
		if (log.isTraceEnabled()) log.trace(
			"\n  .getAttributeByAttribId(" + attributeId + 
			") \n\t(this.entityId = " + id + ")");
		Set<AzAttribute<T>> setMatchedAttrs = new HashSet<AzAttribute<T>>();
		AzAttribute<T> attr = null;
		AzAttribute<T> attrMatch = null;
		Iterator<? extends AzAttribute<T>> it = attributeSet.iterator();
		while (it.hasNext()){
			attr = it.next();
			if (log.isTraceEnabled()) log.trace(
				"\n    in loop w attrId = " + attr.getAttributeId());
			if (attr.getAttributeId() == attributeId)
			{
				if (log.isTraceEnabled()) log.trace(
					"\n    .getAttributeByAttribId() found attribute with: " +
					"\n\t attributeId: " + attributeId +
					"\n\t  class:      " + attr.getAzAttributeValue().getClass() +
					"\n\t  value:      " + attr.getAzAttributeValue().getValue());
				attrMatch = attr;
				setMatchedAttrs.add(attrMatch);
			}
			else {
				if (log.isTraceEnabled()) log.trace(
					"\n    .getAttributeByAttribId() " + 
					"did not find attribute with: " +
					"\n\t attributeId: " + attributeId +
					"\n\t  class:      " + attr.getAzAttributeValue().getClass() +
					"\n\t  value:      " + 
					attr.getAzAttributeValue().getValue());

			}
		}
		if (attrMatch == null) {
			if (log.isTraceEnabled()) log.trace(
					"\n    .getAttributeByAttribId() " +
					"did not find attribute: " +
					"\n\twith attributeId = " + attributeId +
					"\n\tin AzEntity: " + this.getId());
		}
		else {
			if (log.isTraceEnabled()) log.trace(
					"\n    .getAttributeByAttribId() " +
					" found attributes: " + setMatchedAttrs);
		}
		return setMatchedAttrs;
	}
	
	// Helper class create methods
	public AzDataDateTime createAzDataDateTime(
			Date date,
			int actualTimeZone,
			int intendedTimeZone,
			int nanoSeconds){
		if (log.isTraceEnabled()) log.trace(
			"\n   .createAzDataDateTime(" + date +")");
		AzDataDateTimeImpl testAzDataDateTime = 
			new AzDataDateTimeImpl(
					date, actualTimeZone, intendedTimeZone, nanoSeconds);
		return testAzDataDateTime;
	}
	
	// TODO: implement
	public AzDataDayTimeDuration createAzDataDayTimeDuration(
			boolean isNegativeDuration,
			long days,
			long hours,
			long minutes,
			long seconds,
			int nanoSeconds){
		AzDataDayTimeDurationImpl testAzDataDayTimeDuration = null;
		return testAzDataDayTimeDuration;
	}
	
	// TODO: implement
	public AzDataYearMonthDuration createAzDataYearMonthDuration(
			boolean isNegativeDuration,
			long years,
			long months){
		AzDataYearMonthDurationImpl testAzDataYearMonthDuration = null;
		return testAzDataYearMonthDuration;
	}
	
	public AzDataByteArray createAzDataByteArray(
			byte[] byteArray){
		AzDataByteArrayImpl azDataByteArray = 
			new AzDataByteArrayImpl(byteArray);
		return azDataByteArray;
	}

	/*
	public AzAttributeValueDateTime createAzAttributeValueDateTime(){
		return new TestAzAttributeValueDateTime();	
	}
	public AzAttributeValueDateTime createAzAttributeValueDateTime(
			Date date, int nanoseconds,
			int timeZone, int defaultedTimeZone){
		return new TestAzAttributeValueDateTime(
				date, nanoseconds, timeZone, defaultedTimeZone);	
	}
	
	public AzAttributeValueTime createAzAttributeValueTime(){
		return new TestAzAttributeValueTime();	
	}
	public AzAttributeValueTime createAzAttributeValueTime(
			Date date, int nanoseconds,
			int timeZone, int defaultedTimeZone){
		return new TestAzAttributeValueTime(
				date, nanoseconds, timeZone, defaultedTimeZone);	
	}
	
	public AzAttributeValueDate createAzAttributeValueDate(){
		return new TestAzAttributeValueDate();
	}
	public AzAttributeValueDate createAzAttributeValueDate(
			Date date, int timeZone, int defaultedTimeZone){
		return new TestAzAttributeValueDate(
				date, timeZone, defaultedTimeZone);
	}
	
	public AzAttributeValueString createAzAttributeValueString(
			String s){
		return new TestAzAttributeValueString(s);
	}
	
	public AzAttributeValueAnyURI createAzAttributeValueAnyURI(
			URI uri){
		return new TestAzAttributeValueAnyURI(uri);
	}
	
	public AzAttributeValueBoolean createAzAttributeValueBoolean(
			Boolean b){
		return new TestAzAttributeValueBoolean(b);
	}
	
	public AzAttributeValueInteger createAzAttributeValueInteger(
			Long i){
		return new TestAzAttributeValueInteger(i);
	}
	
	public AzAttributeValueDouble createAzAttributeValueDouble(
			Double d){
		return new TestAzAttributeValueDouble(d);
	}
	
	public AzAttributeValueHexBinary createAzAttributeValueHexBinary(
			AzDataByteArray azBA){
		return new TestAzAttributeValueHexBinary(azBA);
	}
	
	public AzAttributeValueBase64Binary createAzAttributeValueBase64Binary(
			AzDataByteArray azBA){
		return new TestAzAttributeValueBase64Binary(azBA);
	}
	
	public AzAttributeValueDayTimeDuration createAzAttributeValueDayTimeDuration(
			AzDataDayTimeDuration azDTD){
		return new TestAzAttributeValueDayTimeDuration(azDTD);
	}

	public AzAttributeValueYearMonthDuration 
		createAzAttributeValueYearMonthDuration(
			AzDataYearMonthDuration azYMD){
		return new TestAzAttributeValueYearMonthDuration(azYMD);
	}
	
	public AzAttributeValueX500Name createAzAttributeValueX500Name(
			X500Principal x500Principal){
		return new TestAzAttributeValueX500Name(x500Principal);
	}
	
	public AzAttributeValueRfc822Name createAzAttributeValueRfc822Name(
			String rfc822Name){
		return new TestAzAttributeValueRfc822Name(rfc822Name);
	}
	
	public AzAttributeValueIpAddress createAzAttributeValueIpAddress(
			String ipAddress){
		return new TestAzAttributeValueIpAddress(ipAddress);
	}
	
	public AzAttributeValueDnsName createAzAttributeValueDnsName(
			String dnsName){
		return new TestAzAttributeValueDnsName(dnsName);
	}
	*/

	/*
	//public abstract void setAzAttribute(AzAttribute attributeTuple);
	public void setTestAzAttribute(AzAttribute attributeTuple)
	{
		attributes.add(attributeTuple);
		System.out.println("TestAzEntity.setAzAttribute: \n\t Added attribute: " +
			attributeTuple.getAttributeId());
		return;
	}
	public boolean add(AzAttribute attributeTuple){
		attributes.add(attributeTuple);
		System.out.println("TestAzEntity.add: \n\t Added attribute: " + 
			attributeTuple.getAttributeId());
		return true;
	}
	/*
	public Iterator<AzAttribute> iterator(){
		return attributes.iterator();
	}
	public int size(){
		return attributes.size();
	}
	*/

}
