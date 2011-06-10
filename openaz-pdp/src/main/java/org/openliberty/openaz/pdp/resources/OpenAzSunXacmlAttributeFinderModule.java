
/*
 * @(#)TestAttributeFinderModule.java
 *
 * Copyright 2004 Sun Microsystems, Inc. All Rights Reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   1. Redistribution of source code must retain the above copyright notice,
 *      this list of conditions and the following disclaimer.
 * 
 *   2. Redistribution in binary form must reproduce the above copyright
 *      notice, this list of conditions and the following disclaimer in the
 *      documentation and/or other materials provided with the distribution.
 *
 * Neither the name of Sun Microsystems, Inc. or the names of contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 * 
 * This software is provided "AS IS," without a warranty of any kind. ALL
 * EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES, INCLUDING
 * ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE
 * OR NON-INFRINGEMENT, ARE HEREBY EXCLUDED. SUN MICROSYSTEMS, INC. ("SUN")
 * AND ITS LICENSORS SHALL NOT BE LIABLE FOR ANY DAMAGES SUFFERED BY LICENSEE
 * AS A RESULT OF USING, MODIFYING OR DISTRIBUTING THIS SOFTWARE OR ITS
 * DERIVATIVES. IN NO EVENT WILL SUN OR ITS LICENSORS BE LIABLE FOR ANY LOST
 * REVENUE, PROFIT OR DATA, OR FOR DIRECT, INDIRECT, SPECIAL, CONSEQUENTIAL,
 * INCIDENTAL OR PUNITIVE DAMAGES, HOWEVER CAUSED AND REGARDLESS OF THE THEORY
 * OF LIABILITY, ARISING OUT OF THE USE OF OR INABILITY TO USE THIS SOFTWARE,
 * EVEN IF SUN HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGES.
 *
 * You acknowledge that this software is not designed or intended for use in
 * the design, construction, operation or maintenance of any nuclear facility.
 */
package org.openliberty.openaz.pdp.resources;

import com.sun.xacml.finder.AttributeFinderModule;

import com.sun.xacml.EvaluationCtx;

import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.BagAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.attr.AttributeValue;

import com.sun.xacml.cond.EvaluationResult;

import com.sun.xacml.ctx.Status;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.Map;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openliberty.openaz.azapi.AzAttributeValue;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.AzAttributeFinder;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzAttribute;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectCodebase;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdAnyURI;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdRfc822Name;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdString;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdX500Name;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;

/**
 * An <code>AttributeFinderModule</code> adapted from the SunXacml
 * TestAttributeFinderModule.
 * <p>
 * This module implements the SunXacml findAttribute interface and
 * within that implementation invokes the AzApi findAttribute interface.
 * In other words this is a "double callback" whereby the implementation
 * of the SunXacml callback invokes the AzApi callback. The AzApi callback
 * is implemented in a separate class.
 * <p>
 * This module is a "sample" SunXacml AttributeFinder, which uses the
 * metadata passed in from caller of the SunXacml pdp.evaluate(reqCtx)
 * as subject-codebase named attributes, to access the AzApi AzRequestContext
 * in order to make an OpenAz AzAttributeFinder.findAttribute() call.
 * <p>
 * The plan for this module is to have it invoke an azapi
 * callback module, which may require some initialization
 * gimmickry.
 * <p>
 * Basically, what's required is that this module must have
 * access to:
 * <pre> 
 *   - the list of registered AzAttributeFinders at the
 *      AzApi layer, 
 *   - the list of AzRequestContext objects that are currently
 *      active, esp for multi-threading environments
 *   - a parameter passed with each call identifying the specific
 *      AzRequestContext to look up in the list, which is passed
 *      as an attribute in the subject-codebase
 *   - a parameter indicating which AzEntity is targeted as the
 *      destination for the requested attribute; this is partially
 *      determined by category, but if there are multiple instances
 *      of a given category for multi-request scenarios the azapi
 *      has to be told which one is of current interest. Again, this
 *      is accomplished via a parameter passed as an attribute
 *      in the subject codebase. 
 *
 * @author Seth Proctor
 * @author Rich Levinson
 */
