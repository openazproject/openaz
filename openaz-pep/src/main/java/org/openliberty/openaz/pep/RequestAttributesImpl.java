package org.openliberty.openaz.pep;

import java.util.Date;

import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.Attributes;
import org.openliberty.openaz.azapi.pep.RequestAttributes;
import org.openliberty.openaz.azapi.pep.RequestAttributesFactory;
import org.openliberty.openaz.azapi.pep.JavaObjectMapper;
import org.openliberty.openaz.azapi.pep.PepException;

import org.openliberty.openaz.azapi.AzAttribute;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdBoolean;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdDateTime;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdInteger;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdString;
/**
 * A wrapper around an authorization provider entity object (subject, action,
 * resource, or environment) that provides a common interface for
 * the setting of its properties which is used by the {@link JavaObjectMapper} 
 * implementations.  The interface makes a simplifying assumption
 * that there are reasonable defaults between Java Objects at the PepApi
 * application layer and the
 * XACML Attribute Types used by an AzApi provider. 
 *  
 * <table>
 * <tr>
 * <td>Java Class</td>
 * <td>XACML Type (AzAttributeValue)</td>
 * </tr>
 * <tr>
 * <td>java.lang.String</td>
 * <td>{@link AzAttributeValueString}</td>
 * </tr>
 * <tr>
 * <td>java.lang.Integer</td>
 * <td>{@link AzAttributeValueInteger}</td>
 * </tr>
 * <tr>
 * <td>java.lang.Boolean</td>
 * <td>{@link AzAttributeValueBoolean}</td>
 * </tr>
 * <tr>
 * <td>java.util.Date</td>
 * <td>{@link AzAttributeValueDate}</td>
 * </tr>
 * </table>
 * Note: for non-AzApi authorization providers it may be necessary to
 * extend this interface to enable the mappers to provide more
 * appropriate parameters to the setAttribute methods.
 * @param <T>
 */
