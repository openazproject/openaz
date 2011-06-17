package test.policies;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import test.policies.OpenAzTokens.LineType;

import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.combine.DenyOverridesPolicyAlg;
import com.sun.xacml.combine.DenyOverridesRuleAlg;
import com.sun.xacml.combine.OrderedDenyOverridesPolicyAlg;
import com.sun.xacml.combine.OrderedDenyOverridesRuleAlg;
import com.sun.xacml.combine.OrderedPermitOverridesPolicyAlg;
import com.sun.xacml.combine.OrderedPermitOverridesRuleAlg;
import com.sun.xacml.combine.PermitOverridesPolicyAlg;
import com.sun.xacml.combine.PermitOverridesRuleAlg;
//import com.sun.xml.internal.bind.v2.schemagen.xmlschema.List;

/*
 * This class contains OpenAz-defined short "abbreviations" for 
 * the corresponding official XACML-defined URIs,
 * that are typically quite lengthy and can tend to obscure
 * the underlying logic by simply taking up too much space.
 * <p>
 * In addition, constants are also defined in order to make the
 * program logic independent of specific constant definitions. 
 */
public class OpenAzPolicyConstants {
    
	// OpenAz Xacml Simple language constructs/abbreviations
    public static final String AZ_POLICYSET = 				"PolicySet";
    public static final String AZ_POLICY = 					"Policy";
    public static final String AZ_RULES = 					"Rules(Policy)";
    public static final String AZ_RULE = 					"Rule";
    public static final String AZ_TARGET_POLICYSET = 		"Target(PolicySet)";
    public static final String AZ_TARGET_POLICY = 			"Target(Policy)";
    public static final String AZ_TARGET_RULE = 			"Target(Rule)";
    public static final String AZ_TARGET_SUBJECT = 			"TS";
    public static final String AZ_TARGET_RESOURCE = 		"TR";
    public static final String AZ_TARGET_ACTION = 			"TA";
    public static final String AZ_TARGET_ENVIRONMENT = 		"TE";
    public static final String AZ_CONDITION_RULE = 			"Condition(Rule)";
    public static final String AZ_OBLIGATIONS_POLICY = 		"Obligations(Policy)";
    public static final String AZ_OBLIGATION = 				"Obligation";
    public static final String AZ_OBLIGATION_ATTRIBUTE = 	"OA";
    
    public static final String AZ_VALUE_NULL = "!"; // "!" = "not" ~="null"
    
    // OpenAz token names: the string is the "language-provided" designator
    // for AttributeId and AttributeValue the names are default first
    // and 2nd values in the line
    public static final String OPENAZ_AME_ATTRIBUTE_DESIGNATOR = "LineTypeToken";
    public static final String OPENAZ_AME_ATTRIBUTE_ID = "AttributeId";
    public static final String OPENAZ_AME_ATTRIBUTE_VALUE = "AttributeValue";
    public static final String OPENAZ_AME_ATTRIBUTE_DATA_TYPE = "dt";
    public static final String OPENAZ_AME_ATTRIBUTE_ISSUER = "issuer";
    public static final String OPENAZ_AME_MATCH_ID = "mtId";
    public static final String OPENAZ_AME_MUST_BE_PRESENT = "mbp";
    public static final String OPENAZ_AME_CONDITION_FUNCTION_ID = "fnId";

    public static final String OPENAZ_AME_USING_PARENT_LINETYPE = 
    												"UsingParentLineType";
    public static final String OPENAZ_AME_USING_PARENT_LINETYPE_TRUE = "t";
    public static final String OPENAZ_AME_USING_PARENT_LINETYPE_FALSE = "f";

    public static final String OPENAZ_POLICY_SET_ID = "id";
    public static final String OPENAZ_POLICY_COMBINING_ALG = "cb";
    public static final String OPENAZ_POLICY_SET_M_LEVEL = "mlev";
    public static final String OPENAZ_POLICY_SET_DESCRIPTION = "desc";
    
    public static final String OPENAZ_POLICY_ID = "id";
    public static final String OPENAZ_RULE_COMBINING_ALG = "cb";
    public static final String OPENAZ_POLICY_DESCRIPTION = "desc";
    
