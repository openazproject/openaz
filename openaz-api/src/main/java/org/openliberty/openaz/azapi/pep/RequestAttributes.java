package org.openliberty.openaz.azapi.pep;

import java.util.Date;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.constants.AzCategoryId;

/**
 * A wrapper around {@link AzEntity} attribute
 * collections that simplifies 
 * the setting of some common XACML attributes
 * using Java Objects. 
 * (The wrapped AzEntity object may be obtained using the
 * parent interface 
 * <code>{@link Attributes#getAzEntity()}</code> method). 
 * <p>
 * The simplifying assumption is 
 * that there are reasonable defaults between
 * Java Objects and XACML Attribute Types
 * <table>
 * <tr>
 * <td>Java Class</td>
 * <td>XACML Type (AzAttributeValue)</td>
 * </tr>
 * <tr>
 * <td>java.lang.String</td>
 * <td>AzAttributeValueString</td>
 * </tr>
 * <tr>
 * <td>java.lang.Boolean</td>
 * <td>AzAttributeValueBoolean</td>
 * </tr>
 * <tr>
 * <td>java.util.Date</td>
 * <td>AzAttributeValueDate</td>
 * </tr>
 * <tr>
 * <td>java.util.Integer</td>
 * <td>AzAttributeValueInteger</td>
 * </tr>
 * </table>
 * <p>
 * Some helper methods (setAttribute(String, *) see below
 * for details) are provided for setting values of
 * specific types of attributes. 
 * <p> 
 * Note: the <code>RequestAttributes</code> object is generally
 * only directly required to be used in the process 
 * of developing mappers when implementing the 
 * {@link JavaObjectMapper#map} method, where the setAttribute 
 * methods can be used to set attributes in the AzEntity object
 * that will be part of the overall authorization Request.
 * <p>
 * For examples of how mappers are written and how the methods
 * in RequestAttributes are used, one may examine the source
 * code of:
 * <ul>
 * <li>org.openliberty.openaz.pep.SimpleJavaObjectMapper</li>
 * <li>org.openliberty.openaz.pep.SimpleJavaPermissionMapper</li>
 * </ul>
 * @param <T> the <code>{@link AzCategoryId}</code> 
 * subtype of the contained 
 * <code>{@link AzEntity}</code> attribute collection
 * <p>
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public interface RequestAttributes<T extends Enum<T> & AzCategoryId> 
	extends Attributes<T>{
	
    /**
     * Adds the name, date parameter pair as an AttributeId and
     * Date AttributeValue to the wrapped AzEntity object.
     * @param name a string with a name to be used as AttributeId
     * @param date a Date to be used as an AttributeValue
     */
    public void setAttribute(String name, Date date);
    
    /**
     * Adds the name, value parameter pair as an AttributeId and
     * String AttributeValue to the wrapped AzEntity object.
     * @param name a string AttributeId of the attribute being set
     * @param value a String value
     */
    public void setAttribute(String name, String value);
 
    /**
     * Adds the name, value parameter pair as an AttributeId and
     * Integer AttributeValue to the wrapped AzEntity object.
     * @param name a string AttributeId of the attribute being set
     * @param value an Integer value
     */
    public void setAttribute(String name, Integer value);
    
    /**
     * Adds the name, value parameter pair as an AttributeId and
     * Boolean AttributeValue to the wrapped AzEntity object.
     * @param name a string AttributeId of the attribute being set
     * @param value a Boolean value
     */
    public void setAttribute(String name, Boolean value);
    
   /**
     * Return the PepRequest object that contains this wrapped
     * AzEntity object.
     * @return {@link PepRequest} object
     */
    public PepRequest getPepRequest();
}
