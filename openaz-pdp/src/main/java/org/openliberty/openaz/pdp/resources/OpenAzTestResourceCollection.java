package org.openliberty.openaz.pdp.resources;

import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;
import java.util.List;
import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.pdp.provider.SimpleConcreteSunXacmlService;

/**
 * This is a very simple-minded test tool to emulate a
 * collection of resources that are represented as a list
 * of resource-id, action pairs.
 * <p>
 * Another way to think of it is as a 3 column table where
 * <UL>
 * <LI>resource-type-name</LI>
 * <LI>resource-id = prefix + integer (0-&gt;n in sequence></LI>
 * <LI>action-id</LI>
 * </UL>
 * <p>
 * The resource-id is structure such that it has a prefix,
 * which is the name of the resource type, followed by an
 * integer instance-id running from 0->n where n is the 
 * number of instances created in the constructor.
 * <p>
 * There is one resource-action pair for each action
 * associated with the resource-type within each instance
 * of that resource-type.
 * <p>
 * enums can be used to name the instances within a resource-type
 * such that the enum name maps to the corresponding instance
 * integer by simply prefixing the resource type to the integer.
 * <p>
 * If more complex selection schemes are needed, then any structure
 * that maps to a sequence of integers can be used instead of
 * the enum scheme, where one would construct the structure, then
 * query the structure for the list of results, then one at a time
 * submit the results and extend the list for each integer result
 * from the structure.
 * <p>
 * The only intent of this class is to enable some limited testing
 * of query for some scopes. 
 *
 * @author rlevinson
 *
 */
