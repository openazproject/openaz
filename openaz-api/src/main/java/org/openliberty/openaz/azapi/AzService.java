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
import java.util.Set;

/**
 * The AzService interface is the main entry point and service
 * module for this package, which is generally referred to as
 * the "AzAPI" or "AzApi" (Authorization Application Programming Interface).
 * <p>
 * TBD: configuration specifics need to be abstracted:
 * An instance of an implementation of the AzService is obtained from
 * the <code>org.openliberty.openaz.pdp.provider.AzServiceFactory</code>.
 * <p>
 * The AzService interface provides a factory interface from which
 * instances of an AzRequestContext object can be created. The
 * AzRequestContext object is the baseline object within the AzAPI
 * that enables all the information to be assembled that is required
 * to construct either a single authorization request or a
 * collection that may include multiple authorization requests.
 * <p>
 * The AzRequestContext object that is created to submit an authorization
 * decision request and the AzResponseContext object that is returned
 * from that authorization decision request are compatible with the
 * XACML 2.0 Core Specification:
 * <br>
 * http://docs.oasis-open.org/xacml/2.0/access_control-xacml-2.0-core-spec-os.pdf
 * <br>
 * as well as with the "multiple <Resource> elements" option of the
 * XACML 2.0 Multiple Resource Profile:
 * <br>
 * http://docs.oasis-open.org/xacml/2.0/access_control-xacml-2.0-mult-profile-spec-os.pdf
 * <p>
 * AzService also provides the main methods decide() and query() that
 * process the AzRequestContext object, generally using enterprise
 * infrastructure services (Policy Decision Points (PDPs) and Policy
 * Information Points (PIPs)), and upon receiving results from the
 * infrastructure services, the AzService prepares and returns the
 * results in the AzResponseContext object. It is generally assumed that
 * this AzAPI will be utilized by enterprise infrastructure Policy
 * Enforcement Points (PEPs) to obtain the decision which enables
 * the PEP to apply the appropriate enforcement actions.
 * <p>
 * Note: Being an "API", the "user" of this AzAPI is generally a
 * program that calls the methods available in the AzAPI. That program
 * will be referred to as the "AzAPI caller". The AzAPI may generally
 * be considered to be making authorization requests on behalf of
 * the Subject(s) represented within the AzRequestContext, and
 * generally not on behalf of the "AzAPI caller", except in the
 * case where Subject codebase may represent the AzAPI caller as
 * part of some specific authorization request.
 * <p>
 * Note: Phonetically, the documentation in this package uses the
 * consonant-sounding dialect of XACML, as in "a XACML Attribute",
 * where the "X" is pronounced like a "Z".
 *
 * @author Rich
 *
 */
public interface AzService extends AzRequestContextFactory{

	
	/**
	 * Returns the authorization decision(s) in an AzResponseContext
	 * object.
	 * <p>
	 * There will be one AzDecision contained in an AzResult object in
	 * the AzResponseContext object, where one AzResult/AzDecision
	 * corresponds to one AzResourceActionAssociation contained in the
	 * submitted AzRequestContext object.
	 * <p>
	 * @param  azRequestContext an azRequestContext that has been prepared to
	 * contain the request information needed for one or more authorization
	 * decisions
	 * @return an azResponseContext that contains all the authorization
	 * decisions corresponding to the one or more requests that were
	 * provided in the azRequestContext
	 */
	public AzResponseContext decide(AzRequestContext azRequestContext);
	
	/**
	 * Returns the set of AzResourceActionAssociations that are
	 * allowed (or not allowed, depending on boolean below)
	 * for the specified "scope", where "scope" is a
	 * String expression representing a set of target resources.
	 * <br>
	 * The specific format of "scope" is based on how the resources
	 * and actions are represented within the Policy definitions
	 * on the PDP.
	 *
	 * @param scope             PDP policy-specific resource representation
	 * @param azRequestContext  only the AzEnvironment and AzSubject entities will
	 * be considered in the evaluation
	 * @param allowedNotAllowed  true: return allowed results; false: return
	 * the not-allowed results.
	 * @return a set of azResourceActionAssociations representing
	 * the allowed (or not-allowed)resource-actions within the
	 * requested scope.
	 *
	 */
	public Set<AzResourceActionAssociation> query(
			String scope, AzRequestContext azRequestContext,
			boolean allowedNotAllowed);
	
	/**
	 * Returns an AzResponseContext that contains all the results
	 * for queries that are within the specified "scope", where
	 * "scope" is a String expression representing a set of target
	 * resources.
	 * <p>
	 * The AzResponseContext will contain full information including
	 * Obligations for each request within the scope.
	 *
	 * @param scope             PDP policy-specific resource representation
	 * @param azRequestContext  only the AzEnvironment and AzSubject entities will
	 * be considered in the evaluation
	 * @return  azResponseContext with AzDecision and all info for
	 * resource-actions within the requested scope.
	 *
	 */
	public AzResponseContext queryVerbose(
			String scope, AzRequestContext azRequestContext);
	
	public <T extends Enum<T> & AzCategoryId> 
		void registerAzAttributeFinder(
			AzAttributeFinder<T> azAttributeFinder);
}