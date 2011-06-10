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
import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;

import java.util.Set;
import java.util.HashSet;
import java.util.Iterator;

import org.openliberty.openaz.pdp.provider.AzEntityFactory;

//line below is expected max printable width
/**********************************************************************************
 * OpenAz reference impl for AzRequestContext.
 */
public class AzRequestContextImpl implements AzRequestContext {
	Log log = LogFactory.getLog(this.getClass()); 
	
	// Note: according to Practicalities.FAQ302C in
	// Angelica Langer's FAQ, 
	// http://www.angelikalanger.com/GenericsFAQ/FAQSections/ProgrammingIdioms.html#FAQ302C
	// "Wildcards can have at most one upper bound, while type
	// parameters can have several upper bounds"
	// So, below we cannot say <? extends Enum<?> & AzCategoryId>
	// although we could have said just <? extends Enum<?>> instead
	// of <? extends AzCategoryId>
	// However, the main point is that it is a wildcard, and
	// the impl controls the details of what is assigned.
	AzEntity<? extends AzCategoryId> environment;
	Set<AzEntity<? extends AzCategoryId>> subjects;
	Set<AzEntity<? extends AzCategoryId>> resources;
	Set<AzEntity<? extends AzCategoryId>> actions;
	Set<AzResourceActionAssociation> resourceActionAssociations;
	String azRequestContextId = null;
	String y[] = new String[]{"A","B","C","D","E"};

	static int idCounter = 0;
	static int idEntityCounter = 0;
	static int idAzEntityCounter = 0;
	static int idAzRequestContextCounter = 0;
	
	public AzRequestContextImpl(){
		subjects = new HashSet<AzEntity<? extends AzCategoryId>>();
		resources = new HashSet<AzEntity<? extends AzCategoryId>>();
		actions = new HashSet<AzEntity<? extends AzCategoryId>>();
		resourceActionAssociations = 
			new HashSet<AzResourceActionAssociation>();
		azRequestContextId = "AzRequestContext-" +
			new Integer(idAzRequestContextCounter++).toString();
		if (log.isTraceEnabled()) log.trace(
			"\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + 
			"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" +
			"\n    Constructor creating new AzRequestContext:" +
			"\n\tAzRequestContext.getId = " + azRequestContextId +
			"\n$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$" + 
			"$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$$\n"
			);
	}
	
	public String getId() {
		return azRequestContextId;
	}
	
	/**
	 * Create and return a new AzEntity<T> object of the
	 * specified AzCategoryId subtype T.
	 */
	public  <T extends Enum<T> & AzCategoryId> 
			AzEntity<T> createAzEntity(T t){
		if (log.isTraceEnabled()) log.trace(
				"\n\tusing generic createAzEntity(AzCategoryId = " + 
				t.getClass().getSimpleName() + ")");
		
		AzEntity<T> azEntityT = createNewAzEntity(t);
		
		if ( t.equals(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION) ) {
			if (log.isTraceEnabled()) log.trace(
					"\n    .createAzEntity: t = " + t.getClass().getSimpleName() + 
					";\n\tadding AzEntity, getId() = " + azEntityT.getId() + 
					",\n\t\tto AzEntity<?> actions");
			actions.add(azEntityT);
			
		} else if (t.equals(AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT)) {
			if (log.isTraceEnabled()) log.trace(
					"\n    .createAzEntity: t = " + t.getClass().getSimpleName() + 
					";\n\tsetting AzEntity, getId() = " + azEntityT.getId() + 
					",\n\t\tto AzEntity<?> environment");
			environment = azEntityT;
			
		} else if ( t.equals(AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE) ) {
			if (log.isTraceEnabled()) log.trace(
					"\n    .createAzEntity: t = " + t.getClass().getSimpleName() + 
					";\n\tadding AzEntity, getId() = " + azEntityT.getId() + 
					",\n\t\tto AzEntity<?> resources");
			resources.add(azEntityT);
			
		} else if ( t.equals(
				AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS) ) {
			if (log.isTraceEnabled()) log.trace(
					"\n    .createAzEntity: t = " + t.getClass().getSimpleName() + 
					";\n\tadding AzEntity, getId() = " + azEntityT.getId() + 
					",\n\t\tto AzEntity<?> subjects");
			subjects.add(azEntityT);
			//TODO: need to add all subject categories
			
		} else {
			if (log.isTraceEnabled()) log.trace(
					"\n    .createAzEntity: t = " + t.getClass().getSimpleName() + 
					"parameter, t, requested unsupported AzEntity<T> type " +
					"for including in AzRequestContext object: " + t);
			azEntityT = null;
		}			
		return azEntityT;
	}
		
