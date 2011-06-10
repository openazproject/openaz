package org.openliberty.openaz.azapi.pep;

import java.util.Set;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.constants.*;

/**
 * <code>JavaObjectMapper</code> is responsible for converting 
 * a POJO (typically a native Java Object from the application space
 * that contains one or more attributes to be used for
 * authorization decisions) into a 
 * <code>{@link RequestAttributes}&lt;T&gt;</code> object, 
 * which is a wrapper for an 
 * <code>{@link AzEntity}&lt;T&gt;</code>.
 * <p>
 * The conversion, for the most part, consists of taking members
 * from the native Java Object and using them to set 
 * attributes of the 
 * <code>{@link RequestAttributes}&lt;T&gt;</code>
 * object that is passed to the <code>{@link #map}</code> method.  
 * Basically mapping
 * the information in the native Java Object to a set of 
 * simple name-value pairs.  
 * <p>
 * <code>JavaObjectMapper</code>s
 * are configured for each of the 
 * <code>{@link RequestAttributesFactory}&lt;T&gt;</code> 
 * instances associated with the {@link PepRequestFactory}.
 * This allows for each <code>AzCategoryId</code> 
 * ({@link AzCategoryIdSubjectAccess},
 * {@link AzCategoryIdAction},
 * {@link AzCategoryIdResource} and 
 * {@link AzCategoryIdEnvironment})
 * to have its own mapper.  
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 * @see RequestAttributes
 */
public interface JavaObjectMapper {
    
    /**
     * Returns true if the Mapper can map the Object that is 
     * passed in as the parameter to this method, false otherwise.
     * 
     * @param obj Java Object (ex: Permission) which contains
     * one or more attributes, generally from the application
     * space that needs to be mapped to a 
     * {@link RequestAttributes} object
     * 
     * @return true if this <code>JavaObjectMapper</code> can
     * map the obj, false otherwise
     */
    public boolean canMapObject(Object obj);
    
    /**
     * Performs the mapping of an <code>Object</code> to a 
     * <code>{@link RequestAttributes}&lt;T&gt;</code> object.
     * <p>  
     * Note: the map method is called with a specific subtype of 
     * <code>{@link RequestAttributes}&lt;T&gt;</code>.
     * Typically, the map method will test the generic parameter
     * using 
     * <code>{@link RequestAttributes#getAzEntity()}.getAzCategoryId()</code>
     * <p>
     * Note: the reason for returning a reference to the
     * {@link RequestAttributes}
     * is simply to allow for a mapper to reuse some existing 
     * state, such as a cached <code>AzEntity</code>, if such
     * a strategy is desired. 
     * In such a case, 
     * the <code>JavaObjectMapper</code> would need to use
     * the passed in {@link RequestAttributes} to obtain the
     * parent <code>PepRequest</code> and then get the 
     * {@link AzRequestContext} and replace 
     * the <code>AzEntity</code> in
     * the <code>AzRequestContext</code> 
     * with the cached <code>AzEntity</code>.  
     * 
     * @param javaObject
     * @param azWrapperObject
     * @return a {@link RequestAttributes} object
     * @throws PepException
     */
    public <T extends Enum<T> & AzCategoryId> 
    	RequestAttributes<T> map(
    			Object javaObject, 
    			RequestAttributes<T> azWrapperObject) 
    		throws PepException;
    
    /**
     * Returns a Set of all the classes that are supported by
     * the Mapper that implements this interface.
     * @return a set of class objects
     */
    public Set<Class> getSupportedClasses();
}