    public static final String OPENAZ_RULE_ID = "id";
    public static final String OPENAZ_RULE_EFFECT = "ef";
    // allow users case-free input, but it is convered to lower case later
    public static final String OPENAZ_RULE_EFFECT_PERMIT = "permit";
    public static final String OPENAZ_RULE_EFFECT_DENY = "deny";
    
    public static final String OPENAZ_OBLIGATION_ID = "id";
    public static final String OPENAZ_OBLIGATION_FULFILL_ON = "fulfillOn";
    
    public static final String OPENAZ_CATEGORY_SUBJECT = "Subject";
    public static final String OPENAZ_CATEGORY_RESOURCE = "Resource";
    public static final String OPENAZ_CATEGORY_ACTION = "Action";
    public static final String OPENAZ_CATEGORY_ENVIRONMENT = "Environment";
    
    
    // There are abbreviations for commonly used AttributeIds like "res-id"
    // that are used in place of the full official xacml names. These need
    // to be substituted from the language shorthand to the XACML URN.
    public static final String OPENAZ_XACML_SUBJECT_ID = "sub-id";
    public static final String OPENAZ_XACML_SUBJECT_AUTHN_ID = "sub-authn";
    public static final String OPENAZ_AZAPI_SUBJECT_ROLE_ID = "az-sub-role-id";
    public static final String OPENAZ_XACML_RESOURCE_ID = "res-id";
    public static final String OPENAZ_AZAPI_RESOURCE_TYPE = "az-res-typ";
    public static final String OPENAZ_XACML_ACTION_ID = "act-id";
    public static final String OPENAZ_XACML_ENVIRONMENT_ID = "env-id";
    
    // There are abbreviations for the individual XACML DataTypes:
    public static final String OPENAZ_XACML_DATATYPE_ANYURI = "anyURI";
    public static final String OPENAZ_XACML_DATATYPE_RFC822 = "rfc822";
    public static final String OPENAZ_XACML_DATATYPE_X500 = "x500name";
    public static final String OPENAZ_XACML_DATATYPE_STRING = "string";
    public static final String OPENAZ_XACML_DATATYPE_BOOLEAN = "boolean";
    public static final String OPENAZ_XACML_DATATYPE_INTEGER = "integer";

    // There are abbreviations for the individual XACML MatchId functions:
    public static final String OPENAZ_XACML_MATCH_ID_STR_EQUAL = "str-eq";
    public static final String OPENAZ_XACML_MATCH_ID_BOOL_EQUAL = "boo-eq";
    public static final String OPENAZ_XACML_MATCH_ID_INT_EQUAL = "int-eq";
    public static final String OPENAZ_XACML_MATCH_ID_RFC822_EQUAL = "rf8-eq";
    public static final String OPENAZ_XACML_MATCH_ID_X500_EQUAL = "x50-eq";
    public static final String OPENAZ_XACML_MATCH_ID_URI_EQUAL = "uri-eq";
    public static final String OPENAZ_XACML_MATCH_ID_REGEXP_MATCH = "rgx-mt";
    
    public static final String OPENAZ_XACML_CONDITION_FCN_ID_ONE_ONLY = "1&only";
    
    public static final String OPENAZ_XACML_MUSTBE_PRES_TRUE = "t";
    public static final String OPENAZ_XACML_MUSTBE_PRES_FALSE = "f";
    
    // combining algorithms:
    public static final String OPENAZ_XACML_POLICY_COMB_ORD_PERM_OVRD = "opo";
    public static final String OPENAZ_XACML_POLICY_COMB_ORD_DENY_OVRD = "odo";
    public static final String OPENAZ_XACML_POLICY_COMB_PERM_OVRD = "po";
    public static final String OPENAZ_XACML_POLICY_COMB_DENY_OVRD = "do";
    public static final String OPENAZ_XACML_RULE_COMB_ORD_PERM_OVRD = "ord-po";
    public static final String OPENAZ_XACML_RULE_COMB_ORD_DENY_OVRD = "ord-do";
    public static final String OPENAZ_XACML_RULE_COMB_PERM_OVRD = "po";
    public static final String OPENAZ_XACML_RULE_COMB_DENY_OVRD = "do";
    
