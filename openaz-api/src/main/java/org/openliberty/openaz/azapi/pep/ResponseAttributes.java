package org.openliberty.openaz.azapi.pep;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdObligation;
import org.openliberty.openaz.azapi.constants.AzCategoryIdStatusDetail;

/**
 * This is primarily an internal interface used to wrap
 * {@link AzEntity} attribute collections in PepResponses of 
 * types {@link AzCategoryIdObligation} 
 * and {@link AzCategoryIdStatusDetail}.
 * @param <T>
 */
public interface ResponseAttributes<T extends Enum<T> & AzCategoryId> 
	extends Attributes<T> {
}