	public <T extends Enum<T> & AzCategoryId> 
			AzEntity<T>  createNewAzEntity(T t){
		if (log.isTraceEnabled()) log.trace(
				"\n\tcreateNewAzEntity(" + t.getClass().getSimpleName() + ")");
		AzEntity<T> azEntity = 
			new AzEntityImpl<T>(t, idAzEntityCounter++);
		return azEntity;
	}
	
	
	public <T extends Enum<T> & AzCategoryId> 
		boolean addAzEntity(AzEntity<T> azEntity) {
		// TODO: do something here - implement addAzEntity
		if (log.isTraceEnabled()) log.trace(
			"\n\taddAzEntity function not yet implemented: " +
			"\n\t\taddAzEntity was attempted on: " + azEntity.getId() + "\n");
		return true;
	}
		
	/**
	 * Note: The returned Set<AzEntity<? extends AzCategoryId>> is the
	 * direct impl of the stored variables: actions, resources, etc.,
	 * and probably the variable to which it returns needs to be 
	 * declared as such when this method is used.
	 */
	public <T extends Enum<T> & AzCategoryId> 
			Set<AzEntity<? extends AzCategoryId>> getAzEntitySet(T t){
		Set<AzEntity<? extends AzCategoryId>> azEntitySetT = null;
		//	Set<AzEntity<T>> getAzEntitySet(T t){
		//Set<AzEntity<T>> azEntitySetT = null;
		if (t.equals(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION)){
			//azEntitySetT = (Set<AzEntity<T>>) actions;
			azEntitySetT = actions;
		}
		else if (t.equals(AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE))
			azEntitySetT = resources;
		return azEntitySetT;
	}

	public <T extends Enum<T> & AzCategoryId>
			AzEntity<T> getAzEntity(T t){
		AzEntity<T> azEntityT = null;
		if (log.isTraceEnabled()) log.trace(
			"\n    AzRequestContext.getAzEntity(t): " +
			"\n\t looking for t = " + t +
			"\n\t t.ordinal() = " + t.ordinal());
		// TBD: need to determine how to pick one AzEntity from a set
		Iterator<AzEntity<? extends AzCategoryId>> it = null;
		if ( t.equals(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION) ) {
			if (log.isTraceEnabled()) log.trace(
					"\n    getAzEntity: t = action ");
			//Iterator<AzEntity<AzCategoryIdAction>> iAct = 
			it = actions.iterator();
			while (it.hasNext()){
				// need selection criteria here
				azEntityT = (AzEntity<T>) it.next();
				if (log.isTraceEnabled()) log.trace(
					"\n    getAzEntity: found Action: " + 
						"\n\t id: " + azEntityT.getId());
			}
		} else if ( t.equals(AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT) ) {
			if (log.isTraceEnabled()) log.trace(
					"\n    getAzEntity: t = environment ");
			azEntityT = (AzEntity<T>)environment;
		} else if ( t.equals(AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE) ) {
			if (log.isTraceEnabled()) log.trace("    getAzEntity: t = resource ");
			//Iterator<AzEntity<AzCategoryIdResource>> iRes = 
			it = resources.iterator();
			while (it.hasNext()){
				// need selection criteria here
				azEntityT = (AzEntity<T>) it.next();
				if (log.isTraceEnabled()) log.trace(
						"\n    getAzEntity: found Resource: " + 
						"\n\t id: " + azEntityT.getId());
			}
		} else if ( t.equals(
				AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS) ) {
			if (log.isTraceEnabled()) log.trace(
					"\n    getAzEntity: t == subject-access");
			//Iterator<AzEntity<AzCategoryIdSubjectAccess>> iSubj = 
			it = subjects.iterator();
			while (it.hasNext()){
				// need selection criteria here
				azEntityT = (AzEntity<T>) it.next();
				if (log.isTraceEnabled()) log.trace(
						"\n    getAzEntity: found Subject: " + 
						"\n\twith AzEntity.getId() = " + azEntityT.getId());
			}
		} else {
			if (log.isTraceEnabled()) log.trace(
				"\n    getAzEntity: t == something else = " + t);
		}
		switch(t.ordinal()) {
		case 0:
			// environment?
		}
		return azEntityT;		
	}

