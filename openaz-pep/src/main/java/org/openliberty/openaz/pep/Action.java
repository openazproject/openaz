/**
 * Copyright 2009 Oracle, Inc.
 * Licensed under the Apache License, Version 2.0 (the "License"); 
 * you may not use this file except in compliance with the License. 
 * You may obtain a copy of the License at 
 *   http://www.apache.org/licenses/LICENSE-2.0 
 * Unless required by applicable law or agreed to in writing, software 
 * distributed under the License is distributed on an "AS IS" BASIS, 
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
 * See the License for the specific language governing permissions and 
 * limitations under the License.
 *
 * Authors:
 * 		Josh Bregman (Oracle)
 * 		Rich Levinson (Oracle)
 *              Prateek Mishra (Oracle)
 */


package org.openliberty.openaz.pep;

import org.openliberty.openaz.azapi.AzEntity;
import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.RequestAttributes;
import org.openliberty.openaz.azapi.pep.RequestAttributesFactory;
import org.openliberty.openaz.azapi.constants.AzCategoryIdAction;

/**
 * Action is a specific subtype of 
 * <code>{@link RequestAttributes}&lt;T&gt;</code>,
 * namely 
 * <code>RequestAttributesImpl&lt;AzCategoryIdAction&gt;</code>.
 * This enables it to be a non-generic specific container for 
 * {@link AzCategoryIdAction} attributes.
 */
public class Action extends 
	RequestAttributesImpl<AzCategoryIdAction> {
       
    public Action(AzEntity<AzCategoryIdAction> action, 
    			  PepRequest ctx, 
		  		  RequestAttributesFactory<AzCategoryIdAction> factory) {
        super(action,ctx,factory);
    }
    
}
