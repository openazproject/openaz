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

import java.util.Set;

import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;


/**
 * The AzRequestContext is the structure used to collect all the 
 * information that is required to make one or more authorization 
 * requests.
 * <p>
 * The AzRequestContext is used as the input to a decision. 
 * It primarily consists of a group of collections of AzEntity
 * objects, where each AzEntity contains a collection of 
 * AzAttributes describing that entity. From a XACML perspective,
 * one may think of the AzEntity objects representing "actors" in
 * a XACML decision request, where the actors consist of the entity
 * making the request (Subject), the entity representing the request
 * (Action), the entity representing that which access is being requested
 * to (Resource) and an entity capturing general operating conditions
 * under which the request is being made (Environment).
 * one or more AzSubjects.
 * <p>
 * The official term XACML uses to distinguish these actors is "Category".
 * The allowed Category identifiers are represented in AzApi by Enums that
 * implement the {@link org.openliberty.openaz.azapi.constants.AzCategoryId} interface.
 * The general rule is that a single authorization decision corresponds to
 * a set of entities, where only one entity per Category is allowed. 
 * <p>
 * The general mechanism by which multiple decisions can be requested in a
 * single AzRequestContext object is by including multiple entities within
 * the same category, then for each entity within a category, a separate 
 * decision will be made using each member of the specific category, against
 * a fixed set of members, one each, of the other categories. The specifics
 * of these mechanisms vary, with the most significant constraint being
 * that AzResourceActionAssociations are created to indicate specific
 * resource-action combinations against which a decision will be requested
 * as opposed to simply requesting the cross-product of all resources and
 * actions in an AzRequestContext.
 * <p>
 * Note:  an AzRequestContext may be populated incrementally by 
 * several independent software components prior to the request 
 * actually being submitted to a PDP.
 * <br>
 * Note: there is no direct support for XACML Attribute Selectors,
 * which are primarily xpaths used to access attributes in supplied
 * xml content. All attributes from this AzAPI should be considered
 * to be accessible using XACML Attribute Designators, which are based
 * on using reference to the AttributeId(required), Issuer(optional), 
 * DataType(required), and Category(required, but generally implicit)
 * associated with a XACML Attribute.
 * @author Rich
 *
 */
public interface AzRequestContext {
	
	/**
	 * Create a new unbound AzEntity&lt;T&gt; object, that
	 * may be later added using {@link #addAzEntity(AzEntity)}.
	 * @param <T>
	 * @param t a specific AzCategoryId subtype, 
	 * &lt;? extends AzCategoryId&gt;, indicating the specific
	 * subtype of AzEntity&lt;T&gt; being requested.
	 * @return an unbound AzEntity&lt;T&gt; object of the
	 * type specified in the parameter, t.
	 */
	public <T extends Enum<T> & AzCategoryId> 
		AzEntity<T>  createNewAzEntity(T t);
	
