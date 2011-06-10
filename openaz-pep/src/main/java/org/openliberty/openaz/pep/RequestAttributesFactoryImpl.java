package org.openliberty.openaz.pep;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.HashSet;

import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.RequestAttributes;
import org.openliberty.openaz.azapi.pep.RequestAttributesFactory;
import org.openliberty.openaz.azapi.pep.JavaObjectMapper;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;

/**
 * A factory to use to create an {@link RequestAttributes} that
 * contains one of the underlying AzEntity attribute collections
 * that are contained in the PepRequest.
 * 
 * @author rlevinson, jbregman, pmishra
 *
 * @param <T> an entity identifier: one of: 
 * {@link AzCategoryIdAction}, {@link AzCategoryIdEnvironment}, 
 * {@link AzCategoryIdResource}, {@link AzCategoryIdSubjectAccess}, 
 */
public abstract class RequestAttributesFactoryImpl
			<T extends Enum<T> & AzCategoryId> 
			implements RequestAttributesFactory<T> {

	/**
	 * Create an {@link RequestAttributes} that contains
	 * one of the underlying AzEntity attribute collections
	 * that are contained in the PepRequest with the
	 * {@link AzCategoryId} specified by the generic
	 * parameter <T>.
	 */
	public abstract RequestAttributes<T> 
    			createObject(PepRequest ctx);
    
	// By default these are the mappers set for an AzEntityWrapper factory
    private List<JavaObjectMapper> mappers = 
    	Arrays.asList(new JavaObjectMapper[] {
    			new SimpleJavaPermissionMapper(),
    			new SimpleJavaObjectMapper()});
    
    /**
     * Set the list of mappers that will be used by the AzCategory
     * for this RequestAttributesFactory instance.
	 * (i.e. whether the instance is for Subject, Action, Resource, 
	 * or Environment attribute collections (which are contained in
	 * AzEntity objects wrapped by RequestAttributes<AzCategoryId>
	 * objects)).
	 * <p>
	 * The list of mappers for each category defines the objects
	 * that may be submitted for that category and how the
	 * information in those objects get mapped to xacml
	 * AttributeId and AttributeValue.
     * @param mappers
     */
    public void setMappers(List<JavaObjectMapper> mappers) {
        this.mappers = mappers;
    }

    /**
     * Return the List of {@link JavaObjectMapper} objects
     * that have been set for the AzCategoryId of this factory.
     */
    public List<JavaObjectMapper> getMappers() {
        return mappers;
    }
    /**
     * Returns a Set of all the classes that are supported by
     * the Mapper that implements this interface.
     * @return a set of class objects
     */
    public Set<Class> getSupportedClasses(){
		//log.info("Subject Mapper supported classes: " +
    	//		pepReqFactory.getSubjectFactory().getMappers().
		//			iterator().next().getSupportedClasses());
    	Set<Class> supportedClasses = new HashSet<Class>();
    	Iterator<JavaObjectMapper> itMappers = 
    		this.getMappers().iterator();
    	while (itMappers.hasNext()) {
    		JavaObjectMapper jMapper = itMappers.next();
    		Set<Class> mapperSupportedClasses = 
    			jMapper.getSupportedClasses();
    		supportedClasses.addAll(mapperSupportedClasses);
    	}
    	return supportedClasses;

    }
}
