package test;

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

//import java.util.Arrays;
//import java.util.HashSet;
//import java.util.Set;
import java.util.Iterator;
import java.util.Date;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.HashMap;

import org.openliberty.openaz.azapi.pep.PepRequest;
import org.openliberty.openaz.azapi.pep.PepResponse;
import org.openliberty.openaz.azapi.pep.PepException;
import org.openliberty.openaz.azapi.AzRequestContext;
import org.openliberty.openaz.azapi.AzResourceActionAssociation;
import org.openliberty.openaz.azapi.AzResourceActionAssociationId;
import org.openliberty.openaz.azapi.AzService;
import org.openliberty.openaz.azapi.constants.AzXacmlStrings;
import org.openliberty.openaz.pep.PepRequestFactoryImpl;

//import org.openliberty.openaz.pdp.provider.SimpleConcreteService;


public class TestWithLogging implements AzXacmlStrings{
	
	// variable defns to provide "context" for test program:
	// defn: "container": manages "environment" and "issues" env attributes
	public final static String CONTAINER = "orcl-weblogic";   // [a03]
	// defn: "application": manages "resources" and "issues" resource attrs
	public final static String APPLICATION = "hr-application-01"; // [a04]	
	// test user; normally would be obtained from session info
	public final static String SAMPLE_SESSION_USER_NAME = "Joe User";
	public final static String SAMPLE_SESSION_AUTH_METHOD = "password";

	Log log = LogFactory.getLog(this.getClass()); 
	
	/**
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		
		TestWithLogging twl = new TestWithLogging();
		twl.runTest();
	} // end of main
		
	public void runTest() {
        log.info("Start Simple Decide");		
		AzService azService = 
			new org.openliberty.openaz.
				pdp.provider.SimpleConcreteDummyService();
		PepRequestFactoryImpl pep = 
			new PepRequestFactoryImpl(CONTAINER, azService);
		
        String resourceName = "file:\\\\toplevel";
                
        String [] actions = new String [] 
             {"read","write","delete","read","write","delete"};
        
        for (int i=0; i<actions.length; i++) {    
        	log.info("\n\n\t **************************************" +
        			 "\n\n\t    Start loop: " + i +
        			 "\n\n\t **************************************" +
        			 "\n\n");
            Date now = new Date();
            
            HashMap<String,String> subject = new HashMap<String,String>();           
            subject.put(AzXacmlStrings.X_ATTR_SUBJECT_ID,"josh");
            subject.put(AzXacmlStrings.X_ATTR_SUBJECT_AUTHN_LOC_AUTHENTICATION_METHOD,SAMPLE_SESSION_AUTH_METHOD);
            log.info("Prepare PepRequest for action: " + 
            		i + ": " + actions[i]);		
            
            try {
            	PepRequest req = pep.newPepRequest(subject,actions[i],resourceName,now);
                
            	log.info("Start to issue req.decide()");
            	PepResponse resp = req.decide();
            
            	System.out.println("\n########   " + 
            			resp.allowed() +
            			"   ########\n");
            	log.info("resp.allowed() = " + resp.allowed());
            } catch (PepException p) {
            	log.info("\n\n########   \n" + 
            			"Caught EzPepException: " + p +
            			"\n########\n");
            }         
        }

        /**
		// Test removeAll
		System.out.println("Set 6 before: " + azActionA06.getAzAttributeSet());
		azActionA06.getAzAttributeSet().removeAll(azActionA06.getAzAttributeSet()); 
		System.out.println("Set 6 after: " + azActionA06.getAzAttributeSet());

		System.out.println("Set 5 before: " + azActionA05.getAzAttributeSet());
		System.out.println("Attribute in set 5: " + azActionA05.getAttributeByAttribId(X_ATTR_ACTION_ID).getAttributeId());
		azActionA05.getAzAttributeSet().remove(azActionA05.getAttributeByAttribId(X_ATTR_ACTION_ID));
		System.out.println("Set 5 after: " + azActionA05.getAzAttributeSet());
	
            **/		
	} // End of main()
	
	
	
	public static void printAssociations(AzRequestContext azReqCtx){
		AzResourceActionAssociationId azAssocId = null;
		System.out.println("TestAzApi.printAssociations: ");
		Iterator<AzResourceActionAssociation> it = azReqCtx.getAssociations().iterator();
		while (it.hasNext()){
			azAssocId = it.next().getAzResourceActionAssociationId();
			System.out.println("\t ResourceId: " + azAssocId.getResourceId() +
					"\t  ActionId: " + azAssocId.getActionId());
		}
	}
}
