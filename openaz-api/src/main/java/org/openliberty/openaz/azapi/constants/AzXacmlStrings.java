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
 * This module contains specific XACML-defined identifiers
 * that are used in the course of submitting XACML authorization
 * requests.
 * Specifically, there are several XACML AttributeIds defined, which
 * pertain to commonly required Attributes that are often needed for
 * making authorization decisions.
 * The attributes defined below have their descriptions, if any, taken
 * from the XACML 2.0 Core specification. Below are general descriptions 
 * of the attributes defined here, prefixed by the section of the XACML
 * core specification where the information originates:
 * 
 * <p>
 * B.1. <b>XACML namespaces:</b> There are currently two defined XACML 
 * namespaces: one for Policies and one for Request/Response 
 * interaction.
 * <p>
 * B.4. <b>Subject attributes:</b> These identifiers indicate 
 * attributes of a subject. 
 * <br>When used, they SHALL appear within a 
 * <Subject> element of the request context. They SHALL be accessed 
 * by means of a <SubjectAttributeDesignator> element 
 * (, or an <AttributeSelector> element) that points into a <Subject> 
 * element of the request context.
 * <br>
 * At most one of each of these attributes is associated with each subject. Each attribute associated with authentication included within a single <Subject> element relates to the same authentication event.
 * <br>
 * Where a suitable attribute is already defined in LDAP [LDAP-1, LDAP-2], the XACML identifier SHALL be formed by adding the attribute name to the URI of the LDAP specification. For example, the attribute name for the userPassword defined in the RFC 2256 SHALL be: 
 * <br>http://www.ietf.org/rfc/rfc2256.txt#userPassword
 * <p>
 * B.6. <b>Resource attributes:</b> These identifiers indicate 
 * attributes of the resource. 
 * <br>The corresponding attributes MAY appear 
 * in the <Resource> element of the request context and be accessed by 
 * means of a <ResourceAttributeDesignator> element(, or by an 
 * <AttributeSelector> element) that points into the <Resource> 
 * element of the request context.
 * <p>
 * B.7. <b>Action attributes:</b> These identifiers indicate attributes 
 * of the action being requested. 
 * <br>
 * When used, they SHALL appear within 
 * the <Action> element of the request context. They SHALL be accessed 
 * by means of an <ActionAttributeDesignator> element (, or an 
 * <AttributeSelector> element) that points into the <Action> element 
 * of the request context.
 * <p>
 * B.8. <b>Environment attributes:</b> These identifiers indicate 
 * attributes of the environment within which the decision request 
 * is to be evaluated. When used in the decision request, they SHALL 
 * appear in the <Environment> element of the request context. 
 * They SHALL be accessed by means of an <EnvironmentAttributeDesignator> 
 * element (, or an <AttributeSelector> element) that points into the 
 * <Environment> element of the request context.

 * 
 * @author rlevinson
 *
 */
public interface AzXacmlStrings {
	
	// XACML namespaces
	/** XACML Namespace: Policy schema: <b>urn:oasis:names:tc:xacml:2.0:policy:schema:os</b> */
	public static final String X_SCHEMA_POLICY = 
		"urn:oasis:names:tc:xacml:2.0:policy:schema:os";
	/** XACML Namespace: Request/Response context schema: <b>urn:oasis:names:tc:xacml:2.0:context:schema:os</b> */
	public static final String X_SCHEMA_CONTEXT = 
		"urn:oasis:names:tc:xacml:2.0:context:schema:os";
	
	//XACML Action Attributes

	/** XACML Action Attribute: <b>urn:oasis:names:tc:xacml:1.0:action:action-id</b>. 
	 * <br>This attribute identifies the action for which access is requested. */
	public static final String X_ATTR_ACTION_ID = 
		"urn:oasis:names:tc:xacml:1.0:action:action-id";

	/** XACML action-id Attribute <b>Value</b>: Where the action is implicit, 
	 * the <b>value</b> of the <b>action-id attribute</b> SHALL be: 
	 * <b>urn:oasis:names:tc:xacml:1.0:action:implied-action</b> */
	public static final String X_ATTR_ACTION_IMPLIED_ACTION = 
		"urn:oasis:names:tc:xacml:1.0:action:implied-action";
	
	/** XACML Action Attribute: <b>urn:oasis:names:tc:xacml:1.0:action:action-namespace</b>.
	 * <br>This attribute identifies the namespace in which the action-id attribute is defined. */
	public static final String X_ATTR_ACTION_NAMESPACE = 
		"urn:oasis:names:tc:xacml:1.0:action:action-namespace";
	
