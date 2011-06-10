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
package org.openliberty.openaz.azapi.constants;

/**
 * This enumeration defines the identifiers for the DataTypes that are used 
 * in the XACML specification:
 * <br>
 * <b>
 * http://docs.oasis-open.org/xacml/2.0/access_control-xacml-2.0-core-spec-os.pdf 
 * </b>
 * <br>
 * See Appendix A.2 ("Data-types") of the XACML 2.0 specification for 
 * the source of these DataType definitions and subsequent sections 
 * of the XACML 2.0 specification for detail on the semantics of these
 * data types.
 * <p>
 * In addition, section 6.7 of the XACML 2.0 Specification states that
 * the DataType xml attribute is required for each Attribute with the
 * following explanation of restrictions:
 * <pre>
 *   DataType [Required]
 *     The data-type of the contents of the <xacml-context:AttributeValue>
 *     element. This SHALL be either
 *        - a primitive type defined by the XACML 2.0 specification or
 *        - a type (primitive or structured) defined in a namespace
 *          declared in the <xacml-context> element.
 * </pre>
 * See "All known implementing classes" above for details on specific values
 * of this enum.
 * 
 * @author Rich
 *
 */
public interface AzDataTypeId {

}
