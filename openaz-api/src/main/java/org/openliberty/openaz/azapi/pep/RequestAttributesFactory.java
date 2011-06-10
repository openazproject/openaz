package org.openliberty.openaz.azapi.pep;

import java.util.List;
import java.util.Set;

import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.pep.RequestAttributes;
import org.openliberty.openaz.azapi.pep.PepRequest;

/**
 * This is primarily an internal interface used to create a
 * {@link RequestAttributes} object with a 
 * specific {@link AzCategoryId}
 * as indicated by the generic parameter <T>.
 * @param <T>
 */
public interface RequestAttributesFactory 
	<T extends Enum<T> & AzCategoryId> {
	
	/**
	 * Create a {@link RequestAttributes} Object 
	 * that contains
	 * one of the underlying <code>AzEntity</code> attribute collections
	 * that are contained in the PepRequest with the
	 * {@link AzCategoryId} specified by the generic
	 * parameter <T>.
	 */
	public RequestAttributes<T> 
		createObject(PepRequest ctx);
    
    /**
     * Set the mappers that will be used by each category:
	 * Subject, Action, Resource, Environment.
	 * The list of mappers for each category defines the objects
	 * that may be submitted for that category and how the
	 * information in those objects get mapped to xacml
	 * AttributeId and AttributeValue.
     * @param mappers
     */
    public void setMappers(List<JavaObjectMapper> mappers);
    
    /**
     * Return the List of JavaObjectMappers associated with
     * AzCategoryId of the {@link RequestAttributes} Objects 
     * of the
     * generic category <T> produced by this factory.
     * @return a {@link RequestAttributes} object
     */
    public List<JavaObjectMapper> getMappers();
    
    /**
     * Returns a Set of all the classes that are supported by
     * the Mapper that implements this interface.
     * @return a set of class objects
     */
    public Set<Class> getSupportedClasses();

}