	//XACML Resource Attributes
	/** XACML Resource Attribute: <b>urn:oasis:names:tc:xacml:1.0:resource:resource-id</b> */
	public static final String X_ATTR_RESOURCE_ID = 
		"urn:oasis:names:tc:xacml:1.0:resource:resource-id";
	/** XACML Resource Attribute: <b>urn:oasis:names:tc:xacml:2.0:resource:target-namespace</b> */
	public static final String X_ATTR_RESOURCE_TARGET_NAMESPACE = 
		"urn:oasis:names:tc:xacml:2.0:resource:target-namespace";
	/** XACML Resource Attribute: <b>urn:oasis:names:tc:xacml:1.0:resource:xpath</b> */
	public static final String X_ATTR_RESOURCE_XPATH = 
		"urn:oasis:names:tc:xacml:1.0:resource:xpath";
	
	//XACML Environment Attributes:
	/** XACML Environment Attribute: <b>urn:oasis:names:tc:xacml:2.0:context:schema:os</b> */
	public static final String X_ATTR_ENV_CURRENT_TIME = 
		"urn:oasis:names:tc:xacml:1.0:environment:current-time";
	/** XACML Environment Attribute: <b>urn:oasis:names:tc:xacml:2.0:context:schema:os</b> */
	public static final String X_ATTR_ENV_CURRENT_DATE = 
		"urn:oasis:names:tc:xacml:1.0:environment:current-date";
	/** XACML Environment Attribute: <b>urn:oasis:names:tc:xacml:2.0:context:schema:os</b> */
	public static final String X_ATTR_ENV_CURRENT_DATE_TIME = 
		"urn:oasis:names:tc:xacml:1.0:environment:current-dateTime";
	
	//XACML Subject Attributes:
	/** XACML Subject Attribute: <b>urn:oasis:names:tc:xacml:1.0:subject:subject-id</b> */
	public static final String X_ATTR_SUBJECT_ID = 
		"urn:oasis:names:tc:xacml:1.0:subject:subject-id";
	/** XACML Subject Attribute: <b>urn:oasis:names:tc:xacml:1.0:subject-category</b> */
	public static final String X_ATTR_SUBJECT_CATEGORY = 
		"urn:oasis:names:tc:xacml:1.0:subject-category";
	/** XACML Subject Attribute: <b>urn:oasis:names:tc:xacml:1.0:subject:subject-id-qualifier</b> */
	public static final String X_ATTR_SUBJECT_ID_QUALIFIER = 
		"urn:oasis:names:tc:xacml:1.0:subject:subject-id-qualifier";
	/** XACML Subject Attribute: <b>urn:oasis:names:tc:xacml:1.0:subject:key-info</b> */
	public static final String X_ATTR_SUBJECT_KEY_INFO = 
		"urn:oasis:names:tc:xacml:1.0:subject:key-info";
	/** XACML Subject Attribute: <b>urn:oasis:names:tc:xacml:1.0:subject:authentication-time</b> */
	public static final String X_ATTR_SUBJECT_AUTHENTICATION_TIME = 
		"urn:oasis:names:tc:xacml:1.0:subject:authentication-time";
	/** XACML Subject Attribute: <b>urn:oasis:names:tc:xacml:1.0:subject:authn-locality:authentication-method</b> */
	public static final String X_ATTR_SUBJECT_AUTHN_LOC_AUTHENTICATION_METHOD = 
	   "urn:oasis:names:tc:xacml:1.0:subject:authn-locality:authentication-method";
	/** XACML Subject Attribute: <b>urn:oasis:names:tc:xacml:1.0:subject:request-time</b> */
	public static final String X_ATTR_SUBJECT_REQUEST_TIME = 
		"urn:oasis:names:tc:xacml:1.0:subject:request-time";
	/** XACML Subject Attribute: <b>urn:oasis:names:tc:xacml:1.0:subject:session-start-time</b> */
	public static final String X_ATTR_SUBJECT_SESSION_START_TIME = 
		"urn:oasis:names:tc:xacml:1.0:subject:session-start-time";
	/** XACML Subject Attribute: <b>urn:oasis:names:tc:xacml:1.0:subject:authn-locality:ip-address</b> */
	public static final String X_ATTR_SUBJECT_AUTHN_LOC_IP_ADDRESS = 
		"urn:oasis:names:tc:xacml:1.0:subject:authn-locality:ip-address";
	/** XACML Subject Attribute: <b>urn:oasis:names:tc:xacml:1.0:subject:authn-locality:dns-name</b> */
	public static final String X_ATTR_SUBJECT_AUTHN_LOC_DNS_NAME = 
		"urn:oasis:names:tc:xacml:1.0:subject:authn-locality:dns-name";
	/** XACML Subject Attribute: <b>http://www.ietf.org/rfc/rfc2256.txt#userPassword</b> */
	public static final String X_ATTR_SUBJECT_USER_PASSWORD = 
		"http://www.ietf.org/rfc/rfc2256.txt#userPassword";
	
	
	/*
	//XACML Attribute Categories (from xacml 3.0)
	public static final String X_CATEGORY_RESOURCE = 
		"urn:oasis:names:tc:xacml:3.0:attribute-category:resource";
	public static final String X_CATEGORY_ACTION = 
		"urn:oasis:names:tc:xacml:3.0:attribute-category:action";
	//public static final String X_CATEGORY_ENVIRONMENT = 
	//    "urn:oasis:names:tc:xacml:3.0:attribute-category:environment";
	public static final String X_CATEGORY_SUBJECT_ACCESS = 
		"urn:oasis:names:tc:xacml:1.0:subject-category:access-subject";
	public static final String X_CATEGORY_SUBJECT_RECIPIENT = 
		"urn:oasis:names:tc:xacml:1.0:subject-category:recipient-subject";
	public static final String X_CATEGORY_SUBJECT_INTERMEDIARY = 
		"urn:oasis:names:tc:xacml:1.0:subject-category:intermediary-subject";
	public static final String X_CATEGORY_SUBJECT_CODEBASE = 
		"urn:oasis:names:tc:xacml:1.0:subject-category:codebase";
	public static final String X_CATEGORY_SUBJECT_REQUESTING_MACHINE = 
		"urn:oasis:names:tc:xacml:1.0:subject-category:requesting-machine";
	*/
	
