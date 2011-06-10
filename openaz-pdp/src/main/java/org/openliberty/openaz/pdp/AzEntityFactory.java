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
 * 		Rich Levinson (Oracle)
 * Contributor:
 * 		Rich Levinson (Oracle)
 */
package org.openliberty.openaz.pdp;


//import org.openliberty.openaz.azapi.test.TestAzRequestContext;
//import org.openliberty.openaz.azapi.test.TestAzEntity;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.openliberty.openaz.azapi.AzEntity;
//import org.openliberty.openaz.azapi.AzService;
import org.openliberty.openaz.azapi.constants.AzCategoryId;
import org.openliberty.openaz.azapi.constants.AzCategoryIdEnvironment;

/**
 * @author Rich
 *
 */
public class AzEntityFactory<T extends Enum<T> & AzCategoryId> {
	
	static int idAzEntityCounter = 0;
	public static final String DEFAULT_ENV_PROVIDER_NAME = "AZ_ENV_ENTITY";

	//private static final <T extends Enum<T> & AzCategoryId> 
	//	Map<String, AzEntity<T>> providers =
	//		new ConcurrentHashMap<String, AzEntity<T>>();
	//private static final  
	//	Map<String, AzEntity<AzCategoryIdEnvironment>> envProviders =
	//		new ConcurrentHashMap<String, AzEntity<AzCategoryIdEnvironment>>();
	
	public static <U extends Enum<U> & AzCategoryId> 
	AzEntity<U> getAzEntity(U u){
		//AzEntity<T> azEntity = TestAzRequestContext.createAzEntity(t);
		//AzEntity<T> azEntity = new TestAzEntity<T>(t, idAzEntityCounter++);
		return null;
		//AzEntity<T> azEntity = null;
		//AzCategoryId azCategoryId = t;
		//if (t instanceof AzCategoryIdEnvironment)
		//	azEntity = (AzEntity<T>) 
		//		envProviders.get(DEFAULT_ENV_PROVIDER_NAME);
		//return azEntity;
	}
	/*
	//public static <>
	public static <T extends Enum<T> & AzCategoryId>  
		void registerDefaultProvider(AzEntity<T> azEntity){
			registerProvider(DEFAULT_ENV_PROVIDER_NAME, azEntity);
	}
	public static <T extends Enum<T> & AzCategoryId>
		void registerProvider(String name, AzEntity<T> azEntity){
			AzCategoryId azCategoryId = azEntity.getAzCategoryId();
	    	if (azCategoryId instanceof AzCategoryIdEnvironment)
	    		envProviders.put(name,azEntity);
	}
	*/

	public static <T> 
	T getMiddle(T[] a){
		T aReturn = a[a.length/2];
		return aReturn;
	}
}
