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
package test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.Iterator;
import java.util.Date;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;
import org.openliberty.openaz.pdp.provider.SimpleConcreteDummyService;
import org.openliberty.openaz.pdp.provider.AzServiceFactory;

import org.openliberty.openaz.pdp.provider.SimpleConcreteSunXacmlService;
import com.sun.xacml.ParsingException;
import com.sun.xacml.UnknownIdentifierException;

public class TestAzAPI implements AzXacmlStrings{
	
	// variable defns to provide "context" for test program:
	// defn: "container": manages "environment" and "issues" env attributes
	public final static String CONTAINER = "orcl-weblogic";   // [a03]
	// defn: "application": manages "resources" and "issues" resource attrs
	public final static String APPLICATION = "hr-application-01"; // [a04]	
	// test user; normally would be obtained from session info
	public final static String SAMPLE_SESSION_USER_NAME = "Joe User";
	public final static String SAMPLE_SESSION_AUTH_METHOD = "password";
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		System.out.println("Testing TestAzApi version 110");
		int testCounter = 0;
		String DEFAULT_PROVIDER_NAME = "AZ_SERVICE";
		
        System.out.println("args[] = " + Arrays.toString(args));
        String requestFile = null;
        String [] policyFiles = null;
        if (args.length > 0){
	        if (args[0].equals("-config")) {
	            requestFile = args[1];
	            System.out.println("args[1] = " + requestFile);
	        } else {
	            requestFile = args[0];
	            policyFiles = new String[args.length - 1];            
	            for (int i = 1; i < args.length; i++) {
	                policyFiles[i-1] = args[i];
	                System.out.println("args[" + i + "] = " + policyFiles[i-1]);
	            }
	        }
        }
        else
        	System.out.println("no args provided to " + 
        			" TestAzApi.main(String[] args)");
		// Register the local AzService provider
		// TODO: when configuration strategy is decided, we
		// need to suppress local provider exceptions so
		// that users don't need to be concerned about them.
      	StringWriter sw = new StringWriter();
		try {
			String PDP_CONFIG_PROPERTY =
		        "com.sun.xacml.PDPConfigFile";
	        System.setProperty(PDP_CONFIG_PROPERTY, ".\\config\\sample1.xml");
			String configFile = System.getProperty(PDP_CONFIG_PROPERTY);
	        System.out.println("configFile = " + configFile);
	        if (args.length > 0){
		        if (policyFiles == null) {
		        	System.out.println("Registering: " + 
		        		"SimpleConcreteSunXacmlService()");
					AzServiceFactory.registerProvider(
						DEFAULT_PROVIDER_NAME, 
						new SimpleConcreteSunXacmlService());
		        } else {
		        	System.out.println("Registering: " + 
	        			"SimpleConcreteSunXacmlService(policyFiles)");
					AzServiceFactory.registerProvider(
						DEFAULT_PROVIDER_NAME, 
						new SimpleConcreteSunXacmlService(
								requestFile,
								policyFiles));		        	
		        }
		       
	        } else {
	        	System.out.println("Registering: " + 
        			"SimpleConcreteDummyService()");
				AzServiceFactory.registerProvider(
					DEFAULT_PROVIDER_NAME, 
					new SimpleConcreteDummyService());	        	
	        }
		} catch (ParsingException pe) {
			pe.printStackTrace(new PrintWriter(sw));
			System.out.println("SunXacml ParsingException: " +
					pe.getMessage() + "\n" + sw);
		} catch (UnknownIdentifierException uie) {
			uie.printStackTrace(new PrintWriter(sw));
			System.out.println("SunXacml UnknownIdentifierException: " + 
					uie.getMessage() + "\n" + sw);
		}
		// Get a reference to the authorization service:
		AzService azHandle = AzServiceFactory.getAzService();  // [a06]
		
		// Create an AzRequestContext:
		AzRequestContext azReqCtx = azHandle.createAzRequestContext();  // [a07]
		
		// Create an AzEnvironment entity and add an env attr:
		AzEntity<AzCategoryIdEnvironment> azEnv = 
			azReqCtx.createAzEntity(AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT);

		// Note: following line does not compile because of intended type
		// safety disallowing an AzEntity Resource to rcv requested Environment 
		// category:
		//AzEntity<AzCategoryIdResource> azEnv2 = 
		//	azReqCtx.createAzEntity(AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT);
		