	/*
	//XACML Status Codes:
	public static final String X_STATUS_OK = 
		"urn:oasis:names:tc:xacml:1.0:status:ok";
	public static final String X_STATUS_MISSING_ATTRIBUTE = 
		"urn:oasis:names:tc:xacml:1.0:status:missing-attribute";
	public static final String X_STATUS_SYNTAX_ERROR = 
		"urn:oasis:names:tc:xacml:1.0:status:syntax-error";
	public static final String X_STATUS_PROCESSING_ERROR = 
		"urn:oasis:names:tc:xacml:1.0:status:processing-error";
	*/
	
	/*
	// XACML Data-types:
	public static final String X_BOOLEAN = 
		"http://www.w3.org/2001/XMLSchema#boolean";
	public static final String X_STRING = 
		"http://www.w3.org/2001/XMLSchema#string";
	public static final String X_INTEGER = 
		"http://www.w3.org/2001/XMLSchema#integer";
	public static final String X_DOUBLE = 
		"http://www.w3.org/2001/XMLSchema#double";
	public static final String X_TIME = 
		"http://www.w3.org/2001/XMLSchema#time";
	public static final String X_DATE = 
		"http://www.w3.org/2001/XMLSchema#date";
	public static final String X_DATETIME = 
		"http://www.w3.org/2001/XMLSchema#dateTime";
	public static final String X_ANYURI = 
		"http://www.w3.org/2001/XMLSchema#anyURI";
	public static final String X_HEXBINARY = 
		"http://www.w3.org/2001/XMLSchema#hexBinary";
	public static final String X_BASE64BINARY = 
		"http://www.w3.org/2001/XMLSchema#base64Binary";
	public static final String X_DAYTIME_DURATION = 
	   "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#dayTimeDuration";
	public static final String X_YEARMONTH_DURATION = 
	   "http://www.w3.org/TR/2002/WD-xquery-operators-20020816#yearMonthDuration";
	public static final String X_X500NAME = 
		"urn:oasis:names:tc:xacml:1.0:data-type:x500Name";
	public static final String X_RFC822NAME = 
		"urn:oasis:names:tc:xacml:1.0:data-type:rfc822Name";
	public static final String X_IPADDRESS = 
		"urn:oasis:names:tc:xacml:2.0:data-type:ipAddress";
	public static final String X_DNSNAME = 
		"urn:oasis:names:tc:xacml:2.0:data-type:dnsName";
	*/
}