public class RequestAttributesImpl<T extends Enum<T> & AzCategoryId> 
	extends AttributesImpl<T> 
	implements RequestAttributes<T> {
    
    private PepRequest wrappedCtx;
    private RequestAttributesFactory<T> factory;
	Log log = LogFactory.getLog(this.getClass()); 
    
    /**
     * Creates an implementation instance of the {@link RequestAttributes} 
     * interface.
     * @param wrappedObject
     * @param ctx
     * @param factory the factory used to create the object.  This is used 
     * by the <code>map</code> method to get all of the registered 
     * <code>JavaObjectMapper</code> for that category, and populate
     * the attributes of the underlying <code>AzEntity</code>
     */
    public RequestAttributesImpl(
    		AzEntity<T> wrappedObject, 
    		PepRequest ctx, 
 			RequestAttributesFactory<T> factory) {
        super(wrappedObject);
        this.wrappedCtx = ctx;
        this.factory = factory;
    }
    
    /**
     * Set the PepRequest that contains this 
     * {@link RequestAttributes},
     * which wraps an AzEntity.
     * @param wrappedCtx
     */
    public void setPepRequest(PepRequestImpl wrappedCtx) {
        this.wrappedCtx = wrappedCtx;
    }

    /**
     * Return the PepRequest object that contains this wrapped
     * AzEntity object.
     * @return a PepRequest object
     */
    public PepRequest getPepRequest() {
        return wrappedCtx;
    }
    
    /**
     * Adds the name, date parameter pair as an AttributeId and
     * Date AttributeValue to the wrapped AzEntity object.
     * @param name a string with a name to be used as AttributeId
     * @param date a Date to be used as an AttributeValue
     */
    public void setAttribute(String name, Date date) {
        if (log.isTraceEnabled()) log.trace(
        	"\n   Setting date attribute for: " +
        		"\n\t AttributeId: " + name +
        		"\n\t AzCategoryId: " + wrappedObject.getAzCategoryId() + 
        		"\n\t\t value: " + date.toString());
        AzAttribute<?> azEnvAttr =    // [a13]
            wrappedObject.createAzAttribute(
                wrappedCtx.getPepRequestFactory().getContainerName(), 
                name, 
                wrappedObject.createAzAttributeValue(
                    AzDataTypeIdDateTime.AZ_DATATYPE_ID_DATETIME, 
                    wrappedObject.createAzDataDateTime(date,0,0,0)));
        // Note: above is type safe - to protect against following
        // lines which use non-supported types.
        //AzDataTypeIdDateTime.AZ_DATATYPE_ID_DATETIME, new Date()));
        //AzDataTypeIdDateTime.AZ_DATATYPE_ID_DATETIME, new java.io.File("example")));
    }
    
    /**
     * Adds the name, value parameter pair as an AttributeId and
     * String AttributeValue to the wrapped AzEntity object.
     * @param name a string AttributeId of the attribute being set
     * @param value - a String value
     */
    public void setAttribute(String name, String value) {
        
        if (log.isTraceEnabled()) log.trace(
        	"\n   Setting string attribute for: " +
        		"\n\t AttributeId:  " + name +
        		"\n\t AzCategoryId: " + wrappedObject.getAzCategoryId() + 
        		"\n\t AzDataTypeId: " + AzDataTypeIdString.AZ_DATATYPE_ID_STRING + 
        		"\n\t\t value: " + value);
        AzAttribute<?> azEnvAttr =    
            wrappedObject.createAzAttribute(
                wrappedCtx.getPepRequestFactory().getContainerName(), 
                name, 
                wrappedObject.createAzAttributeValue(
                    AzDataTypeIdString.AZ_DATATYPE_ID_STRING, 
                    value));
    }
    
    /**
     * Adds the name, value parameter pair as an AttributeId and
     * Integer AttributeValue to the wrapped AzEntity object.
     * @param name a string AttributeId of the attribute being set
     * @param value an Integer value
     */
    public void setAttribute(String name, Integer value) {
        
        if (log.isTraceEnabled()) log.trace(
        	"\n   Setting integer attribute for: " +
    		"\n\t AttributeId:  " + name +
    		"\n\t AzCategoryId: " + wrappedObject.getAzCategoryId() + 
    		"\n\t AzDataTypeId: " + AzDataTypeIdInteger.AZ_DATATYPE_ID_INTEGER + 
    		"\n\t\t value: " + value);
        // Switch from Integer to Long, since that is 
        //  what the XACML/XMLSchema "integer" type actually is.
        Long valueLong = new Long(value);
        AzAttribute<?> azEnvAttr =    // [a13]
            wrappedObject.createAzAttribute(
                wrappedCtx.getPepRequestFactory().getContainerName(), 
                name, 
                wrappedObject.createAzAttributeValue(
                    AzDataTypeIdInteger.AZ_DATATYPE_ID_INTEGER, 
                    valueLong));
    }

    /**
     * Adds the name, value parameter pair as an AttributeId and
     * Boolean AttributeValue to the wrapped AzEntity object.
     * @param name a string AttributeId of the attribute being set
     * @param value a Boolean value
     */
    public void setAttribute(String name, Boolean value) {        
        if (log.isTraceEnabled()) log.trace(
        	"\n   Setting boolean attribute for: " +
        		"\n\t AttributeId:  " + name +
        		"\n\t AzCategoryId: " + wrappedObject.getAzCategoryId() + 
        		"\n\t AzDataTypeId: " + 
        			AzDataTypeIdBoolean.AZ_DATATYPE_ID_BOOLEAN + 
        		"\n\t\t value: " + value);
        AzAttribute<?> azEnvAttr =    // [a13]
            wrappedObject.createAzAttribute(
                wrappedCtx.getPepRequestFactory().getContainerName(), 
                name, 
                wrappedObject.createAzAttributeValue(
                    AzDataTypeIdBoolean.AZ_DATATYPE_ID_BOOLEAN, 
                    value));
	}
}