		azEnv = azReqCtx.getAzEntity(
				AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT);
		if (azEnv == null) System.out.println("    TestAzAPI.azEnv is null");

		// Create a general purpose attribute value
		/*AzAttributeValueDateTime currentDateTimeValue = 
			azHandle.createAzAttributeValueDateTime();
		AzAttribute azAttr = azHandle.createAzAttribute(
				AzEnvironmentCategoryId.X_CATEGORY_ENVIRONMENT,  
				X_ATTR_ENV_CURRENT_DATE_TIME, CONTAINER, currentDateTimeValue);
		printAttributeData(azAttr, "env: curr-date-time 1");
		*/
		try {
			Thread.sleep(2000);
			System.out.println("Waited a second to get new time value\n");
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// Create an AzEnvironmentAttribute and add it to AzEnvironment
		//AzEnvironmentAttribute azEnvAttr =    // [a13]
		//	azEnv.createAndAddAzEnvironmentAttribute(
		//		X_ATTR_ENV_CURRENT_DATE_TIME, CONTAINER, 
		//		azEnv.createAzAttributeValueDateTime());
		AzAttribute<AzCategoryIdEnvironment> azEnvAttr =    // [a13]
			azEnv.createAzAttribute(
				CONTAINER, 
				X_ATTR_ENV_CURRENT_DATE_TIME, 
				azEnv.createAzAttributeValue(
					AzDataTypeIdDateTime.AZ_DATATYPE_ID_DATETIME, 
					azEnv.createAzDataDateTime(new Date(),0,0,0)));
					// Note: above is type safe - to protect against following
					// lines which use non-supported types.
					//AzDataTypeIdDateTime.AZ_DATATYPE_ID_DATETIME, new Date()));
					//AzDataTypeIdDateTime.AZ_DATATYPE_ID_DATETIME, new java.io.File("example")));
		
		System.out.println("    TestAzAPI.azEnv.idIdentityCounter = " + 
				azEnv.getId());
		System.out.println("    TestAzAPI.azEnvAttr.attrId = " + 
				azEnvAttr.getAttributeId());
		
		// Following does not compile because bad attributeValue parameters:
		//AzAttribute<AzCategoryIdEnvironment> azEnvAttrBad =    // [a13]
		//	azEnv.createAzAttribute(
		//		CONTAINER, 
		//		X_ATTR_ENV_CURRENT_DATE_TIME, 
		//		azEnv.createAzAttributeValue(
		//			AzDataTypeIdDateTime.AZ_DATATYPE_ID_DATETIME, "test"));
		//System.out.println("    TestAzAPI.azEnv.idIdentityCounter = " + azEnv.getId());
		//System.out.println("    TestAzAPI.azEnvAttrBad.attrId = " + azEnvAttrBad.getAttributeId());
		
		// Add environment object to AzRequestContext
		azReqCtx.addAzEntity(azEnv);  // [a15]
		
		// Get and print the attribute from the AzRequestContext:
		AzAttribute<AzCategoryIdEnvironment> azEnvCurrDateTime = 
			azReqCtx.getAzEntity(
				AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT).
					getAttributeByAttribId(X_ATTR_ENV_CURRENT_DATE_TIME);
		
		System.out.println("    TestAzAPI.azEnv.idIdentityCounter = " + azEnv.getId());

		Set<AzAttribute<AzCategoryIdEnvironment>> azEnvAttrSet = 
			azEnv.getAzAttributeSet();

		System.out.println("    TestAzAPI.azEnv.getAttributeSet()" +
				"\n\t 1st attr id = " + azEnvAttrSet.iterator().next().getAttributeId());
		//printAttributeData(azEnvCurrDateTime, "env: curr-date-time 2");

		//printAttributeData(
		//	azReqCtx.
		//		getAzEntity(AzCategoryIdEnvironment.AZ_CATEGORY_ID_ENVIRONMENT).
		//			getAttributeByAttribId(X_ATTR_ENV_CURRENT_DATE_TIME), 
		//		"env: curr-date-time 3");
		
		// Get a Subject Category and AzEntity for access-subject attributes
		AzCategoryIdSubjectAccess azSubjCat = 
			AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS;

		AzEntity<AzCategoryIdSubjectAccess> accSubj = 
			azReqCtx.createAzEntity(
					azSubjCat);
					//AzCategoryIdSubjectAccess.AZ_CATEGORY_ID_SUBJECT_ACCESS);   // [a17]	
		
		// Three steps to create a subject attribute and add it to collection:
		AzAttributeValueString azAttrValueSubjId = 
			accSubj.createAzAttributeValue(
				AzDataTypeIdString.AZ_DATATYPE_ID_STRING,
				SAMPLE_SESSION_USER_NAME);

		AzAttribute<AzCategoryIdSubjectAccess> azAttrSubjectId = 
			accSubj.createAzAttribute(
					CONTAINER, 
					AzXacmlStrings.X_ATTR_SUBJECT_ID , 
					azAttrValueSubjId);

		System.out.println("TestAzApi azAttrSubjectId.getAttributeId: " + 
				azAttrSubjectId.getAttributeId());
		
		// Test using an "unsupported" type for an attribute
		AzAttributeValue<AzDataTypeIdString, Integer> azAttrValueSubjIdTest = 
			accSubj.createAzAttributeValue(
				AzDataTypeIdString.AZ_DATATYPE_ID_STRING,
				new Integer(3));
		
		// Test using an explicit "unsupported" type for an attribute
		try {
			AzAttributeValue<AzDataTypeIdString, URI> azAttrValueSubjIdTest2 = 
				accSubj.createAzAttributeValue(
					AzDataTypeIdString.AZ_DATATYPE_ID_STRING,
					new URI("http://www.example.com"));
		} catch (URISyntaxException u) {
			System.out.println("Caught exception creating URI" + 
					u.getMessage());
		}
		
		// One step to create a subject attribute and add it to collection: 
		accSubj.createAzAttribute(   // [a23]
				CONTAINER, 
				AzXacmlStrings.X_ATTR_SUBJECT_AUTHN_LOC_AUTHENTICATION_METHOD, 
				accSubj.createAzAttributeValue(
					AzDataTypeIdString.AZ_DATATYPE_ID_STRING, 
					SAMPLE_SESSION_AUTH_METHOD));
		
		// Add the Subject to the AzRequestContext:
		azReqCtx.addAzEntity(accSubj);  // [a26]
		
		// "Print" the attributes:
		AzAttribute<?> azAttr = null;
		azAttr = azReqCtx.getAzEntity(azSubjCat).
			getAttributeByAttribId(AzXacmlStrings.X_ATTR_SUBJECT_ID);
		//printAttributeData(azAttr, "subject: subject-id");

		azAttr = azReqCtx.getAzEntity(azSubjCat).
			getAttributeByAttribId(
				AzXacmlStrings.X_ATTR_SUBJECT_AUTHN_LOC_AUTHENTICATION_METHOD);
		//printAttributeData(azAttr, "subject: auth-method");
		
		// Get a Resource Category and AzEntity
		AzEntity<AzCategoryIdResource> azResource = 
			azReqCtx.createAzEntity(AzCategoryIdResource.AZ_CATEGORY_ID_RESOURCE); // [a31]
		
		// Create a resource id attribute:
		azResource.createAzAttribute(
			APPLICATION, 
			AzXacmlStrings.X_ATTR_RESOURCE_ID,
			azResource.createAzAttributeValue(
				AzDataTypeIdString.AZ_DATATYPE_ID_STRING, "file:C\\toplevel"));
		
		// Create six AzAction Category AzEntity objects
		AzEntity<AzCategoryIdAction> azActionA01 =          // [a35]
			azReqCtx.createAzEntity(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION);
		AzEntity<AzCategoryIdAction> azActionA02 =          // [a36]
			azReqCtx.createAzEntity(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION);
		AzEntity<AzCategoryIdAction> azActionA03 =          // [a36]
			azReqCtx.createAzEntity(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION);
		AzEntity<AzCategoryIdAction> azActionA04 =          // [a36]
			azReqCtx.createAzEntity(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION);
		AzEntity<AzCategoryIdAction> azActionA05 =          // [a36]
			azReqCtx.createAzEntity(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION);
		AzEntity<AzCategoryIdAction> azActionA06 =          // [a36]
			azReqCtx.createAzEntity(AzCategoryIdAction.AZ_CATEGORY_ID_ACTION);
		
		AzCategoryId azCat = azActionA01.getAzCategoryId();
		System.out.println("azCat: class type = " + azCat.getClass().getName());
		
		azActionA01.createAzAttribute(APPLICATION,                     // [a38]
				AzXacmlStrings.X_ATTR_ACTION_ID,
				azActionA01.createAzAttributeValue(
					AzDataTypeIdString.AZ_DATATYPE_ID_STRING, "Read"));
		azActionA02.createAzAttribute(APPLICATION,                     // [a38]
				AzXacmlStrings.X_ATTR_ACTION_ID,
				azActionA02.createAzAttributeValue(
						AzDataTypeIdString.AZ_DATATYPE_ID_STRING, "Write"));
		azActionA03.createAzAttribute(APPLICATION,                     // [a38]
				AzXacmlStrings.X_ATTR_ACTION_ID,
				azActionA03.createAzAttributeValue(
						AzDataTypeIdString.AZ_DATATYPE_ID_STRING, "Delete"));
		azActionA04.createAzAttribute(APPLICATION,                     // [a38]
				AzXacmlStrings.X_ATTR_ACTION_ID,
				azActionA04.createAzAttributeValue(
						AzDataTypeIdString.AZ_DATATYPE_ID_STRING, "Read"));
		azActionA05.createAzAttribute(APPLICATION,                     // [a38]
				AzXacmlStrings.X_ATTR_ACTION_ID,
				azActionA05.createAzAttributeValue(
						AzDataTypeIdString.AZ_DATATYPE_ID_STRING, "Write"));
		azActionA06.createAzAttribute(APPLICATION,                     // [a38]
				AzXacmlStrings.X_ATTR_ACTION_ID,
				azActionA06.createAzAttributeValue(
						AzDataTypeIdString.AZ_DATATYPE_ID_STRING, "Read"));

		// Create set of actions
		Set<AzEntity<AzCategoryIdAction>> azActionSet = 
			new HashSet<AzEntity<AzCategoryIdAction>>();
		
		azActionSet.add(azActionA01);   // [a42]
		azActionSet.add(azActionA02);   // [a42]
		azActionSet.add(azActionA03);   // [a42]
		azActionSet.add(azActionA04);   // [a42]
		azActionSet.add(azActionA05);   // [a42]
		azActionSet.add(azActionA06);   // [a42]
		
		azReqCtx.addResourceActionAssociation(
				azResource, azActionSet);   // [a44]
		printAssociations(azReqCtx);

		AzResponseContext azRspCtx = azHandle.decide(azReqCtx); // [a47]
		AzResult azResult = null;
		Iterator<AzResult> itResults = azRspCtx.getResults().iterator();
		System.out.println("TestAzAPI: azRspCtx.hasNext() = " + 
				itResults.hasNext());
		while (itResults.hasNext()) {	 // [a50]
			testCounter++;
			System.out.println("\n\n***********************************" +
					             "\nTestAzAPI: Processing AzResult # " +
					testCounter + "\n***********************************");
			azResult = itResults.next();  // [a52]
			printResultData(azResult);	 // [a54], [a55]
			switch (azResult.getAzDecision()){
				case AZ_PERMIT: 
					System.out.println("  TestAzAPI: processing permit from the azResult");
					printObligations(azResult);
					break;
				case AZ_DENY:
					break;
				case AZ_NOTAPPLICABLE:
					break;
				case AZ_INDETERMINATE:
					switch (azResult.getAzStatusCode()) {
					case AZ_SYNTAX_ERROR:
						break;
					case AZ_PROCESSING_ERROR:
						break;
					case AZ_MISSING_ATTRIBUTE:
						System.out.println(
							"  TestAzAPI: processing missing attributes case from azResult");
						printMissingAttributes(azResult);
						break;
					}
			} // end switch			
		}

		// Test removeAll
		System.out.println("Set 6 before removeAll: " + azActionA06.getAzAttributeSet());
		azActionA06.getAzAttributeSet().removeAll(azActionA06.getAzAttributeSet()); 
		System.out.println("Set 6 after removeAll: " + azActionA06.getAzAttributeSet());

		System.out.println("Set 5 before removeAll: " + azActionA05.getAzAttributeSet());
		System.out.println("Attribute in set 5: " + azActionA05.getAttributeByAttribId(X_ATTR_ACTION_ID).getAttributeId());
		azActionA05.getAzAttributeSet().remove(azActionA05.getAttributeByAttribId(X_ATTR_ACTION_ID));
		System.out.println("Set 5 after removeAll: " + azActionA05.getAzAttributeSet());
			
	} // End of main()
	
