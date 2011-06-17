package test.policies;

import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

// Effective Java 2nd Edition, item 19: use static import
//  for String constants. Note the ".*" required at end.
import static test.policies.OpenAzPolicyConstants.*;

//line below is expected max printable width
/**********************************************************************************
/**
 * Tokenize an OpenAz Policy Line string
 * 
 * @author rlevinson
 *
 */
public class OpenAzTokens {
	private StringTokenizer st;
	LineType currentLineType = null;

	public enum LineType {INIT,
		PS, TG_PS, 
		PL, TG_PL, 
		RLS, RL, TG_RL, CD_RL,
		OBS, OB, OA,
		TS, TR, TA, TE,
		FINAL};
		
	Log log = LogFactory.getLog(this.getClass());
		
	public OpenAzTokens() {
	}
	
	/**
	 * Return the LineType of the strLine parameter, plus
	 * caller needs to pass in a Map so that the token pairings
	 * that were found can be returned.
	 * 
	 * @param myLineNumber
	 * @param strLine
	 * @param tokenPairings
	 * @param lookingForLineType
	 * @return lineType of strLine
	 */
	public LineType processPolicyLine(
						int myLineNumber, 
						String strLine, 
						Map<String,String> tokenPairings, // for returning tokens
						LineType lookingForLineType){

		if ( (strLine==null)) {
			currentLineType = LineType.FINAL;
			log.trace("Line #: " + myLineNumber +
					" nowInNextState (currentLineType) = " + 
					currentLineType);
		} else {
			// Set the delimiters to begin searching for tokens
			// the following is for:
			//		" ", "\t", "\n", "\r", "\f", "(", and ")"
			//st = new StringTokenizer(strLine," \t\n\r\f()");
			st = new StringTokenizer(strLine," \t\n\r\f()",true);
			currentLineType = null;
			String strToken = null;
			String prevToken = null; // holder for "previous token"
			String attrValue = null;
			boolean firstToken = true;
			boolean startPairing = false;
			boolean attrType = false;
			boolean gettingAttrValue = false;
			boolean usingParentLineType = false;
			String pairId = null;
			String attrId = null;
			
			int tokenNumber = 0;
			while (st.hasMoreTokens()) {
				tokenNumber++;
				prevToken = strToken; // init prevToken each cycle
				
				// if firstToken get the lineType/nextState
				if (firstToken) { // first token only (when currentLineType set)
					// strToken = st.nextToken(" \t\n\r\f");
					// strToken = st.nextToken(" \t\n\r\f:(,"); 
					//  add "(," to catch null cases - didn't work
					strToken = st.nextToken(" \t\n\r\f:");
					if ( (strToken.length() > 1) || 
						 (strToken.equals("+") ) ) { // 1st non-delimiter
						firstToken = false;
						// if strToken.equals("+") getNextState returns null
						currentLineType = getNextState(strToken);
						if ( ! (currentLineType == null) ) {
							switch (currentLineType) {
							case TR:
							case TS:
							case TA:
							case TE:
							case OA:
								attrType = true;
								break;
							default:
								attrType = false;
							}
						} else { // curLineType == null implies "+" token
							attrType = true; // null LineType is continuation
							// Try just using the parent LineType for now, which
							// is what is intended, an "and" with the next
							// attribute Match, which is by defn same category.
							// This may help the problem get resolved, but still
							// ultimately need to keep an indicator. Problem
							// is what if null found for non-attr LineType?
							// i.e. this is not a robust design w/o more analysis
							currentLineType = lookingForLineType; 
							usingParentLineType = true;
						}
						log.trace("Line #: " + myLineNumber +
							", \n\tnowInNextState (currentLineType) = " + 
								currentLineType + 
							" (" + strToken + ")" + " attrType: " + attrType +
							" \n\t\t(lookingForLineType =  " + lookingForLineType + 
							", usingParentLineType = " + usingParentLineType + ")");
						String usingParent = "f"; // default to false
						if (usingParentLineType) {
							usingParent = "t"; // set to true
							usingParentLineType = false; // clear the flag
							log.trace(
								"Line #: " + myLineNumber +
								"  \n\tcontinuation line using parentLineType: " +
								lookingForLineType);
							// this is just moving the hack a little earlier
							tokenPairings.put(
								"LineTypeToken", lookingForLineType.toString());
						} else {
							// put the LineType token in the bag for ref
							tokenPairings.put(
								"LineTypeToken", strToken);
						}
						tokenPairings.put(
							"UsingParentLineType", usingParent);
					}
				} else { // 2nd token and beyond
					if (attrType) {
						// want to get the attr-id and value, then clear the flag
						// need some special delimiter processing to get value
						// which can be in quotes, have other delimiters, etc.
						// the attr-id comes after the "(" and before the ","
						
						// make sure we only go thru here once:
						attrType = false;
						
						// try to get attr-id and attr-value in 2 token pairings:
						strToken = st.nextToken("(,\"");
						tokenNumber++;
						log.trace("Line #: " + myLineNumber + 
								",  Token #: " + tokenNumber + 
								"  Token: \"" + strToken + "\"" +
								" \n\t\tstart attr-id, attr-val processing");
						if ( ! (strToken.equals("(")) ) { 
							// haven't hit the data yet
							prevToken = strToken;
							strToken = st.nextToken("(,\"");
							tokenNumber++;
							log.trace("Line #: " + myLineNumber + 
								",  Token #: " + tokenNumber + 
								"  Token: \"" + strToken + "\"" +
								" \n\t\tlooking for \"(\" ");
						}
						prevToken = strToken;
						strToken = st.nextToken("(,\""); 
						// strToken should now be attr-id
						tokenNumber++;
						log.trace("Line #: " + myLineNumber + 
							",  Token #: " + tokenNumber + 
							"  Token: \"" + strToken + "\"" +
							" \n\t\tshould be attr-id");
						
						prevToken = strToken;
						strToken = st.nextToken("(,\""); 
						// strToken should now be ","
						
						if (strToken.equals(",")) {
							tokenPairings.put(
								OPENAZ_ATTRIBUTE_ID_DESIGNATOR, prevToken);
							gettingAttrValue = true;
						} 
						
						if (gettingAttrValue) {
							prevToken = strToken;
							
							// only try to get left quote
							strToken = st.nextToken("\""); 
							tokenNumber++;
							log.trace("Line #: " + myLineNumber + 
								",  Token #: " + tokenNumber + 
								"  Token: \"" + strToken + "\"" +
								"  \n\t\tlooking for left quote");
							if ( ! strToken.equals("\"") ) {
								// if space between , and quote
								prevToken = strToken;
								strToken = st.nextToken("\""); 
								// should get on 2nd try
								tokenNumber++;
								log.trace("Line #: " + myLineNumber + 
									",  Token #: " + tokenNumber + 
									"  Token: \"" + strToken + "\"");
							}
							if ( ! strToken.equals("\"") ) {
								// if didn't find left quote
								// assume nothing there
								tokenPairings.put(
									OPENAZ_ATTRIBUTE_VALUE_DESIGNATOR, "");
							}
							else { // we have left quote in strToken
								prevToken = strToken;
								strToken = st.nextToken("\""); 
								// should be all up to right quote
								tokenNumber++;
								log.trace("Line #: " + myLineNumber + 
									",  Token #: " + tokenNumber + 
									"  Token: \"" + strToken + "\"" +
									"  \n\t\ttoken after left quote");
								// handle empty value case
								if (strToken.equals("\"")) { // empty value
									tokenPairings.put(
										OPENAZ_ATTRIBUTE_VALUE_DESIGNATOR, "");
								} else if (strToken.startsWith("&lt;")) {
									prevToken = strToken;
									// go to trailing ;
									strToken = st.nextToken(";");
									// make the prevToken the whole expression
									prevToken = prevToken + strToken + ";";
									// now can go to final quote
									strToken = st.nextToken("\""); 
									// should have got it 
									tokenNumber++;
									log.trace("Line #: " + myLineNumber + 
										",  Token #: " + tokenNumber + 
										"  Token: \"" + strToken + "\"" +
										"  \n\t\tshould be right quote");
									tokenPairings.put(
										OPENAZ_ATTRIBUTE_VALUE_DESIGNATOR, 
											prevToken);
								}
								else { // one more read to get the right quote
									prevToken = strToken;
									strToken = st.nextToken("\""); 
									// should have got it 
									tokenNumber++;
									log.trace("Line #: " + myLineNumber + 
										",  Token #: " + tokenNumber + 
										"  Token: \"" + strToken + "\"" +
										"  \n\t\tshould be right quote");
									tokenPairings.put(
										OPENAZ_ATTRIBUTE_VALUE_DESIGNATOR, 
											prevToken);
								}
							}
						}
					} else { // beyond attrType case
						//strToken = st.nextToken(" \t\n\r\f()");						
						strToken = st.nextToken(" \t\n\r\f():");
						if (startPairing) {
							startPairing = false; // clear condition
							tokenPairings.put(
								pairId, strToken);
						}
						if (strToken.equals(":")) {
							startPairing = true;
							pairId = prevToken;
						}
					}
				}
				log.trace("Line #: " + myLineNumber +
						",  Token #: " + tokenNumber +
						"  Token: \"" + strToken + "\"");
			} // end while(moreTokens)
		} // end if ( (strLine==null)) else ...
		return currentLineType;		
	}
	
