package org.openliberty.openaz.pdp.resources;

import java.util.List;
import java.util.Map;

public interface OpenAzResourceDirectory {
	/**
	 * Return a map of all the resources of all types 
	 * that are in the collection.
	 * 
	 * @return Map<resourceType, List<List<actionName>>>
	 */
	public Map<String,List<List<String>>> getResourcesCollection() ;
	
	/**
	 * Return a List of Resource-Action pairs that match the
	 * resourceType and Instance key.
	 * 
	 * @param resourceType a string
	 * @param resourceKey an integer
	 * @return
	 */
	// TODO: come up w finer grained scope call
	//public List<List<String>> getListByInstance(
	//		String resourceType, 
	//		int resourceKey);
	
	/**
	 * Return a list of all resource-action pairs of the 
	 * specified resource-type parameter.
	 * 
	 * @param resourceType a string
	 * @return a List of a List of Strings
	 */
	public List<List<String>> getListByType(
			String resourceType);
}
