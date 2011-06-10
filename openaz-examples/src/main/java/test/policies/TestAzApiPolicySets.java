package test.policies;


/*
 * @(#)SamplePolicyBuilder.java
 *
 * Copyright 2003-2004 Sun Microsystems, Inc. All Rights Reserved.
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


import com.sun.xacml.Indenter;
import com.sun.xacml.Obligation;
import com.sun.xacml.PolicySet;
import com.sun.xacml.Policy;
import com.sun.xacml.Rule;
import com.sun.xacml.Target;
import com.sun.xacml.TargetMatch;
import com.sun.xacml.UnknownIdentifierException;

import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.attr.AttributeValue;

import com.sun.xacml.attr.AnyURIAttribute;
import com.sun.xacml.attr.RFC822NameAttribute;
import com.sun.xacml.attr.StringAttribute;
import com.sun.xacml.attr.X500NameAttribute;

import com.sun.xacml.combine.CombiningAlgFactory;
import com.sun.xacml.combine.DenyOverridesPolicyAlg;
import com.sun.xacml.combine.OrderedPermitOverridesRuleAlg;
import com.sun.xacml.combine.OrderedPermitOverridesPolicyAlg;
import com.sun.xacml.combine.RuleCombiningAlgorithm;
import com.sun.xacml.combine.PolicyCombiningAlgorithm;

import com.sun.xacml.cond.Apply;
import com.sun.xacml.cond.Function;
import com.sun.xacml.cond.FunctionFactory;

import com.sun.xacml.ctx.Result;
import com.sun.xacml.ctx.Attribute;

import java.net.URI;
import java.net.URISyntaxException;

import javax.security.auth.x500.X500Principal;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pdp.provider.SimpleConcreteSunXacmlService;

import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.ArrayList;
import java.util.HashSet;


/**
 * This is an adaptation of the sunxacml SamplePolicyBuilder to
 * build some policies to use with the TestAzApi OpenAz test
 * program. The original class was copied as is, renamed, and
 * then modified where necessary to achieve the desired
 * policies.
 * <p>
 * This class may be used as a template for policy-building,
 * at least for policies that do not have complex conditions.
 * Currently, this class uses a simple paradigm that contains
 * a template for Match functions, which refer to an attribute
 * in the context and compare it to a local value in the Match
 * function. i.e. ActionMatch, ResourceMatch, SubjectMatch.
 * Environment attrs only can be ref'd in Conditions, because
 * sunxacml implements XACML 1.1, which does not all Environment
 * in the Target.
 * <p>
 * The basic recursive structure is initiated by the method called from main:
 * <pre>
 * 
 * 		{@link #createPolicySet(PolicySetEnum)}
 * </pre>
 * <p>
 * Each of these methods constructs a series of Match expressions
 * which are used to identify attributes and a match condition
 * to apply to the attribute. In the case of Obligations, there is
 * no condition to apply to the attribute so the MatchId is null.
 * <p>
 * This is an example program that shows how to build and generate an XACML
 * Policy. This doesn't show all the available features, but it provides a
 * good sample of commonly used functionality. Note that there's a fair amount
 * of duplicate code in this class, since verbosity (in this case) makes the
 * code a little easier to understand. An equivalent Policy to that generated
 * here is found in the policy directory as generated.xml. The generated
 * request can be used with this policy.
 * <p>
 * The Policy primarily uses the "OR" function to create disjoint
 * groupings that can be incorporated to one Policy. They could
 * also be put in a separate policies in a PolicySet (need to test),
 * but the underlying constraint in sunxacml is that only one
 * Policy may be "applicable". This is sensible assuming that this
 * is the top level PDP that makes the final decision. However,
 * it is not clear yet if this has any practical restrictions on
 * how policies may be defined when they are part of PolicySets,
 * etc. i.e. is the constraint really that only one "PolicySet"
 * may be applicable? If so, then this would be ok.
 *
 * @since 1.1
 * @author seth proctor
 * @author rich levinson
 */
public class TestAzApiPolicySets {
	public static int
		OPENAZ_OBLIGATION_DESIGNATOR = 101; // pick a number large enough not to
											// overlap sunxacml designators
	public static String 
		XACML_TYPE_ANYURI = 
			"http://www.w3.org/2001/XMLSchema#anyURI";
	public static String
		XACML_TYPE_STRING = 
			"http://www.w3.org/2001/XMLSchema#string";
	public static String
		XACML_TYPE_RFC822 = 
			"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name";
	public static String
		XACML_TYPE_X500NAME =
			"urn:oasis:names:tc:xacml:1.0:data-type:x500Name";
	public static String
		XACML_FUNC_ANYURI_EQUAL =
			"urn:oasis:names:tc:xacml:1.0:function:anyURI-equal";
	public static String
		XACML_FUNC_REGEXP_STRING_MATCH =
			"urn:oasis:names:tc:xacml:1.0:function:regexp-string-match";
	public static String
		XACML_FUNC_STRING_EQUAL =
			"urn:oasis:names:tc:xacml:1.0:function:string-equal";
	public static String
		XACML_FUNC_RFC822_MATCH =
			"urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match";
	public static String
		XACML_FUNC_X500NAME_MATCH =
			"urn:oasis:names:tc:xacml:1.0:function:x500Name-match";
	public static String
		XACML_FUNC_ONE_AND_ONLY =
			"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only";
	public static String
		XACML_ATTR_SUBJECT_ID =
			"urn:oasis:names:tc:xacml:1.0:subject:subject-id";
	public static String
		XACML_ATTR_RESOURCE_ID =
			"urn:oasis:names:tc:xacml:1.0:resource:resource-id";
	public static String
		XACML_ATTR_ACTION_ID =
			"urn:oasis:names:tc:xacml:1.0:action:action-id";
	public static String
		XACML_ATTR_SUBJECT_AUTH_METHOD =
			"urn:oasis:names:tc:xacml:1.0:subject:" +
    		"authn-locality:authentication-method";
	
	public static String
		OPENAZ_ATTR_RESOURCE_TYPE =
			"urn:openaz:names:xacml:1.0:resource:resource-type";
	public static String
		OPENAZ_ATTR_SUBJECT_ROLE_ID =
			"urn:openaz:names:xacml:1.0:subject:role-id";
		  //"test.role.principal.role-id";
	public static boolean
		OPENAZ_XACML_MUST_BE_PRESENT_TRUE = true;
	public static boolean
		OPENAZ_XACML_MUST_BE_PRESENT_FALSE = false;
	
    public static final String XPATH_1_0_VERSION =
        "http://www.w3.org/TR/1999/Rec-xpath-19991116";
	
	Log log = LogFactory.getLog(this.getClass()); 
	static Log logStatic = LogFactory.getLog(
					TestAzApiPolicySets.class);
	public static boolean logEnabled = false;
	public static boolean textOnly = false;
	public static boolean printFirst = false;
	public static String logString = "";

    /**
     * Simple helper routine that creates a TargetMatch instance.
     *
     * @param type the type of match
     * @param functionId the matching function identifier
     * @param designator the AttributeDesignator used in this match
     * @param value the AttributeValue used in this match
     *
     * @return the matching element
     */
    public static TargetMatch createTargetMatch(
    		int type, 
    		String functionId,
            AttributeDesignator designator,
            AttributeValue value) {
        try {
            // get the factory that handles Target functions and get an
            // instance of the right function
            FunctionFactory factory = FunctionFactory.getTargetInstance();
            Function function = factory.createFunction(functionId);
        
            // create the TargetMatch
            return new TargetMatch(type, function, designator, value);
        } catch (Exception e) {
            // note that in this example, we should never hit this case, but
            // in the real world you need to worry about exceptions, especially
            // from the factory
            return null;
        }
    }
    