	public <T extends Enum<T> & AzCategoryId> 
			boolean removeSubject(T t){
	//public boolean removeSubject(AzCategoryIdSubjectAccess t){
		boolean result = false;
		//Iterator<AzEntity<T>> it = subjects.iterator();
		//AzEntity<T> azSubject = null;
		Iterator<AzEntity<? extends AzCategoryId>> it = subjects.iterator();
		AzEntity<? extends AzCategoryId> azSubject = null;
		while (it.hasNext()) {
			azSubject = it.next();
			if (azSubject.getAzCategoryId().equals(t)) {
				subjects.remove(azSubject);
				if (log.isTraceEnabled()) log.trace(
						"\n    AzRequestContext.removeSubject: "  +
						"removed subject w AzCategoryId = " + t);
				return true;
			}
			
		}
		return result;
	}
	
	
	/**
	 * Add the Resource and Action pair to the RequestCtx. . The Action and Resource entities MUST have an attribute with AttributeId set to the distinguished constant RequestId and ActionId. Else throw an MissingRequestIdException/MissingActionIdException.
	 * @param azResource
	 * @param azAction
	 */
	public AzResourceActionAssociation createAndAddResourceActionAssociation(
			AzEntity<AzCategoryIdResource> azResource, 
			AzEntity<AzCategoryIdAction> azAction){
		resources.add(azResource);
		actions.add(azAction);
		AzResourceActionAssociationImpl testARAA =
			new AzResourceActionAssociationImpl(
				azResource, azAction, idCounter++);
		resourceActionAssociations.add(testARAA);
		return testARAA;
	}

	/**
	 * Add the cross-product of the Resource and Action sets to the RequestCtx. The Action and Resource entities MUST have an attribute with AttributeId set to the distinguished constant RequestId and ActionId respectively. Else throw an MissingRequestIdException/MissingActionIdException.
	 * @param azResourceSet
	 * @param azActionSet
	 */
	public Set<AzResourceActionAssociation> addResourceActionAssociation(
			Set<AzEntity<AzCategoryIdResource>> azResourceSet, 
			Set<AzEntity<AzCategoryIdAction>> azActionSet){
		Set<AzResourceActionAssociation> azRASet = null;
		return azRASet;
	}
	/**
	 * Add the cross-product of a single AzResource and a Set of AzActions 
	 * to the AzRequestContext.
	 * @param azResource
	 * @param azActionSet
	 */
	public Set<AzResourceActionAssociation> addResourceActionAssociation(
			AzEntity<AzCategoryIdResource> azResource, 
			Set<AzEntity<AzCategoryIdAction>> azActionSet){
		Set<AzResourceActionAssociation> azRASet = 
			new HashSet<AzResourceActionAssociation>();
		AzAttribute<AzCategoryIdResource> azAttrResourceId = 
			azResource.getAttributeByAttribId(
					AzXacmlStrings.X_ATTR_RESOURCE_ID);
		if (! (azAttrResourceId == null)){
			String resourceId = azAttrResourceId.
					getAzAttributeValue().getValue().toString();
			if (log.isTraceEnabled()) log.trace(
				"\n\tResource being added w resource-id = " + resourceId);
			if (log.isTraceEnabled()) log.trace(
				"\n   .addResourceActionAssociation(azResource, azActionSet): " +
				" (multi-action)" +
				"\n\t adding AzResource to AzRequestContext w resourceId = " +
				resourceId + "\n");
				//azResource.getAttributeByAttribId(
				//  AzXacmlStrings.X_ATTR_RESOURCE_ID).
				//    getAzAttributeValue().getValue());
		}
		else {
			if (log.isTraceEnabled()) log.trace(
				"Resource being added but has no attr w attrId = resource-id");
		}
		//resources.add(azResource);
		Iterator<AzEntity<AzCategoryIdAction>> it = azActionSet.iterator();
		while (it.hasNext()){
			AzEntity<AzCategoryIdAction> azAction = it.next();
			//actions.add(azAction);
			// azRASet collection may be useful to distinguish 
			// between resources added from this call vs other calls
			azRASet.add(
					this.createAndAddResourceActionAssociation(
							azResource, azAction));
			AzAttribute<AzCategoryIdAction> azAttrActionId = 
				azAction.getAttributeByAttribId(
					AzXacmlStrings.X_ATTR_ACTION_ID);
			if ( ! (azAttrActionId == null)) {
				String actionId = azAttrActionId.
					getAzAttributeValue().getValue().toString();
				if (log.isTraceEnabled()) log.trace(
						"Action being added w action-id = " + actionId);
				if (log.isTraceEnabled()) log.trace(
					"  AzRequestContext.addResourceActionAssociation(" + 
					"azResource, azActionSet): " +
					"\n\t adding AzAction to RequestContext: " +
					actionId);
				//azAction.getAttributeByAttribId(
				//  AzXacmlStrings.X_ATTR_ACTION_ID).
				//    getAzAttributeValue().getValue());
			}
			else
				if (log.isTraceEnabled()) log.trace(
					"Action being added but has no attr w attrId = action-id");
		}
		return azRASet;
	}
	/**
	 * Add the cross-product of a Set of AzResources and a single AzAction 
	 * to the AzRequestContext.
	 * @param azResourceSet
	 * @param azAction
	 */
	public Set<AzResourceActionAssociation> addResourceActionAssociation(
			Set<AzEntity<AzCategoryIdResource>> azResourceSet, 
			AzEntity<AzCategoryIdAction> azAction){
		Set<AzResourceActionAssociation> azRASet = null;
		log.info("not implemented yet");
		return azRASet;
	}
	