	/**
	 * Creates and returns a bound AzEntity&lt;T&gt; object, where T is
	 * any supported subcategory of AzCategoryId and the object
	 * is bound to the current instance of AzRequestContext.
	 * To create unbound AzEntity objects, use 
	 * {@link #createNewAzEntity(Enum)}.
	 * <p> 
	 * The created AzEntity&lt;T&gt; entity is added to the
	 * internal collection for AzRequestContext based
	 * collections for subject, action, resource, and
	 * environment categories.
	 * <p>
	 * Note: for convenience specific AzEntity&lt;T&gt; subtypes,
	 * such as AzEntity&lt;AzCategoryIdResource&gt; may be referred
	 * to in this javadoc by the shortened term Az&lt;Resource&gt;.
	 * or, in general, Az&lt;xacml-category-name&gt;
	 * <p>
	 * Note: adding an AzEntity&lt;{@link AzCategoryIdResource}&gt; object 
	 * or an AzEntity&lt;AzCategoryIdAction&gt; object to the internal
	 * collection does not guarantee the Az&lt;Resource&gt; or
	 * Az&lt;Action&gt; will be used in a decision.
	 * In order to make that guarantee, an Az&lt;Resource&gt; and
	 * an Az&lt;Action&gt; object need to 
	 * be added to an AzResourceActionAssociation object
	 * {@link #createAndAddResourceActionAssociation(AzEntity, AzEntity)}
	 * and similar methods for adding multiple combinations
	 * such as {@link #addResourceActionAssociation(AzEntity, Set)}.
	 * <p>
	 * Note: there is only one Az&lt;Environment&gt; object supported,
	 * so using this method to create an Az&lt;Environment&gt; object
	 * will replace and delete a previously set Az&lt;Environment&gt;
	 * object.
	 * <p>
	 * Note: multiple Az&lt;SubjectAccess&gt; objects can be added,
	 * but only single instances of other Az&lt;Subject*&gt; objects.
	 * <p>
	 * Note: XACML 2.0 Multiple Resource Profile only accepts
	 * multiple Resource elements. However, this is planned
	 * to be expanded in XACML 3.0 to include other categories.
	 * The limits defined here may be changed when the 
	 * XACML 3.0 Profile is clarified.
	 * <p>
	 * @param t a specific &lt;? extends AzCategoryId&gt; Enum subtype
	 * @return a reference to the newly created 
	 * AzEntity&lt;? extends AzCategoryId&gt; subtype that was
	 * also added to the associated internal collection
	 * for that subtype.
	 * @return null if the specified AzEntity&lt;? extends AzCategoryId&gt;
	 * is not supported for adding to the AzRequestContext. For
	 * example, an Az&lt;Obligation&gt; would not be added to the 
	 * AzRequestContext.
	 * 
	 */
	public <T extends Enum<T> & AzCategoryId> 
			AzEntity<T>  createAzEntity(T t);
		
	/**
	 * Adds an AzEntity<T> object to the request context, subject
	 * to the restrictions on the number of AzEntities of that type
	 * allowed {@link #createAzEntity(Enum)}.
	 * @param <T>
	 * @param azEntity
	 * @return boolean true if add was successful, otherwise false
	 */
	public <T extends Enum<T> & AzCategoryId> 
		boolean addAzEntity(AzEntity<T> azEntity);

	/**
	 * Gets an AzEntity&lt;T&gt; object from the internal
	 * collection holding AzEntity objects of specified
	 * type provided by the parameter, t.
	 * <p>
	 * If there is more than one object in the collection
	 * it is indeterminate which one is returned. To search the
	 * collection when it contains multiple members use
	 * {@link #getAzEntitySet(Enum)} which provides a Set
	 * that can be examined with an Iterator.
	 * @param <T>
	 * @param t
	 * @return an {@link AzEntity}&lt;T&gt; object
	 */
	public <T extends Enum<T> & AzCategoryId> 
		AzEntity<T> getAzEntity(T t);
		
	/**
	 * Get the Set&lt;AzEntity&lt;T&gt;&gt; 
	 * held by this AzRequestContext, where T is a subtype of
	 * &lt;? extends AzCategoryId&gt;. 
	 * <p>
	 * If no AzEntity objects have been added in the particular
	 * subtype requested, then an empty Set will be returned.
	 * <p>
	 * @param t a subtype of AzCategoryId
	 * @return a Set&lt;AzEntity&lt;? extends AzCategoryId&gt;&gt; 
	 * containing the
	 * AzEntity objects of the subtype that has been provided.
	 */
	public <T extends Enum<T> & AzCategoryId> 
		Set<AzEntity<? extends AzCategoryId>> 
			getAzEntitySet(T t);
	//Set<AzEntity<T>> // this has repercussions, the above is
	// more of a smooth carry from existing Sets
	
	/**
	 * Removes the AzSubject, if one exists, with the specified AzCategoryId.
	 * If no AzSubjects remain, AzRequestContext provides empty Subject element 
	 * for input. i.e. Subject element will always be provided, even if empty.
	 * 
	 * @param azSubjectCategoryId
	 * @return boolean true if object found and removed, ow false
	 */
	public <T extends Enum<T> & AzCategoryId> 
		boolean removeSubject(T azSubjectCategoryId);
	
