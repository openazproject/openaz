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
package org.openliberty.openaz.pdp.provider;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;
//import org.openliberty.openaz.azapi.test.TestAzService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.AzService;

import org.openliberty.openaz.pdp.resources.OpenAzResourceDirectory;
import org.openliberty.openaz.pdp.resources.OpenAzTestResourceCollection;

public class AzServiceFactory {
	public static final String DEFAULT_PROVIDER_NAME = "AZ_SERVICE";

	private static final  Map<String, AzService> providers =
		new ConcurrentHashMap<String, AzService>();
	static Log logStatic = LogFactory.getLog(AzServiceFactory.class);
	
	/*
	 * This is a collection of test resources that can be
	 * used by query operations. 
	 * TODO: generalize this interface so the resource directory
	 * providers can be registered.
	 * 
	 */
	public static OpenAzResourceDirectory azResourceDirectory =
		new OpenAzTestResourceCollection();
	
	public static AzService getAzService() {
		//System.out.println("AzServiceFactory.getAzService: " +
		//		"\n\t Creating instance of TestAzService-2.");
		String name = DEFAULT_PROVIDER_NAME;
		if (logStatic.isTraceEnabled()) logStatic.trace(
			"\n\topenaz.pdp.provider.AzServiceFactory.getAzService(): " +
			"\n\t\tGetting instance of AzService, " + name + 
			", from set of providers(len=" +
			providers.size() + ").\n");
		//return new TestAzService();
        //        return null;
		return providers.get(name);
	}
	public static void registerDefaultProvider(AzService azService){
		registerProvider(DEFAULT_PROVIDER_NAME, azService);
	}
	public static void registerProvider(String name, AzService azService){
		providers.put(name,azService);
		if (logStatic.isTraceEnabled()) logStatic.trace(
			"\n    openaz.pdp.provider.AzServiceFactory.registerProvider(" +
			"\n\t" + name + ", " + azService.getClass().getName() + ")\n");
	}
}