	/**
	 * delete the Resource and Action association to the RequestCtx, should the association exist. 
	 * @param resource
	 * @param action
	 */
	public void removeResourceActionAssociation(
			AzEntity<AzCategoryIdResource> resource, 
			AzEntity<AzCategoryIdAction> action){
		
		log.info(".removeResourceActionAssociation(): not implemented yet");
	}

	/**
	 * delete the cross-product of the Resource and Action sets to the RequestCtx, should the association exist. 
	 * @param resources
	 * @param actions
	 */
	public void removeResourceActionAssociations(
			Set<AzEntity<AzCategoryIdResource>> resources, 
			Set<AzEntity<AzCategoryIdAction>> actions){
		log.info(".removeResourceActionAssociations(): not implemented yet");
		
	}
	
	public void removeAllResourceActionAssociations(){
		log.info(".removeAllResourceActionAssociations(): not implemented yet");
	}
	
	/**
	 * Return the value corresponding to the distinguished 
	 * constant SubjectId - from the Subject with CategoryId or null.
	 * @param categoryId
	 * @return a String containing the subjectId
	 */
	public String getSubjectId(String categoryId){
		String subjectId=null;
		//tbd
		log.info("\n\t.getSubjectId(): not implemented yet");
		return subjectId;
	}
	
	/**
	 * Get all the values corresponding to the distinguished AttributeId resource-id found in each Resource
	 * @return a Set of Strings, each containing a ResourceId
	 */
	public Set<String> getResourceIds(){
		Set<String> resourceIds=null;
		//tbd
		log.info("\n\t.getResourceIds(): not implemented yet");
		return resourceIds;
	}

	/**
	 * Get all the values corresponding to the distinguished AttributeId action-id found in each Action
	 * @return a Set of Strings containing actionIds
	 */
	public Set<String> getActionIds(){
		Set<String> actionIds = null;
		//tbd
		log.info("\n\t.getActionIds(): not implemented yet");
		return actionIds;
	}

	/**
	 * Return the set of tuples corresponding to the resource 
	 * and action pairs found in the resource context.
	 * @return a Set of AzResourceActionAssorications
	 */
	public Set<AzResourceActionAssociation> getAssociations(){
		return resourceActionAssociations;
	}
	


	/**
	 * Return the appropriate Subject component based on the CategoryId (access-subject, recipient-subject, intermediary-subject, codebase, requesting-machine) or null
	 * @param categoryId
	 * @return
	 */
	/*
	public AzEntity<AzCategoryIdSubjectAccess> getSubject(
			AzCategoryIdSubjectAccess categoryId){
		AzEntity<AzCategoryIdSubjectAccess> azSubject = null;
		AzCategoryIdSubjectAccess azSubjCatId = null;
		Iterator<AzEntity<AzCategoryIdSubjectAccess>> it = subjects.iterator();
		while (it.hasNext()){
			azSubject = it.next();
			azSubjCatId = azSubject.getAzCategoryId();
			System.out.println(
				"TestRequestContext.getSubject: found AzSubject w cat-id: " + azSubjCatId);
			if (categoryId.equals(azSubject.getAzCategoryId())){
				System.out.println(
					"TestRequestContext.getSubject: found AzSubject matches requested cat-id: " + categoryId);
				return azSubject;
			}
		}
		System.out.println(
			"TestRequestContext.getSubject: NO AzSubject found w cat-id: " + categoryId);
		return azSubject;
	}
	*/

	/**
	 * Get the resources held by this ResourceCtx
	 * @return
	 */
	//public Set<AzEntity<AzCategoryIdResource>> getResources(){
	//	return resources;
	//}

	/**
	 * Get the actions held by this ResourceCtx
	 * @return
	 */
	//public Set<AzEntity<AzCategoryIdAction>> getActions(){
	//	return actions;
	//}

	/** 
	 * Add or replace the Environment component of the RequestCtx
	 * @param environment
	 */
	//public void addAzEnvironment(AzEnvironment environment){
	//	this.environment = environment;
	//}
	
	/**
	 * Add the Subject entity to the RequestCtx; if there is an existing Subject entity with the same categoryId, then replace it. The Subject entity MUST have an attribute with AttributeId set to the distinguished constant SubjectId, else throw an MissingSubjectIdException.
	 * @param subject
	 */
	//public void addSubject(AzSubject subject){
	//	subjects.add(subject);
	//}
}