	/**
	 * Add an AzResourceActionAssociation to the AzRequestContext by providing
	 * an AzResource entity and an AzAction entity, which the method will 
	 * combine into the appropriate association object and add it to the Set
	 * of associations.
	 * <p>
	 * Note that the association is based on associating the AzResource and
	 * AzAction objects, independent of the sets of attributes that the 
	 * objects contain. 
	 * <br>
	 * Therefore, the association can be established independently of the 
	 * presence of resource-id and action-id attributes within the objects.
	 * <p>
	 * TBD: do we need to require the presence of resource-id and action-id
	 * attributes and thence a relate Exception? These are not required by 
	 * XACML 2.0 (for resource-id see sections 6.3 (MAY contain one or more 
	 * resource-id attributes), section 6.10 (lines 3080-3083), section B.6 )
	 * <br> 
	 * (for action-id see section B.7, section 6.1 (lines 2896-2898), 
	 * section 6.5
	 * @param azResource
	 * @param azAction
	 * @return a reference to the newly created azResourceActionAssociation,
	 * 			which may be used to correlate with a specific returned
	 * 			azResult.
	 * @see AzResourceActionAssociation
	 * @see AzResult
	 */
	public AzResourceActionAssociation createAndAddResourceActionAssociation(
			AzEntity<AzCategoryIdResource> azResource, 
			AzEntity<AzCategoryIdAction> azAction);

	/**
	 * Return the set of tuples corresponding to the resource and 
	 * action pairs that have been set in the resource context.
	 * @return the Set<AzResourceActionAssociation> currently active
	 * for this AzRequestContext
	 */
	public Set<AzResourceActionAssociation> getAssociations();	

	/**
	 * Add an AzResourceActionAssociation to the AzRequestContext for each
	 * possible AzResource-AzAction combination (cross-product (nxm)) of a 
	 * Set of "n" AzResource entities and a Set of "m" AzAction entities.
	 * <br>   
	 * Note that as in the single entity parameters version of this method,
	 * that all the associations are established based on object pairs. This
	 * means that if multiple AzResource objects have the same resource-id 
	 * attribute, that they will be treated as distinct resources for the
	 * purpose of making decisions.
	 * <br>
	 * Note also, that if another addResourceActionAssociation call is made
	 * with some of the same AzResource and AzAction objects, that any 
	 * identical AzResource-AzAction pairs will degenerate to a single
	 * association. In general, after all the addResourceActionAssociation
	 * calls have been made there is conceptually an NxM matrix of 
	 * N AzResources and M AzActions, where N is the sum of all the 
	 * distinct AzResources in the individual calls and similarly for
	 * the M distinct AzActions. 
	 * <p>
	 * For concreteness consider N AzResource rows and M AzAction columns,
	 * where a single AzResource is a single row and each cell in that row
	 * represents one of the M actions that potentially could be performed
	 * on that resource.
	 * <p> 
	 * Similarly the removeResourceActionAssociation calls will remove
	 * any resource-action object pairs that have been added that are
	 * identical to any of the resource-action object pairs in the
	 * current remove call.
	 * <br>
	 * At the end of all the add and remove calls there will remain N'
	 * AzResource objects and M' AzAction objects participating in a 
	 * maximum of N'xM' associations or a minimum of the larger of (N',M')
	 * associations, or any combination in between.
	 * <br> 
	 * <p>
	 * TBD: there has been some discussion that if the resource-id and
	 * action-id could be guaranteed to be present, singular,  and unique 
	 * in every AzResource and AzAction object, then resource-id<->action-id
	 * pairings would effectively be isomorphic (the same) as the pairings
	 * of the objects described above.
	 * <br> some of the considerations for not doing things this way include
	 * <br> that it is not required by XACML 2.0 or 3.0  
	 * <br>that it is not possible to anticipate in advance the how or why 
	 * that users will want to batch the requests, 
	 * <br> that there is the possibility of two requests having AzResource 
	 * entities for the same resource-id (or action-id), but with a different
	 * set of attributes,
	 * <br> and finally, it would seem a fairly straight-forward extension
	 * to this conceptualization to subclass the AzResource and AzAction
	 * entities and the AzRequestContext to apply filtering to achieve 
	 * this effect.
	 * <p>
	 * Note: use of a MissingRequestIdException/MissingActionIdException,
	 * might be the mechanism to signal this condition, if necessary.
	 * @param azResourceSet
	 * @param azActionSet
	 * @return the Set<AzResourceActionAssociation> objects that were
	 * just added to the collection
	 * @see #createAndAddResourceActionAssociation(AzEntity, AzEntity)
	 */
	public Set<AzResourceActionAssociation> addResourceActionAssociation(
			Set<AzEntity<AzCategoryIdResource>> azResourceSet, 
			Set<AzEntity<AzCategoryIdAction>> azActionSet);
	
