package org.openliberty.openaz.azapi.pep;

import org.openliberty.openaz.azapi.AzAttribute;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.constants.AzCategoryId;

/**
 * A basic wrapper for an <code>{@link AzEntity}&lt;T&gt;</code> 
 * object that can
 * be used for both request and response attribute collections.
 * <p>
 * The <code>{@link AzEntity}&lt;T&gt;</code> interface is the
 * primary interface used by the 
 * <code>{@link JavaObjectMapper#map(Object, RequestAttributes)}</code> 
 * method to map Native Java objects to the associated XACML
 * attributes in the {@link AzRequestContext} object.
 * Each <code>AzEntity&lt;T&gt;</code> object contains
 * XACML <code>{@link AzAttribute}<T></code>s within a 
 * certain <code>{@link AzCategoryId}</code> subtype.
 * <p>
 * The <code>JavaObjectMapper.map(obj,reqAttr)</code> method
 * can determine from
 * <code>{@link AzEntity#getAzCategoryId()}</code>
 * obtained using
 * <code>{@link #getAzEntity()}</code>
 * what the AzCategoryId of the collection is so that the 
 * appropriate attributes can be extracted and mapped from
 * the native Java object supplied to the <code>JavaObjectMapper</code>
 * <p>
 * In general, for the JavaObjectMappers, the {@link RequestAttributes} 
 * object is used to set the specific values of attributes.
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 *
 * @param <T>
 */
public interface Attributes<T extends Enum<T> & AzCategoryId>{

    /**
     * Return the underlying 
     * <code>{@link AzEntity}&lt;T&gt;</code> 
     * attribute collection that is contained by this 
     * object wrapper.
     */
	public AzEntity<T> getAzEntity();    	 
}
