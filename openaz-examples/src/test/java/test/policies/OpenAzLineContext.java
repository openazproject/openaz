package test.policies;

import java.util.Map;
import test.policies.OpenAzTokens.LineType;

public class OpenAzLineContext {
	int currentLineNumber;
	LineType currentLineType;
	String currentLine;
	Map<String,String> tokens;
	int parentLineNumber;
	LineType parentLineType;
	LineType lookingForLineType;
	boolean readAhead;
	String readAheadLine;
	String stateDescriptor;
	String message; // message for exceptions
	/**
	 * the following are the mLevels for managing
	 * the hierarchy; only one policySet may be
	 * open at a given time during the build
	 */
	// only PolicySets have their own mLevel
	int mLevel = -1; // must be zero or greater for valid LineContext
	// all other elements, and PolicySets have a parentMLevel
	int parentMLevel = -1;
	/**
	 * Simple context for users to kick of processing,
	 * typically w 0, LineType.INIT, could even give
	 * argumentless constructor for this.
	 * @param currentLineNumber
	 * @param currentLineType
	 */
	public OpenAzLineContext(
			int currentLineNumber,
			LineType currentLineType){
		//new OpenAzLineContext(
				this(currentLineNumber,
				currentLineType,
				null,
				null,
				0,
				null,
				null,
				false,
				null,
				null);
	}
	/**
	 * Typically this context will be generated starting
	 * with a previous context; Typically, one would 
	 * take the previousLineContext.getCurrentLineType
	 * and line Number and set those as the parentType
	 * and LineNumber. Then set current to current values.
	 * Maybe it needs a done flag to make read only
	 * at completion.
	 * @param currentLineNumber
	 * @param currentLineType
	 * @param currentLine
	 * @param tokens
	 * @param parentLineNumber
	 * @param parentLineType
	 * @param lookingForLineType
	 * @param readAhead
	 * @param readAheadLine
	 * @param message
	 */
	public OpenAzLineContext(
			int currentLineNumber,
			LineType currentLineType,
			String currentLine,
			Map<String,String> tokens,
			int parentLineNumber,
			LineType parentLineType,
			LineType lookingForLineType,
			boolean readAhead,
			String readAheadLine,
			String message){
		this.currentLineNumber = currentLineNumber;
		this.currentLineType = currentLineType;
		this.currentLine = currentLine;
		this.tokens = tokens;
		this.parentLineNumber = parentLineNumber;
		this.parentLineType = parentLineType;
		this.lookingForLineType = lookingForLineType;
		this.readAhead = readAhead;
		this.readAheadLine = readAheadLine;
		this.message = message;
	}
	public int getLineNumber() {
		return currentLineNumber;
	}
	public LineType getLineType() {
		return currentLineType;
	}
	public String getLine() {
		return currentLine;
	}
	public Map<String,String> getTokens() {
		return tokens;
	}
	public int getParentLineNumber() {
		return parentLineNumber;
	}
	public LineType getParentLineType() {
		return parentLineType;
	}
	public LineType getLookingForLineType() {
		return lookingForLineType;
	}
	public boolean getReadAhead() {
		return readAhead;
	}
	public String getReadAheadLine() {
		return readAheadLine;
	}
	public void setLine(String currentLine) {
		this.currentLine = currentLine;
	}
	public void setTokens(Map<String,String> tokens){
		this.tokens = tokens;
	}
	public void setParentLineNumber(int parentLineNumber){
		this.parentLineNumber = parentLineNumber;
	}
	public void setParentLineType(LineType parentLineType) {
		this.parentLineType = parentLineType;
	}
	public void setLookingForLineType(LineType lookingForLineType){
		this.lookingForLineType = lookingForLineType;
	}
	public void setReadAhead(boolean readAhead) {
		this.readAhead = readAhead;
	}
	public void setReadAheadLine(String readAheadLine) {
		this.readAheadLine = readAheadLine;
	}
	public String getMessage(){
		return message;
	}
	public void setStateDescriptor(String stateDescriptor) {
		this.stateDescriptor = stateDescriptor;
	}
	public String getStateDescriptor() {
		return stateDescriptor;
	}
	public void setMLevel(int mLevel) {
		this.mLevel = mLevel;
	}
	public int getMLevel(){
		return mLevel;
	}
	public void setParentMLevel(int parentMLevel) {
		this.parentMLevel = parentMLevel;
	}
	public int getParentMLevel() {
		return parentMLevel;
	}
}