	/**
	 * Maps a String to a LineType enum
	 * <p>
	 * TODO: check if string should be built in to the 
	 * LineType enums. Still need to do comparison, but
	 * the binding would be tighter. However, it is
	 * probably more flexible as an external statically
	 * import set of String constants
	 * @param strToken
	 * @return a LineType if match found, ow null
	 */
    public static LineType getNextState(String strToken) {
    	LineType nextState = null;
		if (strToken.equals(AZ_POLICYSET))
			nextState = LineType.PS;
		else if (strToken.equals(AZ_TARGET_POLICYSET))
			nextState = LineType.TG_PS;
		else if (strToken.equals(AZ_POLICY))
			nextState = LineType.PL;
		else if (strToken.equals(AZ_TARGET_POLICY))
			nextState = LineType.TG_PL;
		else if (strToken.equals(AZ_RULE))
			nextState = LineType.RL;
		else if (strToken.equals(AZ_TARGET_RULE))
			nextState = LineType.TG_RL;
		else if (strToken.equals(AZ_RULES))
			nextState = LineType.RLS;
		else if (strToken.equals(AZ_CONDITION_RULE))
			nextState = LineType.CD_RL;
		else if (strToken.equals(AZ_OBLIGATIONS_POLICY))
			nextState = LineType.OBS;
		else if (strToken.equals(AZ_OBLIGATION))
			nextState = LineType.OB;
		else if (strToken.equals(AZ_OBLIGATION_ATTRIBUTE))
			nextState = LineType.OA;
		else if (strToken.equals(AZ_TARGET_SUBJECT))
			nextState = LineType.TS;
		else if (strToken.equals(AZ_TARGET_RESOURCE))
			nextState = LineType.TR;
		else if (strToken.equals(AZ_TARGET_ACTION))
			nextState = LineType.TA;
		else if (strToken.equals(AZ_TARGET_ENVIRONMENT))
			nextState = LineType.TE;
    	return nextState;
    }
}