public class OpenAzTestResourceCollection 
	implements OpenAzResourceDirectory {
	/*
	 * The Map has a resource type as the String key,
	 * which points to a List of a List of Strings, where
	 * the inner List<String> consists of a 2 member list,
	 * where the 1st member is the resource-id, and the
	 * 2nd member is the action-id.
	 * <p>
	 * The middle List is a List of these pairs that are
	 * members of the same resource type.
	 * The outer Map<String, ...> is the resource type
	 * name that is the parent of the List of List of 
	 * resource-id, action-id String pairs.
	 */
	Map<String,List<List<String>>> resourcesCollection = 
		new HashMap<String,List<List<String>>>();
	
	List<String> resources = new ArrayList<String>();
	List<String> resourceActionPair = null;
	
	Map<String,List<String>> resourceTypeActionList = null;
	
	public static final String RESOURCE_ID_PREFIX =
		"resource-id-";
	
	/*
	 * Array of resource type names that can be in collection
	 */
	String[] resourceTypeName = new String[]{
				"Menu", 
				"EngineeringServer"
	};
	/*
	 * Array of arrays of action-names corresponding by major
	 * index to the resource types in the above array
	 */
	String[][] resourceTypeActions = new String[][]{
			{"Read","Update","Delete"},
			{"shutdown", "install", "admin", "user"}
	};
	/*
	 * Array of number of instance to create of each type
	 * corresponding to the above arrays of resource-types
	 * and resource-action-names associated with the types.
	 * <p>
	 * For example: "3" "Menu"s, each containing "Read", "Update",
	 * and "Delete" actions will be created resulting in 9
	 * resource-id, action pairs that will be in the resource
	 * list created by the constructor.
	 */
	int[] resourceTypeNum = new int[]{
			3,
			5
	};
	
	/**
	 * This enumerator basically serves to produce a name
	 * mapping between the names in the enumerator and the
	 * digits, 0,1,... that the enumerator automatically 
	 * assigns. The numbers can then be mapped to the auto
	 * number assigned to the resource-id by the constructor
	 * of the instances.
	 * <p>
	 * It is a quick and dirty first approximation of a
	 * naming scheme for subsets of resources of a given
	 * type. Knowing that a resource of a type will start with
	 * a root prefix and be assigned an integer from 0->n, the
	 * resource-name = prefix + integer.
	 */
	public enum menuTypes {redMenu, blueMenu, greenMenu};
	
	Log log = LogFactory.getLog(this.getClass()); 
	static Log logStatic = LogFactory.getLog(SimpleConcreteSunXacmlService.class);

	/*
	 * Constructor that creates the collection of resources 
	 */
	public OpenAzTestResourceCollection(){
		// build a Map of Resource types and associated action lists
		Map<String,List<String>> resourceTypeActionList =
			new HashMap<String,List<String>>();
		for (int i=0; i<resourceTypeName.length;i++) {
			resourceTypeActionList.put(
				resourceTypeName[i],
				Arrays.asList(resourceTypeActions[i]));
		}
		this.resourceTypeActionList = resourceTypeActionList;
		
		// Now build a set of resource-action pair instances
		// corresponding to 
		//  for each resource-type
		//	 one instance for each r-a pair within a resource-type
		//    times  
		//   the number of requested instances of that resource-type
		int typeId = 0;
		int numResources = 3;
		int numActionsPerResource = 4;
		List<List<String>> resourceList = null;
		for (String resourceType : resourceTypeActionList.keySet()) {
			resourceList = new ArrayList<List<String>>();
			for (int i=0; i<resourceTypeNum[typeId]; i++) {
				String resourceId = 
					RESOURCE_ID_PREFIX + resourceType +
					"-" + new Integer(i).toString();
				resources.add(resourceId);
				for (String actionName : 
					 	resourceTypeActionList.get(resourceType)){
					String actionId = 
						"action-id-" + actionName;
					resourceList.add(
						resourceActionPair =
							Arrays.asList(resourceId, actionId));
				}
				if (log.isTraceEnabled()) log.trace(
					"constructor: \n\tresourceSet = " + 
						resourceList);
			}
			typeId++;
			resourcesCollection.put(resourceType, resourceList);
			if (log.isTraceEnabled()) log.trace(
				"constructor: \n\tresourceType = " + resourceType +
					"\n\ttypeId = " + new Integer(typeId));
		}
		if (log.isTraceEnabled()) log.trace(
			"constructor: \n\tresourcesCollection = " + 
				resourcesCollection + "\n");
	}
	/**
	 * Return the whole collection.
	 * @return a Map of named Lists of Lists of Strings representing
	 * the complete resource collection of all types
	 */
	public Map<String,List<List<String>>> getResourcesCollection() {
		return resourcesCollection;
	}
	// TODO: this needs to be thought thru further:
	// public 
	List<List<String>> getListByInstance(
			String resourceType, 
			int resourceKey){
		String resourceIdKey = 
			RESOURCE_ID_PREFIX + resourceType +
			"-" + new Integer(resourceKey).toString();

		List<List<String>> resourceList = null;
		List<List<String>> result = new ArrayList<List<String>>();
		for (String resourceTypeName : resourcesCollection.keySet()) {
			if (resourceType.equals(resourceTypeName)){
				resourceList = resourcesCollection.get(resourceType);
				for (List<String> resourceActionPair : resourceList){
					String resourceId = resourceActionPair.get(0);
					if (resourceId.equals(resourceIdKey)){
						result.add(resourceActionPair);
					}
				}
			}
		}
		return result;
	}
	/**
	 * Return a List of 2 member Lists of ResourceId,ActionId strings
	 */
	public List<List<String>> getListByType(
			String resourceType){

		List<List<String>> resourceList = null;
		List<List<String>> result = null;
		for (String resourceTypeName : resourcesCollection.keySet()) {
			if (resourceType.equals(resourceTypeName)){
				resourceList = 
					resourcesCollection.get(resourceType);
				return resourceList;
			}
		}
		return result;
	}
	
	public static void main(String[] args){
		
		logStatic.info(menuTypes.blueMenu.toString());
		OpenAzTestResourceCollection trc = 
			new OpenAzTestResourceCollection();
		logStatic.info("complete resource collection: " + 
				trc.getResourcesCollection());
		
		/*
		// TODO: need to come up w a more general finer
		// grain query scheme:
		List<List<String>> enumerator = null;
		enumerator = trc.getListByInstance(
				"Menu", menuTypes.redMenu.ordinal());
		System.out.println(
				"redMenu - enumerator: " + enumerator);
		
		enumerator = trc.getListByInstance(
				"Menu", menuTypes.greenMenu.ordinal());
		System.out.println(
				"greenMenu - enumerator: " + enumerator);
		*/
		
		List<List<String>> allResourcesOfType = null;
		allResourcesOfType = trc.getListByType(
				"EngineeringServer");
		logStatic.info(
				"EngineeringServer resource action pairs: " +
				allResourcesOfType);
		
	}
}
