package test.objects;

//import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;
import org.openliberty.openaz.azapi.constants.AzCategoryIdResource;
import org.openliberty.openaz.azapi.constants.AzCategoryIdSubjectAccess;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;
import org.openliberty.openaz.azapi.pep.RequestAttributes;
import org.openliberty.openaz.azapi.pep.JavaObjectMapper;
import org.openliberty.openaz.azapi.pep.PepException;

public class ReverseStringMapper implements JavaObjectMapper{

    private Set<Class> supportedClasses = new HashSet<Class>();
    
    public ReverseStringMapper() {
        this.supportedClasses.add(String.class);
    }
    
    public boolean canMapObject(Object obj) {
        return (obj instanceof String);
    }

    public <T extends Enum<T> & AzCategoryId> 
            RequestAttributes<T> map(
                            Object javaObject, 
                            RequestAttributes<T> azWrapperObject)
                    throws PepException {
        
        String attributeId = null;
        //if (azWrapperObject instanceof Subject) {
        if (azWrapperObject.getAzEntity().getAzCategoryId().equals(
                        AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS)) {
            attributeId = AzXacmlStrings.X_ATTR_SUBJECT_ID;
        } else if (azWrapperObject.getAzEntity().getAzCategoryId().equals(
                        AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE)) {
            attributeId = AzXacmlStrings.X_ATTR_RESOURCE_ID;
        } else if (azWrapperObject.getAzEntity().getAzCategoryId().equals(
                        AzCategoryIdAction.AZ_CATEGORY_ID_ACTION)) {
            attributeId = AzXacmlStrings.X_ATTR_ACTION_ID;
        } else {
            throw new PepException("Can't Convert String for "+azWrapperObject.getAzEntity().getAzCategoryId().name());
        }
        
        String theString = (String)javaObject;        
        StringBuffer sb = new StringBuffer(theString.length());
        
        for (int i=theString.length()-1; i!=-1; i--) {
            sb.append(theString.charAt(i));
        }        
        azWrapperObject.setAttribute(attributeId,sb.toString());
        return azWrapperObject;
    }

    public Set<Class> getSupportedClasses() {
        
        return this.getSupportedClasses();
    
    }
}
