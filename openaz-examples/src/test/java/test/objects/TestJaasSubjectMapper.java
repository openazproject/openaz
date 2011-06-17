package test.objects;

//import java.security.Permission;
import java.security.Principal;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.pep.JavaObjectMapper;
import org.openliberty.openaz.azapi.pep.RequestAttributes;

import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdX500Name;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;

import javax.security.auth.Subject;
import javax.security.auth.x500.X500Principal;

/**
 *  TestJaasSubjectMapper is sample class to process
 *  JAAS Subject objects that implements {@link JavaObjectMapper}
 */
public class TestJaasSubjectMapper 
	implements JavaObjectMapper{
	Log log = LogFactory.getLog(this.getClass()); 
	public final static String OPENAZ_ATTR_SUBJECT_ROLE_ID =
		"urn:openaz:names:xacml:1.0:subject:role-id";
	
    public TestJaasSubjectMapper() {
        super();
    }
    
    /**
     * Returns the Set of Classes supported by this Mapper.
     * @return a set of classes
     */
    public Set<Class> getSupportedClasses(){
    	return supportedClasses;
    }
    
    // Define a Set containing the class objects that
    // are supported by this mapper:
	private static Set<Class> supportedClasses =
		new HashSet<Class>() {{ add(Subject.class); }};
		
	/**
	 * Test the Object parameter to determine if this
	 * mapper supports the submitted object
	 * 
	 * @param obj an object to be tested
	 * @return true if object is supported, otherwise false	
	 */
	public boolean canMapObject(Object obj){
    	if (log.isTraceEnabled()) log.trace(
    		"canMap(Object obj): " + obj.getClass().getName());
        if (obj instanceof Subject){
        	if (log.isTraceEnabled()) log.trace(
        		"obj instanceof Subject == true");
            return true;
        } else {
        	if (log.isDebugEnabled()) log.debug(
        		"Cannot map obj instanceof: " + obj.getClass().getName());
            return false;
        }
	}
	
	/**
	 * Map the submitted object to the AzEntity contained
	 * in the submitted {@link RequestAttributes} Object. Note: mappers
	 * are called internally in the azapi.pep package when
	 * PepRequestFactory newPepRequest methods are called.
	 */
	public <T extends Enum<T> & AzCategoryId> 
		RequestAttributes<T> map(
				Object javaObject, 
				RequestAttributes<T> azWrapperRequestObject)
			throws PepException {
		if (log.isTraceEnabled()) log.trace(
			"Object to map: " + 
				javaObject.getClass().getName());
		// check if object is Jaas Subject, and, if so,
		// map it using convertSubject and return
		if (javaObject instanceof Subject) {
			return convertSubject(
					(Subject)javaObject,
					azWrapperRequestObject);
		}	
		// if did not return above, throw exception:
		throw new PepException(
			"Can't map an object of class "+
			javaObject.getClass().getName());
	}
	
	/**
	 * Internal method to convert Jaas Subject to the underlying
	 * AzEntity<T> xacml attribute collection.
	 * @param <T>
	 * @param jaasSubject
	 * @param azWrapperRequestObject
	 * @return
	 * @throws PepException
	 */
    private <T extends Enum<T> & AzCategoryId> 
	RequestAttributes<T> convertSubject(
				Subject jaasSubject,
				RequestAttributes<T> azWrapperRequestObject)
			throws PepException {
    	String attributeId = null;
    	
    	// First: test the to make sure that the wrapper is for
    	// a Subject Category collection:
    	if ( azWrapperRequestObject.getAzEntity().getAzCategoryId().
    			equals(AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS)) {
    		// Get the Principals from the Subject in a HashSet
    	    Set<Principal> xPrins = new HashSet<Principal>();
    	    xPrins = jaasSubject.getPrincipals();
    	    // Iterate over the Principals
    	    Iterator<? extends Principal> itPrin = xPrins.iterator();
    	    while (itPrin.hasNext()) {
    	    	Principal principal = itPrin.next();
    	    	// Determine what kind of Principal each is and for
    	    	// the known kinds, map them to xacml attrs:
    	    	if (principal instanceof X500Principal ) {
    	    		// Map X500 principal to subject-id xacml attr
		    		attributeId = AzXacmlStrings.X_ATTR_SUBJECT_ID;
		    		azWrapperRequestObject.setAttribute(
		    				attributeId, 
		    				principal.getName());
		    		azWrapperRequestObject.getAzEntity().
		    			createAzAttribute(
		    				// get issuer from container name
		    				azWrapperRequestObject.getPepRequest().
		    					getPepRequestFactory().getContainerName(), 
		    				attributeId, 
		    				// create an X500Name attribute value
		    				azWrapperRequestObject.getAzEntity().
		    					createAzAttributeValue(
		    						AzDataTypeIdX500Name.AZ_DATATYPE_ID_X500NAME, 
		    						(X500Principal)principal));
		    		log.info("Created AzAttribute: " + 
		    				"\n\tAttributeId = " + attributeId +
		    				"\n\tAttributeValue = " + principal.getName());
    	    	}
    	    	else if (principal instanceof TestRolePrincipal) {
    	    		// Map Role type principal to xacml role attr
    	    		// note: these are not xacml-defined attr ids,
    	    		// and need to be custom defined for the role
    	    		// use case; see also xacml rbac profile for
    	    		// additional considerations
		    		attributeId = OPENAZ_ATTR_SUBJECT_ROLE_ID;
		    		azWrapperRequestObject.setAttribute(
		    				attributeId,
		    				principal.getName());
		    		log.info("Created AzAttribute: " + 
		    				"\n\tAttributeId = " + attributeId +
		    				"\n\tAttributeValue = " + principal.getName());
	    	    }
    	    	else {
    	    		log.warn("Unknown principal cannot be mapped: " +
    	    				principal.getClass().getName());
    	    	}
    	    }
    	}
    	else {
    		throw new PepException(
    			"Can't convert JAAS Subject to wrapped object " +
    			" that is not Subject Category; This wrapped object " +
    			" has AzCategoryId: " +
    			azWrapperRequestObject.getAzEntity().getAzCategoryId() +
    			"and is of type: " + 
    			azWrapperRequestObject.getAzEntity().getClass().getName());
    	}
    	return azWrapperRequestObject;
    }

}