    /**
     * Creates an attribute value based on xacml type
     */
    public static AttributeValue createAttributeValue(
    			AttributeMatchExpression ame) 
    		throws URISyntaxException {
	    AttributeValue attrValue = null;
	    if (ame.getDataType().equals(XACML_TYPE_ANYURI)){
	    		//"http://www.w3.org/2001/XMLSchema#anyURI")){
	    	attrValue =
		        new AnyURIAttribute(
		        	new URI(ame.getValue()));	    	
	    } else if (ame.getDataType().equals(XACML_TYPE_STRING)){
	    		//"http://www.w3.org/2001/XMLSchema#string")){
	    	attrValue =
	    		new StringAttribute(ame.getValue());
	    } else if (ame.getDataType().equals(XACML_TYPE_RFC822)){
	    		//"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name")){
	    	attrValue =
	    		new StringAttribute(ame.getValue());
	    } else if (ame.getDataType().equals(XACML_TYPE_X500NAME)){
	    		//"urn:oasis:names:tc:xacml:1.0:data-type:x500Name")){
	    	attrValue =
	    		new X500NameAttribute(
	    			new X500Principal(ame.getValue()));
	    }
	    return attrValue;
    }

    /**
     * Creates a Target Attribute Match section
     * (subject, resource, action) to
     * be added to one of the collections in the Target element.
     * Since ultimately all the attribute values that are in
     * here will need to be converted to or from string along
     * the way, this method is for convenience to just provide
     * strings as args and let it load the URIs.
     * <p>
     * This method does the inner "AND" loop when multiple
     * attribute expressions are defined.
     * <p>
     * Also, the main thing it is doing internally is dispatching
     * on the type of AttributeValue that needs to be created
     * based on attributeDataType, and it creates that
     * AttributeValue from the string passed in.
     * <p>
     * This method may not be usable for all DataTypes, and it
     * relies on the user to know what combos are acceptable,
     * although that type of checking could be added later.
     * <p>
     * It is assumed for now that the AttributeDesignatorExpression
     * can be defined to contain all the info necessary to do a
     * match or comparison of a request attribute to some value
     * defined in the policy, which is the attr value held in
     * the AttributeDesignatorExpression. For now, assume we can
     * do: =, %lt;, &gt;, and that the 2-d array of attrDesExp
     * can infer an inner "AND" and an outer "OR" for a particular
     * defn of some target collection of entities.
     * 
     * @param targetMatchDesignators an array of 
     * {@link AttributeMatchExpression}s
     * @return targetAttributeMatch a List of attribute matches
     * that will be ANDed when put in the Policy.
     */
    public static List createTargetAttributeMatchList(
	    		AttributeMatchExpression[] targetMatchDesignators) 
    		throws URISyntaxException {
    	
	    // create the Resource, Action, or Subject section
	    List targetAttributeMatch = new ArrayList();
	
	    for (int i=0; i<targetMatchDesignators.length; i++) {
		    URI attributeDataTypeURI = 
		    	new URI(targetMatchDesignators[i].getDataType());
		    URI attributeIdURI = 
		    	new URI(targetMatchDesignators[i].getId());
		    URI attributeMatchIdURI =
		    	new URI(targetMatchDesignators[i].getMatchId());
		    URI attributeIssuer = null;
		    if ( ! (targetMatchDesignators[i].getIssuer() == null) ) {
		    	attributeIssuer = 
		    		new URI(targetMatchDesignators[i].getIssuer());
		    }
		    boolean mustBePresent = 
		    	targetMatchDesignators[i].getMustBePresent();
		    AttributeDesignator attributeDesignator =
		        new AttributeDesignator(
	        		targetMatchDesignators[i].getDesignatorType(),
	        		attributeDataTypeURI,
	                attributeIdURI, 
	                mustBePresent,
	                attributeIssuer);
		    AttributeValue attrValue = createAttributeValue(
		    		targetMatchDesignators[i]);
		    targetAttributeMatch.add(createTargetMatch(
		    	targetMatchDesignators[i].getDesignatorType(), 
		    	targetMatchDesignators[i].getMatchId(),
    			attributeDesignator, 
    			attrValue));
	    } //end loop targetMatchDesignators[i]
	    return targetAttributeMatch;
    }
    
    /**
     * Create a set of AttributeAssignments for Obligations
     * @param targetMatchDesignators
     * @return a List of SunXacml attributes for obligations
     * @throws URISyntaxException
     */
    public static List createObligationAttributeList(
    		AttributeMatchExpression[] targetMatchDesignators) 
		throws URISyntaxException {
	
    // create a List of sunxacml Attributes
    List targetAttributeMatch = new ArrayList();

    for (int i=0; i<targetMatchDesignators.length; i++) {
	    URI attributeDataTypeURI = 
	    	new URI(targetMatchDesignators[i].getDataType());
	    URI attributeIdURI = 
	    	new URI(targetMatchDesignators[i].getId());
	    String attributeIssuer =
	    	targetMatchDesignators[i].getIssuer();
	    //URI attributeMatchIdURI =
	    //	new URI(targetMatchDesignators[i].getMatchId());
	    AttributeValue attrValue = createAttributeValue(
	    		targetMatchDesignators[i]);
	    Attribute attribute =
	        new Attribute(
			        		attributeIdURI,
			        		attributeIssuer,
			                null, 
			                attrValue);
	    targetAttributeMatch.add(attribute);
    } //end loop targetMatchDesignators[i]
    return targetAttributeMatch;
}

    /**
     * Creates the Target used by Policy or Rules. 
     * <p>
     * Passed 2-d arrays of AttributeDesignatorExpression[][]
     * <p>
     * Left index is outer "OR" of entities
     * Right index is inner "AND" of entities
     * <p>
     * Note: for xacml 1.1 distinction between empty and null
     * is used such that null produces <AnyAction>, etc. whereas
     * empty produces <Actions/>, where latter means no actions
     * supported in xacml 1.1, but means any action supported
     * in xacml 2.0.
     * 
     * @return the target
     *
     * @throws URISyntaxException if there is a problem with any of the URIs
     */
    public static Target createPolicyOrRuleTarget(
				AttributeMatchExpression[][] attrSubMatchExprs, 
				AttributeMatchExpression[][] attrResMatchExprs, 
    			AttributeMatchExpression[][] attrActMatchExprs) 
    		throws URISyntaxException {
    	// Create the lists for Target
        List actions = null; // needs to be null for AllActions
        List resources = null;
        List subjects = null;
        
        // create the sections for each Target list
        List action = null;
        if ( ! (attrActMatchExprs == null) ) {
        	actions = new ArrayList();
	        for (int i=0; i<attrActMatchExprs.length; i++){ 
	        	// create a list of Action sections
		        action = createTargetAttributeMatchList(
		        			attrActMatchExprs[i]);
		        // put the Action sections in the Actions list
		        actions.add(action);
	        }
        }
        List subject = null;
        if ( ! (attrSubMatchExprs == null) ) {
        	subjects = new ArrayList();
	        for (int i=0; i<attrSubMatchExprs.length; i++){ 
	        	// create a list of Subject sections
		        subject = createTargetAttributeMatchList(
		        			attrSubMatchExprs[i]);
		        // put the Subject sections in the Subject list
		        subjects.add(subject);
	        }
        }
        List resource = null;
        if ( ! (attrResMatchExprs == null) ) {
        	resources = new ArrayList();
	        for (int i=0; i<attrResMatchExprs.length; i++){ 
	        	// create a list of Resource sections
		        resource = createTargetAttributeMatchList(
		        			attrResMatchExprs[i]);
		        // put the Resource sections in the Resources list
		        resources.add(resource);
	        }
        }
        // create & return the new Target
        // Note: null arg produces 1.1 construct <AnyAction>, etc.,
        // which is need to produce the 2.0 default behavior.
        // i.e. an empty list arg will produce an
        // empty <Actions/> element, which in 1.1 is no actions
        // match.
        return new Target(subjects, resources, actions);
    }