public class OpenAzSunXacmlAttributeFinderModule<T extends Enum<T> & AzCategoryId>
	extends AttributeFinderModule
{

    /**
     * The example identifier this module supports
     */
    public static final String ROLE_IDENTIFIER =
        "urn:oasis:names:tc:xacml:1.0:example:attribute:role";
    
    /**
     * OpenAz example identifier
     */
    public static final String OPENAZ_TEST_IDENTIFIER =
    	"urn:openaz:privilege:frisbee";
    
    // the standard identifier for subject-id
    private static URI subjectIdentifier = null;

    /**
     * These URIs are for use getting context from AzService
     * environment in order to invoke AzApi callback
     */
    public static final String subjectCodebaseCategory =
    	AzCategoryIdSubjectCodebase.
    		AZ_CATEGORY_ID_SUBJECT_CODEBASE.toString();
    public static final String subjectCodebaseDataType =
    	AzDataTypeIdString.AZ_DATATYPE_ID_STRING.toString();
    public static final String subjectCodebaseIssuer =
    	"openaz:sunxacml:azserviceimpl";
    
    // attribute ids for attrs added to subject codebase,
    // that identify an AzEntity, where the value is the
    // value returned by getId() on AzEntity or AzRequestContext
    public static final String requestAzRequestContextAttributeId =
    	"urn:openaz:azrequestcontext:request:id";
    public static final String subjectAzEntityAttributeId =
    	"urn:openaz:azentity:subject:id";
    public static final String environmentAzEntityAttributeId =
    	"urn:openaz:azentity:environment:id";
    public static final String resourceAzEntityAttributeId =
    	"urn:openaz:azentity:resource:id";
    public static final String actionAzEntityAttributeId =
    	"urn:openaz:azentity:action:id";
    // initialize the standard subject identifier
    private static URI subjectCodebaseDataTypeURI = null;
    private static URI subjectCodebaseIssuerURI = null;
    private static URI subjectCodebaseCategoryURI = null;
    
    private static URI requestAzRequestContextAttributeIdURI = null;
    private static URI subjectAzEntityAttributeIdURI = null; 
    private static URI resourceAzEntityAttributeIdURI = null; 
    private static URI actionAzEntityAttributeIdURI = null; 
    private static URI environmentAzEntityAttributeIdURI = null; 
    
	private static final Map<Integer, String> 
	openAzAttrDesignatorToStringMap = 
        new HashMap<Integer,String>() {{
			put(AttributeDesignator.SUBJECT_TARGET,"Subject");
			put(AttributeDesignator.RESOURCE_TARGET,"Resource");
			put(AttributeDesignator.ACTION_TARGET,"Action");
			put(AttributeDesignator.ENVIRONMENT_TARGET,"Environment");
	}};
	Log log = LogFactory.getLog(this.getClass()); 
	static Log logStatic = LogFactory.getLog(
							OpenAzSunXacmlAttributeFinderModule.class);

	static {
        try {
            subjectIdentifier =
            	new URI(AzXacmlStrings.X_ATTR_SUBJECT_ID);
            
        	subjectCodebaseCategoryURI = 
        		new URI(subjectCodebaseCategory);
            subjectCodebaseDataTypeURI = 
            	new URI(subjectCodebaseDataType); 
        	subjectCodebaseIssuerURI =
        		new URI(subjectCodebaseIssuer);
        	
        	requestAzRequestContextAttributeIdURI = 
            	new URI(requestAzRequestContextAttributeId); 
            subjectAzEntityAttributeIdURI = 
            	new URI(subjectAzEntityAttributeId); 
            resourceAzEntityAttributeIdURI = 
            	new URI(resourceAzEntityAttributeId); 
            actionAzEntityAttributeIdURI = 
            	new URI(actionAzEntityAttributeId); 
            environmentAzEntityAttributeIdURI = 
            	new URI(environmentAzEntityAttributeId); 
        } catch (URISyntaxException urise) {
            logStatic.info("won't happen in this code");
        }
    };
    
    private Map<String,AzRequestContext> azRequestContextMap = null;
    private List<AzAttributeFinder> azAttributeFinderList = null;

    /**
     * Default constructor.
     */
    public OpenAzSunXacmlAttributeFinderModule() {

    }

    /**
     * Always returns true, since designators are supported.
     *
     * @return true
     */
    public boolean isDesignatorSupported() {
        return true;
    }

    /**
     * Returns only <code>SUBJECT_TARGET</code> since this module only
     * supports Subject attributes.
     *
     * @return a <code>Set</code> with an <code>Integer</code> of value
     *         <code>AttributeDesignator.SUBJECT_TARGET</code>
     */
    public Set getSupportedDesignatorTypes() {
        Set set = new HashSet();

        set.add(new Integer(AttributeDesignator.SUBJECT_TARGET));

        return set;
    }

    /**
     * Returns the one identifier this module supports.
     *
     * @return a <code>Set</code> containing <code>ROLE_IDENTIFIER</code>
     */
    public Set getSupportedIds() {
        Set set = new HashSet();

        set.add(ROLE_IDENTIFIER);

        return set;
    }

    /**
     * SunXacml AttributeFinderModule main interface.
     * <p>
     * The OpenAz Attr Finder uses specifically named subject-codebase
     * attributes in conjunction with a handle, azRequestContextMap,
     * that is externally initialized at creation to the broader
     * AzRequestContext to aid the attribute finding process,
     * for example the following shows how to get the attribute type
     * of the subject-codebase attr with id "...azentity:subject:id"
     * the value of which contains the id of the 
     * AzEntity<AzCategoryIdSubjectAccess> ref'd by the current req:
     * <pre>
     * 		EvaluationResult evalResult = 
     * 		    context.getSubjectAttribute(
     * 				subjectCodebaseDataTypeURI, 
     * 				subjectAzEntityAttributeIdURI, 
     * 				subjectCodebaseCategoryURI);
     * 		AttributeValue attrValue = evalResult.getAttributeValue();
     * 		if (attrValue.evaluatesToBag()) {
     * 			BagAttribute bagAttrValues = (BagAttribute) attrValue;
     * 			Iterator bagIt = bagAttrValues.iterator();
     * 			while (bagIt.hasNext()) {
     * 				AttributeValue bagAttrValue = (AttributeValue) bagIt.next();
     * 				if (log.isTraceEnabled()) log.trace(
     * 					"\n   bagAttrValue = " + bagAttrValue.encode());
     * 			}
     * 		}
     * </pre>
     * The above should evaluate to a bag attribute value type, then the
     * bag can be read for specific instances of the value found for the
     * given attributeId.
     * <p>
     * (Interface defn that follows is from SunXacml AttributeFinderModule)
     * 
     * @param attributeType the datatype of the attributes to find
     * @param attributeId the identifier of the attributes to find
     * @param issuer the issuer of the attributes, or null if unspecified
     * @param subjectCategory the category of the attribute if the designatorType 
     * 		is SUBJECT_TARGET, otherwise null
     * @param context the representation of the request data
     * @param designatorType the type of designator as named by the *_TARGET 
     * 		fields in AttributeDesignator
     * 
     * @return the result of attribute retrieval, which will be a bag of 
     * 		attributes or an error
     */
    public EvaluationResult findAttribute(URI attributeType, 
    									  URI attributeId,
                                          URI issuer, 
                                          URI subjectCategory,
                                          EvaluationCtx context,
                                          int designatorType) {
    	StringWriter sw = new StringWriter();
    	if (log.isInfoEnabled()) log.info(
    		"\n    CallBack (1st level) SunXacml AttrFinder Parameters: " +
				"\n\tattributeId = " + attributeId +
    			"\n\tattributeType = " + attributeType +
    			"\n\tsubjectCategory = " + subjectCategory +
    			"\n\tissuer = " + issuer +
    			"\n\tcontext.getResourceId() = " + 
					context.getResourceId().encode() +
				//"\n\t (indicative value from the context for ref)" +
    			"\n\tdesignatorType = " + new Integer(designatorType) + " (" + 
    				openAzAttrDesignatorToStringMap.get(
    						new Integer(designatorType)) + ")\n");
    	if (log.isTraceEnabled()) {
    		/* 
    		// use this dummy exception to get a stacktrace to find out
    		// where this finder was called from:
        	try {
        		throw new Exception();
        	}
        	catch (Exception e) {
    			e.printStackTrace(new PrintWriter(sw));
     			log.trace("stackTrace entering finder:\n" + sw); 		
        	}
        	*/
    		log.trace(
    		"\n   Get datatype of subject attribute" + 
    		 "\n\t w category: subject-codebase" + 
    		 "\n\t w attrId: ...azentity:subject-id" +
			"\n\tcontext.getSubjectAttribute(" + 
    		"\n\t\ttype = " + subjectCodebaseDataTypeURI.toString() + 
    		",\n\t\tid = " + subjectAzEntityAttributeIdURI.toString() + 
    		",\n\t\tcategory = " + subjectCodebaseCategoryURI.toString() + 
    		")." +
    		"\n\t\t\tgetAttributeValue().getClass().getName() = \n\t\t\t\t" +
				context.getSubjectAttribute(
    					subjectCodebaseDataTypeURI, 
    					subjectAzEntityAttributeIdURI, 
    					subjectCodebaseCategoryURI).
						getAttributeValue().getClass().getName());
    	}
    	
    	
    	// get the AzEntityId of the AzEntity to which the attribute
    	// being found by this method is associated.
		EvaluationResult evalResult = 
			context.getSubjectAttribute(
				subjectCodebaseDataTypeURI, 
				subjectAzEntityAttributeIdURI, 
				subjectCodebaseCategoryURI);
		AttributeValue attrValue = evalResult.getAttributeValue();
		if (log.isTraceEnabled()) log.trace(
			"\n\tattrValue.evaluatesToBag() = " +
				attrValue.evaluatesToBag());
		if (attrValue.evaluatesToBag()) {
			BagAttribute bagAttrValues = (BagAttribute) attrValue;
			Iterator bagIt = bagAttrValues.iterator();
			while (bagIt.hasNext()) {
				AttributeValue bagAttrValue = (AttributeValue) bagIt.next();
				if (log.isTraceEnabled()) log.trace(
					"\n " + subjectAzEntityAttributeIdURI + " = " +
					"\n\t(i.e. bagAttrValue = ) " + bagAttrValue.encode());
			}
		}

		evalResult = 
			context.getSubjectAttribute(
				subjectCodebaseDataTypeURI, 
				requestAzRequestContextAttributeIdURI, 
				subjectCodebaseCategoryURI);
		attrValue = evalResult.getAttributeValue();
		if (log.isTraceEnabled()) log.trace(
			"\n\tattrValue.evaluatesToBag() = " +
				attrValue.evaluatesToBag());
		AttributeValue bagAttrValue = null;
		if (attrValue.evaluatesToBag()) {
			BagAttribute bagAttrValues = (BagAttribute) attrValue;
			Iterator bagIt = bagAttrValues.iterator();
			while (bagIt.hasNext()) {
				bagAttrValue = (AttributeValue) bagIt.next();
				if (log.isTraceEnabled()) log.trace(
					"\n " + requestAzRequestContextAttributeIdURI + " = " +
					"\n\t(i.e. bagAttrValue = ) " + bagAttrValue.encode());
			}
		}
		
		AzRequestContext azReqCtx = null;
		//String azReqCtxId = bagAttrValue.toString();
		String azReqCtxId = bagAttrValue.encode();
		azReqCtx = azRequestContextMap.get(azReqCtxId);
		if (log.isTraceEnabled()) log.trace(
			"\n    azRequestContextMap: " +
				"\n\t keySet= " + azRequestContextMap.keySet() +
				"\n\t valueSet = " + azRequestContextMap.values() +
				"\n\t azReqCtxId = " + azReqCtxId);
		if ( ! (azReqCtx == null)  ) {
			if (log.isTraceEnabled()) log.trace(
					"\n\tazReqCtx.getId() = " + azReqCtx.getId());
		} else if (log.isTraceEnabled()) log.trace(
				"\n\tazReqCtx = null");

		// Need to set the appropriate AzCategoryId:
		//Enum<? extends AzCategoryId> t = null;
		
		// get the AzEntity
		// it is determined by combo of category that is requested
		// and the AzEntity id in the context
		AzEntity<?> azEntity = null;
		Set<AzEntity<? extends AzCategoryId>> azEntitySet = null;
		switch (designatorType) {
			case AttributeDesignator.ACTION_TARGET: 
				azEntity = azReqCtx.getAzEntity(
					AzCategoryIdAction.AZ_CATEGORY_ID_ACTION);
				azEntitySet = azReqCtx.getAzEntitySet(
						AzCategoryIdAction.AZ_CATEGORY_ID_ACTION);
				// TODO: test this code for multi-request (bulk-decide) use case
				for (AzEntity<? extends AzCategoryId> azEntityItem : azEntitySet) {
					AzAttribute<?> azAttributeItem =
						azEntityItem.getAttributeByAttribId(
							actionAzEntityAttributeId);
					if ( ! ( azAttributeItem == null ) ) {
						// match the arg value from the subject codebase in
						// find call to the arg value of the attr w same id
						// in the current AzEntity; if match this is the one
						// we want to pass along as the one that needs the
						// additional attribute.
						// for now just match automatically:
						azEntity = azEntityItem;
						break;
					}
				}
				break;
			case AttributeDesignator.RESOURCE_TARGET:
				azEntity = azReqCtx.getAzEntity(
					AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE);
				break;
			case AttributeDesignator.SUBJECT_TARGET:
				azEntity = azReqCtx.getAzEntity(
					AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS);
				break;
			case AttributeDesignator.ENVIRONMENT_TARGET:
				azEntity = azReqCtx.getAzEntity(
					AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT);
				break;
		}
		
		// try to call the azapi finder from here
		// get the AzRequestContext
			
		AzAttribute<?> azAttribute = null;
		String issuerName = null;
		if ( ! ( issuer == null ) ) {
			issuerName = issuer.toString();
		}
		if (log.isTraceEnabled()) log.trace(
			"\n    issuerName = " + issuerName);
		if (log.isTraceEnabled()) log.trace(
			"\n   attributeType = " + attributeType.toString() +
			"\n     Sample attr datatypes above type can match: " +
				"\n\t AzDataTypeIdString = " + 
					AzDataTypeIdString.AZ_DATATYPE_ID_STRING +
				"\n\t AzDataTypeIdAnyURI = " + 
					AzDataTypeIdAnyURI.AZ_DATATYPE_ID_ANYURI +
				"\n\t AzDataTypeIdRfc822Name = " + 
					AzDataTypeIdRfc822Name.AZ_DATATYPE_ID_RFC822NAME +
				"\n\t AzDataTypeIdX500Name = " + 
					AzDataTypeIdX500Name.AZ_DATATYPE_ID_X500NAME
				);
		
		// The following block of code determines what the DataType
		// of the requested attribute is and creates a null 
		// AzAttributeValue of that DataType to pass to the azapi
		// AzAttributeFinder; This enables the AzAttributeFinder
		// to know what DataType is needed.
		// TODO: should remove these null attributeValues from the
		// AzEntity attribute lists
		if (log.isTraceEnabled()) log.trace(
			"\n   Find the datatype of the attribute being looked for, " +
			"\n\tand create an attribute with null value to pass the " +
			"\n\tattribute metadata to the OpenAz AzAttributeFinder module");
		if (attributeType.toString().equals(
				AzDataTypeIdString.AZ_DATATYPE_ID_STRING.toString())) {
			if (log.isTraceEnabled()) log.trace(
				"\n\tPassed the equals(AzDataTypeIdString." + 
					"AZ_DATATYPE_ID_STRING.toString()) test");
			azAttribute = 
				azEntity.createAzAttribute(
					issuerName, 
					attributeId.toString(), 
					azEntity.createAzAttributeValue(
						AzDataTypeIdString.AZ_DATATYPE_ID_STRING,
						//new String("")));
						null));
		} else if (attributeType.toString().equals(
				AzDataTypeIdAnyURI.AZ_DATATYPE_ID_ANYURI.toString())) {
			if (log.isTraceEnabled()) log.trace(
				"\n\tPassed the equals(AzDataTypeIdAnyURI." + 
					"AZ_DATATYPE_ID_ANYURI.toString()) test");
			azAttribute = 
				azEntity.createAzAttribute(
					issuerName, 
					attributeId.toString(), 
					azEntity.createAzAttributeValue(
						AzDataTypeIdAnyURI.AZ_DATATYPE_ID_ANYURI,
						null));
		} else if (attributeType.toString().equals(
				AzDataTypeIdRfc822Name.AZ_DATATYPE_ID_RFC822NAME)) {
			if (log.isTraceEnabled()) log.trace(
				"\n\tPassed the equals(AzDataTypeIdRfc822Name." + 
					"AZ_DATATYPE_ID_RFC822NAME) test");
			azAttribute = 
				azEntity.createAzAttribute(
					issuerName, 
					attributeId.toString(), 
					azEntity.createAzAttributeValue(
						AzDataTypeIdRfc822Name.AZ_DATATYPE_ID_RFC822NAME,
						null));
		} else if (attributeType.toString().equals(
				AzDataTypeIdRfc822Name.AZ_DATATYPE_ID_RFC822NAME.toString())) {
			if (log.isTraceEnabled()) log.trace(
				"\n    Passed the equals(AzDataTypeIdRfc822Name." + 
					"AZ_DATATYPE_ID_RFC822NAME.toString()) test");
			azAttribute = 
				azEntity.createAzAttribute(
					issuerName, 
					attributeId.toString(), 
					azEntity.createAzAttributeValue(
						AzDataTypeIdRfc822Name.AZ_DATATYPE_ID_RFC822NAME,
						null));
		} else if (attributeType.toString().equals(
				AzDataTypeIdX500Name.AZ_DATATYPE_ID_X500NAME.toString())) {
			if (log.isTraceEnabled()) log.trace(
				"\n    Passed the equals(AzDataTypeIdX500Name." + 
					"AZ_DATATYPE_ID_X500NAME.toString()) test");
			azAttribute = 
				azEntity.createAzAttribute(
					issuerName, 
					attributeId.toString(), 
					azEntity.createAzAttributeValue(
						AzDataTypeIdX500Name.AZ_DATATYPE_ID_X500NAME,
						null));
		} else if (log.isTraceEnabled()) log.trace(
				"\n    attributeType not supported (yet): " + 
							attributeType.toString());
		if ( ! (azEntity == null) ) {
			if (log.isTraceEnabled()) log.trace(
					"\n\tazEntity.getId() = " + azEntity.getId());
		} else if (log.isTraceEnabled()) log.trace("azEntity = null");
		if ( ! (azAttribute == null) ) {
			if (log.isTraceEnabled()) log.trace(
				"\n   About to use AzAttributeFinder.findAttribute() callback " +
					"to get this attribute: " +
					"\n\tazAttribute.getAttributeId() = " + 
					azAttribute.getAttributeId() +
					"\n\tazAttribute.getAzCategoryId() = " +
					azAttribute.getAzCategoryId().toString() +
					"\n\tazAttribute.getType() = " +
					azAttribute.getAzAttributeValue().getType().toString() +
					"\n\tazAttribute.getAzAttributeValue().toXacmlString() = " +
					azAttribute.getAzAttributeValue().toXacmlString() + "\n");
		} else if (log.isTraceEnabled()) log.trace(
				"\n\tazAttribute = null");
		
		// The following code calls the azapi AzAttributeFinder
		// by going thru the List of AzAttributeFinders that
		// are configured in the azAttributeFinderList member 
		// variable.
		// TODO: this code needs to be cleaned up for proper list
		// handling; right now there is only one finder configured
		// so the loop processing is only executed one time thru.
		Collection<AzAttribute> foundAttributes = null;
		if ( ! (azAttributeFinderList == null ) ) {
			Iterator<AzAttributeFinder> itAttrFinder = 
				azAttributeFinderList.iterator();
			
			for (AzAttributeFinder azAttrFinder : azAttributeFinderList) {
				if (log.isTraceEnabled()) log.trace(
					"\n   Call the OpenAz AzAttributeFinder callback using:" +
					"\n\t" + azAttrFinder.getClass().getName());
				foundAttributes = azAttrFinder.findAzAttribute(
									azReqCtx, azEntity, azAttribute);
			}
		} else {
			if (log.isTraceEnabled()) log.trace(
				"\n    No registered azapi AzAttributeFinders found." +
					azAttributeFinderList);
		}
		if (log.isTraceEnabled()) log.trace(
				"\n    foundAttributes = " + foundAttributes);
		
        // make sure this is the identifier we support
		// need to replace this code w something "real"
		// note: the code from the example was more instructive
		// in the sense that role mapping is potentially useful
		// here; but for now w azapi, it is just a test value.
        //if (! attributeId.toString().equals(ROLE_IDENTIFIER)) {
		if (log.isTraceEnabled()) log.trace(
			"\n   This SunXacml AttributeFinder is only a sample that " +
			"\n\tlooks for one dummy attribute with id: " + 
			OPENAZ_TEST_IDENTIFIER);
        if (! attributeId.toString().equals(OPENAZ_TEST_IDENTIFIER)) {
        	if (log.isTraceEnabled()) log.trace(
        		"\n    Returning empty bag: " +
        			"\n\twas expecting request for attributeId: " +
        			//ROLE_IDENTIFIER + 
        			OPENAZ_TEST_IDENTIFIER + 
        			"\n\t but received request for: " + attributeId.toString());
            return new EvaluationResult(BagAttribute.
                                        createEmptyBag(attributeType));
        }
        else if (log.isTraceEnabled()) log.trace(
        	"\n    got expected identifier: " + attributeId.toString());

        // the following code is ultra-restrictive just to get
        // some test data thru; a lot more can be done here
        // make sure we've been asked for a string
        if (! attributeType.toString().equals(StringAttribute.identifier)) {
        	if (log.isTraceEnabled()) log.trace(
        		"\n\tReturning empty bag, no attributes found.\n");
            return new EvaluationResult(BagAttribute.
                                        createEmptyBag(attributeType));
        }
        else if (log.isTraceEnabled()) log.trace(
        		"\n    got expected string identifier: " + 
        				attributeType.toString());

        // retrieve the subject identifier from the context
        EvaluationResult result =
            context.getSubjectAttribute(attributeType, subjectIdentifier,
                                        issuer, subjectCategory);
        if (log.isTraceEnabled()) log.trace(
        	"\n    Args passed to getSubjectAttribute: " + 
        		"\n\tattributeType =     " + attributeType + 
        		"\n\tsubjectIdentifier = " + subjectIdentifier + 
        		"\n\tissuer =            " + issuer +
        		"\n\tsubjectCategory =   " + subjectCategory);
        
        if ( ! ( result == null) ) {	
        	if (log.isTraceEnabled()) log.trace(
        		"\n    result.getStatus() = " + result.getStatus() +
        			 "\n\tresult.toString() = " + result.toString());
            if (result.indeterminate()) {
            	if (log.isTraceEnabled()) log.trace(
            		"\n   result from getSubjectAttribute is indeterminate\n");
                return result;
            }
        } else if (log.isTraceEnabled()) log.trace(
        		"\n    result = " + result);
 
        // check that we succeeded in getting the subject identifier
        BagAttribute bag = (BagAttribute)(result.getAttributeValue());
        if (bag.isEmpty()) {
        	if (log.isTraceEnabled()) log.trace(
        		"\n\tbag is empty - returning missing attribute " +
        			 "for sunxacml test code for now\n");
            ArrayList code = new ArrayList();
            code.add(Status.STATUS_MISSING_ATTRIBUTE);
            Status status = new Status(code, "missing subject-id");
            return new EvaluationResult(status);
        }
        
        // This is where we have to put the "foundAttributes" into
        // the return structure:
        // Collection<AzAttribute> foundAttributes = null;
        
        // finally, look for the subject who has the role-mapping defined,
        // and if they're the identified subject, add their role
        BagAttribute returnBag = null;
        Iterator it = bag.iterator();
        while (it.hasNext()) {
            StringAttribute attr = (StringAttribute)(it.next());
            if (log.isTraceEnabled()) log.trace(
            		"\n\tattr.getValue() = " + attr.getValue());
            //if (attr.getValue().equals("Julius Hibbert")) {
            // the following is only test code that looks for
            // a user subject-id of "fred", and if found returns
            // a string of "throw" (set by the AzAttributeFinder)
            // in a StringAttribute that will be incorporated to
            // the pdp evaluation; this is all rigged for a single
            // passthru to work, but can easily be generalized.
            if (attr.getValue().equals("fred")) {
            	if (log.isTraceEnabled()) log.trace(
            		"\n\tgot the needed subject-id: " + 
            			attr.getValue());
                Set set = new HashSet();
                Iterator<AzAttribute> itFound = foundAttributes.iterator();
                AzAttribute foundAttr = null;
                AzAttributeValue foundAttrValue = null;
                while (itFound.hasNext()) {
                	foundAttr = itFound.next();
                	foundAttrValue = foundAttr.getAzAttributeValue();
                	String foundAttrValueData = 
                		(String) foundAttrValue.getValue();
                	set.add(new StringAttribute(foundAttrValueData));
                	if (log.isTraceEnabled()) log.trace(
                		"\n    Return StringAttribute " + "" +
                	  		"w foundAttrValueData: " + 
                			foundAttrValueData);
                }
                //set.add(new StringAttribute("Physician"));
                returnBag = new BagAttribute(attributeType, set);
                break;
            }
            else
            	if (log.isTraceEnabled()) log.trace(
            		"\n\tdid not get needed subject-id, fred, got: " + 
            		attr.getValue());
        }
        if ( ! ( returnBag == null ) ) {
        	if (log.isTraceEnabled()) log.trace(
        			"\n    returnBag.size() = " + returnBag.size() + "\n");
        } else if (log.isTraceEnabled()) log.trace(
        		"returnBag = " + returnBag + "\n");

        return new EvaluationResult(returnBag);
    }
    
    public void setAzRequestContextMap(
    		Map<String, AzRequestContext> azRequestContextMap) {
    	this.azRequestContextMap = azRequestContextMap;
    	if (log.isTraceEnabled()) log.trace(
    			"\n    azRequestContextMap has been initialized.");
    }
    public void setAzAttributeFinderList(
    		List<AzAttributeFinder> azAttributeFinderList) {
    	this.azAttributeFinderList = azAttributeFinderList;
    	if (log.isTraceEnabled()) log.trace(
    		"\n    azAttributeFinderList has been initialized: \n\t" +
    			azAttributeFinderList);
    }

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}


