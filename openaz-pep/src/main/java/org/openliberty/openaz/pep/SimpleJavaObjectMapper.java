package org.openliberty.openaz.pep;

import java.util.Date;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;
import java.util.HashSet;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.pep.JavaObjectMapper;
import org.openliberty.openaz.azapi.pep.RequestAttributes;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;
/**
 * Default implementation of <code>JavaObjectMapper</code>.  
 * It supports the mapping of 
 * <ul>
 * <li>
 * <code>java.util.Date</code>,
 * <li>
 * <code>java.lang.String</code>, and 
 * <li>
 * <code>java.util.Map&lt;String,Object&gt;</code> where Object
 * can be one of Date, String, Integer, or Boolean, or if
 * declared as Object, can be a mix of those 4 classes.
 * </ul>
 * to {@link RequestAttributes}
 * <p>
 * Note: The Date and String "standalone" map to the XACML-defined
 * AttributeIds: ...subject-id, ...action-id, ...resource-id, and
 * ...currentDateTime for Subject, Action, Resource, Environment
 * categories, respectively.
 * <p>
 * The map is inherently more flexible, since what is submitted is
 * a collection of "pairs", where the first of the pair is a String
 * containing a user-specified AttributeId, and the value can
 * be one of Date, String, Integer, Boolean.
 * @author Josh Bregman, Rich Levinson, Prateek Mishra
 */
public class SimpleJavaObjectMapper implements JavaObjectMapper {
	/**
	 * Set of class objects supported by this mapper
	 */
	private static Set<Class> supportedClasses =
		new HashSet<Class>() {{ add(Date.class); 
								add(String.class); 
								add(Map.class); }};
	Log log = LogFactory.getLog(this.getClass()); 
	
	/**
	 * Create a SimpleJavaObjectMapper
	 */
    public SimpleJavaObjectMapper() {
        super();
    }

    /**
     * Returns the set of class objects supported by this Mapper
     */
    public Set<Class> getSupportedClasses(){
    	return supportedClasses;
    }
    
    /**
     * Determines if this Mapper can map the type of class that
     * that is passed in as a parameter. Basically involves checking
     * that the class matches one of a set of known classes that
     * can be mapped.
     */
    public boolean canMapObject(Object obj) {
    	if (log.isTraceEnabled()) log.trace(
    		"\n   canMap(Object obj = " + obj.getClass().getName() + ") ?");
        if (obj instanceof Date){
        	if (log.isTraceEnabled()) log.trace(
        		"\n\tobj instanceof Date == true");
            return true;
        } else if (obj instanceof String) {
        	if (log.isTraceEnabled()) log.trace(
        			"\n\tobj instanceof String == true");
        	return true;
        } else if (obj instanceof HashMap) {
        	if (log.isTraceEnabled()) log.trace(
        			"\n\tobj instanceof HashMap == true");
        	return true;
        } else if (obj instanceof Map) {
        	if (log.isTraceEnabled()) log.trace(
        			"\n\tobj instanceof Map == true");
        	return true;
        //} else if (obj instanceof String) {
        //	return true;
        } else {
        	if (log.isTraceEnabled()) log.trace(
        		"\n\tCannot map obj instanceof: " + 
        			obj.getClass().getName());
            return false;
        }
    }

    /**
     * Maps a Java Object to one or more AzAttributes. In general
     * a Java Object can contain several attributes that may be
     * needed as AzAttributes in the decision process.
     */
    public <T extends Enum<T> & AzCategoryId> 
    	RequestAttributes<T> map(
    			Object javaObject,
                RequestAttributes<T> azWrapperObject) 
            throws PepException{
        if (log.isTraceEnabled()) log.trace(
        		"\n   Object to map: " + 
        			javaObject.getClass().getName());
        if (javaObject instanceof Date) {
            return convertDate((Date)javaObject,azWrapperObject);
        } else if (javaObject instanceof String) {
            return convertString(javaObject,azWrapperObject);
        } else if (javaObject instanceof Map) {
            return convertMap(javaObject,azWrapperObject);
        }
        
        throw new PepException(
        		"Can't map an object of class " +
        		javaObject.getClass().getName());        
    }

    /**
     * Converts a Date to an environment AzEntity date attribute
     * @param <T>
     * @param date
     * @param azWrapperObject
     * @return a RequestAttributes AzEntity wrapper
     */
    public <T extends Enum<T> & AzCategoryId> 
    		RequestAttributes<T> convertDate(
    				Date date,
                    RequestAttributes<T> azWrapperObject) {
    	T t = azWrapperObject.getAzEntity().getAzCategoryId();
        if (t.equals(AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT)) {
        	String attributeId = AzXacmlStrings.X_ATTR_ENV_CURRENT_DATE_TIME;
            azWrapperObject.setAttribute(attributeId, date);
    		log.info(
				"\n    Mapped Date object to:" + 
				"\n\t AzCategoryId   = " + t.getClass().getSimpleName() +
				"\n\t AttributeId    = " + attributeId +
				"\n\t AttributeValue = " + date + "\n");
        }        
        return azWrapperObject;
    }