    // Constants for building sunxacml structures
    // Note: the constants below will often correlate with the constants
    // above; below is internal programming constructs, above are the
    // "easy" OpenAz Policy language constructs. For example the 
    // OPENAZ_XACML_MUSTBE_PRES_TRUE string found in an OpenAz line
    // will correlate with the OPENAZ_XACML_MUST_BE_PRESENT_TRUE boolean
    // that gets set for the sunxacml object.
    
    // xacml datatypes:
	public static String 
	XACML_TYPE_ANYURI = 
		"http://www.w3.org/2001/XMLSchema#anyURI";
	public static String
		XACML_TYPE_BOOLEAN = 
			"http://www.w3.org/2001/XMLSchema#boolean";
	public static String
		XACML_TYPE_INTEGER = 
			"http://www.w3.org/2001/XMLSchema#integer";
	public static String
		XACML_TYPE_STRING = 
			"http://www.w3.org/2001/XMLSchema#string";
	public static String
		XACML_TYPE_RFC822 = 
			"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name";
	public static String
		XACML_TYPE_X500NAME =
			"urn:oasis:names:tc:xacml:1.0:data-type:x500Name";
	
	// xacml match function names
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
		XACML_FUNC_BOOLEAN_EQUAL =
			"urn:oasis:names:tc:xacml:1.0:function:boolean-equal";
	public static String
		XACML_FUNC_INTEGER_EQUAL =
			"urn:oasis:names:tc:xacml:1.0:function:integer-equal";
	public static String
		XACML_FUNC_RFC822_MATCH =
			"urn:oasis:names:tc:xacml:1.0:function:rfc822Name-match";
	public static String
		XACML_FUNC_X500NAME_MATCH =
			"urn:oasis:names:tc:xacml:1.0:function:x500Name-match";
	
	// xacml general function names
	public static String
		XACML_FUNC_ONE_AND_ONLY =
			"urn:oasis:names:tc:xacml:1.0:function:string-one-and-only";
	
	// xacml special attribute-ids
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
		XACML_ATTR_ENVIRONMENT_ID =
			"TBD";
	public static String
		XACML_ATTR_SUBJECT_AUTH_METHOD =
			"urn:oasis:names:tc:xacml:1.0:subject:" +
			"authn-locality:authentication-method";
	
	// xacml combining algs
	public static String
		XACML_COMBINE_POLICY_PERMIT_OVERRIDES =
			PermitOverridesPolicyAlg.algId;
	public static String
		XACML_COMBINE_POLICY_DENY_OVERRIDES =
			DenyOverridesPolicyAlg.algId;
	public static String
		XACML_COMBINE_POLICY_ORDERED_PERMIT_OVERRIDES =
			OrderedPermitOverridesPolicyAlg.algId;
	public static String
		XACML_COMBINE_POLICY_ORDERED_DENY_OVERRIDES =
			OrderedDenyOverridesPolicyAlg.algId;
	public static String
		XACML_COMBINE_RULE_PERMIT_OVERRIDES =
			PermitOverridesRuleAlg.algId;
	public static String
		XACML_COMBINE_RULE_DENY_OVERRIDES =
			DenyOverridesRuleAlg.algId;
	public static String
		XACML_COMBINE_RULE_ORDERED_PERMIT_OVERRIDES =
			OrderedPermitOverridesRuleAlg.algId;
	public static String
		XACML_COMBINE_RULE_ORDERED_DENY_OVERRIDES =
			OrderedDenyOverridesRuleAlg.algId;
	
	// openaz attribute-ids:
	public static String
		OPENAZ_ATTR_RESOURCE_TYPE =
			"urn:openaz:names:xacml:1.0:resource:resource-type";
	public static String
		OPENAZ_ATTR_SUBJECT_ROLE_ID =
			"urn:openaz:names:xacml:1.0:subject:role-id";
		  //"test.role.principal.role-id";
	
	// openaz metadata values:
	public static String
		OPENAZ_ATTRIBUTE_ID_DESIGNATOR = "AttributeId";
	public static String
		OPENAZ_ATTRIBUTE_VALUE_DESIGNATOR = "AttributeValue";
	
