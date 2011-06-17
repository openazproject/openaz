package test.policies;
import test.policies.OpenAzTokens.LineType;
/**
 * An object and a line type, where the object will be
 * a SunXacml object that may be used in constructing a
 * PolicySet, that will typically correspond to
 * the first token in the line specification, such as:
 * Policy, Rule, Condition, Target, Obligation, etc.
 * @author rlevinson
 *
 */
public class OpenAzXacmlObject {
	private Object object;
	private LineType lineType;
	private boolean readAhead = false;
	private Object readAheadObject;
	public OpenAzXacmlObject(
			LineType lineType, 
			Object object){
		this.object = object;
		this.lineType = lineType;
	}
	public OpenAzXacmlObject(
			LineType lineType, 
			Object object, 
			boolean readAhead,
			Object readAheadObject){
		this.object = object;
		this.lineType = lineType;
		this.readAhead = readAhead;
		this.readAheadObject = readAheadObject;
	}
	public LineType getLineType() {
		return lineType;
	}
	public Object getObject() {
		return object;
	}
	public boolean getReadAhead() {
		return readAhead;
	}
	public Object getReadAheadObject() {
		return readAheadObject;
	}
	public void setReadAhead(boolean readAhead){
		this.readAhead = readAhead;
	}
	public void setReadAheadObject(Object readAheadObject) {
		this.readAheadObject = readAheadObject;
	}

}