    /**
     * Converts a Java object to a String value and names it
     * with an AttributeId associated with the Id attribute
     * conventionally used with the associated AzEntity type.
     * @param <T>
     * @param javaObject
     * @param azWrapperObject
     * @return a RequestAttributes AzEntity wrapper
     * @throws PepException
     */
    public <T extends Enum<T> & AzCategoryId> 
    	RequestAttributes<T> convertString(
    			Object javaObject,
                RequestAttributes<T> azWrapperObject) 
            throws PepException {
        
        String attributeId = null;
        T t = azWrapperObject.getAzEntity().getAzCategoryId();
        if (t.equals(AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS)) {
            attributeId = AzXacmlStrings.X_ATTR_SUBJECT_ID;
        } else if (t.equals(AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE)) {
            attributeId = AzXacmlStrings.X_ATTR_RESOURCE_ID;
        } else if (t.equals(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION)) {
            attributeId = AzXacmlStrings.X_ATTR_ACTION_ID;
        } else {
            throw new PepException(
        		"Can't Convert String for " +
        		t.name());
        }
        
        azWrapperObject.setAttribute(attributeId,(String)javaObject);
		log.info(
				"\n    Mapped String object to:" + 
				"\n\t AzCategoryId   = " + t.getClass().getSimpleName() +
				"\n\t AttributeId    = " + attributeId +
				"\n\t AttributeValue = " + (String)javaObject + "\n");
		return azWrapperObject;
    }

    /**
     * Converts a Map of String, Object pairs to one AzAttribute Id
     * and value associated with each of the pairs.
     * @param <T>
     * @param javaObject
     * @param azWrapperObject
     * @return a RequestAttributes AzEntity wrapper
     */
    public <T extends Enum<T> & AzCategoryId> 
    	RequestAttributes<T> convertMap(
						Object javaObject,
                        RequestAttributes<T> azWrapperObject) {
        
        //Map<String,String> map = (Map<String,String>)javaObject;
        Map<String,Object> map = (Map<String,Object>)javaObject;
        T t = azWrapperObject.getAzEntity().getAzCategoryId();
        
        //Iterator<Map.Entry<String,String>> it = 
        Iterator<Map.Entry<String,Object>> it = 
        	map.entrySet().iterator();
        
        boolean skipped;
        while (it.hasNext()) {
        	skipped = false;
            //Map.Entry<String,String> entry = it.next();
            Map.Entry<String,Object> entry = it.next();
            Object objValue = entry.getValue();
            String objType = objValue.getClass().getSimpleName();
            if (objType.equals("Date")) {
            	Date dateValue = (Date)objValue;
            	azWrapperObject.setAttribute(
            		entry.getKey(),dateValue);
            } else if (objType.equals("String")) {
            	String strValue = (String)objValue;
            	azWrapperObject.setAttribute(
            		entry.getKey(),strValue);           	
            } else if (objType.equals("Integer")) {
            	Integer intValue = (Integer)objValue;
            	azWrapperObject.setAttribute(
            		entry.getKey(),intValue);           	
            } else if (objType.equals("Boolean")) {
            	Boolean boolValue = (Boolean)objValue;
            	azWrapperObject.setAttribute(
            		entry.getKey(),boolValue);           	
            } else { skipped = true;}
    		//log.info(
			//	"\n    Mapped Map<String(key),String(value)> to:" + 
			//	"\n\t AzCategoryId          = " + t.getClass().getSimpleName() +
			//	"\n\t AttributeId(key)      = " + entry.getKey() +
			//	"\n\t AttributeValue(value) = " + entry.getValue() + "\n");
            if (skipped) {
              	log.info(
          			"\n    Skipped Map<String(key),Object(value)> with:" +
    				"\n\t AzCategoryId          = " + t.getClass().getSimpleName() +
    				"\n\t AttributeId(key)      = " + entry.getKey() +
    				"\n\t AttributeValue(type)  = " + entry.getValue().getClass().getName() +
    				"\n\t AttributeValue(value) = " + entry.getValue() + 
            		"\n\t because only AttributeValue(types): " + 
            		"Date, String, Integer, Boolean are supported");            	
            }
            else {
            	log.info(
    				"\n    Mapped Map<String(key),Object(value)> to:" + 
    				"\n\t AzCategoryId          = " + t.getClass().getSimpleName() +
    				"\n\t AttributeId(key)      = " + entry.getKey() +
    				"\n\t AttributeValue(type)  = " + entry.getValue().getClass().getName() +
    				"\n\t AttributeValue(value) = " + entry.getValue() + "\n");
            }
        }        
        return azWrapperObject;        
    }
}