	/**
	 * Add the cross-product of a single AzResource and a Set of AzActions 
	 * to the AzRequestContext.
	 * @param resource
	 * @param actions
	 * @return the Set<AzResourceActionAssociation> objects that were
	 * just added to the collection
	 */
	public Set<AzResourceActionAssociation> addResourceActionAssociation(
			AzEntity<AzCategoryIdResource> resource, 
			Set<AzEntity<AzCategoryIdAction>> actions);
	
	/**
	 * Add the cross-product of a Set of AzResources and a single AzAction 
	 * to the AzRequestContext.
	 * @param resources
	 * @param action
	 * @return a reference to the Set<AzResourceActionAssociation> just
	 * added to AzRequestContext
	 */
	public Set<AzResourceActionAssociation> addResourceActionAssociation(
			Set<AzEntity<AzCategoryIdResource>> resources, 
			AzEntity<AzCategoryIdAction> action);
	
	/**
	 * Delete the AzResource and AzAction association from the AzRequestContext 
	 * collection of AzResourceActionAssociations.
	 * <p>
	 * It is only the association that is deleted. If the AzResource entity 
	 * and/or the AzAction entity belong to other AzResourceActionAssociations,
	 * then the AzResource and/or AzAction object will be kept in its 
	 * respective collection. If an AzAction or AzResource object entity no 
	 * longer is part of any association, then no reference to the object
	 * will be kept by the AzRequestContext and it will be considered deleted
	 * from the AzRequestContext.  
	 * @param resource
	 * @param action
	 */
	public void removeResourceActionAssociation(
			AzEntity<AzCategoryIdResource > resource, 
			AzEntity<AzCategoryIdAction> action);
	
	/**
	 * delete the cross-product of the AzResource and AzAction sets from 
	 * the AzRequestContext, should the association exist. 
	 * @param resources
	 * @param actions
	 */
	public void removeResourceActionAssociations(
			Set<AzEntity<AzCategoryIdResource>> resources, 
			Set<AzEntity<AzCategoryIdAction>> actions);
	
	/**
	 * Remove all AzResourceActionAssociations from AzRequestContext.
	 * Helper method to safely reset collection of AzResourceActionAssociations.
	 */
	public void removeAllResourceActionAssociations();

	/**
	 * Get a local non-XACML implementation-specific id that
	 * can be used to distinguish this {@link AzRequestContext}
	 * object from other AzRequestContext objects. 
	 * This is primarily intended to be an id used for 
	 * azapi implementations to assist in
	 * identifying and referencing AzRequestContext objects
	 * where object references may not be readily available
	 * such as support for legacy PDP callback implementations.
	 * <p>
	 * @return a String containing an identifier associated with
	 * this AzRequestContext object.
	 */
	public String getId();


	/**
	 * Get the Set<AzEntity<AzCategoryIdAction>> held by this 
	 * AzRequestContext. 
	 * <p>
	 * If no Action entities have been added an empty Set will be returned.
	 * <p>
	 * Note: This Set may contain AzEntity<AzCategoryIdAction> objects
	 * that will not be included in decisions, unless they have been
	 * identified in the set of AzResourceActionAssociations that 
	 * control for which resource-action pairs decisions are being
	 * requested.
	 * 
	 * @return a Set<AzEntity<AzCategoryIdAction>> containing the
	 * actions that have been provided.
	 */
	//public Set<AzEntity<AzCategoryIdAction>> getActions();
	
	/**
	 * Get all the values corresponding to the distinguished 
	 * AttributeId resource-id found in each Resource
	 * <p>
	 * Only AzActions that have been identified by 
	 * AzResourceActionAssociations will be included in authorization
	 * requests.
	 * @return
	 */
	//public Set<String> getResourceIds();

	/**
	 * Get all the values corresponding to the distinguished 
	 * AttributeId action-id found in each Action
	 * @return
	 */
	//public Set<String> getActionIds();

}