	// other openaz constants
	public static boolean
		OPENAZ_XACML_MUST_BE_PRESENT_TRUE = true;
	public static boolean
		OPENAZ_XACML_MUST_BE_PRESENT_FALSE = false;
	
	// Basically, this is a Category like Subject, Action, Resource and
	// Environment for collecting attributes in the XACML Response
	// pick a number large enough not to
	// overlap sunxacml designators
	// Note: the concept of "Category" and "entity" are still under
	// discussion in XACML. At present the XACML definitions are not
	// rigorous and leave room to create significant potential 
	// interoperability problems
	public static int
		OPENAZ_OBLIGATION_DESIGNATOR = 101; 
	
	// xacml xpath ref string
	public static final String 
		XPATH_1_0_VERSION =
			"http://www.w3.org/TR/1999/Rec-xpath-19991116";
	
	public static final Map<String,Integer> 
		openAzLineTypeToAttrDesignatorMap = 
	        new HashMap<String,Integer>() {{
				put(LineType.TS.toString(), 
						AttributeDesignator.SUBJECT_TARGET);
				put(LineType.TR.toString(), 
						AttributeDesignator.RESOURCE_TARGET);
				put(LineType.TA.toString(), 
						AttributeDesignator.ACTION_TARGET);
				put(LineType.TE.toString(), 
						AttributeDesignator.ENVIRONMENT_TARGET);
				put(LineType.OA.toString(), 
						OPENAZ_OBLIGATION_DESIGNATOR);
		}};
	
	public static final Map<String,String> 
		openAzAttrIdToXacmlAttrIdMap = 
	        new HashMap<String,String>() {{
				put(OPENAZ_XACML_SUBJECT_ID, 
						XACML_ATTR_SUBJECT_ID);
				put(OPENAZ_XACML_SUBJECT_AUTHN_ID, 
						XACML_ATTR_SUBJECT_AUTH_METHOD);
				put(OPENAZ_AZAPI_SUBJECT_ROLE_ID,
						OPENAZ_ATTR_SUBJECT_ROLE_ID);
				put(OPENAZ_XACML_RESOURCE_ID, 
						XACML_ATTR_RESOURCE_ID);
				put(OPENAZ_AZAPI_RESOURCE_TYPE, 
						OPENAZ_ATTR_RESOURCE_TYPE);
				put(OPENAZ_XACML_ACTION_ID, 
						XACML_ATTR_ACTION_ID);
				put(OPENAZ_XACML_ENVIRONMENT_ID, 
						XACML_ATTR_ENVIRONMENT_ID);
			}};

	/**
	 * Table to map openaz "shorthand" strings for DataType to the 
	 * xacml "long" URI official DataType names
	 */
	public static final Map<String,String> 
		openAzDataTypeToXacmlDataTypeMap = 
	        new HashMap<String,String>() {{
				put(OPENAZ_XACML_DATATYPE_STRING, 
						XACML_TYPE_STRING);
				put(OPENAZ_XACML_DATATYPE_BOOLEAN, 
						XACML_TYPE_BOOLEAN);
				put(OPENAZ_XACML_DATATYPE_INTEGER, 
						XACML_TYPE_INTEGER);
				put(OPENAZ_XACML_DATATYPE_ANYURI, 
						XACML_TYPE_ANYURI);
				put(OPENAZ_XACML_DATATYPE_RFC822, 
						XACML_TYPE_RFC822);
				put(OPENAZ_XACML_DATATYPE_X500, 
						XACML_TYPE_X500NAME);
			}};
			