    /**
     * Creates the Condition used in the Rule. Note that 
     * a Condition is just a special kind of Apply.
     * <p>
     * Also, this condition only will work with a limited
     * set of functions, in particular it is the same as
     * the match functions (an attribute designator plus
     * a comparator) except that it applies the one and
     * only function to the attribute designator, which
     * is implicitly performed by the match functions.
     * However this is more limited than match since
     * match will go thru a list of values and return
     * true when and if it finds a match. This will only
     * allow one and only one value to be in the list.
     *
     * @return the condition
     *
     * @throws URISyntaxException if there is a problem 
     * with any of the URIs
     */
    public static Apply createRuleCondition(
    			AttributeMatchExpression[] tmd) 
    		throws URISyntaxException {
    	if (tmd.length == 0) return null;
        List conditionArgs = new ArrayList();

        // get the function that the condition uses
        FunctionFactory factory = 
        	FunctionFactory.getConditionInstance();
        Function conditionFunction = null;
        try {
            conditionFunction =
                factory.createFunction(
                		tmd[0].getMatchId());
        } catch (Exception e) {
            // see comment in createTargetMatch()
            return null;
        }
        
        // now create the apply section that gets the designator value
        List applyArgs = new ArrayList();

        factory = FunctionFactory.getGeneralInstance();
        Function applyFunction = null;
        try {
            applyFunction =
                factory.createFunction(
                		tmd[0].getFunctionId());
        } catch (Exception e) {
            // see comment in createTargetMatch()
            return null;
        }
        
        URI designatorType =
            new URI(tmd[0].getDataType());
        URI designatorId =
            new URI(tmd[0].getId());
            //new URI("group");
        URI designatorIssuer =
            new URI(tmd[0].getIssuer());
         // new URI("admin@users.example.com");
        boolean mustBePresent = tmd[0].getMustBePresent();
        AttributeDesignator designator =
            new AttributeDesignator(
            		AttributeDesignator.SUBJECT_TARGET,
                    designatorType, 
                    designatorId, 
                    mustBePresent,
                    designatorIssuer);
        applyArgs.add(designator);

        Apply apply = new Apply(applyFunction, applyArgs, false);
        
        // add the new apply element to the list of inputs to the condition
        conditionArgs.add(apply);

        // create an AttributeValue and add it to the input list
        StringAttribute value = 
        	new StringAttribute(
					tmd[0].getValue());
        		 // "developers");
        conditionArgs.add(value);

        // finally, create & return our Condition
        return new Apply(conditionFunction, conditionArgs, true);
    }

    /**
     * Creates the Rule used in the Policy.
     *
     * @return the rule
     *
     * @throws URISyntaxException if there is a problem with any 
     * of the URIs
     */
    public static Rule createRule(
	    		int ruleEffect,
	    		String ruleId,
	    		Target ruleTarget,
	    		Apply condition)
    		throws URISyntaxException {
        // define the identifier for the rule
        URI ruleIdURI = new URI(ruleId);

        // define the effect for the Rule
        int effect = ruleEffect;

        // get the Condition for the rule
        //Apply condition = null;
        //if (ruleCondition.length > 0)
        //	condition = createRuleCondition(ruleCondition);
        
        return new Rule(ruleIdURI, effect, null, ruleTarget, condition);
    }
    