	/**
	 * Some print modules to help test:
	 * @param azResult
	 */
	public static void printObligations(AzResult azResult){
		AzObligations azObligations = azResult.getAzObligations();
		if ( !(azObligations == null) ) {
			Iterator<AzEntity<AzCategoryIdObligation>> itOb = 
				azResult.getAzObligations().iterator();
			System.out.println("  TestAzAPI: itOb.hasNext() = " + 
				itOb.hasNext());
			while (itOb.hasNext()){
				AzEntity<AzCategoryIdObligation> azObligation = itOb.next();
				Iterator<AzAttribute<?>> itAttr = 
					azObligation.getAzAttributeMixedSet().iterator();
				System.out.println("  TestAzAPI: itAttr.hasNext() = " +
					itAttr.hasNext());
				while (itAttr.hasNext()){
	                                //itAttr.next();
					printAttributeData(itAttr.next(), 
						"Obligation attribute: ");
				}
			}
		} else {
			System.out.println("No AzObligations element found " +
				" so no obligations to print");
		}
	}
	public static void printMissingAttributes(AzResult azResult){
		Iterator<AzAttribute<?>> itMAD = 
			azResult.getAzStatusDetail().getAzAttributeMixedSet().iterator();
		System.out.println("  TestAzAPI: itMAD.hasNext() = " + 
			itMAD.hasNext());
		while (itMAD.hasNext()){
			AzAttribute<?> azMissingAttributeDetail = itMAD.next();
//			printAttributeData(azMissingAttributeDetail, 
//					"MissingAttributeDetail attribute: ");
		}		
	}
	
