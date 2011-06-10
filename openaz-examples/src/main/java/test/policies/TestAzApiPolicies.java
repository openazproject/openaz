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
 * The basic structures can be seen in the methods called from main:
 * <pre>
 * 
 * 		{@link #createPolicyTarget()}
 * 		{@link #createPolicyRuleList()}
 * 		{@link #createPolicyObligations()}
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
public class TestAzApiPolicies {
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
	
    public static final String XPATH_1_0_VERSION =
        "http://www.w3.org/TR/1999/Rec-xpath-19991116";
	

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
     * @param targetMatchDesignators an array of AttributeMatchExpressions
     * to convert to target matches
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
		    AttributeDesignator attributeDesignator =
		        new AttributeDesignator(
	        		targetMatchDesignators[i].getDesignatorType(),
	        		attributeDataTypeURI,
	                attributeIdURI, 
	                false);
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
     * @return a List of SunXacml Attributes for an Obligation
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
        AttributeDesignator designator =
            new AttributeDesignator(
            		AttributeDesignator.SUBJECT_TARGET,
                    designatorType, 
                    designatorId, 
                    false,
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
     * @throws URISyntaxException if there is a problem with any of the URIs
     */
    public static Rule createRule(
	    		int ruleEffect,
	    		String ruleId,
	    		AttributeMatchExpression[][] ruleSubjectTarget,
	    		AttributeMatchExpression[][] ruleResourceTarget,
	    		AttributeMatchExpression[][] ruleActionTarget,
	    		AttributeMatchExpression[] ruleCondition) 
    		throws URISyntaxException {
        // define the identifier for the rule
        URI ruleIdURI = new URI(ruleId);

        // define the effect for the Rule
        int effect = ruleEffect;

        // get the Target for the rule
        Target target = createPolicyOrRuleTarget(
        					ruleSubjectTarget,
        					ruleResourceTarget,
        					ruleActionTarget);

        // get the Condition for the rule
        Apply condition = null;
        if (ruleCondition.length > 0)
        	condition = createRuleCondition(ruleCondition);
        
        return new Rule(ruleIdURI, effect, null, target, condition);
    }
    
    /**
     * Create a Set of Obligations for the Policy to use
     */
    public static Set createPolicyObligations() 
    		throws URISyntaxException {
        Set obligationSet = new HashSet();
        AttributeMatchExpression[] obligationData = null;
        Obligation obligation = null;
        List obligationAttributeList = null;
        
        // put the actual obligation defns here:
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
                		XACML_ATTR_RESOURCE_ID)
        };
            		
        obligationAttributeList =
        	createObligationAttributeList(obligationData);
        obligation = new Obligation(
        		new URI("LogSuccessfulAccess"),
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
        		new URI("LogDeniedAccess"),
        		Result.DECISION_DENY,
        		obligationAttributeList);
	    obligationSet.add(obligation);
	    
        return obligationSet;
    }

    /**
     * Create a set of rules for the policy
     */
    public static List createPolicyRuleList() 
    		throws URISyntaxException {
        AttributeMatchExpression[][] subjectAttrMatchExprs = null;
        AttributeMatchExpression[][] resourceAttrMatchExprs = null;
        
        // *** build the read rule (note: these are ORs of Action
        // elements, because each AttributeMatchExpression is
        // in a separate (outer) array:
        AttributeMatchExpression[][] actionAttrMatchExprs = 
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
        AttributeMatchExpression[] cmd = 
        	new AttributeMatchExpression[] {};
        // create the read rule
        Rule readRule = createRule(
        		Result.DECISION_PERMIT,
        		"ReadRule",
        		subjectAttrMatchExprs,
        		resourceAttrMatchExprs,
        		actionAttrMatchExprs,
        		cmd);
        
        // *** build the write rule
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
        cmd = new AttributeMatchExpression[] {
            	new AttributeMatchExpression(
            		AttributeDesignator.SUBJECT_TARGET,
            		XACML_FUNC_STRING_EQUAL,
            		XACML_TYPE_STRING,
            		"password",
            		XACML_ATTR_SUBJECT_AUTH_METHOD,
            		"orcl-weblogic", 
            		XACML_FUNC_ONE_AND_ONLY)
            };
        // create the write rule
        Rule writeRule = createRule(
        		Result.DECISION_PERMIT,
        		"WriteRule",
        		subjectAttrMatchExprs,
        		resourceAttrMatchExprs,
        		actionAttrMatchExprs,
        		cmd);
        
        // *** build the role-write rule
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
	    cmd = new AttributeMatchExpression[] {
	        	new AttributeMatchExpression(
	        		AttributeDesignator.SUBJECT_TARGET,
	        		XACML_FUNC_STRING_EQUAL,
	        		XACML_TYPE_STRING,
	        		"developer",
	        		OPENAZ_ATTR_SUBJECT_ROLE_ID,
	        		"orcl-weblogic", 
	        		XACML_FUNC_ONE_AND_ONLY)
	        };
	    // create the role-write rule
	    Rule roleWriteRule = createRule(
	    		Result.DECISION_PERMIT,
	    		"RoleWriteRule",
        		subjectAttrMatchExprs,
        		resourceAttrMatchExprs,
        		actionAttrMatchExprs,
	    		cmd);
    
        // *** build the commit rule
	    actionAttrMatchExprs = new AttributeMatchExpression[][] {{
            	new AttributeMatchExpression(
            		AttributeDesignator.ACTION_TARGET,
            		XACML_FUNC_STRING_EQUAL,
            		XACML_TYPE_STRING,
            		"commit",
            		XACML_ATTR_ACTION_ID)
            }};
        cmd = new AttributeMatchExpression[] {
            	new AttributeMatchExpression(
            		AttributeDesignator.SUBJECT_TARGET,
            		XACML_FUNC_STRING_EQUAL,
            		XACML_TYPE_STRING,
            		"developers",
            		"group",
            		"admin@users.example.com", 
            		XACML_FUNC_ONE_AND_ONLY)
            };
        
        // create the commit rule
        Rule commitRule = createRule(
        		Result.DECISION_PERMIT,
        		"CommitRule",
        		subjectAttrMatchExprs,
        		resourceAttrMatchExprs,
        		actionAttrMatchExprs,
        		cmd);

        // create the default, fall-through rule
        Rule defaultRule = new Rule(new URI("FinalRule"), Result.DECISION_DENY,
                                    null, null, null);

        // create a list for the rules and add our rules in order
        List ruleList = new ArrayList();
        ruleList.add(readRule);
        ruleList.add(writeRule);
        ruleList.add(roleWriteRule);
        ruleList.add(commitRule);
        ruleList.add(defaultRule);
    	
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
    public static Target createPolicyTarget() 
    		throws URISyntaxException {

        AttributeMatchExpression[] tmd = null;
                
        // construct big double array:
        // inner array is AND of ActionMatch, etc elements
        // outer array is OR of Action, etc. elements
        AttributeMatchExpression[][] subjectAttrMatchExprs = null;
        
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
        
        // alternative approach: construct on big double array:
        AttributeMatchExpression[][] resourceAttrMatchExprs = null;
        
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
    	};

        // alternative approach: construct on big double array:
        AttributeMatchExpression[][] actionAttrMatchExprs = null;
        
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
	                
        // create & return new Target
        Target target = createPolicyOrRuleTarget(
        		subjectAttrMatchExprs,
        		resourceAttrMatchExprs,
        		actionAttrMatchExprs);
        return target;
    }
    public static PolicySet getPolicySet(List policies) 
	throws URISyntaxException,
		   UnknownIdentifierException {
		// define the identifier for the policySet
		URI policySetId = new URI("TestAzApi-GeneratedPolicySet");
		
		// get the combining algorithm for the policySet
		URI combiningAlgId = new URI(OrderedPermitOverridesPolicyAlg.algId);
		CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
		PolicyCombiningAlgorithm combiningAlg =
		    (PolicyCombiningAlgorithm)(factory.createAlgorithm(combiningAlgId));
		
		// Get an allow all Target for the policySet
		Target policySetTarget = new Target(null, null, null);
		
		// Create and return the policySet
		PolicySet policySet = 
			new PolicySet(policySetId, 
						  combiningAlg, 
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
        // define the identifier for the policy
        URI policyId = new URI("TestAzApi-GeneratedPolicy");

        // get the combining algorithm for the policy
        URI combiningAlgId = new URI(OrderedPermitOverridesRuleAlg.algId);
        CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
        RuleCombiningAlgorithm combiningAlg =
            (RuleCombiningAlgorithm)(factory.createAlgorithm(combiningAlgId));

        // add a description for the policy
        String description =
            "This policy applies to any accounts at users.example.com " +
            "accessing server.example.com. The one Rule applies to the " +
            "specific action of doing a CVS commit, but other Rules could " +
            "be defined that handled other actions. In this case, only " +
            "certain groups of people are allowed to commit. There is a " +
            "final fall-through rule that always returns Deny.";

        // create the target for the policy
        Target policyTarget = createPolicyTarget();

        // create the Rules the policy will use
        List ruleList = createPolicyRuleList();
        
        // This is where we should add obligations to policy:
        // in xacml 3.0 can add obls to rule as well, which is
        // probably preferable for a policy w many rules.
        
        String defaultVersion = XPATH_1_0_VERSION;
        Set obligationSet = createPolicyObligations();

        // create the policy
        Policy policy = new Policy(policyId, combiningAlg, description,
                                   policyTarget, defaultVersion,
                                   ruleList, obligationSet);
        
        // add policy to list
        List policies = new ArrayList();
        policies.add(policy);
       
        // create a PolicySet and put the policies in it
        PolicySet policySet = getPolicySet(policies);
        
        // finally, encode the policy and print it to standard out
        //policy.encode(System.out, new Indenter());
        policySet.encode(System.out, new Indenter());
    }
    
}
