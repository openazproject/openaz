package org.openliberty.openaz.pep;

import java.util.Date;
import java.util.Set;

import org.openliberty.openaz.azapi.AzAttribute;
import org.openliberty.openaz.azapi.AzDataDateTime;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;

/**
 * A wrapper around an authorization provider response entity object 
 * (obligation)
 * @author rlevinson, jbregman, pmishra
 *
 * @param <T> a response entity identifier {@link AzCategoryIdObligation}
 */
public class ResponseAttributesImpl<T extends Enum<T> & AzCategoryId> 
			extends AttributesImpl<T> {

    public ResponseAttributesImpl(AzEntity<T> wrappedObject) {
        super(wrappedObject);
    }

    public String getStringAttribute(String name) {       
        Object obj = this.getObjectByName(name);       
        System.out.println(obj);       
        return obj.toString();
    }
    
    public Date getDateAttribute(String name) {
        Object obj = this.getObjectByName(name);        
        AzDataDateTime dt = (AzDataDateTime)obj;        
        return dt.getDate();        
    }
    
    protected Object getObjectByName(String name) {
    	//tbd: review this code to see what is intended
        AzAttribute attribute = 
                wrappedObject.getAttributeByAttribId(name);
        Set azEnvAttrSet = 
                wrappedObject.getAzAttributeSet();        
        Object obj = 
        	((AzAttribute)this.wrappedObject.
        		getAzAttributeSet().iterator().next()).
        			getAzAttributeValue().getValue();    
        return obj;        
    }
}
