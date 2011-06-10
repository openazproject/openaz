package org.openliberty.openaz.pdp.provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;

import org.apache.commons.logging.LogFactory;
import org.apache.commons.logging.Log;
import org.openliberty.openaz.azapi.AzAttribute;
import org.openliberty.openaz.azapi.AzAttributeFinder;
import org.openliberty.openaz.azapi.AzAttributeValue;
import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzDataTypeId;
import org.openliberty.openaz.azapi.constants.AzDataTypeIdString;
import org.openliberty.openaz.pdp.resources.OpenAzResourceQueryBuilder;

public class SimpleDummyAzAttributeFinder<T extends Enum<T> & AzCategoryId,
										  U extends Enum<U> & AzDataTypeId,
										  V> 
		implements AzAttributeFinder<T> {
	
	static Log logStatic = LogFactory.getLog(
							SimpleDummyAzAttributeFinder.class);
	Log log = LogFactory.getLog(this.getClass());

	/**
	 * This is a dummy attribute finder, which simply returns
	 * a specific result. It is an implementation of 
	 * {@link AzAttributeFinder}.
	 * 
	 * @param azRequestContext
	 * @param azEntity
	 * @param azAttribute
	 * 
	 * @return a collection of AzAttribut&lt;T&gt;
	 */
	public Collection<AzAttribute<T>> findAzAttribute(
			AzRequestContext azRequestContext,
			AzEntity<T> azEntity,
			AzAttribute<T> azAttribute) {
		
		List<AzAttribute<T>> azAttributesToReturn = 
								new ArrayList<AzAttribute<T>>();
		
		if ( ! ( azAttribute == null ) ) {
		if (log.isInfoEnabled()) log.info(
	    	"\n    CallBack (2nd level) AzApi AttrFinder Parameters: " +
				"\n\tattributeId: " + azAttribute.getAttributeId() +
				"\n\tof datatype: " + 
					azAttribute.getAzAttributeValue().getType() +
				"\n\tof category: " + azAttribute.getAzCategoryId() +
				"\n\tby issuer: " + azAttribute.getAttributeIssuer() +
				"\n\tin AzEntity: " + azEntity.getId() +
				"\n\tin AzRequestContext: " + 
					azRequestContext.getId() + "\n");
		} else if (log.isInfoEnabled()) log.info("\n   azAttribute = null");
		
		AzAttribute<T> azAttributeToFind = null;
		AzDataTypeId azDataType =
			azAttribute.getAzAttributeValue().getType();
		if (azDataType.equals(AzDataTypeIdString.AZ_DATATYPE_ID_STRING)) {
			AzAttributeValue<AzDataTypeIdString, String> azAttributeValue =
				azEntity.createAzAttributeValue(
						AzDataTypeIdString.AZ_DATATYPE_ID_STRING,
						"throw");
						//"This is a sample string value from " +
						//"the SimpleDummyAzAttributeFinder.");
			// try using the created attribute to replace itself
			//azAttribute =
			azAttributeToFind =
				azEntity.createAzAttribute(
						azAttribute.getAttributeIssuer(), 
						azAttribute.getAttributeId(), 
						azAttributeValue);
			//azAttributeToFind = azAttribute;
		}
		else {
			if (log.isTraceEnabled()) log.trace(
				"\n\tDataType not supported by SimpleDummyAzAttributeFinder: " +
				azDataType);
		}
		if ( ! ( azAttributeToFind == null ) ) {
			azAttributesToReturn.add(azAttributeToFind);
			if (log.isTraceEnabled()) log.trace(
				"\n    Returning found attributes" + azAttributesToReturn + "\n");
		}
		else if (log.isTraceEnabled()) log.trace(
				"\n    Found no attributes to return\n");
		return azAttributesToReturn;
	}

}
