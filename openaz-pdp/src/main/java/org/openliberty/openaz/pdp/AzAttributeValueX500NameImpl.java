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

import javax.security.auth.x500.X500Principal;
//import org.openliberty.openaz.azapi.constants.AzDataType;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openliberty.openaz.azapi.*;
import org.openliberty.openaz.azapi.constants.*;

public class AzAttributeValueX500NameImpl
	extends AzAttributeValueImpl<AzDataTypeIdX500Name, X500Principal> 
	implements AzAttributeValueX500Name {
	X500Principal x500Principal = null;
	Log log = LogFactory.getLog(this.getClass()); 
	public AzAttributeValueX500NameImpl(X500Principal p){
		super(AzDataTypeIdX500Name.AZ_DATATYPE_ID_X500NAME, p);
		this.x500Principal = p;
		if (log.isTraceEnabled()) log.trace(
			"TestAzAttributeValueX500Name: X500Name Created = " + value);
	}
	public AzDataTypeIdX500Name getType(){
		return AzDataTypeIdX500Name.AZ_DATATYPE_ID_X500NAME;
	}
	public void setValue(X500Principal p){
		this.x500Principal = p;
	}
	public X500Principal getValue() {
		return x500Principal;
	}
	public String toXacmlString() {
		if ( ! ( x500Principal == null ) )
			return x500Principal.getName();
		else
			return "";
	}
}