	/**
	 * Table to map openaz "shorthand" strings for MatchId&lt;type&gt;
	 * to the xacml "long" URI official MatchId&lt;type&gt; names
	 */
	public static final Map<String,String> 
		openAzMatchIdToXacmlMatchIdMap = 
			new HashMap<String,String>() {{
				put(OPENAZ_XACML_MATCH_ID_STR_EQUAL, 
						XACML_FUNC_STRING_EQUAL);
				put(OPENAZ_XACML_MATCH_ID_BOOL_EQUAL, 
						XACML_FUNC_BOOLEAN_EQUAL);
				put(OPENAZ_XACML_MATCH_ID_INT_EQUAL, 
						XACML_FUNC_INTEGER_EQUAL);
				put(OPENAZ_XACML_MATCH_ID_URI_EQUAL, 
						XACML_FUNC_ANYURI_EQUAL);
				put(OPENAZ_XACML_MATCH_ID_REGEXP_MATCH, 
						XACML_FUNC_REGEXP_STRING_MATCH);
				put(OPENAZ_XACML_MATCH_ID_RFC822_EQUAL, 
						XACML_FUNC_RFC822_MATCH);
				put(OPENAZ_XACML_MATCH_ID_X500_EQUAL, 
						XACML_FUNC_X500NAME_MATCH);
			}};
			
	/**
	 * Table to map openaz "shorthand" strings for FunctionId&lt;type&gt;
	 * to the xacml "long" URI official FunctionId&lt;type&gt; names
	 * <p>
	 * Note: there is overlap with this table and the MatchId table.
	 * MatchIds can be considered the subset of FunctionIds that can
	 * be used in the Policy Match statements in Target elements,
	 * as opposed to the more broad set of FunctionIds that can
	 * be used in Condition statements.
	 */
	public static final Map<String,String> 
		openAzFunctionIdToXacmlFunctionIdMap = 
			new HashMap<String,String>() {{
				put(OPENAZ_XACML_CONDITION_FCN_ID_ONE_ONLY,
						XACML_FUNC_ONE_AND_ONLY);
				put(OPENAZ_XACML_MATCH_ID_STR_EQUAL, 
						XACML_FUNC_STRING_EQUAL);
				put(OPENAZ_XACML_MATCH_ID_BOOL_EQUAL, 
						XACML_FUNC_BOOLEAN_EQUAL);
				put(OPENAZ_XACML_MATCH_ID_INT_EQUAL, 
						XACML_FUNC_INTEGER_EQUAL);
				put(OPENAZ_XACML_MATCH_ID_URI_EQUAL, 
						XACML_FUNC_ANYURI_EQUAL);
				put(OPENAZ_XACML_MATCH_ID_REGEXP_MATCH, 
						XACML_FUNC_REGEXP_STRING_MATCH);
				put(OPENAZ_XACML_MATCH_ID_RFC822_EQUAL, 
						XACML_FUNC_RFC822_MATCH);
				put(OPENAZ_XACML_MATCH_ID_X500_EQUAL, 
						XACML_FUNC_X500NAME_MATCH);
			}};
	public static final Map<String,String> 
		openAzRuleCombAlgToXacmlRuleCombAlgMap = 
			new HashMap<String,String>() {{
				put(OPENAZ_XACML_RULE_COMB_ORD_PERM_OVRD,
						XACML_COMBINE_RULE_ORDERED_PERMIT_OVERRIDES);
				put(OPENAZ_XACML_RULE_COMB_ORD_DENY_OVRD,
						XACML_COMBINE_RULE_ORDERED_DENY_OVERRIDES);
				put(OPENAZ_XACML_RULE_COMB_PERM_OVRD,
						XACML_COMBINE_RULE_PERMIT_OVERRIDES);
				put(OPENAZ_XACML_RULE_COMB_DENY_OVRD,
						XACML_COMBINE_RULE_DENY_OVERRIDES);
			}};
	public static final Map<String,String> 
		openAzPolicyCombAlgToXacmlPolicyCombAlgMap = 
			new HashMap<String,String>() {{
				put(OPENAZ_XACML_POLICY_COMB_ORD_PERM_OVRD,
						XACML_COMBINE_POLICY_ORDERED_PERMIT_OVERRIDES);
				put(OPENAZ_XACML_POLICY_COMB_ORD_DENY_OVRD,
						XACML_COMBINE_POLICY_ORDERED_DENY_OVERRIDES);
				put(OPENAZ_XACML_POLICY_COMB_PERM_OVRD,
						XACML_COMBINE_POLICY_PERMIT_OVERRIDES);
				put(OPENAZ_XACML_POLICY_COMB_DENY_OVRD,
						XACML_COMBINE_POLICY_DENY_OVERRIDES);
			}};
}
