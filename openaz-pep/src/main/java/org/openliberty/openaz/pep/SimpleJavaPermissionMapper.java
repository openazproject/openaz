package org.openliberty.openaz.pep;

//import java.util.Date;
//import java.util.HashMap;
//import java.util.Map;
import java.util.HashSet;
import java.util.Set;
import java.security.Permission;

import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.pep.JavaObjectMapper;
import org.openliberty.openaz.azapi.pep.RequestAttributes;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Default Java Permission implementation of <code>JavaObjectMapper</code>.
 * It supports the mapping of 
 * <ul>
 * <li>
 * a Java Permission object
 * </ul>
 * to 
 * <ul>
 * <li>
 * a {@link RequestAttributes} 
 * </ul>
 * containing 
 * <ul>
 * <li>
 * a XACML AttributeId and an AttributeValue using the Permission classname as a XACML az-resource-type, 
 * <li>
 * and using the Permission.getName() as XACML resource-id,
 * <li>
 * and the Permission.getActions() as the XACML action-id
 * </ul>
 * Basically, a typical Java Permission is converted to three XACML name,value
 * pairs of AttributeId and AttributeValue.
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public class SimpleJavaPermissionMapper implements JavaObjectMapper {
	public static String OPENAZ_ATTR_RESOURCE_TYPE =
		"urn:openaz:names:xacml:1.0:resource:resource-type";
	private static Set<Class> supportedClasses =
		new HashSet<Class>() {{ add(Permission.class); }};
	Log log = LogFactory.getLog(this.getClass()); 
    public SimpleJavaPermissionMapper() {
        super();
    }
    /**
     * Returns the Set of Classes supported by this Mapper.
     * @return a set of classes
     */
    public Set<Class> getSupportedClasses(){
    	return supportedClasses;
    }

    /**
     * Determines if the object requested to map is a Java 
     * Permission.
     * 
     * @param obj An Object
     * @return a boolean set to true if obj is a Java Permission, ow false
     */
    public boolean canMapObject(Object obj) {
    	if (log.isTraceEnabled()) log.trace(
    		"\n   canMap(Object obj = " + obj.getClass().getName() + ") ?");
        if (obj instanceof Permission){
        	if (log.isTraceEnabled()) log.trace(
        		"\n\tobj instanceof Permission == true");
            return true;
        } else {
        	if (log.isTraceEnabled()) log.trace(
        		"\n\tCannot map obj instanceof: " + 
        			obj.getClass().getName() + " .");
            return false;
        }
    }

    /**
     * Maps a Java Permission object to a set of XACML attributes.
     * The Java Permission maps as follows:
     * <pre>
     *   - The value of Permission.getName() maps to the value of
     *      the Resource Attribute with AttributeId:
     *        "urn:oasis:names:tc:xacml:1.0:resource:resource-id"
     *   - The value of Permission.getClass().getName() maps to 
     *      the value of a Resource Attribute with AttributeId:
     *       "urn:openaz:names:xacml:1.0:resource:resource-type"
     *   - The value of Permission.getActions() maps to the value of
     *      the Action Attribute with AttributeId:
     *       "urn:oasis:names:tc:xacml:1.0:action:implied-action"  
     * </pre>
     */
    public <T extends Enum<T> & AzCategoryId>
    	RequestAttributes<T> map(
    				Object javaObject,
            		RequestAttributes<T> azWrapperRequestObject) 
    			throws PepException {
		if (log.isTraceEnabled()) log.trace(
			"\n   Object to map: " + 
				javaObject.getClass().getName());
		if (javaObject instanceof Permission) {
			return convertPermission(
					(Permission)javaObject,
					azWrapperRequestObject);
		}		
		throw new PepException(
			"Can't map an object of class "+
			javaObject.getClass().getName());
}
    
    public <T extends Enum<T> & AzCategoryId> 
    	RequestAttributes<T > convertPermission(
    				Permission javaPermission,
    				RequestAttributes<T> azWrapperRequestObject)
    		throws PepException {
        T t = azWrapperRequestObject.getAzEntity().getAzCategoryId();
    	String attributeId = null;
    	if ( azWrapperRequestObject.getAzEntity().getAzCategoryId().
    			equals(AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE)) {
    		attributeId = AzXacmlStrings.X_ATTR_RESOURCE_ID;
    		azWrapperRequestObject.setAttribute(
    				attributeId, 
    				javaPermission.getName());
    		log.info(
    			"\n\tMapped Java Permission getName() to " +
    			"Xacml Attribute resource-id:" +
				"\n\t AzCategoryId:           " + t.getClass().getSimpleName() +
    			"\n\t AttributeId:            " + attributeId +
    			"\n\t AttrValue(resource-id): " + javaPermission.getName() );
    		
    		// map permission class name to resource type
    		azWrapperRequestObject.setAttribute(
    				OPENAZ_ATTR_RESOURCE_TYPE,
    				javaPermission.getClass().getName());
    		log.info(
    			"\n\tMapped Java Permission getClass().getName() to " +
    			"Xacml OpenAz Attribute resource-type:" +
   				"\n\t AttributeId:               " + OPENAZ_ATTR_RESOURCE_TYPE +
    			"\n\t AttrValue(resource-type):  " + 
    					javaPermission.getClass().getName() + "\n");
    	}
    	else if ( azWrapperRequestObject.getAzEntity().getAzCategoryId().
    			equals(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION)) {
    		attributeId = AzXacmlStrings.X_ATTR_ACTION_ID;
    		String actions = javaPermission.getActions();
    		azWrapperRequestObject.setAttribute(
    				attributeId,
    				actions);
    		log.info(
    			"\n\tMapped Java Permission getActions() to " +
    			"Xacml Attribute action-id:" +
				"\n\t AzCategoryId:         " + t.getClass().getSimpleName() +
    			"\n\t AttributeId:          " + attributeId +
    			"\n\t AttrValue(action-id): " + actions + "\n");
    	}
    	else {
    		throw new PepException(
    			"Can't convert Java Permission to wrapped object " +
    			" that is not Resource or Action; This wrapped object " +
    			" has AzCategoryId: " +
    			azWrapperRequestObject.getAzEntity().getAzCategoryId() +
    			"and is of type: " + 
    			azWrapperRequestObject.getAzEntity().getClass().getName());
    	}
    	return azWrapperRequestObject;
    }

}