	public static void printResultData(AzResult azResult){
		System.out.println(
			"\nTestAzAPI: " +
			"\n\t azResult.getAzResourceActionAssociation.getCorrelationId: " +
					azResult.getAzResourceActionAssociation().getCorrelationId() +
				"\n\t azResult.getAzResourceActionAssociationId: " +
					"\n\t\t " + azResult.getAzResourceActionAssociation() +
				"\n\t azResult.getAzResourceActionAssociation.getAzResource: " +
					"\n\t\t " + azResult.getAzResourceActionAssociation().getAzResource().getAttributeByAttribId(X_ATTR_RESOURCE_ID) +
				"\n\t azResult.getAzResourceActionAssociation.getAzResourceActionAssociationId.getResourceId: " +
					"\n\t\t " + azResult.getAzResourceActionAssociation().getAzResourceActionAssociationId().getResourceId() +
				"\n\t azResult.getAzResourceActionAssociation.getAzAction: " +
					"\n\t\t " + azResult.getAzResourceActionAssociation().getAzAction().getAttributeByAttribId(X_ATTR_ACTION_ID) +
				"\n\t azResult.getAzResourceActionAssociation.getAzResourceActionAssociationId.getActionId: " +
					"\n\t\t " + azResult.getAzResourceActionAssociation().getAzResourceActionAssociationId().getActionId() +
				"\n\t azResult.getAzDecision: " +
					azResult.getAzDecision() +
				"\n\t azResult.getAzStatusCode: " +
					azResult.getAzStatusCode() +
				"\n\t azResult.getStatusMessage: " +
					"\n\t\t " + azResult.getStatusMessage());		
	}
	public static 
		<T extends Enum<T> & AzCategoryId> void printAttributeData(AzAttribute<T> azAttr, String info){
		String azCatIdClass = null;
		if (azAttr.getAzCategoryId() == null)
			azCatIdClass = "Null - no category in this attribute.";
		else
			azCatIdClass = azAttr.getAzCategoryId().getClass().getName();
		AzAttributeValue<?,?> azAttrVal = azAttr.getAzAttributeValue();
		System.out.println(
			"    TestAzAPI.printAttributeData: " + info +
			"\n\t azAttr.getAttributeId: " + azAttr.getAttributeId() +
			"\n\t azAttr.getClass: " + azAttr.getClass() +
			"\n\t azAttr.getCat: " + azAttr.getAzCategoryId() +
			"\n\t azAttr.getAzCategoryId.getClass: " + azCatIdClass +
			"\n\t azAttr.getAttrVal.getType: " + azAttrVal.getType() +
			"\n\t azAttr.getAttrVal.getValue: " + azAttrVal.getValue());
	}
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
