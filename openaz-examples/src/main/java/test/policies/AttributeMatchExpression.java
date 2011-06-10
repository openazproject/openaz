package test.policies;

/*
 * This structure is used to collect attribute expression
 * data. It will probably need to evolve, but currently
 * is intended to support Match elements with Match@MatchId 
 * functions which use a designator and a value, and 
 * Condition functions which use both Condition@FunctionId,
 * which should be placed in the attributeMatchId member,
 * and Apply@FunctionId, which should be placed in the
 * attributeFunctionId member. This structure only supports
 * simple Condition elements, such as those that use
 * a Condition@FunctionId of "...string-equal" and a child
 * Apply@FunctionId that uses "...string-one-and-only on a 
 * designator with an accompanying AttributeValue.
 * <p>
 * The more general structure to support more complex Conditions
 * might be some kind of attribute/function list or tree: tbd
 */
public class AttributeMatchExpression {
	int attributeDesignatorType;	// Subject, Resource, Action, Env
	boolean mustBePresent = false;	// force finder or indeterminate
	String attributeMatchId;		// type of matching function to apply
	String attributeDataType;		// data type of designated attr id
	String attributeValue;			// value to compare the attr against
	String attributeId;				// attr-id of attr to find in request
	String attributeIssuer = null;	// optional issue of the attr
	String attributeFunctionId = null; // id for the Apply element in Conditions
	boolean usingParentLineType=false; // used for noting AND sequence of AMEs
	String designatorEntity = "";	// Subject, Resource, Action, etc.

	public AttributeMatchExpression(
			int attributeDesignatorType,// xacml category: Action,Resource, etc.
    		String attributeMatchId,	// xacml match operation
    		String attributeDataType,	// xacml type being compared
    		String attributeValue,		// value to compare req against
    		String attributeId,			// xacml attr id
    		String attributeIssuer, 	// xacml optional issuer
    		String attributeFunctionId) { // optional Apply fcn for conditions
		this.attributeDesignatorType = attributeDesignatorType;
		this.attributeMatchId = attributeMatchId;
		this.attributeDataType = attributeDataType;
		this.attributeValue = attributeValue;
		this.attributeId = attributeId;
		this.attributeIssuer = attributeIssuer;
		this.attributeFunctionId = attributeFunctionId;
	}
	public AttributeMatchExpression(
			int attributeDesignatorType,// xacml category: Action,Resource, etc.
    		String attributeMatchId,	// xacml match operation
    		String attributeDataType,	// xacml type being compared
    		String attributeValue,		// value to compare req against
    		String attributeId,			// xacml attr id
    		String attributeIssuer, 	// xacml optional issuer
    		String attributeFunctionId, // optional Apply fcn for conditions
			boolean mustBePresent) { // optional xacml MustBePresent attribute
		this.attributeDesignatorType = attributeDesignatorType;
		this.attributeMatchId = attributeMatchId;
		this.attributeDataType = attributeDataType;
		this.attributeValue = attributeValue;
		this.attributeId = attributeId;
		this.attributeIssuer = attributeIssuer;
		this.attributeFunctionId = attributeFunctionId;
		this.mustBePresent = mustBePresent;
	}
	public AttributeMatchExpression(
			int attributeDesignatorType,
    		String attributeMatchId,
    		String attributeDataType,
    		String attributeValue,
    		String attributeId) {
		this.attributeDesignatorType = attributeDesignatorType;
		this.attributeMatchId = attributeMatchId;
		this.attributeDataType = attributeDataType;
		this.attributeValue = attributeValue;
		this.attributeId = attributeId;
	}
	public AttributeMatchExpression(
			int attributeDesignatorType,
    		String attributeMatchId,
    		String attributeDataType,
    		String attributeValue,
    		String attributeId,
    		boolean mustBePresent) {
		this.attributeDesignatorType = attributeDesignatorType;
		this.attributeMatchId = attributeMatchId;
		this.attributeDataType = attributeDataType;
		this.attributeValue = attributeValue;
		this.attributeId = attributeId;
		this.mustBePresent = mustBePresent;
	}
	public int getDesignatorType(){
		return attributeDesignatorType;
	}
	public String getMatchId(){
		return attributeMatchId;
	}
	public String getDataType(){
		return attributeDataType;
	}
	public String getValue(){
		return attributeValue;
	}
	public String getId(){
		return attributeId;
	}
	public String getIssuer(){
		return attributeIssuer;
	}
	public String getFunctionId(){
		return attributeFunctionId;
	}
	public boolean getMustBePresent(){
		return mustBePresent;
	}
	public void setUsingParentLineType(boolean usingParentLineType) {
		this.usingParentLineType = usingParentLineType;
	}
	public boolean getUsingParentLineType() {
		return usingParentLineType;
	}
	public void setDesignatorEntity(String designatorEntity) {
		this.designatorEntity = designatorEntity;
	}
	public String getDesignatorEntity() {
		return designatorEntity;
	}
}