    /**
     * Create a Set of Obligations for the Policy to use
     */
    public static Set createPolicyObligations(
    		ObligationListEnum obligationListEnum) 
    			throws URISyntaxException {
        Set obligationSet = new HashSet();
        AttributeMatchExpression[] obligationData = null;
        Obligation obligation = null;
        List obligationAttributeList = null;
        
        // put the actual obligation defns here:
        switch (obligationListEnum) {
        case OL_P1:
	        obligationData = 
	        	new AttributeMatchExpression[] {
	            	new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"user",
	                		XACML_ATTR_SUBJECT_ID),
	    			new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"resource",
	                		XACML_ATTR_RESOURCE_ID),
	    			new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_STRING,
	                		"&lt;SubjectAttributeDesignator " +
	                		"AttributeId=\"urn:oasis:names:tc:xacml:1.0:subject:subject-id\" " +
	                		"DataType=\"http://www.w3.org/2001/XMLSchema#string\"/&gt;",
	                		XACML_ATTR_SUBJECT_ID)
	        	};
        
		    obligationAttributeList =
		    	createObligationAttributeList(obligationData);
		    obligation = new Obligation(
		    		new URI("LogSuccessfulAccess_OL_P1"),
		    		Result.DECISION_PERMIT,
		    		obligationAttributeList);
		    obligationSet.add(obligation);
		    
		    obligationData = 
		    	new AttributeMatchExpression[] {
		        	new AttributeMatchExpression(
		        			OPENAZ_OBLIGATION_DESIGNATOR,
		            		null,
		            		XACML_TYPE_ANYURI,
		            		"user",
	                		XACML_ATTR_SUBJECT_ID),
					new AttributeMatchExpression(
		        			OPENAZ_OBLIGATION_DESIGNATOR,
		            		null,
		            		XACML_TYPE_ANYURI,
		            		"resource",
		            		XACML_ATTR_RESOURCE_ID),
					new AttributeMatchExpression(
		        			OPENAZ_OBLIGATION_DESIGNATOR,
		            		null,
		            		XACML_TYPE_ANYURI,
		            		"action",
		            		XACML_ATTR_ACTION_ID)
		    };
		        		
		    obligationAttributeList =
		    	createObligationAttributeList(obligationData);
		    obligation = new Obligation(
		    		new URI("LogDeniedAccess_OL_P1"),
		    		Result.DECISION_DENY,
		    		obligationAttributeList);
		    obligationSet.add(obligation);
		    break;
        case OL_P2:
	        obligationData = 
	        	new AttributeMatchExpression[] {
	            	new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"User1",
	                		XACML_ATTR_SUBJECT_ID),
	    			new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"test.objects.TestResourcePermission",
	                		OPENAZ_ATTR_RESOURCE_TYPE)
	        	};
        
		    obligationAttributeList =
		    	createObligationAttributeList(obligationData);
		    obligation = new Obligation(
		    		new URI("LogSuccessfulAccess_OL_P2"),
		    		Result.DECISION_PERMIT,
		    		obligationAttributeList);
		    obligationSet.add(obligation);
		    
        	break;
        case OL_P3:
	        obligationData = 
	        	new AttributeMatchExpression[] {
	            	new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"User1",
	                		XACML_ATTR_SUBJECT_ID),
	            	new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"http://www.example.com/A/B/.*",
	                		XACML_ATTR_RESOURCE_ID),
	    			new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"test.objects.TestResourcePermission",
	                		OPENAZ_ATTR_RESOURCE_TYPE)
	        	};
        
		    obligationAttributeList =
		    	createObligationAttributeList(obligationData);
		    obligation = new Obligation(
		    		new URI("LogSuccessfulAccessQuery_OL_P3"),
		    		Result.DECISION_PERMIT,
		    		obligationAttributeList);
		    obligationSet.add(obligation);
		    
        	break;
        case OL_P4:
	        obligationData = 
	        	new AttributeMatchExpression[] {
	            	new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"User1",
	                		XACML_ATTR_SUBJECT_ID),
	    			new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"test.objects.TestResourcePermission",
	                		OPENAZ_ATTR_RESOURCE_TYPE)
	        	};
        
		    obligationAttributeList =
		    	createObligationAttributeList(obligationData);
		    obligation = new Obligation(
		    		new URI("LogSuccessfulAccess_OL_P4"),
		    		Result.DECISION_PERMIT,
		    		obligationAttributeList);
		    obligationSet.add(obligation);
		    
        	break;
        case OL_P5:
	        obligationData = 
	        	new AttributeMatchExpression[] {
	            	new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"User1",
	                		XACML_ATTR_SUBJECT_ID),
	            	new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"http://www.example.com/A/D/.*",
	                		XACML_ATTR_RESOURCE_ID),
	    			new AttributeMatchExpression(
	            			OPENAZ_OBLIGATION_DESIGNATOR,
	                		null,
	                		XACML_TYPE_ANYURI,
	                		"test.objects.TestResourcePermission",
	                		OPENAZ_ATTR_RESOURCE_TYPE)
	        	};
        
		    obligationAttributeList =
		    	createObligationAttributeList(obligationData);
		    obligation = new Obligation(
		    		new URI("LogSuccessfulAccessQuery_OL_P5"),
		    		Result.DECISION_PERMIT,
		    		obligationAttributeList);
		    obligationSet.add(obligation);
		    
        	break;
        } // end switch (obligationListEnum)
	    
        return obligationSet;
    }
    
    public static Apply createPolicyRuleCondition(
    							ConditionEnum conditionEnum)
    		throws URISyntaxException {
    	Apply condition = null;
        AttributeMatchExpression[] ruleCondition = 
        	new AttributeMatchExpression[] {};
    	switch (conditionEnum) {
    	case CD_P1_R1: // use empty ruleCondition
    	case CD_P6_R1:
    	case CD_P8_R1: 
    		break;
    	case CD_P1_R2:
	        ruleCondition = new AttributeMatchExpression[] {
	            	new AttributeMatchExpression(
	            		AttributeDesignator.SUBJECT_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"password",
	            		XACML_ATTR_SUBJECT_AUTH_METHOD,
	            		"orcl-weblogic", 
	            		XACML_FUNC_ONE_AND_ONLY)
	            };
    		break;
    	case CD_P1_R3:
		    ruleCondition = new AttributeMatchExpression[] {
		        	new AttributeMatchExpression(
		        		AttributeDesignator.SUBJECT_TARGET,
		        		XACML_FUNC_STRING_EQUAL,
		        		XACML_TYPE_STRING,
		        		"developer",
		        		OPENAZ_ATTR_SUBJECT_ROLE_ID,
		        		"orcl-weblogic", 
		        		XACML_FUNC_ONE_AND_ONLY)
		        };
    		break;
    	case CD_P1_R4:
	        ruleCondition = new AttributeMatchExpression[] {
	            	new AttributeMatchExpression(
	            		AttributeDesignator.SUBJECT_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"developers",
	            		"group",
	            		"admin@users.example.com", 
	            		XACML_FUNC_ONE_AND_ONLY)
	            };
    		break;
    	}
    	if (ruleCondition.length > 0)
    		condition = createRuleCondition(ruleCondition);
    	return condition;
    }

    /**
     * Create a set of rules for the policy
     */
    public static List createPolicyRuleList(RuleListEnum ruleListEnum) 
    		throws URISyntaxException {
        List ruleList = new ArrayList();
        Target ruleTarget = null;
        Rule defaultRule = null;
        Apply ruleCondition = null;
        
        switch (ruleListEnum) {
        case RL_P1:
	        // *** build the read rule 
	        ruleTarget = createPolicyTarget(TargetEnum.TG_P1_R1);
	        // create the read rule
	        Rule readRule = createRule(
	        		Result.DECISION_PERMIT,
	        		"ReadRule_" + ruleListEnum,
	        		ruleTarget,
	        		ruleCondition);
	        
	        // *** build the write rule
	        ruleTarget = createPolicyTarget(TargetEnum.TG_P1_R2);
	        ruleCondition = createPolicyRuleCondition(
					ConditionEnum.CD_P1_R2);
	        // create the write rule
	        Rule writeRule = createRule(
	        		Result.DECISION_PERMIT,
	        		"WriteRule_" + ruleListEnum,
	        		ruleTarget,
	        		ruleCondition);
	        
	        // *** build the role-write rule
	        ruleTarget = createPolicyTarget(TargetEnum.TG_P1_R3);
	        ruleCondition = createPolicyRuleCondition(
	        					ConditionEnum.CD_P1_R3);
		    // create the role-write rule
		    Rule roleWriteRule = createRule(
		    		Result.DECISION_PERMIT,
		    		"RoleWriteRule_" + ruleListEnum,
		    		ruleTarget,
		    		ruleCondition);
		    
	        // *** build the commit rule 
		    ruleTarget = createPolicyTarget(TargetEnum.TG_P1_R4);
	        ruleCondition = createPolicyRuleCondition(
	        						ConditionEnum.CD_P1_R4);
	        // create the commit rule
	        Rule commitRule = createRule(
	        		Result.DECISION_PERMIT,
	        		"CommitRule_" + ruleListEnum,
	        		ruleTarget,
	        		ruleCondition);
	
	        // create the default, fall-through rule
	        defaultRule = 
	        	new Rule(new URI("FinalRule_" + ruleListEnum), 
	        			 Result.DECISION_DENY,
	                     null, null, null);
	
	        // create a list for the rules and add our rules in order
	        ruleList.add(readRule);
	        ruleList.add(writeRule);
	        ruleList.add(roleWriteRule);
	        ruleList.add(commitRule);
	        ruleList.add(defaultRule);
	        break; // end case RL_P1
        case RL_P2:
	        defaultRule = 
	        	new Rule(new URI("FinalRule_" + ruleListEnum), 
	        			 Result.DECISION_PERMIT,
	                     null, null, null);
	        ruleList.add(defaultRule);
        	break;
        case RL_P3:
	        defaultRule = 
	        	new Rule(new URI("FinalRule_" + ruleListEnum), 
	        			 Result.DECISION_PERMIT,
	                     null, null, null);
	        ruleList.add(defaultRule);
        	break;
        case RL_P4:
	        defaultRule = 
	        	new Rule(new URI("FinalRule_" + ruleListEnum), 
	        			 Result.DECISION_PERMIT,
	                     null, null, null);
	        ruleList.add(defaultRule);
        	break;
        case RL_P5:
	        defaultRule = 
	        	new Rule(new URI("FinalRule_" + ruleListEnum), 
	        			 Result.DECISION_PERMIT,
	                     null, null, null);
	        ruleList.add(defaultRule);
        	break;
        case RL_P6:
        	ruleTarget = createPolicyTarget(TargetEnum.TG_P6_R1);
	        ruleCondition = createPolicyRuleCondition(
	        					ConditionEnum.CD_P6_R1);
	        // create the subject rule
	        Rule subjectRuleP6 = createRule(
	        		Result.DECISION_PERMIT,
	        		"SubjectRule_" + ruleListEnum,
	        		ruleTarget,
	        		ruleCondition);
	        defaultRule = 
	        	new Rule(new URI("FinalRule_" + ruleListEnum), 
	        			 Result.DECISION_DENY,
	                     null, null, null);
	        ruleList.add(subjectRuleP6);
	        ruleList.add(defaultRule);
        	break;
        case RL_P7:
	        defaultRule = 
	        	new Rule(new URI("FinalRule_" + ruleListEnum), 
	        			 Result.DECISION_DENY,
	                     null, null, null);
	        ruleList.add(defaultRule);
        	break;
        case RL_P8:
        	ruleTarget = createPolicyTarget(TargetEnum.TG_P8_R1);
	        // create the subject rule
	        ruleCondition = createPolicyRuleCondition(
	        							ConditionEnum.CD_P8_R1);
	        Rule subjectRuleP8 = createRule(
	        		Result.DECISION_PERMIT,
	        		"SubjectRule_" + ruleListEnum,
	        		ruleTarget,
	        		ruleCondition);
	        defaultRule = 
	        	new Rule(new URI("FinalRule_" + ruleListEnum), 
	        			 Result.DECISION_DENY,
	                     null, null, null);
	        ruleList.add(subjectRuleP8);
	        ruleList.add(defaultRule);
        	break;
        } // end switch (ruleListEnum)
    	
    	return ruleList;
    }
    

    /**
     * Creates the Target used in the Policy. This Target specifies that
     * the Policy applies to any example.com users who are requesting some
     * form of access to server.example.com.
     * <p>
     * This module creates specific Subject and Resource match
     * blocks. Could do actions as well.
     * <p>
     * Each statement of the form:
     * <pre>
     *     subjects.add(createTargetAttributeMatchList(
     * </pre>
     * potentially creates an ANDed list of 
     * AttributeMatchExpressions.
     * A sequence of such statements defines an ORed list, effectively
     * providing a mechanism for an outer OR and inner AND required
     * by the XACML Target format.
     * 
     *
     *
     * @return the target
     *
     * @throws URISyntaxException if there is a problem with any of the URIs
     */
    public static Target createPolicyTarget(TargetEnum targetEnum) 
    		throws URISyntaxException {

        AttributeMatchExpression[] tmd = null;
                
        // construct Target with one double array for each of
        // XACML Subjects, Resources, and Actions
        // inner array is AND of SubjectMatch, etc elements
        // outer array is OR of Subject, etc. elements
        // the XACML elements are created by SunXacml, these
        // arrays are just the parameters of each statement
        AttributeMatchExpression[][] subjectAttrMatchExprs = null;
        AttributeMatchExpression[][] resourceAttrMatchExprs = null;
        AttributeMatchExpression[][] actionAttrMatchExprs = null;
        
    	if (logEnabled && printFirst) {
    		String logString = 
    			"    createTarget:     " + targetEnum;
    		printText(logString);
    	}

    	switch (targetEnum) {
        case TG_ALL:
        	// empty target, allows all subjects, resources, and actions
        	// by simply taking the 3 null default values
        	break;
        case TG_P8_R1:
	        // *** build the FrisBee rule
	        subjectAttrMatchExprs = new AttributeMatchExpression[][] {{
	        		new AttributeMatchExpression(
	            		AttributeDesignator.SUBJECT_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"fred",
	            		XACML_ATTR_SUBJECT_ID),
		    		new AttributeMatchExpression(
	            		AttributeDesignator.SUBJECT_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"throw",
	            		"urn:openaz:privilege:frisbee",
	            		"orcl-weblogic",
	            		null,
	            		OPENAZ_XACML_MUST_BE_PRESENT_TRUE)} };
	        resourceAttrMatchExprs = new AttributeMatchExpression[][] {{
        		new AttributeMatchExpression(
            		AttributeDesignator.RESOURCE_TARGET,
            		XACML_FUNC_STRING_EQUAL,
            		XACML_TYPE_STRING,
            		"resource-id-FrisBee-3",
            		XACML_ATTR_RESOURCE_ID)} };
        	break;
        case TG_P6_R1:
	        // *** build the P6 subject,resource target
	        subjectAttrMatchExprs = new AttributeMatchExpression[][] {{
	        		new AttributeMatchExpression(
	            		AttributeDesignator.SUBJECT_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"fred",
	            		XACML_ATTR_SUBJECT_ID)} };
	        resourceAttrMatchExprs = new AttributeMatchExpression[][] {{
        		new AttributeMatchExpression(
            		AttributeDesignator.RESOURCE_TARGET,
            		XACML_FUNC_STRING_EQUAL,
            		XACML_TYPE_STRING,
            		"resource-id-EngineeringServer-3",
            		XACML_ATTR_RESOURCE_ID)} };
       	break;
        case TG_P1_R1:
	        // *** build the read rule Target 
        	// (note: these are ORs of Action
	        // elements, because each AttributeMatchExpression is
	        // in a separate (outer) array:
	        actionAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {{
	            	new AttributeMatchExpression(
	            		AttributeDesignator.ACTION_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"Read",
	            		XACML_ATTR_ACTION_ID)},{
	            	new AttributeMatchExpression(
	            		AttributeDesignator.ACTION_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"read",
	            		XACML_ATTR_ACTION_ID)}
	            };
        	break;
        case TG_P1_R2:
	        // *** build the write rule Target
	        actionAttrMatchExprs = new AttributeMatchExpression[][] {{
	        		new AttributeMatchExpression(
	            		AttributeDesignator.ACTION_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"write",
	            		XACML_ATTR_ACTION_ID)},{
	            	new AttributeMatchExpression(
	            		AttributeDesignator.ACTION_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"Write",
	            		XACML_ATTR_ACTION_ID)
	            }};
        	break;
        case TG_P1_R3:
	        // *** build the role-write rule Target
	        actionAttrMatchExprs = new AttributeMatchExpression[][] {{
	    		new AttributeMatchExpression(
	        		AttributeDesignator.ACTION_TARGET,
	        		XACML_FUNC_STRING_EQUAL,
	        		XACML_TYPE_STRING,
	        		"write",
	        		XACML_ATTR_ACTION_ID)},{
	        	new AttributeMatchExpression(
	        		AttributeDesignator.ACTION_TARGET,
	        		XACML_FUNC_STRING_EQUAL,
	        		XACML_TYPE_STRING,
	        		"Write",
	        		XACML_ATTR_ACTION_ID)
		        }};
        	break;
        case TG_P1_R4:
	        // *** build the commit rule Target
		    actionAttrMatchExprs = new AttributeMatchExpression[][] {{
	            	new AttributeMatchExpression(
	            		AttributeDesignator.ACTION_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"commit",
	            		XACML_ATTR_ACTION_ID)
	            }};
        	break;
        case TG_PS7:
	        resourceAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
        		  { new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"EngineeringServer",
	            		OPENAZ_ATTR_RESOURCE_TYPE) },
  	              { new AttributeMatchExpression(
  	    				AttributeDesignator.RESOURCE_TARGET,
  	    				XACML_FUNC_STRING_EQUAL,
  	            		XACML_TYPE_STRING,
  	            		"Menu",
  	            		OPENAZ_ATTR_RESOURCE_TYPE)},	            		
	              { new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"FrisBee",
	            		OPENAZ_ATTR_RESOURCE_TYPE)} };	            		
        	break;
        case TG_P6:
	        resourceAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
        		  { new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"EngineeringServer",
	            		OPENAZ_ATTR_RESOURCE_TYPE)} };	            		
        	break;
        case TG_P7:
	        resourceAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
        		  { new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"Menu",
	            		OPENAZ_ATTR_RESOURCE_TYPE)} };	            		
        	break;
        case TG_P8:
	        resourceAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
        		  { new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"FrisBee",
	            		OPENAZ_ATTR_RESOURCE_TYPE)} };	            		
        	break;
        case TG_PS6:
	        break;
        case TG_P4:
	        resourceAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
        		  { new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_REGEXP_STRING_MATCH,
	            		XACML_TYPE_STRING,
	            		"http://www.example.com/A/D/.*",
	            		XACML_ATTR_RESOURCE_ID),
	                new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"test.objects.TestResourcePermission",
	            		OPENAZ_ATTR_RESOURCE_TYPE)} };	            		
        	break;
        case TG_P5:
	        resourceAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
        		  { new AttributeMatchExpression(
  	    				AttributeDesignator.RESOURCE_TARGET,
  	    				XACML_FUNC_STRING_EQUAL,
  	            		XACML_TYPE_STRING,
  	            		"/-", // key string for query
  	            		XACML_ATTR_RESOURCE_ID),
  	                new AttributeMatchExpression(
  	    				AttributeDesignator.RESOURCE_TARGET,
  	    				XACML_FUNC_STRING_EQUAL,
  	            		XACML_TYPE_STRING,
  	            		"test.objects.TestResourcePermission",
  	            		OPENAZ_ATTR_RESOURCE_TYPE)} };	            		
        	break;
        case TG_PS5:
	        resourceAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
        		  { new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_REGEXP_STRING_MATCH,
	            		XACML_TYPE_STRING,
	            		"http://www.example.com/A/B/.*",
	            		XACML_ATTR_RESOURCE_ID),
	                new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"test.objects.TestResourcePermission",
	            		OPENAZ_ATTR_RESOURCE_TYPE)},
          		  { new AttributeMatchExpression(
  	    				AttributeDesignator.RESOURCE_TARGET,
  	    				XACML_FUNC_STRING_EQUAL,
  	            		XACML_TYPE_STRING,
  	            		"/-", // key string for query
  	            		XACML_ATTR_RESOURCE_ID),
  	                new AttributeMatchExpression(
  	    				AttributeDesignator.RESOURCE_TARGET,
  	    				XACML_FUNC_STRING_EQUAL,
  	            		XACML_TYPE_STRING,
  	            		"test.objects.TestResourcePermission",
  	            		OPENAZ_ATTR_RESOURCE_TYPE)} };	            		
	        break;
        case TG_P2:
	        resourceAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
        		  { new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_REGEXP_STRING_MATCH,
	            		XACML_TYPE_STRING,
	            		"http://www.example.com/A/B/.*",
	            		XACML_ATTR_RESOURCE_ID),
	                new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"test.objects.TestResourcePermission",
	            		OPENAZ_ATTR_RESOURCE_TYPE)} };	            		
        	break;
        case TG_P3:
	        resourceAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
        		  { new AttributeMatchExpression(
  	    				AttributeDesignator.RESOURCE_TARGET,
  	    				XACML_FUNC_STRING_EQUAL,
  	            		XACML_TYPE_STRING,
  	            		"/-", // key string for query
  	            		XACML_ATTR_RESOURCE_ID),
  	                new AttributeMatchExpression(
  	    				AttributeDesignator.RESOURCE_TARGET,
  	    				XACML_FUNC_STRING_EQUAL,
  	            		XACML_TYPE_STRING,
  	            		"test.objects.TestResourcePermission",
  	            		OPENAZ_ATTR_RESOURCE_TYPE)} };	            		
        	break;
        case TG_PS4:
	        subjectAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
	        		{ new AttributeMatchExpression(
	        			AttributeDesignator.SUBJECT_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"User1",
	            		XACML_ATTR_SUBJECT_ID) } };
	        break;
        case TG_P1:
	        // build the subjects (note: these are ORs of Subject
	        // elements, because each AttributeMatchExpression is
	        // in a separate (outer) array:
	        subjectAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
	        		{ new AttributeMatchExpression(
	        			AttributeDesignator.SUBJECT_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"Joe User",
	            		XACML_ATTR_SUBJECT_ID) },
	        		{ new AttributeMatchExpression(
	            		AttributeDesignator.SUBJECT_TARGET,
	            		XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"josh",
	            		XACML_ATTR_SUBJECT_ID) },
	        		{ new AttributeMatchExpression(
	                	AttributeDesignator.SUBJECT_TARGET,
	            		XACML_FUNC_RFC822_MATCH,
	            		XACML_TYPE_RFC822,
	            		"users.example.com",
	            		XACML_ATTR_SUBJECT_ID) },
	        		{ new AttributeMatchExpression(
	                	AttributeDesignator.SUBJECT_TARGET,
	            		XACML_FUNC_X500NAME_MATCH,
	            		XACML_TYPE_X500NAME,
	            		"CN=Rich,OU=Identity Management,O=Oracle,C=US",
	            		XACML_ATTR_SUBJECT_ID)} 
	        	};
	        
	        // build the resources:
	        resourceAttrMatchExprs = 
	        	new AttributeMatchExpression[][] {
	                // do URL protection w URI path
	        		{ new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	            		XACML_FUNC_ANYURI_EQUAL,
	            		XACML_TYPE_ANYURI,
	            		"http://www.example.com/toplevel",
	            		XACML_ATTR_RESOURCE_ID) },
	                // do file protection w string path
	        		{ new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"file:C\\toplevel",
	            		XACML_ATTR_RESOURCE_ID)},
	        		{ new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"file:///C/toplevel/permissionTest",
	            		XACML_ATTR_RESOURCE_ID),
	            	  new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"java.io.FilePermission",
	            		//"java.xxx.FilePermission",
	            		OPENAZ_ATTR_RESOURCE_TYPE)},
	            	// emulate a real FilePermission
	        		{ new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_REGEXP_STRING_MATCH,
	            		XACML_TYPE_STRING,
	            		"file:///C/toplevel/permissionTest/.*",
	            		XACML_ATTR_RESOURCE_ID),
	            	  new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"java.io.FilePermission",
	            		//"java.xxx.FilePermission",
	            		OPENAZ_ATTR_RESOURCE_TYPE)},
	            	// bulk decide resources:
	        		{ new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"file:\\\\toplevel00",
	            		XACML_ATTR_RESOURCE_ID)},
	        		{ new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_STRING_EQUAL,
	            		XACML_TYPE_STRING,
	            		"file:\\\\toplevel01",
	            		XACML_ATTR_RESOURCE_ID) },
	            	// use regexp match (see also regexp notes below
	        		{ new AttributeMatchExpression(
	    				AttributeDesignator.RESOURCE_TARGET,
	    				XACML_FUNC_REGEXP_STRING_MATCH,
	            		XACML_TYPE_STRING,
	            		"file:\\\\\\\\toplevel.*\\\\", // produces: .*file:\\\\toplevel.*\\.*
	            		XACML_ATTR_RESOURCE_ID) }        		
	    		}; // end new AttrMatchExpr[][]
	        break; // end case TGP1
	        
        } // end switch(targetId)  
        
        // create & return new Target
        Target target = createPolicyOrRuleTarget(
        		subjectAttrMatchExprs,
        		resourceAttrMatchExprs,
        		actionAttrMatchExprs);
        return target;
    }
	
    // regexp notes: 
    // for creating paths and the constructs reqd.
    // note: need 4 "\" to get one in the match:
    // need to double once for Java and double again for regex
    // none of the following work to match "file:\\toplevel01":
    // because need to have a double \\ for each \ in the match
    
    // also note that sunxacml MatchFunction.evaluate prepends
    // and appends a ".*" to the input arguments in order to
    // comply with:
    // http://www.w3.org/TR/2002/WD-xquery-operators-20020816/#func-matches
    // which is ref'd by xacml 2.0 string-regexp-match sec A.3.13
    // which was prev named "regexp-string-match" in xacml 1.1
    // that sunxacml is based on.
    
    // In the following, lhs is code in java file, rhs is
    // the regular expression produced by the sunxacml 
    // MatchFunction module; but what shows up in the Policy
    // does not incl leading and trailing ".*". What's happening
    // is that expression in the policy (and java code) would 
    // need to have initial "^" and trailing "$" for regexp
    // syntax to do exact str rather than substr.
    //
    
	//"file:\\\\*",             // produces: .*file:\\*.*
	//"file:\\\\*\\",           // produces: .*file:\\*\.*
	//"file:\\\\toplevel",      // produces: .*file:\\toplevel.*
	//"file:\\\\toplevel*",     // produces: .*file:\\toplevel*.*
	//"file:\\\\toplevel*\\",   // produces: .*file:\\toplevel*\.*
	//"file:\\\\toplevel*\\*",    // produces: .*file:\\toplevel*\*.*
	//"file:\\\\toplevel.*\\*",    // produces: .*file:\\toplevel.*\*.*
	//"file:\\\\toplevel.*\\.*",    // produces: .*file:\\toplevel.*\.*.*
	//"file:\\\\toplevel.*\\.*",    // produces: .*file:\\toplevel.*\.*.*
	//"file:\\\\toplevel.*",    // produces: .*file:\\toplevel.*\.*.*
	//"file:\\\\toplevel*\\*.*",// produces: .*file:\\toplevel*\*.*.*

    // The following works for the specific "file:\\toplevel05\x.y"
    //"file:\\\\\\\\toplevel05\\\\x.y", // produces: .*file:\\\\toplevel05\\x.y.*
    // The following only works with trailing "\" on the dir:
	//"file:\\\\\\\\toplevel.*\\\\", // produces: .*file:\\\\toplevel.*\\.*
    
    public static Policy createPolicy(PolicyEnum policyEnum)
    		throws URISyntaxException,
    			   UnknownIdentifierException {
    	
    	// Variables to be set within each PolicyEnum
    	URI policyId = null;
    	URI combiningAlgId = null;
    	String description = null;
    	Target policyTarget = null;
        String defaultVersion = XPATH_1_0_VERSION;
        List ruleList = null;
        Set obligationSet = null;
        // internal variables
        CombiningAlgFactory factory = null;
    	RuleCombiningAlgorithm combiningAlg = null;
    	
    	if (logEnabled && printFirst) {
    		String logString = 
    			"  createPolicy:     " + policyEnum;
    		printText(logString);
    	}
        
    	switch (policyEnum) {
    	case P1:
	        // define the identifier for the policy
	        policyId = new URI("P1-TestAzApi-GeneratedPolicy");
	
	        // get the combining algorithm for the policy
	        combiningAlgId = new URI(
	        	OrderedPermitOverridesRuleAlg.algId);
	        factory = CombiningAlgFactory.getInstance();
	        combiningAlg = (RuleCombiningAlgorithm)
	        		(factory.createAlgorithm(combiningAlgId));
	
	        // add a description for the policy
	        description =
	            "This policy applies to any accounts at users.example.com " +
	            "accessing server.example.com. The one Rule applies to the " +
	            "specific action of doing a CVS commit, but other Rules could " +
	            "be defined that handled other actions. In this case, only " +
	            "certain groups of people are allowed to commit. There is a " +
	            "final fall-through rule that always returns Deny.";
	
	        // create the target for the policy
	        policyTarget = createPolicyTarget(TargetEnum.TG_P1);
	
	        // create the Rules the policy will use
	        ruleList = createPolicyRuleList(RuleListEnum.RL_P1);
	        
	        // This is where we should add obligations to policy:
	        // in xacml 3.0 can add obls to rule as well, which is
	        // probably preferable for a policy w many rules.
	        
	        obligationSet = createPolicyObligations(ObligationListEnum.OL_P1);
	        break; // end case P1
    	case P2:
	        policyId = new URI("P2-Policy-User1-Rule1");
	        combiningAlgId = new URI(
	        		OrderedPermitOverridesRuleAlg.algId);
	        factory = CombiningAlgFactory.getInstance();
	        combiningAlg = (RuleCombiningAlgorithm)
	        		(factory.createAlgorithm(combiningAlgId));
	        description = "This policy contains one rule";
	        policyTarget = createPolicyTarget(TargetEnum.TG_P2);
	        ruleList = createPolicyRuleList(RuleListEnum.RL_P2);
	        obligationSet = createPolicyObligations(
	        		ObligationListEnum.OL_P2);
	        break; // end case P2
    	case P3:
	        policyId = new URI("P3-Policy-User1-Rule1-Query");
	        combiningAlgId = new URI(
	        		OrderedPermitOverridesRuleAlg.algId);
	        factory = CombiningAlgFactory.getInstance();
	        combiningAlg = (RuleCombiningAlgorithm)
	        		(factory.createAlgorithm(combiningAlgId));
	        description = "This policy contains the query rule";
	        policyTarget = createPolicyTarget(TargetEnum.TG_P3);
	        ruleList = createPolicyRuleList(RuleListEnum.RL_P3);
	        obligationSet = createPolicyObligations(
	        		ObligationListEnum.OL_P3);
	        break; // end case P3
    	case P4:
	        policyId = new URI("P4-Policy-User1-Rule2");
	        combiningAlgId = new URI(
	        		OrderedPermitOverridesRuleAlg.algId);
	        factory = CombiningAlgFactory.getInstance();
	        combiningAlg = (RuleCombiningAlgorithm)
	        		(factory.createAlgorithm(combiningAlgId));
	        description = "This policy contains one rule";
	        policyTarget = createPolicyTarget(TargetEnum.TG_P4);
	        ruleList = createPolicyRuleList(RuleListEnum.RL_P4);
	        obligationSet = createPolicyObligations(
	        		ObligationListEnum.OL_P4);
	        break; // end case P4
    	case P5:
	        policyId = new URI("P5-Policy-User1-Rule2-Query");
	        combiningAlgId = new URI(
	        		OrderedPermitOverridesRuleAlg.algId);
	        factory = CombiningAlgFactory.getInstance();
	        combiningAlg = (RuleCombiningAlgorithm)
	        		(factory.createAlgorithm(combiningAlgId));
	        description = "This policy contains the query rule";
	        policyTarget = createPolicyTarget(TargetEnum.TG_P5);
	        ruleList = createPolicyRuleList(RuleListEnum.RL_P5);
	        obligationSet = createPolicyObligations(
	        		ObligationListEnum.OL_P5);
	        break; // end case P5
    	case P6:
	        policyId = new URI("P6-Policy-EngineeringServer-Query");
	        combiningAlgId = new URI(
	        		OrderedPermitOverridesRuleAlg.algId);
	        factory = CombiningAlgFactory.getInstance();
	        combiningAlg = (RuleCombiningAlgorithm)
	        		(factory.createAlgorithm(combiningAlgId));
	        description = "This policy contains the EngineeringServer rule";
	        policyTarget = createPolicyTarget(TargetEnum.TG_P6);
	        ruleList = createPolicyRuleList(RuleListEnum.RL_P6);
	        //obligationSet = createPolicyObligations(
	        //		ObligationListEnum.OL_P5);
	        break; // end case P6
    	case P7:
	        policyId = new URI("P7-Policy-Menu-Query");
	        combiningAlgId = new URI(
	        		OrderedPermitOverridesRuleAlg.algId);
	        factory = CombiningAlgFactory.getInstance();
	        combiningAlg = (RuleCombiningAlgorithm)
	        		(factory.createAlgorithm(combiningAlgId));
	        description = "This policy contains the Menu rule";
	        policyTarget = createPolicyTarget(TargetEnum.TG_P7);
	        ruleList = createPolicyRuleList(RuleListEnum.RL_P7);
	        break; // end case P7
    	case P8:
	        policyId = new URI("P8-Policy-Frisbee-Query");
	        combiningAlgId = new URI(
	        		OrderedPermitOverridesRuleAlg.algId);
	        factory = CombiningAlgFactory.getInstance();
	        combiningAlg = (RuleCombiningAlgorithm)
	        		(factory.createAlgorithm(combiningAlgId));
	        description = 
	        	"This policy contains the Frisbee target and rule";
	        policyTarget = createPolicyTarget(TargetEnum.TG_P8);
	        ruleList = createPolicyRuleList(RuleListEnum.RL_P8);
	        break; // end case P8
	        
    	} // end switch (policyEnum)

    	if (logEnabled && !printFirst) {
    		String logString = 
    			"createPolicy: " + policyEnum +
    			"\n\tpolicyId: " + policyId +
    			"\n\tcombiningAlgId: " + combiningAlg.toString();
    		printText(logString);
    	}
        // create the policy
        Policy policy = 
        	new Policy(policyId, combiningAlg, description,
                       policyTarget, defaultVersion,
                       ruleList, obligationSet);
    	return policy;
    }
    
    public static PolicySet createPolicySet(
    		PolicySetEnum policySetEnum) 
				throws URISyntaxException,
					   UnknownIdentifierException {
    	
    	// Variables to set for each PolicySet case
    	URI policySetId = null;
        URI combiningAlgId = null;
        String description = null;
    	Target policySetTarget = null;
        List policies = new ArrayList();
        
        // Internal variables that are set automatically
        CombiningAlgFactory factory = null;
    	PolicyCombiningAlgorithm combiningAlg = null;
    	
    	if (logEnabled && printFirst) {
    		String logString = 
    			"createPolicySet: " + policySetEnum;
    		printText(logString);
    	}
        
    	switch (policySetEnum) {
    	case PS1:
    		// PS1 is top level PolicySet; It will contain major
    		// subgroupings as contained policies and policysets

    		// define the identifier for the policySet
			policySetId = new URI("PS1-OpenAz-TopLevelPolicySet");	
			// get the combining algorithm for the policySet
			combiningAlgId = new URI(
					OrderedPermitOverridesPolicyAlg.algId);
			factory = CombiningAlgFactory.getInstance();
			combiningAlg = (PolicyCombiningAlgorithm)
				(factory.createAlgorithm(combiningAlgId));
			// Get an "allow all" Target for the policySet
			//policySetTarget = new Target(null, null, null);
			policySetTarget = createPolicyTarget(TargetEnum.TG_ALL);
	        // add the test program policySet PS1 to list
	        policies.add(createPolicySet(PolicySetEnum.PS2));
	        policies.add(createPolicySet(PolicySetEnum.PS3));
	        policies.add(createPolicySet(PolicySetEnum.PS7));
    		break;
    	case PS2:
			// define the identifier for the policySet
			policySetId = new URI("PS2-TestAzApi-GeneratedPolicySet");
			
			// get the combining algorithm for the policySet
			combiningAlgId = new URI(
					OrderedPermitOverridesPolicyAlg.algId);
			factory = CombiningAlgFactory.getInstance();
			combiningAlg = (PolicyCombiningAlgorithm)
				(factory.createAlgorithm(combiningAlgId));
			
			// Get an "allow all" Target for the policySet
			//policySetTarget = new Target(null, null, null);
			policySetTarget = createPolicyTarget(TargetEnum.TG_ALL);
			
	        // add policy P1 to list
	        policies.add(createPolicy(PolicyEnum.P1));
	        break;
    	case PS3:
			// define the identifier for the policySet
			policySetId = new URI("PS3-QueryTest-Policy-Based-PolicySet");
			
			// get the combining algorithm for the policySet
			combiningAlgId = new URI(
					OrderedPermitOverridesPolicyAlg.algId);
			factory = CombiningAlgFactory.getInstance();
			combiningAlg = (PolicyCombiningAlgorithm)
				(factory.createAlgorithm(combiningAlgId));
			
			// Get an "allow all" Target for the policySet
			//policySetTarget = new Target(null, null, null);
			policySetTarget = createPolicyTarget(TargetEnum.TG_ALL);
			
	        // add policy P1 to list
	        policies.add(createPolicySet(PolicySetEnum.PS4));
	        break;
    	case PS4:
			// define the identifier for the policySet
			policySetId = new URI("PS4-QueryTest-User1-PolicySet");
			
			// get the combining algorithm for the policySet
			// try deny overrides so all policies get eval'd
			combiningAlgId = new URI(
					DenyOverridesPolicyAlg.algId);
			factory = CombiningAlgFactory.getInstance();
			combiningAlg = (PolicyCombiningAlgorithm)
				(factory.createAlgorithm(combiningAlgId));
			
	        // create the target (allow User1) for the policy
	        policySetTarget = createPolicyTarget(TargetEnum.TG_PS4);
			
	        // add policySet PS5 to list
	        policies.add(createPolicySet(PolicySetEnum.PS5));
	        policies.add(createPolicySet(PolicySetEnum.PS6));
			break;
    	case PS5:
			// define the identifier for the policySet
			policySetId = new URI("PS5-QueryTest-User1-Rule1-PolicySet");
			
			// get the combining algorithm for the policySet
			combiningAlgId = new URI(
					OrderedPermitOverridesPolicyAlg.algId);
			factory = CombiningAlgFactory.getInstance();
			combiningAlg = (PolicyCombiningAlgorithm)
				(factory.createAlgorithm(combiningAlgId));
			
	        policySetTarget = createPolicyTarget(TargetEnum.TG_PS5);
			
	        // add policies P2,P3 to list
	        policies.add(createPolicy(PolicyEnum.P2));
	        policies.add(createPolicy(PolicyEnum.P3));
			break;
    	case PS6:
			// define the identifier for the policySet
			policySetId = new URI("PS6-QueryTest-User1-Rule2-PolicySet");
			
			// get the combining algorithm for the policySet
			combiningAlgId = new URI(
					OrderedPermitOverridesPolicyAlg.algId);
			factory = CombiningAlgFactory.getInstance();
			combiningAlg = (PolicyCombiningAlgorithm)
				(factory.createAlgorithm(combiningAlgId));
			
	        policySetTarget = createPolicyTarget(TargetEnum.TG_PS6);
			
	        // add policies P2,P3 to list
	        policies.add(createPolicy(PolicyEnum.P4));
	        policies.add(createPolicy(PolicyEnum.P5));
			break;
    	case PS7:
			// define the identifier for the policySet
			policySetId = new URI("PS7-QueryTest-Resource-Based-PolicySet");
			
			// get the combining algorithm for the policySet
			combiningAlgId = new URI(
					OrderedPermitOverridesPolicyAlg.algId);
			factory = CombiningAlgFactory.getInstance();
			combiningAlg = (PolicyCombiningAlgorithm)
				(factory.createAlgorithm(combiningAlgId));
			
			description = 
			    "This PolicySet is for resource-type-based " +
			    "policies that target a set of resource-types, " +
			    "then each Policy has Rules for one of the resource-types";
			
	        policySetTarget = createPolicyTarget(TargetEnum.TG_PS7);
			
	        // add policies P6,P7 to list
	        policies.add(createPolicy(PolicyEnum.P6));
	        policies.add(createPolicy(PolicyEnum.P7));
	        policies.add(createPolicy(PolicyEnum.P8));
			break;
    	} // end switch(policySetEnum)
    	
    	if (logEnabled && !printFirst) {
    		String logString = 
    			"createPolicySet: " + policySetEnum +
    			"\n\tpolicyId: " + policySetId +
    			"\n\tcombiningAlgId: " + combiningAlg.toString();
    		printText(logString);
    	}
		// Create and return the policySet
		PolicySet policySet = 
			new PolicySet(policySetId, 
						  combiningAlg, 
						  description,
						  policySetTarget,
						  policies);
		return policySet;
	}
    
    /**
     * Command-line routine that bundles together all the information needed
     * to create a Policy and then encodes the Policy, printing to standard
     * out.
     */
    public static void main(String [] args) throws Exception {
    	
    	//logEnabled = true;
    	//textOnly = true;
    	//printFirst = true;
    	if (logEnabled) logStatic.info(
    			"args = " + Arrays.toString(args));
    	               
        // create the PolicySet and put the policies in it
        PolicySet policySet = createPolicySet(PolicySetEnum.PS1);
        
        // finally, encode the PolicySet and print it to standard out
        if ( ! logEnabled )
        	policySet.encode(System.out, new Indenter());
    }
    
    public static void printText(String logString) {
		if (textOnly)
			System.out.println(logString);
		else
			logStatic.info(logString);
    }
    
    public enum PolicySetEnum {PS1, PS2, PS3, PS4, PS5, PS6,
    						   PS7};
    public enum PolicyEnum {P1, P2, P3, P4, P5,
    						P6, P7, P8};
    public enum TargetEnum {TG_PS1, TG_PS2, TG_PS3, TG_PS4, TG_PS5, TG_PS6,
    					    TG_PS7,
    						TG_P1, TG_P2, TG_P3, TG_P4, TG_P5,
    						TG_P6, TG_P7, TG_P8,
    						TG_P1_R1, TG_P1_R2, TG_P1_R3, TG_P1_R4,
    						TG_P6_R1,
    						TG_P8_R1,
    						TG_ALL};
    public enum ConditionEnum {CD_P1_R1, CD_P1_R2, CD_P1_R3, CD_P1_R4,
    						   CD_P6_R1,
    						   CD_P8_R1};
    public enum RuleListEnum {RL_P1, RL_P2, RL_P3, RL_P4, RL_P5,
    						  RL_P6, RL_P7, RL_P8};
    public enum ObligationListEnum {OL_P1, OL_P2, OL_P3, OL_P4, OL_P5};
}
