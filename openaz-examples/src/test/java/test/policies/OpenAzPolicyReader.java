package test.policies;

/**
 * 		Index of modules:
 * 
 * 			void			    main(args)
 * 			OpenAzXacmlObject   readAzLine(parentLineCtx) 
 * 			OpenAzXacmlObject   processInit(curLineCtx)
 * 			OpenAzXacmlObject   processPS(curLineCtx)
 * 			OpenAzXacmlObject   processPL(curLineCtx)
 * 			OpenAzXacmlObject   processRL(curLineCtx)
 * 			OpenAzXacmlObject   processRLS(curLineCtx)
 * 			OpenAzXacmlObject   processOBS(curLineCtx)
 * 			OpenAzXacmlObject   processOB(curLineCtx)
 * 			OpenAzXacmlObject   processTGPS(curLineCtx)
 * 			OpenAzXacmlObject   processTGPL(curLineCtx)
 * 			OpenAzXacmlObject   processTGRL(curLineCtx)
 * 			OpenAzXacmlObject   processCDRL(curLineCtx)
 * 			OpenAzXacmlObject   processTS(curLineCtx)
 * 			OpenAzXacmlObject   processTemplate(curLineCtx)
 * 			OpenAzXacmlObject   processCondition(curLineCtx)
 * 			OpenAzXacmlObject   processObligation(curLineCtx)
 * 			OpenAzXacmlObject   processObligations(curLineCtx)
 * 			OpenAzXacmlObject   processRuleList(curLineCtx)
 * 			OpenAzXacmlObject   processTargetPolicy(curLineCtx)
 * 			AME[][] 			moveData(curLineCtx, arraySize, ameList)
 * 			List<Integer>		calculateArraySize(curLineCtx, attrsList)
 * 			OpenAzXacmlObject   processRule(curLineCtx)
 * 			OpenAzXacmlObject   processPolicy(curLineCtx)
 * 			OpenAzXacmlObject   processPolicySet(curLineCtx)
 * 			OpenAzXacmlObject   processPS(curLineCtx)
 * 			OpenAzXacmlObject   processPS(curLineCtx)
 */
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.FileReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URI;
import java.net.URISyntaxException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.sun.xacml.Indenter;
import com.sun.xacml.Obligation;
import com.sun.xacml.Policy;
import com.sun.xacml.PolicySet;
import com.sun.xacml.Rule;
import com.sun.xacml.Target;
import com.sun.xacml.UnknownIdentifierException;
import com.sun.xacml.attr.AttributeDesignator;
import com.sun.xacml.combine.CombiningAlgFactory;
import com.sun.xacml.combine.OrderedPermitOverridesPolicyAlg;
import com.sun.xacml.combine.OrderedPermitOverridesRuleAlg;
import com.sun.xacml.combine.PolicyCombiningAlgorithm;
import com.sun.xacml.combine.RuleCombiningAlgorithm;
import com.sun.xacml.cond.Apply;
import com.sun.xacml.ctx.Attribute;
import com.sun.xacml.ctx.Result;

import test.policies.OpenAzParseException;
import test.policies.OpenAzXacmlObject;

import test.policies.OpenAzTokens.LineType;

//Effective Java 2nd Edition, item 19: use static import
//for String constants. Note the ".*" required at end.
import static test.policies.OpenAzPolicyConstants.*;

// line below is expected max printable width
/**********************************************************************************
 * This class reads a text file written in OpenAz Xacml shorthand
 * format and creates a corresponding XACML Policy using SunXacml
 * @author rlevinson
 *
 */
public class OpenAzPolicyReader {

	// get tokens above from OpenAzPolicyConstants 

    private static int lineNumber = 0;
	private BufferedReader br = null;
	private OpenAzTokens openAzTokens = null;
	private static XacmlPolicyBuilder xpb = 
						new XacmlPolicyBuilder(true,false,true,true);
 
    public static final String 
		XPATH_1_0_VERSION =
			"http://www.w3.org/TR/1999/Rec-xpath-19991116";
    public String currentState = "";
 	static StringWriter swStatic = new StringWriter();

	Log log = LogFactory.getLog(this.getClass()); 
	static Log logStatic = LogFactory.getLog(
					OpenAzPolicyReader.class);
	 
	/**
	 * Constructor that generates a PolicySet from the
	 * file containing the XACML Shorthand policy description.
	 * @param pseudoPolicyFile
	 */
    public OpenAzPolicyReader(String pseudoPolicyFile) {
		// Read the file into a BufferedReader that will return
		// a full line in a single String
    	openAzTokens = new OpenAzTokens();
		try {
			br = new BufferedReader(new FileReader(pseudoPolicyFile));
		} catch (FileNotFoundException fnf) {
			log.trace(
				"FileNotFoundException: " + fnf.getMessage());
		}
    }
    
	public static void main(String[] args){
		logStatic.info("args[] = " + Arrays.toString(args));
		String pseudoPolicyFile = null;
		if (args.length > 0) {
			pseudoPolicyFile = args[0];
		}
		else 
			logStatic.trace("args.length = 0; no parameters");
		//pseudoPolicyFile = "xxxyyy";
		logStatic.trace("pseudoPolicyFile: " + pseudoPolicyFile);
		
		OpenAzPolicyReader openAzPolicyReader = 
			new OpenAzPolicyReader(pseudoPolicyFile);
		
		try {
			int myLineNumber = 0;
			LineType currentLineType = LineType.INIT;
			//LineType parentLineType = null;
			//LineType lookingForLineType = null;
			//boolean readAhead = false;
			//String readAheadLine = null;
			OpenAzLineContext openAzLineContext =
				new OpenAzLineContext(myLineNumber,
						currentLineType);
			logStatic.trace(
				"TRACE: ************************************");
			logStatic.trace(
				"TRACE: main CALLING into readAzLine() using" +
				"\n\t\t\tcurrentLineNumber = " + myLineNumber);
			
			// Main call to produce the sunxacml policy
			OpenAzXacmlObject openAzXacmlObject = 
				openAzPolicyReader.readAzLine(openAzLineContext);
			
			logStatic.trace(
				"TRACE: RETURNing to main from readAzLine(" +
				"\n\t\t\tcurrentLineNumber = " + 
					openAzLineContext.getLineNumber() +
				"\n\t\t\tcurrentLineType = " + 
					openAzLineContext.getLineType() +
				"\n\t\t\tlookingForType = " + 
					openAzLineContext.getLookingForLineType() +
				"\n\treturned openAzXacmlObject = " + 
					openAzXacmlObject +
				"\n\t\t\treturned readAhead flag = " + 
					openAzXacmlObject.getReadAhead());
			if ( ! ( openAzXacmlObject == null ) ) {
				LineType parsedObjectLineType = 
					openAzXacmlObject.getLineType();
				Object parsedObject = 
					openAzXacmlObject.getObject();
				logStatic.trace(
					"TRACE: openAzXacmlObject.getLineType() = " + 
					parsedObjectLineType + 
					"\n\tclass = " + 
					parsedObject.getClass().getName());
				
				// write the sunxacml policy or policyset to xml xacml
				switch (parsedObjectLineType) {
				case PS:
					// most typical and general case
					PolicySet ps = (PolicySet) parsedObject;
					ps.encode(System.out, new Indenter());
					break;
				case PL:
					// Single policy returned
					Policy pl = (Policy) parsedObject;
					pl.encode(System.out, new Indenter());
					break;
					default:
					logStatic.trace(
						"TRACE: unexpected parse object returned: " +
						parsedObject.getClass().getName());
				}
				
			} else {
				logStatic.trace(
					"TRACE: null object returned to main. Check for errors.");
			}
		} catch (OpenAzParseException ope) {
			logStatic.trace(
				"OpenAzParseException at lineNumber: " + 
					ope.getErrorOffset() +
				" message: " + ope.getMessage());
		}
		logStatic.info("DONE");
	}
    	
	/**
	 * Main module for parsing: readAzLine is called, the caller
	 * provides the current line number.
	 * <p>
	 * Caller passes in its own lineNumber and LineType, plus
	 * an optional specific requested LineType to look for,
	 * plus an optional readAhead flag, and readAhead data for
	 * cases where next line has already been read, which will
	 * be stored and provided to the caller to use as input.
	 * <p>
	 * Conceptual aid to understanding this code:
	 * One can think of the "currentLine" as being a line of text from 
	 * the policy source that has been read in right after readAzLine 
	 * was called and is currently being processed by readAzLine and
	 * other methods used by readAzLine.
	 * When all the processing that can be done with "currentLine" has 
	 * been done before needing to read additional lines, then readAzLine
	 * may call itself recursively, using the currentLine number and
	 * type as parameters.
	 * <p>
	 * One may think of currentLineNumber (or myLineNumber) as being
	 * a "unique identifier" of a specific line in the policy source
	 * (where the source can be a file or some other functional
	 * equivalent). Each line in the policy is considered distinct and
	 * identifiable by its line number.
	 * <p>
	 * So, one can think of "currentLine" as orchestrating the things
	 * that are to follow. When "currentLine" calls readAzLine, a new
	 * "currentLine" emerges with a new line number. So, when 
	 * "currentLine" calls readAzLine it is turning control over to 
	 * a new "currentLine", but since this is code the original
	 * "currentLine" lives on in the call stack and will be revisited 
	 * when its call to readAzLine is complete and has returned the
	 * requested information, which is usually an Object that can
	 * be part of a sunxacml Policy structure.
	 * <p>
	 * In some cases "currentLine" may call readAzLine several times.
	 * The "nature" of "currentLine" depends on currentLine's LineType. 
	 * So, if currentLine is a Policy, it will need to get a Target, 
	 * some Rules, and possibly some Obligations before it can consider
	 * itself "whole" and ready to return. When it calls readAzLine, 
	 * what is returned is an object that it should know what to do
	 * with and be prepared to attempt to cast the object to the
	 * expected sunxacml object type. This returned object is an 
	 * OpenAzPolicyObject. It can contain, for example, a real "Target" 
	 * object from sunxacml, which was assembled by the various 
	 * currentLines that were activated by the readAzLines and
	 * descendants thereof that were used to complete the object.
	 * <p>
	 * <pre>
	 * So, when calling readAzLine, you need to provide the following:
	 *   - line number of currentLine, which the callee regards as its parent
	 *   - LineType of currentLine, which the callee regards as context
	 *   - if currentLine is looking for specific LineType, it must 
	 *     specify that LineType.
	 *   - a flag to indicate whether currentLine is providing the next line
	 *     to be "read", in which case, readAzLine will use that instead
	 *     of reading from the source (this is used when reading a
	 *     new line discovers that whatever has been going on is now
	 *     complete, because a new context is beginning, such as a new
	 *     Rule or whatever, which implicitly means the prev Rule is
	 *     done. But the prev Rule didn't know that until now, so this
	 *     is the mechanism for reading the change and not skipping
	 *     over it.
	 *   - the line, itself that the callee will use instead of
	 *     reading from the source
	 * </pre>
	 * Note: when readAzLine is called as in several modules below,
	 * typically the process<sunXacmlObject> methods, such as 
	 * processCondition, processPolicy, etc., the parameter that
	 * is passed is "currentLineContext". That exact same parameter
	 * is the parameter that is supplied in the method below that
	 * is named "parentLineContext". The point is that within this
	 * invocation, a new "currentLineContext" is created, and while
	 * it may take some values to init from the parentLineContext,
	 * one may consider the new currentLineContext to be the active
	 * context for all modules that are invoked directly or indirectly
	 * by readAzLine.
	 * 
	 * @param parentLineContext a ref the parentLineContext
	 * caller wants the LineType read from the source to be
	 * @return an OpenAzXacmlObject containing any SunXacml object
	 * @throws OpenAzParseException
	 */
    public OpenAzXacmlObject readAzLine(
    			OpenAzLineContext parentLineContext)
    		throws OpenAzParseException {
    	
    	// Preliminary setup of context for the processing of the
    	// "current line", which is about to be read:
    	
    	// Parent Context data: (Note: "parent" is from callee perspective
    	// and refers to the context data from the previous line which
    	// is passed in as parameter to readAzLine
		int parentLineNumber = parentLineContext.getLineNumber();
		LineType parentLineType = parentLineContext.getLineType();
		LineType lookingForLineType = parentLineContext.getLookingForLineType();
		boolean readAhead = parentLineContext.getReadAhead();
		String readAheadLine = parentLineContext.getReadAheadLine();
		int grandParentMLevel = parentLineContext.getParentMLevel();
		int parentMLevel = parentLineContext.getMLevel();
		
		// readAzLine data:
		OpenAzLineContext curLineCtx = null;
    	OpenAzXacmlObject resultObject = null;
    	//Object object = null; // Object to return whatever is found
		LineType currentLineType = null;
    	int myLineNumber;
    	String msg = ""; // use to build error messages
		String strLine = "";
		//Object tokens = null; // object to hold parsed tokens once defined.
		//String strToken = null;
		//StringTokenizer st = null;
		String raTag = "";
				
		// only increment lineNumber for real reads:
		if (readAhead) {
			// if using same line as last pass thru use same line #
			myLineNumber = lineNumber;
			raTag = " (ra)";
		}
		else
			// this is a new line so increment the line number
			myLineNumber = ++lineNumber;
		
		log.trace(
		    "\nTRACE: **************************************" + 
		    "**************************" + 
			"\n\t    readAzLine: BEGIN processing:   Line #: " + 
			lineNumber + "  ENTER" + raTag +
			"\nTRACE: **************************************" +
			"**************************");

		// check readAhead from parent context to read a new line
		// or to process the previous line which was purposely not
		// processed by the previous call and stored as "readAhead"
		// for the "next call" which is where we are now.
		try {
			if (readAhead) {
				// use the line from the readAhead data
				strLine = readAheadLine;
				log.trace("Using readAheadLine for strLine");
			}
			else{
				boolean assumeLineWillBeAComment = true; // assume will be comment
				while (assumeLineWillBeAComment) {
					// get a new line from the policy source
					strLine = br.readLine();
					log.trace("Using br.readLine() for strLine");
					if ( ! (strLine == null) ) { 
						// say it's not a comment
						boolean itsNotAComment = true;
						String trimStrLine = strLine.trim();
						String reason = "";
						if (trimStrLine.startsWith("//")) {
							itsNotAComment = false; // It is a comment!
							reason = "starts with //";
						}
						if (trimStrLine.isEmpty()) {
							itsNotAComment = false; // It is a comment!
							reason = "string is empty or only white space";
						}
						if (itsNotAComment) {
							// not a comment breaks the loop
							assumeLineWillBeAComment = false;
						} else {
						    // trace the line and skip it
						    log.trace("skip comment line( " + reason + 
						    		  "):\n" + strLine + "\n");						
						}
					} else {
						// null strLine means EOF, not a comment, so break loop
						assumeLineWillBeAComment = false; 
					}
				}
			}
			log.trace("\n" + strLine + "\n");
						
			// If line is null this means end of policy source, so
			// the currentLineType effectively becomes LineType.FINAL
			// which is put in the resultObject to be returned below
			if ( strLine == null) {
				resultObject = new OpenAzXacmlObject(LineType.FINAL, null);
				log.trace(
					"TRACE: strLine==null implies end of input stream " +
					" currentLineState will be set as FINAL" +
					"\nTBD: make sure all policies and policysets " + 
					"get finished when this occurs - probably need FINAL state " +
					"to be handled at SL2 in processPS and processPL");
				//return resultObject; // do this below, not here
			}
			
			log.trace("TRACE: Parent (caller) OpenAzLineContext data:(" +
				"\n\tparentLineNumber(parentLineContext.getLineNumber()) = " + 
					parentLineNumber +
				",\n\tparentLineType = " + parentLineType +
				",\n\tlookingForLineType = " + lookingForLineType +
				",\n\tparentMLevel(parentLineContext.getMLevel()) = " + 
					parentMLevel +
				",\n\treadAhead = " + readAhead);

			// Provide a bag to processPolicyLine to return the tokens:
	        HashMap<String,String> tokenPairings = new HashMap<String,String>();           
			// Read the next line and parse the tokens
			currentLineType = 
				openAzTokens.processPolicyLine(
					myLineNumber, strLine, tokenPairings, lookingForLineType);
			
			log.trace("TRACE: tokenPairings = " + tokenPairings);
			log.trace("TRACE: ***********************************");
			
			// global member variable: just set it here but use anywhere
			currentState = "*** " + parentLineType + 
							" -> " + currentLineType + " ***";
			log.trace(
					"TRACE: Now processing state: " + currentState + "\n");
			
			// TODO: right now "continuation" attributeMatchExpression
			// lines (AND) return null currentLineType; could auto-set to
			// prev linetype here, which is sort of what we want except
			// need to convey that fact w/o obscuring the AND; probably
			// need some kind of AND flag to pass along. For now, just
			// set it to see what happens
			
			if (currentLineType == null ) {
				currentLineType = lookingForLineType;
				log.trace("TRACE: NEED FIX HERE, but FYI: " +
					"currentLineType was null, so set to lookingForLineType: " +
					lookingForLineType);
				currentState = "*** " + parentLineType + 
					" -> " + currentLineType + " ***";
				log.trace(
					"TRACE: Now processing state: " + currentState + "\n");
			}
			
			// We now have the parsed line, and are ready to process it:
			//   1. The new line must be "acceptable" to the parent, 
			//      so switch on the parent to provide a parent-oriented
			//      context for processing
			log.trace(
				"TRACE: (prev line: #" + parentLineNumber + 
					") parentLineType:     " + parentLineType);
			log.trace(
				"TRACE: (this line: #" + myLineNumber + 
					") currentLineType:    " + currentLineType);
			log.trace(
				"TRACE: (this line: #" + lineNumber + ") lookingForLineType: " + 
					lookingForLineType + 
				"  (optional, caller constraint to apply to currentLineType)");
				//"  (optional, but required if parent requests)");
			if (parentLineNumber == 0)
				log.trace(
					"TRACE: (Note: \"lookingForLineType\" means " + 
					"LineType that parent line is looking for, " + 
					"\nso current=lookingFor is generally good.)" );
			
			// set a parsing error message for general use with values
			// filled in for current line context
			msg =
				"OpenAz Parsing error: " +
				"\n\t\tParent LineType: " + parentLineType +
				"\n\t\t  current (Child) LineType found: " + currentLineType +
				"\n\t\t  looking for LineType: " + lookingForLineType +
				"\n\t child found incompatible w parent looking for type.";
			
			log.info("Input: Line #: " + myLineNumber + "\n" + strLine);
			// set the OpenAzLineContext to begin processing the current 
			// line:
			curLineCtx =
				new OpenAzLineContext(
					myLineNumber,		// line number currently being processed
					currentLineType,	// line type currently being processed
					strLine,			// text of line currently being processed
					tokenPairings,		// parsed tokens from line
					parentLineNumber,
					parentLineType,
					lookingForLineType, // keep around, can be reset
					false,				// new readAhead, may be reset later
					null,				// new readAheadLine
					msg);
			
			String stateDescriptor = parentLineType + ":" + currentLineType;
			curLineCtx.setStateDescriptor(stateDescriptor);
			curLineCtx.setParentMLevel(parentMLevel);
			if (currentLineType == LineType.PS) {
				// need to set mLevel to the token from the PS line
				String mLevelStr = 
					curLineCtx.getTokens().get(OPENAZ_POLICY_SET_M_LEVEL);
				log.trace("TRACE: mlevel token = " + mLevelStr +
						"\n\tparentMLevel = " + parentMLevel);
				if (mLevelStr == null) {
					 // default to level +1 above current parent
					mLevelStr = new Integer(parentMLevel + 1).toString();
				}
				Integer mLevelInt = new Integer(mLevelStr);
				curLineCtx.setMLevel(mLevelInt.intValue());
			} else {
				// everyone else just inherits their parent's mLevel
				curLineCtx.setMLevel(parentMLevel);
			}
			// 1st level switch is based on the line type of the preceding 
			// record: for example, if the preceding record was Policy,
			// this record must be a Target.
			
			// Note: the terms "parent line" and "previous line" are used
			// interchangeably. Often the structure in the prev line is,
			// in fact, the parent of the structure on the current line,
			// but this is not always the case. It may be a peer as well,
			// and in the case of PolicySets it can be an uncle or cousin.
			// However, it always is the "previous" line, and it always
			// sets the "context" for the current line.
			
			// SL1: switch level 1 (line type of the parent of current record)
			//switch (parentLineType) {	
			switch (curLineCtx.getParentLineType()) {	
			
			// SL1-INIT: switch level 1: INIT
			case INIT: // Parent is the Root of the tree
				log.trace(
					"TRACE: SL1-INIT: readAzLine CALLING processINIT");
				resultObject = processINIT(curLineCtx);
				break; // INIT switch level 1	
				
			// SL1-PS:  PolicySet parent
			case PS: // Parent (prev line) is a PolicySet
				log.trace(
					"TRACE: SL1-PS: readAzLine CALLING processPS");
				resultObject = processPS(curLineCtx);
				log.trace(
					"TRACE: SL1-PS: processPS RETURNING to readAzLine");
				break; // end SL1-PS	
				
			// SL1-PL:  Policy parent
			case PL: // Parent line is a Policy
				log.trace(
					"TRACE: SL1-PL: readAzLine CALLING processPL");
				resultObject = processPL(curLineCtx);
				//currentLineType, lookingForLineType,
				//strLine, myLineNumber);
				log.trace(
					"TRACE: SL1-PL: processPL RETURNING to readAzLine");
				break; // end SL1-PL	
				
			// SL1-RL:  Rule parent
			case RL: // Parent line is a Rule
				log.trace(
					"TRACE: SL1-RL: readAzLine CALLING processRL");
				resultObject = processRL(curLineCtx);
				log.trace(
					"TRACE: SL1-RL: processRL RETURNED to readAzLine");
				break; // end SL1-RL	
				
			// SL1-TG_PS: (parent line, i.e. prev line is TG_PS
			case TG_PS: // parent of current record is Target child of PS
				log.trace(
						"TRACE: SL1-TG_PS: readAzLine CALLING processTGPS");
				resultObject = processTGPS(curLineCtx);
				break; // end SL1-TG_PS
				
			// SL1-TG_PL: (parent, i.e. prev is TG_PL
			case TG_PL: 
				// basically if TG_PL is the parent then we are
				// effectively looking for AttributeMatch elements,
				// i.e. the TS, TR, TA elements should come next
				log.trace(
					"TRACE: SL1-TG_PL: readAzLine CALLING processTGPL");
				resultObject = processTGPL(curLineCtx);
				log.trace(
				"TRACE: SL1-TG_PL: processTGPL RETURNED to readAzLine");
				break; // end SL1-TG_PL
				
			// SL1-TG_RL: (parent, i.e. prev is TG_RL
			case TG_RL: 
				// basically if TG_RL is the parent then we are
				// effectively looking for AttributeMatch elements,
				// i.e. the TS, TR, TA elements should come next
				log.trace(
					"TRACE: SL1-TG_RL: readAzLine CALLING processTGRL");
				resultObject = processTGRL(curLineCtx);
				break; // end SL1-TG_RL
				
			// SL1-CD_RL: (parent, i.e. prev is CD_RL
			case CD_RL: 
				// basically if CD_RL is the parent then we are
				// going to need attribute match elements similar
				// to those found in Targets. For now we are just
				// doing simple conditions that are basically the
				// same tests as the Target matches.
				log.trace(
					"TRACE: SL1-CD_RL: readAzLine CALLING processCDRL");
				resultObject = processCDRL(curLineCtx);
				break; // end SL1-CD_RL
				
			// SL1-TS: (parent, i.e. prev is TS (a SubjectMatch in Target)
			case TS: 
				log.trace(
					"TRACE: SL1-TS: readAzLine CALLING processTS");
				resultObject = processTS(curLineCtx);
				break; // end SL1-TS
				
			// SL1-RLS: (parent, i.e. prev is an RLS (RuleList)
			case RLS:
				log.trace(
						"TRACE: SL1-RLS: readAzLine CALLING processRLS");
				// Note: RuleList is a sunxacml construct for convenience))
				lookingForLineType = LineType.RL; // RuleList needs Rules
				curLineCtx.setLookingForLineType(lookingForLineType);
				resultObject = processRLS(curLineCtx);
				log.trace(
					"TRACE: SL1-RLS: processRLS RETURNED to readAzLine");
				break;
				
			case OBS:
				log.trace(
						"TRACE: SL1-OBS: readAzLine CALLING processOBS");
				// Note: RuleList is a sunxacml construct for convenience))
				lookingForLineType = LineType.OB; // ObsList needs individual Ob's
				curLineCtx.setLookingForLineType(lookingForLineType);
				resultObject = processOBS(curLineCtx);
				log.trace(
					"TRACE: SL1-OBS: processOBS RETURNED to readAzLine");
				break;
				
			case OB:
				log.trace("TRACE: SL1-OB: readAzLine CALLING processOB");
				// Note: RuleList is a sunxacml construct for convenience))
				lookingForLineType = LineType.OA; // ObsList needs individual Ob's
				curLineCtx.setLookingForLineType(lookingForLineType);
				resultObject = processOB(curLineCtx);
				break;
				
			case FINAL:
				break;
					
			// SL1-default:
			default:
				log.trace(
					"TRACE: SL1-default: " + 
					"unexpected parent LineType at switch level 1" +
					"\n\t\t\tbad parent LineType: " + parentLineType);
			} // end SL1-*
		} catch (IOException io) {
			log.trace("IOException: " + io.getMessage());
		}
		
		log.trace(
			"TRACE: readAzLine: returning resultObject = " + resultObject);
		if ( ! (resultObject == null) ) {
			String resultObjectClass = "null";
			if ( ! (resultObject.getObject()==null) )
				resultObjectClass = resultObject.getObject().getClass().getName();
			log.trace("TRACE: readAzLine: returning resultObject" + 
				"\n\t.getLineType() = " + resultObject.getLineType() +
				"\n\t.getObject()   = " + resultObjectClass +
				"\n\t.getReadAhead() = " + resultObject.getReadAhead() +
				"\n\t.getReadAheadObj() = " + resultObject.getReadAheadObject());
		}
		log.trace(
			"\nTRACE: **********************************************************" +			 
			"\n\t     readAzLine: END processing:   Line #: " + 
			curLineCtx.getLineNumber() + "  EXIT" + 
			"\nTRACE: **********************************************************");
			
		return resultObject;
    }
    
	/**
	 * Process the first line of the policy source	
	 * <p>
	 * These are the level 2 (SL2) options
	 * @return an OpenAzXacmlObject containing a 
	 *  SunXacml Policy or PolicySet
	 */
    public OpenAzXacmlObject processINIT(
    			OpenAzLineContext curLineCtx)
			throws OpenAzParseException {
    	
		LineType currentLineType = curLineCtx.getLineType();
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		String strLine = curLineCtx.getLine();
		int myLineNumber = curLineCtx.getLineNumber(); 
    	
    	OpenAzXacmlObject resultObject = null;
    	LineType parentLineType = LineType.INIT; // by defn
		String msg =
			"OpenAz Parsing error: " +
			"\n\t\tParent LineType: " + parentLineType +
			"\n\t\t  current (Child) LineType found: " + currentLineType +
			"\n\t\t  looking for LineType: " + lookingForLineType +
			"\n\t child found incompatible w parent looking for type.";

		log.trace("TRACE: INIT switch level 1");
		
		// first line of whole Policy must be Policy or PolicySet
		log.trace("TRACE: currentLineType: " + currentLineType +
				"  parentLineType: " + parentLineType);
		log.trace(
				"TRACE: got currentLineType: " + currentLineType);
		
		// process the LineType accordingly

		// SL2-INIT
		switch (currentLineType) {
		// SL2-INIT:PS
		case PS: // PolicySet (Root is often a PolicySet)
			log.trace("TRACE: " + curLineCtx.getStateDescriptor() +
					" switch level 2");
			// process a Root (mLevel=0) PolicySet record line from the file
			log.trace("TRACE: " + curLineCtx.getStateDescriptor() +
					" processing initial policyset. As root need to have both " +
					"mLevel and parentMLevel = 0 in curLineCtx: " + 
					"\n\t\tmLevel = " + curLineCtx.getMLevel() + 
					"\n\t\tparentMLevel = " + curLineCtx.getParentMLevel());
			curLineCtx.setParentMLevel(0);
			log.trace("TRACE: " + curLineCtx.getStateDescriptor() +
					" have now set parentMLevel in curLineCtx: " + 
					"\n\t\tmLevel = " + curLineCtx.getMLevel() + 
					"\n\t\tparentMLevel = " + curLineCtx.getParentMLevel());
			resultObject = processPolicySet(curLineCtx);
			break;
		// SL2-INIT:PL 
		case PL: // Policy (Root can also be a Policy)
			log.trace("TRACE: INIT:PS switch level 2");
			resultObject = processPolicy(curLineCtx);
			break;
		// SL2-INIT:default:  
		default:
			log.trace("TRACE: SL2-INIT:default in processINIT");
			throw new OpenAzParseException(msg, myLineNumber);
		} // end SL2-INIT
		
		log.trace(
				"TRACE: exiting processINIT():  parentLineType: " + 
					parentLineType +
					", currentLineType: " + currentLineType +
					"\n\tstrLine: " + strLine + "\n");
    	return resultObject;
    } // end processINIT
    
	/**
	 * This module contains the level 2 switch options for
	 * the level 1 switch case: parent = PolicySet (PS).
	 * <p>
	 * PolicySet knows when looking for Target, so that will
	 * be set first time thru
	 * PolicySet knows when done processing Target and looking
	 * for either Policy or PolicySets - in this case lookingFor
	 * will be null since either ok
	 * From current line perspective, if current is target-ps
	 * then looking for must match
	 * if current is ps or pl then lookingfor should be null,
	 * but if not then throw a parsing exception, since the
	 * parent plus looking for establishes the context
	 *   
	 * @return an OpenAzXacmlObject containing a SunXacml PolicySet
	 */
    public OpenAzXacmlObject processPS(
    			OpenAzLineContext curLineCtx)
    		throws OpenAzParseException {
    	
		LineType currentLineType = curLineCtx.getLineType();
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		String strLine = curLineCtx.getLine();
		int myLineNumber = curLineCtx.getLineNumber();
    	
    	OpenAzXacmlObject resultObject = null;
    	// by defn of level 1 switch, when this method called, 
    	// parent is PS
    	LineType parentLineType = LineType.PS;
    	
		// set up the message info for parsing error if found
		String msg =
			"OpenAz Parsing error: " +
			"\n\t\tParent LineType: " + parentLineType +
			"\n\t\t  current (Child) LineType found: " + currentLineType +
			"\n\t\t  looking for LineType: " + lookingForLineType +
			"\n\t child found incompatible w parent looking for type.";

		log.trace("TRACE: PS: switch level 1");
		log.trace("TRACE: currentLineType: " + currentLineType +
				"  parentLineType: " + parentLineType);

		// SL2:PS (SwitchLevel2:PS (switch on curLinTyp, where parent=PS)) 
		switch (currentLineType){
		
		// SL2-PS:TG_PS (case: curLinTyp = TG_PS, where parent=PS) 
		case TG_PS:
			//resultObject = processTargetPolicySet(curLineCtx);
			resultObject = processTargetPolicy(curLineCtx);
			break; // end case TG_PS (SL2-PS:TG_PS)
			
		// SL2-PS:PS (case: curLinTyp = PS, where parent=PS) 
		case PS:
			if ( ! (lookingForLineType == null) ) {
				throw new OpenAzParseException(msg, myLineNumber);
			}
			// will probably end up here frequently since PS terminates
			// most other structures and keeps getting kicked up until
			// it hits a parent PS, where the mLevel logic applies
			// if mLevel = parentMLevel + 1, then 
			//		this is the default mLevel case; 
			//		and current PS is child of current parent
			// however if mLevel > parentMLevel + 1, then
			// need to treat as terminator
			// is simply a child of the parent.
			log.trace("TRACE: PS:PS case");
			int parentMLevel = curLineCtx.getParentMLevel();
			int currentMLevel = curLineCtx.getMLevel(); // current PS mLevel
			if (currentMLevel == (parentMLevel+1)) {
				// default or "normal" case where PS plugged into parent
				resultObject = processPolicySet(curLineCtx);
			} else {
				// other cases are either parse error or terminate this level
				if ( (currentMLevel <= 0) ||
					 (currentMLevel > parentMLevel+1)) {
					log.trace(
						"TRACE: illegal mLevel constructs: " +
						"\n\t\tparentMLevel = " + parentMLevel +
							"\n\t\tcurrentMLevel = " + currentMLevel);
					throw new OpenAzParseException(msg,myLineNumber);
				} else {
					// this is the terminator case where we set readAhead
					// and close things on the way up until called again
					// at lower mLevel, which should repeat until we get
					// to the default case
					resultObject = new OpenAzXacmlObject(
						currentLineType, null, true, strLine);
				}
			}
			break;
			
		// SL2-PS:PL: (case: curLinTyp = PL, where parent=PS) 
		case PL:
			if ( ! (lookingForLineType == null) ) {
				throw new OpenAzParseException(msg, myLineNumber);
			}
			log.trace("TRACE: PS:PL case");
			log.trace("TRACE: calling processPolicy(Line# = " +
					myLineNumber + ", currentLineType = " + currentLineType + ")");
			resultObject = processPolicy(curLineCtx);
			break;
			
		case FINAL: // terminators for PS
			log.trace("TRACE: SL2-PS:terminator(" + 
					currentLineType + ") pop up the call stack to try again");
			// 3rd param, readAhead is false for FINAL
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, false, null);
			break;

			// SL2-PS:default: (case: curLinTyp = ?, where parent=PS) 
		default:
			log.trace(
					"TRACE: SL2-PS:default in processPS");
			throw new OpenAzParseException(msg, myLineNumber);
		} // end SL2-PS
		log.trace(
			"TRACE: exiting processPS():  parentLineType: " + 
				parentLineType +
				", currentLineType: " + currentLineType +
				"\n\tstrLine: " + strLine + "\n");
    	return resultObject;
    } // end processPS

	/**
	 * This module contains the level 2 switch options for
	 * the level 1 switch case: parent = Policy (PL).
	 * 
	 * Process the currentLine when the parentLine is a Policy.
	 * First record must be a Target, then any number of Rules,
	 * then any Obligations	
	 * @return an OpenAzXacmlObject containing a SunXacml Policy
	 */
    public OpenAzXacmlObject processPL(
    			OpenAzLineContext curLineCtx)
			throws OpenAzParseException {
    	
		LineType currentLineType = curLineCtx.getLineType();
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		String strLine = curLineCtx.getLine();
		int myLineNumber = curLineCtx.getLineNumber();
    	
    	LineType parentLineType = LineType.PL; // by defn
    	OpenAzXacmlObject resultObject = null;
		OpenAzXacmlObject azObject = null;
    	List ruleList = new ArrayList();
    	Set obligationSet = new HashSet();
    	
		// set up the message info for parsing error if found
		String msg =
			"OpenAz Parsing error: " +
			"\n\t\tParent LineType: " + parentLineType +
			"\n\t\t  current (Child) LineType found: " + currentLineType +
			"\n\t\t  looking for LineType: " + lookingForLineType +
			"\n\t child found incompatible w parent looking for type.";
		
		log.trace("TRACE: PL switch level 1");
		
		log.trace("TRACE: currentLineType: " + currentLineType +
				"  parentLineType: " + parentLineType);
		log.trace(
				"TRACE: got currentLineType: " + currentLineType);
		
		// process the level 2, current LineType accordingly

		// SL2-PL:
		switch (currentLineType) {
		
			// SL2-PL:TG_PL  (build and return the Policy Target)
			case TG_PL:
				resultObject = processTargetPolicy(curLineCtx);
				break;
				
			// SL2-PL:RLS: (build and return the Policy RuleList)
			case RLS:
				resultObject = processRuleList(curLineCtx);
				break;

			// SL2-PL:OBS  (build and return the Policy Obligations)
			case OBS: // (in processPL) parent is PL, current is OBS
				resultObject = processObligations(curLineCtx);
					break;
				
			case PL: // terminators for PL
			case PS:
			case FINAL:
				log.trace("TRACE: terminator(" + 
						currentLineType + ") pop up the call stack to try again");
				resultObject = new OpenAzXacmlObject(
						currentLineType, null, true, strLine);
				break;
				
			// SL2-PL:default:
			default:
				log.trace(
					"TRACE: SL2-PL:default in processPL");
				throw new OpenAzParseException(msg, myLineNumber);
		} // end SL2-PL (switch (currentLineType))
		
		log.trace(
			"TRACE: exiting processPL(): parentLineType: " + parentLineType);
		return resultObject;
    } // end processPL
    
	/**
	 * This module contains the level 2 switch options for
	 * the level 1 switch case: parent = Rule (RL).
	 * <p>
	 * Process the currentLine when the parentLine is a Rule.
	 * First record must be a Target, then an optional Condition	
	 * @return an OpenAzXacmlObject containing a SunXacml Rule
	 */
    public OpenAzXacmlObject processRL(
    			OpenAzLineContext curLineCtx)
			throws OpenAzParseException {
    	
		LineType currentLineType = curLineCtx.getLineType();
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		String strLine = curLineCtx.getLine();
		int myLineNumber = curLineCtx.getLineNumber();
    	LineType parentLineType = LineType.RL; // by defn
    	OpenAzXacmlObject resultObject = null;
		// set up the message info for parsing error if found
		String msg =
			"OpenAz Parsing error: " +
			"\n\t\tParent LineType: " + parentLineType +
			"\n\t\t  current (Child) LineType found: " + currentLineType +
			"\n\t\t  looking for LineType: " + lookingForLineType +
			"\n\t child found incompatible w parent looking for type.";
		
		log.trace("TRACE: RL switch level 1");
		log.trace("TRACE: currentLineType: " + currentLineType +
				"  parentLineType: " + parentLineType);
		
		// process the level 2 current LineType accordingly

		// SL2-RL:
		switch (currentLineType) {
		
			// SL2-RL:TG_RL  (build and return the Rule Target)
			case TG_RL:
				//processTarget"Policy" also covers Rule and PolicySet
				log.trace(
					"TRACE: SL20-: processRL CALLING processTargetPolicy");
				resultObject = processTargetPolicy(curLineCtx);
				break;
				
			// SL2-RL:CD_RL  (build and return the Rule Condition)
			case CD_RL:
				log.trace(
					"TRACE: SL20-: processRL CALLING processCondition");
				resultObject = processCondition(curLineCtx);
				break;

			// SL2-RL:RL:
			case RL:
				// This is case where rule terminated by subsequent rule
				log.trace("TRACE: RL:RL switch level 2");
				log.trace("TRACE: processing combo RL:RL" +
					"\n\twhich means a RL follows a RL, which means " +
					"\n\t processing Target is complete, so " +
					"\n\tcancel the read and cleanup w readAhead flag true");
				// put buffer string as object and set readAhead as true
				// so that when return object caller can handle
				resultObject = new OpenAzXacmlObject(
						currentLineType, null, true, strLine);
				break;
				
			// SL2-RL:OBS
			case OBS: // no OBS in Rule in Xacml 2.0, but will be in 3.0
				log.trace("TRACE: RL:OBS case - terminator");
				resultObject = new OpenAzXacmlObject(
						currentLineType, null, true, strLine);
				break;
				
			case PL: // terminators for RL
			case PS:
			case FINAL:
				log.trace("TRACE: terminator(" + 
						currentLineType + ") pop up the call stack to try again");
				resultObject = new OpenAzXacmlObject(
						currentLineType, null, true, strLine);
				break;
				
			// SL2-RL:default
			default:
				log.trace(
					"TRACE: SL2-RL:default in processRL");
				throw new OpenAzParseException(msg, myLineNumber);
		} // end SL2-RL (switch (currentLineType))
		
		log.trace(
			"TRACE: exiting processRL(): parentLineType: " + parentLineType);
		return resultObject;
    } // end processRL (Rule)
    
	/**
	 * Process the currentLine when the parentLine is an RLS (RuleList).
	 * First record must be a Rule, then the Rule must be processed,
	 * then any additional Rules must be found and processed.
	 * Termination may be by an Obligations construct, or anything
	 * that indicates beginning of a new Policy or PolicySet,
	 * or simply the end of the Policy source data.
	 * 
	 * @return an OpenAzXacmlObject containing a 
	 * 	List of SunXacml Rules
	 */
    public OpenAzXacmlObject processRLS(
    			OpenAzLineContext curLineCtx)
			throws OpenAzParseException {
    	
		LineType currentLineType = curLineCtx.getLineType();
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		String strLine = curLineCtx.getLine();
		int myLineNumber = curLineCtx.getLineNumber(); 
    	LineType parentLineType = LineType.RLS; // by defn
    	OpenAzXacmlObject resultObject = null;
		// set up the message info for parsing error if found
		String msg =
			"OpenAz Parsing error: " +
			"\n\t\tParent LineType: " + parentLineType +
			"\n\t\t  current (Child) LineType found: " + currentLineType +
			"\n\t\t  looking for LineType: " + lookingForLineType +
			"\n\t child found incompatible w parent looking for type.";
		log.trace("TRACE: RLS switch level 1");
		// first line must be Rule
		log.trace("TRACE: currentLineType: " + currentLineType +
				"\n\t  parentLineType: " + parentLineType);

		// SL2-RLS:
		switch (currentLineType) {
		
			// SL2-RLS;RL:  (build and return a Rule)
			case RL:
				log.trace("TRACE: SL2-RLS:RL: switch level 2");
				// if current is RL, then should match lookingFor
				if ( (lookingForLineType == null) ||
					 ! (currentLineType.equals(lookingForLineType)) ) {
					// if current line is RL and lookingFor is null
					// OR lookingFor not null and is not RL
					// then throw an exception.
					throw new OpenAzParseException(msg, curLineCtx.getLineNumber());
				}
				resultObject = processRule(curLineCtx);
				break;
				
			// SL2-RLS:OBS:
			case OBS:
				log.trace("TRACE: terminator(" + 
						currentLineType + ") pop up the call stack to try again");
				resultObject = new OpenAzXacmlObject(
						currentLineType, null, true, strLine); 
				break;
				
			case PL: // terminators for RLS
			case PS:
			case FINAL:
				log.trace("TRACE: terminator(" + 
						currentLineType + ") pop up the call stack to try again");
				resultObject = new OpenAzXacmlObject(
						currentLineType, null, true, strLine);
				break;
			// SL2-RLS:default:
			default:
				log.trace(
					"TRACE: SL2-RLS:default in processRLS - " + 
						"Should never get here");
				throw new OpenAzParseException(msg, myLineNumber);
		} // end SL2-PL (switch (currentLineType))
		
		log.trace(
			"TRACE: exiting processRLS(): parentLineType: " + parentLineType);
		return resultObject;
    } // end processRLS

    /**
     * processOBS means that an OBS was encountered on the
     * previous readAzLine and that now we are actively
     * processing the OBS by reading the subsequent records
     * and letting the semantics of OBS orchestrate.
     * 
     * In fact, it is the slot on the stack that is processing
     * OBS that called readAzLine that invoked the current slot,
     * which should be an OB record as the currentLineType.
     * 
     * By doing the subsequent reads, we should be able to get
     * all the OAs and thus return the OB as an obligation
     * to caller who would assemble them in "Obligations",
     * the collection of multiple obligations.
     * 
     * @param curLineCtx
     * @return an OpenAzXacmlObject containing 
     * 	SunXacml Obligations
     * @throws OpenAzParseException
     */
    public OpenAzXacmlObject processOBS(
    			OpenAzLineContext curLineCtx)
			throws OpenAzParseException {
    	
		LineType currentLineType = curLineCtx.getLineType();
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		String strLine = curLineCtx.getLine();
		int myLineNumber = curLineCtx.getLineNumber();
    	
    	LineType parentLineType = LineType.OBS; // by defn
    	OpenAzXacmlObject resultObject = null;
    	
		// set up the message info for parsing error if found
		String msg =
			"OpenAz Parsing error: " +
			"\n\t\tParent LineType: " + parentLineType +
			"\n\t\t  current (Child) LineType found: " + currentLineType +
			"\n\t\t  looking for LineType: " + lookingForLineType +
			"\n\t child found incompatible w parent looking for type.";
		
		log.trace("TRACE: OBS switch level 1");
		// first line must be Obligation
		log.trace("TRACE: currentLineType: " + currentLineType +
				"\n\t  parentLineType: " + parentLineType);
		log.trace("TRACE: SL2-OBS:OB: switch level 2");
		
		// SL2-OBS:
		switch (currentLineType) {
		
			// SL2-OBS:OB:  (build and return an Obligation)
			case OB:
				resultObject = processObligation(curLineCtx);
				break;
				
			case PL: // terminators for OBS
			case PS:
				log.trace("TRACE: terminator(" + 
						currentLineType + ") pop up the call stack to try again");
				resultObject = new OpenAzXacmlObject(
						currentLineType, null, true, strLine);
				break;

			// SL2-OBS:default:
			default:
				// got something else besides an OB in the OBS
				log.trace(
					"TRACE: SL2-OBS:default in processOBS - " + 
					"got something else besides an OB in the OBS");
				throw new OpenAzParseException(
						msg, myLineNumber);
		} // end SL2-OBS: (switch (currentLineType))
		// should only get here w resultObject containing obligation
		
		log.trace(
			"TRACE: exiting processOBS(): parentLineType: " + parentLineType);
		return resultObject;
    } // end processOBS

    /**
     * TODO: Compare to the way
     * Targets are processed to determine if structure
     * is consistent.
     * Note: this is called by processOBS when it calls
     * readAzLine with its currentLineType = OB.
     * @param curLineCtx
     * @return an OpenAzXacmlObject containing a 
     * SunXacml Obligation
     * @throws OpenAzParseException
     */
    public OpenAzXacmlObject processOB(
    			OpenAzLineContext curLineCtx)
			throws OpenAzParseException {
    	
		LineType currentLineType = curLineCtx.getLineType();
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		String strLine = curLineCtx.getLine();
		int myLineNumber = curLineCtx.getLineNumber();
    	LineType parentLineType = LineType.OB; // by defn
    	OpenAzXacmlObject resultObject = null;
		// set up the message info for parsing error if found
		String msg =
			"OpenAz Parsing error: " +
			"\n\t\tParent LineType: " + parentLineType +
			"\n\t\t  current (Child) LineType found: " + currentLineType +
			"\n\t\t  looking for LineType: " + lookingForLineType +
			"\n\t child found incompatible w parent looking for type.";
		log.trace("TRACE: OB switch level 1");
		// parent line is implicitly OB, this line should be
		// either OA or terminator
		log.trace("TRACE: currentLineType: " + currentLineType +
				"\n\t  parentLineType: " + parentLineType);

		// SL2-OB:
		switch (currentLineType) {
		
			// SL2-OB:OA  (build and return an Obligation attribute)
			case OA:
				log.trace(
					"TRACE: SL2-OB:OA getting attribute for Obligation");
				OpenAzXacmlObject azObject = null;
				resultObject = processTS(curLineCtx);
				break;
				
			case OB: // terminators for OB
			case PL:
			case PS:
				log.trace("TRACE: SL2-OB: terminator(" + 
						currentLineType + ") pop up the call stack to try again");
				resultObject = new OpenAzXacmlObject(
						currentLineType, null, true, strLine);
				break;

			// SL2-OB:default:
			default:
				log.trace(
					"TRACE: SL2-OB:default in processOB - " + 
						"Should never get here");
				throw new OpenAzParseException(msg, myLineNumber);
		} // end SL2-OB: (switch (currentLineType))
		
		log.trace(
			"TRACE: exiting processOB(): parentLineType: " + parentLineType);
		return resultObject;
    } // end processOB

    // Note: saved version has processOA in case a need is found
    // for it, but for now looks like it was a false path.
   
    
	/**
     * This method handles the cases where the previous line in the source
     * for the policy is a PolicySet Target line (i.e a Target child
     * of specifically a PolicySet). In the file these appear
     * with a line header of Target(PolicySet)
     * <p>
     * The cases handled are for each possible line that can follow
     * a PolicySet/Target, which include attribute match elements as
     * well as terminating elements which include another Policy or
     * PolicySet.
     * @param curLineCtx
     * @return an OpenAzXacmlObject containing a SunXacml Target
     */
    public OpenAzXacmlObject processTGPS(
    			OpenAzLineContext curLineCtx)
    		throws OpenAzParseException {
    	
		LineType currentLineType = curLineCtx.getLineType();
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		String strLine = curLineCtx.getLine();
		int myLineNumber = curLineCtx.getLineNumber();
    	OpenAzXacmlObject resultObject = null;
    	LineType parentLineType = LineType.TG_PS; // by defn
		// set up the message info for parsing error if found
		String msg =
			"OpenAz Parsing error: " +
			"\n\t\tParent LineType: " + parentLineType +
			"\n\t\t  current (Child) LineType found: " + currentLineType +
			"\n\t\t  looking for LineType: " + lookingForLineType +
			"\n\t child found incompatible w parent looking for type.";
		log.trace("TRACE: TG_PS switch level 1");
		log.trace(
				"TRACE: processing parent TG_PS:");
		log.trace(
				"TRACE: currentLineType = " + currentLineType);
		
		// SL2-TG_PS looking for either a child of Target or 
		// a terminator of the Target
		switch (currentLineType) {
		
		// SL2-TG_PS:PS:
		case PS:
			log.trace("TRACE: TG_PS:PS switch level 2");
			log.trace("TRACE: processing combo TG_PS:PS" +
					"\n\twhich means a PS follows a TG_PS, which means " +
					"\n\tcancel the read and cleanup w readAhead flag true");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine); 
			break;
			
		// SL2-TG_PS:PL:
		case PL:
			log.trace("TRACE: TG_PS:PL switch level 2");
			log.trace("TRACE: processing combo TG_PS:PL" +
				"\n\twhich means a PL follows a TG_PS, which means " +
				"\n\tcancel the read and cleanup w readAhead flag true");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine);
			break;
			
		// SL2-TG_PS:TS: (PolicySet Target containing a SubjectMatch)
		case TS:
			log.trace(
				"TRACE: in TG_PS:TS should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
			
		// SL2-TG_PS:TR:
		case TR: // TODO fix this to call processTR
			log.trace(
				"TRACE: in TG_PS:TR should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
						
		// SL2-TG_PS:TA:
		case TA: // TODO fix this to call processTA
			log.trace(
				"TRACE: in TG_PS:TA should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
			
		// SL2-TG_PS:default:
		default:
			log.trace("TRACE: SL2-TG_PS:default in processTGPL");
			throw new OpenAzParseException(msg, myLineNumber);
		} // end SL2-TG_PS-* (i.e. end of choices to follow a TG_PS)
		log.trace(
			"TRACE: exiting processTGPS(): parentLineType: " + parentLineType);
    	return resultObject;
    } // end processTGPS
    
    /**
     * This method handles the cases where the previous line in the source
     * for the policy is a Policy Target line (i.e a Target child
     * of specifically a Policy). In the file these appear
     * with a line header of Target(Policy)
     * <p>
     * The cases handled are for each possible line that can follow
     * a Policy/Target, which include attribute match elements as
     * well as terminating elements which include Rules, or 
     * Obligations, or possibly Policy or PolicySet?
     * @param curLineCtx
     * @return an OpenAzXacmlObject containing a SunXacml Target
     */
    public OpenAzXacmlObject processTGPL(
    			OpenAzLineContext curLineCtx)
    		throws OpenAzParseException {
    	
		LineType currentLineType = curLineCtx.getLineType();
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		String strLine = curLineCtx.getLine();
		int myLineNumber = curLineCtx.getLineNumber();
    	LineType parentLineType = LineType.TG_PL; // by defn
    	OpenAzXacmlObject resultObject = null;
		// set up the message info for parsing error if found
		String msg =
			"OpenAz Parsing error: " +
			"\n\t\tParent LineType: " + parentLineType +
			"\n\t\t  current (Child) LineType found: " + currentLineType +
			"\n\t\t  looking for LineType: " + lookingForLineType +
			"\n\t child found incompatible w parent looking for type.";
		log.trace("TRACE: TG_PL switch level 1");
		log.trace(
				"TRACE: processing parent TG_PL:");
		log.trace(
				"TRACE: currentLineType = " + currentLineType);
		
		// SL2-TG_PL: looking for either a child of Target or 
		// a terminator of the Target
		switch (currentLineType) {
		
		// SL2-TG_PL:PS
		case PS:
			log.trace("TRACE: TG_PL:PS switch level 2");
			log.trace("TRACE: processing combo TG_PL:PS" +
					"\n\twhich means a PS follows a TG_PL, which means " +
					"\n\tcancel the read and cleanup w readAhead flag true");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine); 
			break;
			
		// SL2-TG_PL:PL:
		case PL:
			log.trace("TRACE: TG_PL:PL switch level 2");
			log.trace("TRACE: processing combo TG_PL:PL" +
				"\n\twhich means a PL follows a TG_PL, which means " +
				"\n\t processing Target is complete, so " +
				"\n\tcancel the read and cleanup w readAhead flag true");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine);
			break;
			
		// SL2-TG_PL:TS: (Policy Target containing a SubjectMatch)
		case TS:
			log.trace(
				"TRACE: in TG_PL:TS should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
			
		// SL2-TG_PL:TR:
		case TR: // TODO fix this to call processTR
			log.trace(
				"TRACE: in TG_PL:TR should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
			
		// SL2-TG_PL:TA:
		case TA: // TODO fix this to call processTR
			log.trace(
				"TRACE: in TG_PL:TA should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
			
		// SL2-TG_PL:RLS:
		case RLS: // TG_PL terminated by start of Rules
			log.trace(
					"TRACE: create readAhead strLine for RLS terminator");
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine); 
			break;
			
		// SL2-TG_PL:default:
		default:
			log.trace("TRACE: SL2-TG_PL:default in processTGPL");
			throw new OpenAzParseException(msg, myLineNumber);
		} // end SL2-TG_PL-* (i.e. end of choices to follow a TG_PL)
		log.trace(
			"TRACE: exiting processTGPL(): parentLineType: " + parentLineType);
		return resultObject;
    } // end processTGPL
    
    /**
     * This method handles the cases where the previous line in the source
     * for the policy is a Rule Target line (i.e a Target child
     * of specifically a Rule). In the file these appear
     * with a line header of Target(Rule)
     * <p>
     * The cases handled are for each possible line that can follow
     * a Rule/Target, which include attribute match elements that
     * are part of the Rule Target as well as terminating elements 
     * which include Conditions and start of new Policy, PolicySet,
     * or end of policy source.
     * 
     * @param curLineCtx
     * @return an OpenAzXacmlObject containing a SunXacml Target
     */
    public OpenAzXacmlObject processTGRL(
    			OpenAzLineContext curLineCtx)
    		throws OpenAzParseException {
    	
		LineType currentLineType = curLineCtx.getLineType();
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		String strLine = curLineCtx.getLine();
		int myLineNumber = curLineCtx.getLineNumber();
    	LineType parentLineType = LineType.TG_RL; // by defn
    	OpenAzXacmlObject resultObject = null;
		// set up the message info for parsing error if found
		String msg =
			"OpenAz Parsing error: " +
			"\n\t\tParent LineType: " + parentLineType +
			"\n\t\t  current (Child) LineType found: " + currentLineType +
			"\n\t\t  looking for LineType: " + lookingForLineType +
			"\n\t child found incompatible w parent looking for type.";
		
		log.trace("TRACE: TG_RL switch level 1");
		log.trace(
				"TRACE: processing parent TG_RL:");
		log.trace(
				"TRACE: currentLineType = " + currentLineType);
		
		// SL2-TG_RL: looking for either a child of Rule Target or 
		// a terminator of the Target
		switch (currentLineType) {
		
		// SL2-TG_RL:PS
		case PS:
			log.trace("TRACE: TG_RL:PS switch level 2");
			log.trace("TRACE: processing combo TG_RL:PS" +
					"\n\twhich means a PS follows a TG_RL, which means " +
					"\n\tcancel the read and cleanup w readAhead flag true");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine); 
			break;
			
		// SL2-TG_RL:PL:
		case PL:
			log.trace("TRACE: TG_RL:PL switch level 2");
			log.trace("TRACE: processing combo TG_RL:PL" +
				"\n\twhich means a PL follows a TG_RL, which means " +
				"\n\t processing Target is complete, so " +
				"\n\tcancel the read and cleanup w readAhead flag true");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine);
			break;
			
		// SL2-TG_RL:RL:
		case RL:
			log.trace("TRACE: TG_RL:RL switch level 2");
			log.trace("TRACE: processing combo TG_RL:RL" +
				"\n\twhich means a RL follows a TG_RL, which means " +
				"\n\t processing Target is complete, so " +
				"\n\tcancel the read and cleanup w readAhead flag true");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine);
			break;
			
		// SL2-TG_RL:CD_RL:
		case CD_RL:
			// terminate a TG_RL with a CD_RL
			log.trace("TRACE: TG_RL:CD_RL switch level 2");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine);
			break;
			
		// SL2-TG_RL:OBS:
		case OBS:
			// terminate a TG_RL with an OBS
			log.trace("TRACE: TG_RL:CD_RL switch level 2");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine);
			break;

		// SL2-TG_RL:TS: (Rule Target containing a SubjectMatch)
		case TS:
			log.trace(
				"TRACE: in TG_RL:TS should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
			
		// SL2-TG_RL:TR:
		case TR: // TODO fix this to call processTR
			log.trace(
				"TRACE: in TG_RL:TR should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
						
		// SL2-TG_RL:TA:
		case TA: // TODO fix this to call processTR
			log.trace(
				"TRACE: in TG_RL:TA should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
						
		case FINAL:  // terminators for TG_RL
			log.trace("TRACE: terminator(" + 
					currentLineType + ") pop up the call stack to try again");
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine);
			break;
			
		// SL2-TG_RL:default:
		default:
			log.trace("TRACE: SL2-TG_RL:default in processTGRL");
			throw new OpenAzParseException(msg, myLineNumber);
		} // end SL2-TG_RL-* (i.e. end of choices to follow a TG_RL)
		log.trace(
			"TRACE: exiting processTGRL(): parentLineType: " + parentLineType);
    	return resultObject;
    } // end processTGRL
    
    
    /**
     * This method handles the cases where the previous line in the source
     * for the policy is a Rule Condition line (i.e a Condition child
     * of a Rule). In the file these appear
     * with a line header of Condition(Rule)
     * <p>
     * The cases handled are for each possible line that can follow
     * a Rule/Condition, which include attribute match elements that
     * are part of the Rule Target as well as terminating elements 
     * which include start of new Rule, Policy, PolicySet,
     * or end of policy source.
     * 
     * @param curLineCtx
     * @return an OpenAzXacmlObject containing a SunXacml Condition
     */
    public OpenAzXacmlObject processCDRL(
    			OpenAzLineContext curLineCtx)
    		throws OpenAzParseException {
    	
		LineType currentLineType = curLineCtx.getLineType();
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		String strLine = curLineCtx.getLine();
		int myLineNumber = curLineCtx.getLineNumber();
    	LineType parentLineType = LineType.CD_RL; // by defn
    	OpenAzXacmlObject resultObject = null;
		// set up the message info for parsing error if found
		String msg =
			"OpenAz Parsing error: " +
			"\n\t\tParent LineType: " + parentLineType +
			"\n\t\t  current (Child) LineType found: " + currentLineType +
			"\n\t\t  looking for LineType: " + lookingForLineType +
			"\n\t child found incompatible w parent looking for type.";
		
		log.trace("TRACE: CD_RL switch level 1");
		log.trace(
				"TRACE: processing parent CD_RL:");
		log.trace(
				"TRACE: currentLineType = " + currentLineType);
		
		// SL2-CD_RL: looking for either a child of a Rule Condition or 
		// a terminator of the Target
		switch (currentLineType) {
		
		// SL2-CD_RL:PS
		case PS:
			log.trace("TRACE: CD_RL:PS switch level 2");
			log.trace("TRACE: processing combo CD_RL:PS" +
					"\n\twhich means a PS follows a CD_RL, which means " +
					"\n\tcancel the read and cleanup w readAhead flag true");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine); 
			break;
			
		// SL2-CD_RL:PL:
		case PL:
			log.trace("TRACE: CD_RL:PL switch level 2");
			log.trace("TRACE: processing combo CD_RL:PL" +
				"\n\twhich means a PL follows a CD_RL, which means " +
				"\n\t processing Condition is complete, so " +
				"\n\tcancel the read and cleanup w readAhead flag true");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine);
			break;
			
		// SL2-CD_RL:RL:
		case RL:
			log.trace("TRACE: CD_RL:RL switch level 2");
			log.trace("TRACE: processing combo CD_RL:RL" +
				"\n\twhich means a RL follows a CD_RL, which means " +
				"\n\t processing Condition is complete, so " +
				"\n\tcancel the read and cleanup w readAhead flag true");
			// put buffer string as object and set readAhead as true
			// so that when return object caller can handle
			resultObject = new OpenAzXacmlObject(
					currentLineType, null, true, strLine);
			break;
			
		// SL2-CD_RL:CD_RL:
		case CD_RL:
			log.trace("TRACE: CD_RL:CD_RL switch level 2");
			break;
			
		// SL2-CD_RL:TS: (Rule Condition containing a SubjectMatch)
		case TS:
			log.trace(
				"TRACE: in CD_RL:TS should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
			
		// SL2-CD_RL:TR:
		case TR: // TODO fix this to call processTR
			log.trace(
				"TRACE: in CD_RL:TR should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
						
		// SL2-CD_RL:TA:
		case TA: // TODO fix this to call processTR
			log.trace(
				"TRACE: in CD_RL:TA should have all tokens to build msg.");
			resultObject = processTS(curLineCtx);
			break;
						
		// SL2-CD_RL:default:
		default:
			log.trace("TRACE: SL2-CD_RL:default in processCDRL");
			throw new OpenAzParseException(msg, myLineNumber);
		} // end SL2-CD_RL-* (i.e. end of choices to follow a CD_RL)
		log.trace(
			"TRACE: exiting processCDRL(): parentLineType: " + parentLineType);
    	return resultObject;
    } // end processCDRL
    
    
	/**
	 * This method processes the tokens for TS, TR, TA, and "+" statements,
	 * which are AttributeMatchExpressions, such as SubjectMatch, ActionMatch,
	 * ResourceMatch that contain SubjectDesignator and AttributeValue
	 * elements that compare an attribute in the request context indicated
	 * by the designator with an AttributeValue in the Match element.
	 * Similar expressions also occur in Condition elements, but further
	 * analysis is needed for all but the simplest conditions.
	 * 	
	 * @return an OpenAzXacmlObject containing an AME for the particular
	 * attribute being processed
	 */
    public OpenAzXacmlObject processTS(
    			OpenAzLineContext curLineCtx) {
    	
		LineType parentLineType = curLineCtx.getParentLineType();
		LineType currentLineType = curLineCtx.getLineType();
		String strLine = curLineCtx.getLine();
    	OpenAzXacmlObject resultObject = null;
    	
    	// There are 4 AME constructors; all have 5 reqd params
    	// 3 have diff combos of optional mbp,issuer,fcnId.
    	// We can go thru all the tokens and see what we have,
    	// and only process the non-optional ones:
    	// Set up variables for all 8 copied from ame class:
    	//int attributeDesignatorType = -1;	// Subject, Resource, Action, Env
    	
    	// Allocate variables to stored resolved token values; the loop
    	// below gets the abbreviations from the parsed input and looks
    	// up all the abbreviations to get resolved value suitable for
    	// xacml processing.
    	// Same loop applies to all LineTypes, but the list of tokens for
    	// each LineType is generally different, representing metadata
    	// for corresponding xacml elements.
    	// try what hopefully is a benign default:
       	// int attributeDesignatorType = 3;	// Subject, Resource, Action, Env
        int attributeDesignatorType = 0;	// Subject, Resource, Action, Env
    	boolean mustBePresent = false;	// force attr finder or indeterminate
    	String attributeMatchId="";		// type of matching function to apply
    	String attributeDataType="";		// data type of designated attr id
    	String attributeValue="";			// value to compare the attr against
    	String attributeId="";				// attr-id of attr to find in request
    	String attributeIssuer = null;	// optional issue of the attr
    	String attributeFunctionId = null; // id for Apply element in Conditions
    	boolean usingParentLineType = false; // for AND'ing AMEs
    	String designatorEntity = "";  // string name of entity type: subj,act,etc
    	
    	Map<String,String> map = curLineCtx.getTokens();
    	log.trace(
    		"TRACE: Building an AME using these tokens: " + map);
    	String prefix = "("+new Integer(curLineCtx.getLineNumber()).toString()+")";
    	// Purpose of this loop is to collect all the token, resolved value
    	// pairs. Then will be ready to move resolved values to sunxacml.
    	Set<String> keys = map.keySet();
    	for (String s : keys) {
    		String sValue = map.get(s);
    		log.trace("TRACE" + prefix + ": loop for AME:  key: " + s +
    				",  sValue: " + sValue);
    		if (sValue.equals("!")) {
    			log.trace(
    				"TRACE:\t sValue is a null, skip to next key");
    			continue;
    		}
    		int size = keys.size();
			log.trace("TRACE:\t keys.size() = " + size);
    		for (int i=0; i<size; i++) { // iterate over i to test each case
    			log.trace(
    				"TRACE" + prefix + ":   Inner loop: (" + i +
    				") match key then value: key = " + s);
    			switch (i) { //based on i, one of the cases is tested
    			case 0:
    				if (s.equals(OPENAZ_AME_ATTRIBUTE_DESIGNATOR)) {
    					// break loop when thru; not sure if "break" 
    					// would do it here nested in an if and switch
    					i = size; // force loop break only need 1 match
    	    			log.trace(
	        				"TRACE:   Inner loop: key matched:" + 
	        				OPENAZ_AME_ATTRIBUTE_DESIGNATOR +
	        				" now test value: sValue = " + sValue);
    	    			attributeDesignatorType =
    	    				openAzLineTypeToAttrDesignatorMap.get(sValue);
    					// TODO: need to handle the extension line types "+"
    	    			
    	    			switch (attributeDesignatorType) {
    	    			case AttributeDesignator.SUBJECT_TARGET:
    	    				designatorEntity = OPENAZ_CATEGORY_SUBJECT;
    	    				break;
    	    			case AttributeDesignator.RESOURCE_TARGET:
    	    				designatorEntity = OPENAZ_CATEGORY_RESOURCE;
    	    				break;
    	    			case AttributeDesignator.ACTION_TARGET:
    	    				designatorEntity = OPENAZ_CATEGORY_ACTION;
    	    				break;
    	    			case AttributeDesignator.ENVIRONMENT_TARGET:
    	    				designatorEntity = OPENAZ_CATEGORY_ENVIRONMENT;
    	    				break;
    	    			default: 
    	    				designatorEntity = "Designator-" + 
    	    					new Integer(attributeDesignatorType).toString();
    	    			}
    					
	    				log.trace(
	    					"TRACE:\t created attributeDesignatorType: " + 
    						"\n\t\tusing key: " + s +
	    					",  with sValue: " + sValue + 
	    					"\n\t\t\t results in designator integer value:  " +
	    					attributeDesignatorType);
    				} // end if (s.equals(OPENAZ_AME_ATTRIBUTE_DESIGNATOR))
    				break;
    			case 1: // 1st token sValue is the AttributeId (or abbrev)
    				if (s.equals(OPENAZ_AME_ATTRIBUTE_ID)) {
    					i = size; // force break on loop condition
    	    			log.trace(
	        				"TRACE:   Inner loop: key matched:" + 
	        				OPENAZ_AME_ATTRIBUTE_ID +
	        				" now test value: sValue = " + sValue);
    	    			
    	    			if ( ! (openAzAttrIdToXacmlAttrIdMap.
    	    										containsKey(sValue)) ) {
    						log.trace(
								  "TRACE:\t AttributeId sValue does not match " +
								  "any known abbreviated options, " + 
								  "assume its value is direct: " + sValue);
	    					attributeId = sValue;   	    				
		    			} else {
		    				attributeId = openAzAttrIdToXacmlAttrIdMap.get(sValue);
		    				log.trace("TRACE: sValue(AttributeId): " +
		    						attributeId);
		    			}
	    				log.trace(
	    					"TRACE:\t created attributeId: key: " + s +
	    					",  with sValue: " + sValue + 
	    					"\n\t\t\tresults in attributeId\n\t\t\t\t" + 
	    					attributeId);
	    			}
    				break;
    			case 2: // 2nd token, AttributeValue is the sValue
    				// attribute value should be good as is unless it
    				// starts getting abbreviated as well.
    				if (s.equals(OPENAZ_AME_ATTRIBUTE_VALUE)) {
    					i = size; // force break on loop condition
    	    			log.trace(
	        				"TRACE:   Inner loop: key matched:" + 
	        				OPENAZ_AME_ATTRIBUTE_VALUE +
	        				" now test value: sValue = " + sValue);
    					attributeValue = sValue;
	    				log.trace(
	    					"TRACE:\t created attrValue s: " + s +
	    					",  sValue: " + sValue);
    				}
    				break;
    			case 3:  // DataType (dt:sValue)
    				if (s.equals(OPENAZ_AME_ATTRIBUTE_DATA_TYPE)) {
    					i = size; // force break on loop condition
    	    			log.trace(
	        				"TRACE:   Inner loop: key matched:" + 
	        				OPENAZ_AME_ATTRIBUTE_DATA_TYPE +
	        				" now test value: sValue = " + sValue);
    	    			if ( ! (openAzDataTypeToXacmlDataTypeMap.
    	    										containsKey(sValue)) ) {
    						log.trace(
							  "TRACE:\t Attribute DataType sValue does not " +
							  "match any known abbreviated options, " + 
							  "assume its value is direct: " + sValue);
    						attributeDataType = sValue;   	    				
    	    			} else {
    	    				attributeDataType = 
    	    					openAzDataTypeToXacmlDataTypeMap.get(sValue);
    	    				log.trace(
    	    					"TRACE: sValue(AttributeDataType): ");
    	    			}
	    				log.trace(
	    					"TRACE:\t created attributeDataType: " + s +
	    					"  value: " + sValue + 
	    					"\n\t\t\t results in AME value:  \n\t\t\t\t" +
	    					attributeDataType);
       				}
    				break;
    			case 4: // MatchId (mtId:sValue)
    				if (s.equals(OPENAZ_AME_MATCH_ID)) {
    					i = size; // force break on loop condition
    	    			log.trace(
	        				"TRACE:   Inner loop: key matched:" + 
	        				OPENAZ_AME_MATCH_ID +
	        				" now test value: sValue = " + sValue);
    	    			if ( ! (openAzMatchIdToXacmlMatchIdMap.
    	    							containsKey(sValue)) ) {
    						log.trace(
							  "TRACE:\t Attribute MatchId sValue does not " +
							  "match any known abbreviated options s , " + s +
							  "assume its value is direct: " + sValue +
							  "\nopenAzMatchIdToXacmlMatchIdMap" + 
							  openAzMatchIdToXacmlMatchIdMap);
    						attributeMatchId = sValue;   	    				
    	    			} else {
    	    				attributeMatchId = 
    	    					openAzMatchIdToXacmlMatchIdMap.get(sValue);
    	    				log.trace(
    	    					"TRACE: sValue(attributeMatchId): ");
    	    			}
	    				log.trace(
	    					"TRACE:\t created attributeMatchId:  key = " + s +
	    					",  with sValue: " + sValue + 
	    					"\n\t\t\tresulted in setting AME value to: " + 
	    					"\n\t\t\t\t" + attributeMatchId);
    				}
    				break;
    			case 5: // FunctionId (fnId:sValue)
    				if (s.equals(OPENAZ_AME_CONDITION_FUNCTION_ID)) {
    					i = size; // force break on loop condition
    	    			log.trace(
	        				"TRACE:   Inner loop: key matched:" + 
	        				OPENAZ_AME_CONDITION_FUNCTION_ID +
	        				" now test value: sValue = " + sValue);
    	    			if ( ! (openAzFunctionIdToXacmlFunctionIdMap.
    	    							containsKey(sValue)) ) {
    						log.trace(
							  "TRACE:\t Attribute FunctionId sValue does not " +
							  "match any known abbreviated options s , " + s +
							  "assume its value is direct: " + sValue +
							  "\nopenAzFunctionIdToXacmlFunctionIdMap" + 
							  openAzFunctionIdToXacmlFunctionIdMap);
    						attributeFunctionId = sValue;   	    				
    	    			} else {
    	    				attributeFunctionId = 
    	    					openAzFunctionIdToXacmlFunctionIdMap.get(sValue);
    	    				log.trace(
    	    					"TRACE: sValue(attributeFunctionId): " +
    	    						attributeFunctionId);
    	    			}
	    				log.trace(
	    					"TRACE:\t created attributeFunctionId:  key = " + s +
	    					",  with sValue: " + sValue + 
	    					"\n\t\t\tresulted in setting AME value to: " + 
	    					"\n\t\t\t\t" + attributeFunctionId);
    				}
    				break;
    			case 6: // 
    				// usingParentLineType is a boolean w string "t" or "f"
    				// starts getting abbreviated as well.
    				if (s.equals(OPENAZ_AME_USING_PARENT_LINETYPE)) {
    					i = size; // force break on loop condition
    	    			log.trace(
	        				"TRACE:   Inner loop: key matched:" + 
	        				OPENAZ_AME_USING_PARENT_LINETYPE +
	        				" now test value: sValue = " + sValue);
    	    			if (sValue.equals(OPENAZ_AME_USING_PARENT_LINETYPE_TRUE)) {
    	    				// default is false, so only set when true
    	    				usingParentLineType = true;
    	    			}
	    				log.trace(
	    					"TRACE:\t created usingParentLineType s: " + s +
	    					",  sValue: " + sValue + 
	    					",   usingParentLineType: " + usingParentLineType);
    				}
    				break;
    			case 7:
    				// attribute issuer should be good as is however we can
    				// abbreviate it as when and if it seems useful, which
    				// means adding some list process, similar to what is
    				// in AttributeId above
    				if (s.equals(OPENAZ_AME_ATTRIBUTE_ISSUER)) {
    					i = size; // force break on loop condition
    	    			log.trace(
	        				"TRACE:   Inner loop: key matched:" + 
	        				OPENAZ_AME_ATTRIBUTE_ISSUER +
	        				" now test value: sValue = " + sValue);
    					attributeIssuer = sValue;
	    				log.trace(
	    					"TRACE:\t created attributeIssuer s: " + s +
	    					",  sValue: " + sValue);
    				}
    				break;
    			case 8:
    				// mustBePresent is a boolean
    				if (s.equals(OPENAZ_AME_MUST_BE_PRESENT)) {
    					i = size; // force break on loop condition
    	    			log.trace(
	        				"TRACE:   Inner loop: key matched:" + 
	        				OPENAZ_AME_MUST_BE_PRESENT +
	        				" now test value: sValue = " + sValue);
    	    			if (sValue.equals(OPENAZ_AME_USING_PARENT_LINETYPE_TRUE)) {
    	    				// default is false, so only set when true
    	    				mustBePresent = true;
    	    			}
	    				log.trace(
	    					"TRACE:\t created mustBePresent s: " + s +
	    					",  sValue: " + sValue + 
	    					",   mustBePresent: " + mustBePresent);
    				}
    				break;
    			} // end switch(i)
       		} // end for (i<size)
    	} // end for (more keys)
    	
    	// TBD: replace this with obtaining the tokens to init the ame
		log.trace("TRACE:  " + 
			"\n\tLine# " + curLineCtx.getLineNumber() +
			": Creating AttributeMatchExpression to include in resultObject - " +
			"available tokens: \n\t\t" + curLineCtx.getTokens());
		
		// set some defaults, if necessary:
		if (attributeDataType.equals("")) {
			attributeDataType = 
				openAzDataTypeToXacmlDataTypeMap.get(
						OPENAZ_XACML_DATATYPE_STRING);
        	log.trace(
        		"TRACE: No \"dt\" token provided, so " + 
        		"setting attribute DataType to default: " +
        		attributeDataType);
		}
		if (attributeMatchId.equals("")) {
			attributeMatchId = 
				openAzMatchIdToXacmlMatchIdMap.get(
						OPENAZ_XACML_MATCH_ID_STR_EQUAL);
        	log.trace(
        		"TRACE: No \"dt\" token provided, so " + 
        		"setting attribute DataType to default: " +
        		attributeMatchId);
		}

		AttributeMatchExpression ame = 
    		new AttributeMatchExpression(
    				attributeDesignatorType, 
    				attributeMatchId, 
    				attributeDataType,
    				attributeValue,
    				attributeId,
    				attributeIssuer,
    				attributeFunctionId,
    				mustBePresent);
    	log.trace(
    		"TRACE: AttributeMatchExpresssion basic values:" +
    		"\n\tAttributeId = " + attributeId +
    		"\n\tAttributeValue = " + attributeValue +
     		"\n\tDataType = " + attributeDataType +
     		"\n\tMatchId = " + attributeMatchId);
    	if (usingParentLineType) {
    		ame.setUsingParentLineType(usingParentLineType);
    	}
    	ame.setDesignatorEntity(designatorEntity);
    	// probably don't need to set readAhead, because we 
    	// didn't read anything.
    	resultObject = new OpenAzXacmlObject(
    			currentLineType, ame, 
    			false, null);
    	
		log.trace(
			"TRACE: exiting processTS(): parentLineType: " + parentLineType);
    	return resultObject;
    } // end processTS
    
	/**
	 * Template for switch options	
	 * @return an OpenAzXacmlObject 
	 * containing any SunXacml Object
	 */
    public OpenAzXacmlObject processTemplate(
    			OpenAzLineContext curLineCtx)
    {
    	OpenAzXacmlObject resultObject = null;
    	return resultObject;
    }
    
    /**
     * Returns an OpenAzXacmlObject containing a Condition for SunXacml or
     * containing a null if no Condition expressions found.
     * <P>
     * A Condition for the current release is simply a container
     * of some AttributeMatchExpressions that by default are AND'd.
     * There is more that can be done w Conditions and Apply
     * expressions, which will be explored further as OpenAz
     * develops.
     * <p>
     * The FunctionId attribute of the Apply element is currently
     * incorporated with the AttributeMatchExpression. Whether this
     * construct will hold w more complex Condition capabilities is TBD.
     * @param curLineCtx
     * @return an OpenAzXacmlObject containing a SunXacml Condition
     * @throws OpenAzParseException
     */
    public OpenAzXacmlObject processCondition(
    			OpenAzLineContext curLineCtx)
    		throws OpenAzParseException {
    	
		OpenAzXacmlObject resultObject = null;
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		LineType currentLineType = curLineCtx.getLineType();
		int myLineNumber = curLineCtx.getLineNumber();
		String msg = curLineCtx.getMessage();
		
		OpenAzXacmlObject azObject = null;
		
		// basically if currentLineType is optional CD_RL then if 
		// we are looking for it, then require it to be there,
		// but we are not required to look for it.
		if ( ! (lookingForLineType == null) &&
			 ! (currentLineType.equals(lookingForLineType)) ) {
			// if current line is CD_RL and lookingFor is not null
			// AND lookingFor not null and is not CD_RL
			// then throw an exception.
			throw new OpenAzParseException(msg, curLineCtx.getLineNumber());
		}
		
		// if currentLineType is CD_RL, then its parent should
		// be RL, and it should have called with lookingFor=null,
		// since it is optional
		// TODO: assert RL:CD_RL 
		log.trace("TRACE: " + curLineCtx.getStateDescriptor() + 
				" switch level 2");

		// Structure to accumulate Rule Condition records
	    Set<AttributeMatchExpression> conditionAttrs = 
	    	new HashSet<AttributeMatchExpression>();
	
		// this loop looks for child records of a Target(Rule)
	    log.trace("TRACE: Try to read more records");
		boolean tryToReadMoreRecords = true;
		tryToReadMoreRecords = true;
		azObject = null;
		curLineCtx.setLookingForLineType(null); //extensionLineType
		while (tryToReadMoreRecords) {
			log.trace(
				"\nTRACE: *****************************************");
			log.trace(
				"TRACE: processCondition:" + curLineCtx.getStateDescriptor() + 
				" CALLING into readAzLine() using" +
				"\n\t\t\tparentLineNumber = " + myLineNumber);
			azObject = readAzLine(curLineCtx);
			log.trace(
				"TRACE: " + curLineCtx.getStateDescriptor() + 
				" SL2-" + curLineCtx.getStateDescriptor() + 
				": RETURNed to processCondition() " +
				"SL2-" + curLineCtx.getStateDescriptor() + 
				" loop from readAzLine(" +
				"\n\t\t\tparentLineNumber(myLineNumber) = " + 
						 myLineNumber +
				"\n\t\t\tparentLineType(currentLineType) = " +
						currentLineType +
				"\n\t\t\tlookingForType = " + null +
				"\n\t\t\treadAhead flag = " + azObject.getReadAhead());
			
			// Verify we got an AME as a child of Condition
			AttributeMatchExpression ame =
				(AttributeMatchExpression) azObject.getObject();
			if ( ! (ame == null)) {
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() + 
						" Line# " + curLineCtx.getLineNumber() + 
						": processCondition obtained AME: " + 
						"\n\t\tame.getId() =" + ame.getId() + 
						"\n\t\tame.getValue() = " + ame.getValue());
			} else {
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() + 
					" Line# " + curLineCtx.getLineNumber() +
					" processCondition did not get an AME, null object returned");
			}
			
			// set ctx LookingForLineType to enable continuation if more
			// AME (T*) records found
			LineType recordLineType = azObject.getLineType();
			curLineCtx.setLookingForLineType(recordLineType);
			log.trace(
				"TRACE: OpenAzXacmlObject LineType read under " + 
				"Condition loop for more records = " + 
				recordLineType);
			
			// SL3-RL:CD_RL: Switch level 3:
			switch (recordLineType) {
			case TS:
			case TR:
			case TA:
			case TE:
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() + 
						 ": " + recordLineType + ": switch level 3:" + 
						 recordLineType);
				conditionAttrs.add(ame);
				break;
			default:
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() + 
						": default: switch level 3: " + 
						azObject.getLineType());
				tryToReadMoreRecords = false; // terminate the loop
				break;
			} // end switch (recordLineType)
		} // end while (tryToReadMoreRecords)
		
		// move the condition's attr matches from HashSet to array
	    AttributeMatchExpression[] conditionAttrMatchExprs = 
			new AttributeMatchExpression[]{};
		int condItems = conditionAttrs.size();
		Iterator<AttributeMatchExpression> itCondItems = 
			conditionAttrs.iterator();
		if (condItems > 0) {
			conditionAttrMatchExprs = 
				new AttributeMatchExpression[condItems];
			for (int j=0;j<condItems;j++){
				conditionAttrMatchExprs[j] = itCondItems.next();
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() + 
					" PL:TG_PL: Adding condition AME: " + 
					conditionAttrMatchExprs[j].getId() +
					"\n\t\t\t value: " +
					conditionAttrMatchExprs[j].getValue() +
					"\n\t\t\t dataType: " +
					conditionAttrMatchExprs[j].getDataType() +
					"\n\t\t\t matchId: " +
					conditionAttrMatchExprs[j].getMatchId() +
					"\n\t\t\t attributeDesignator: " +
					conditionAttrMatchExprs[j].getDesignatorType());
			}
		}

		try {
			log.trace("TRACE:  " + 
				"\n\tLine# " + curLineCtx.getLineNumber() +
				": Creating Condition to include in resultObject - " +
				"available tokens: \n\t\t" + curLineCtx.getTokens());
			log.trace(
				"TRACE: " + curLineCtx.getStateDescriptor() + 
					" create a Condition for Rule");
			Apply ruleCondition =
				xpb.createRuleCondition(conditionAttrMatchExprs);
			if ( ! (ruleCondition == null) ) {
				ByteArrayOutputStream bo = new ByteArrayOutputStream();
				ruleCondition.encode(bo, new Indenter());
				log.trace("TRACE: rule condition = \n" + bo.toString()); 
			} else {
				log.trace("TRACE: rule condition = null"); 				
			}
			log.trace(
				"TRACE: " + curLineCtx.getStateDescriptor() + 
					" RL:CD_RL put Condition in " + 
					"OpenAzXacmlObject for return");
			log.trace(
				"TRACE: " + curLineCtx.getStateDescriptor() + 
				" put Condition in OpenAzXacmlObject " +
				"for return;\n\t\t also set the readAheadObject, with" +
				"the readAheadObject created by the last line read");
			log.trace(
				"TRACE: line# " + curLineCtx.getLineNumber() +
				",  readAheadObjectLineType = " + 
				azObject.getLineType() +
				", \n\t\treadAheadObject = " + 
				azObject.getReadAheadObject());
			resultObject = 
				new OpenAzXacmlObject(
					currentLineType, ruleCondition, 
					true, azObject.getReadAheadObject());
		} catch (URISyntaxException uri) {
			log.trace("URISyntaxException: (" +
					"lineNumber = " + myLineNumber + 
					")" +
					uri.getMessage());
		}
		return resultObject;
    } // end processCondition
     
  
    /**
     * An Obligation contains a collection of AttributeAssignments
     * that may be returned in a Xacml Response.
     * <p>
     * An Obligation has the following parameters to its constructor:
     * <pre>
     * Obligation(
     * 		URI id, 
     * 		int fulfillOn, 
     * 		List assignments)
     * 
     *  Constructor that takes all the data associated with an obligation.
     * </pre>
     */
    public OpenAzXacmlObject processObligation(
    			OpenAzLineContext curLineCtx)
    		throws OpenAzParseException {
    	
		OpenAzXacmlObject resultObject = null;
		LineType currentLineType = curLineCtx.getLineType();
		int myLineNumber = curLineCtx.getLineNumber();
		String msg = curLineCtx.getMessage();
		
		// create a list in prep for obligation attributes
		List<AttributeMatchExpression> obligationAmeList = 
			new ArrayList<AttributeMatchExpression>();
		
		// intent here is to loop thru all the Obligation attributes
		// (OA), which has the effect of processing one Obligation;
		// We want to readAzLine until we hit a terminator ( i.e. a
		// terminating LineType, meaning no more OA records expected
		// for the current Obligation.
		OpenAzXacmlObject azObject = null;
		curLineCtx.setLookingForLineType(LineType.OA); //extensionLineType
		boolean moreObligationAttributes = true;
		while (moreObligationAttributes){
			log.trace(
			"\nTRACE: *****************************************");
			log.trace(
				"TRACE: " + curLineCtx.getStateDescriptor() + 
				" processObligation:OB CALLING into readAzLine() using" +
				"\n\t\t\tparentLineNumber = " + myLineNumber);
			azObject = readAzLine(curLineCtx);
			log.trace(
				"TRACE: " + curLineCtx.getStateDescriptor() + 
				" SL2-OBS:OB: RETURNed to processObligation:OB " +
				"SL2-OBS:OB loop from readAzLine(" +
				"\n\t\t\tparentLineNumber(myLineNumber) = " + 
						 myLineNumber +
				"\n\t\t\tparentLineType(currentLineType) = " +
						currentLineType +
				"\n\t\t\tlookingForType = " + null +
				"\n\t\t\treadAhead flag = " + azObject.getReadAhead());
			
			LineType recordLineType = azObject.getLineType();
			// set this to enable continuation (extensionLineType)
			curLineCtx.setLookingForLineType(recordLineType);
			switch (recordLineType) {
			case OA:
				log.trace("TRACE: OBS:OB:OA switch level 3");
				// Try to save the attribute just returned
				try {
					// object for obl attr to be stored
					AttributeMatchExpression ame =  
						(AttributeMatchExpression) azObject.getObject();
					obligationAmeList.add(ame);
				} catch (ClassCastException cce) {
					// if the OA msg is bad end things here
					cce.printStackTrace(new PrintWriter(swStatic));
					String msgClassCast = "ClassCastException: " + 
						cce.getMessage() + 
						" looking for AttributeMatchExpression, " + 
						"got something else." + swStatic;
					throw new OpenAzParseException(
							msgClassCast, curLineCtx.getLineNumber());
				}		
				break;
			default:
				// Any non-OA LineType is effectively a terminator;
				// just break the loop, let ancestors determine
				// legitimacy of the terminator LineType
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() +
					" OBS:OB:default  switch level 3: " + 
						" next line is LineType: " + azObject.getLineType() + 
						"  terminate the OA loop");
				moreObligationAttributes = false; // terminate the loop
				break;
			} // end switch (recordLineType)					
		} // end while (moreObligationAttributes)
		
		// get the Obligation metadata from the current OB line
		// Allocate variables to collect Obligation token data:
		String obligationId = null;
	    String obligationFulfillOnStr = null;

	    // Go thru tokens to get metadata values for the Obligation
        String thisObject = "Obligation";
    	Map<String,String> map = curLineCtx.getTokens();
    	log.trace(
    		"TRACE: Building a " + thisObject + 
    		" using these tokens: " + map);
    	Set<String> keys = map.keySet();
    	for (String s : keys) {
    		String sValue = map.get(s);
    		log.trace("TRACE: loop for " + thisObject + 
    				":\n\t\t  key: " + s +
    				",\n\t\t  sValue: " + sValue);
    		if (sValue.equals("!")) {
    			log.trace(
    				"TRACE:\t sValue is a null, skip to next key");
    			continue;
    		}
    		int size = keys.size();
    		for (int i=0; i<size; i++) {
    			log.trace(
    				"TRACE:   Inner loop: (" + i +
    				") match key then value: key = " + s);
    			switch (i) {
    			case 0:
    				if (s.equals(OPENAZ_OBLIGATION_ID)) {
    					i = size; // force break on loop condition
    	    			log.trace(
	        				"TRACE:   Inner loop: key matched:" + 
	        				OPENAZ_OBLIGATION_ID +
	        				" now test value: sValue = " + sValue);
    	    			// append line num to ruleId, may remove/option later	    	    		
    	    			obligationId = sValue + "-Line-" +
 								new Integer(myLineNumber).toString();;
	    				log.trace(
		    					"TRACE:\t created obligationId: key: " + s +
		    					",  with sValue: " + sValue + 
		    					"\n\t\t\tresults in obligationId\n\t\t\t\t" + 
		    					obligationId);
    				}
    				break;
    			case 1:
    				if (s.equals(OPENAZ_OBLIGATION_FULFILL_ON)) {
    					i = size; // force break on loop condition
    	    			log.trace(
	        				"TRACE:   Inner loop: key matched:" + 
	        				OPENAZ_OBLIGATION_FULFILL_ON +
	        				" now test value: sValue = " + sValue);
    	    			obligationFulfillOnStr = sValue;
	    				log.trace(
	    					"TRACE:\t created obligation fulfillOn key: " + s +
	    					",  with sValue: " + sValue + 
	    					"\n\t\t\tresults in obligationFulfillOnStr" + 
	    					"\n\t\t\t\t" + obligationFulfillOnStr +
    						" which will be converted " + 
    						"to lower case for processing");
	    				// force it lower case, so users don't need to worry
	    				obligationFulfillOnStr = 
	    						obligationFulfillOnStr.toLowerCase();
    				}
    				break;
    			} // end switch (i)
    		} // end for (i<size)
    	} // end for (more keys)
		
		// set default data
    	if (obligationId == null)
    	{
    		obligationId = "defaultObligationID-LineNumber-" +
				new Integer(myLineNumber).toString();
    		log.trace(
    			"TRACE: no ObligationId provided, so setting default to: " +
    			obligationId);
    	}
    	if (obligationFulfillOnStr == null) {
    		obligationFulfillOnStr = OPENAZ_RULE_EFFECT_DENY;
    		log.trace(
        			"TRACE: no FulfillOn provided, so setting default to: " +
        			obligationFulfillOnStr);
    	}
		int obligationFulfillOn = Result.DECISION_DENY; // default
		if (obligationFulfillOnStr.length() > 0) {
			if (obligationFulfillOnStr.equals(OPENAZ_RULE_EFFECT_DENY)) {
				obligationFulfillOn = Result.DECISION_DENY;
				log.trace("TRACE(" + curLineCtx.getLineNumber() + 
					") setting Obligation FulfillOn = Deny");
			} else if (obligationFulfillOnStr.equals(OPENAZ_RULE_EFFECT_PERMIT)) {
				obligationFulfillOn = Result.DECISION_PERMIT;
				log.trace("TRACE(" + curLineCtx.getLineNumber() + 
					") setting Obligation FulfillOn = Permit");
			}
		}
	    
		// process Obligation attributes collected above in obligationAmeList
		// move the AMEs to an array and process
	    int ameSize = obligationAmeList.size();
	    AttributeMatchExpression[] obligationAme = 
	    	new AttributeMatchExpression[ameSize];
	    Iterator<AttributeMatchExpression> itAme = 
	    	obligationAmeList.iterator();
	    for (int i=0; i<ameSize; i++){
	    	obligationAme[i] = itAme.next();
	    	log.trace(
	    		"TRACE: retrieving obligation attr: " +
	    			obligationAme[i].attributeId);
	    }
	    List obligationAttributeList = new ArrayList();
	    try {
	        obligationAttributeList = 
	        	xpb.createObligationAttributeList(obligationAme);
			log.trace("TRACE:  " + 
				"\n\tLine# " + curLineCtx.getLineNumber() +
				": Creating Obligation to include in resultObject - " +
				"available tokens: \n\t\t" + curLineCtx.getTokens());
		    Obligation obligation = new Obligation(
		    		new URI(obligationId),
		    		obligationFulfillOn,
		    		obligationAttributeList);
		
		    // what we are trying to do here is
		    // return the obl plus reread the OB
		    resultObject = new OpenAzXacmlObject(
					currentLineType,
					obligation,
					true,
					azObject.getReadAheadObject());
	    } catch (URISyntaxException uri) {
	    	String msgURI = uri.getMessage();
			throw new OpenAzParseException(
					msgURI, myLineNumber);		        	
	    }
	    return resultObject;
    } // end processObligation
    
    /**
     * The "Obligations" element is simply a collector of
     * "Obligation" elements. There are no properties or
     * metadata associated specifically w the "Obligations"
     * element.
     * @param curLineCtx
     * @return an OpenAzXacmlObject containing a SunXacml Obligations
     * @throws OpenAzParseException
     */
    public OpenAzXacmlObject processObligations(
    			OpenAzLineContext curLineCtx)
    		throws OpenAzParseException {
		OpenAzXacmlObject resultObject = null;
		OpenAzXacmlObject azObject = null;
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		LineType currentLineType = curLineCtx.getLineType();
		String msg = curLineCtx.getMessage();
		int myLineNumber = curLineCtx.getLineNumber();
    	Set obligationSet = new HashSet();
		log.trace("TRACE: PL:OBS case (OBS current, PL parent)");
		log.trace(
			"TRACE: SL2-PL:OBS lookingForType = " + 
			lookingForLineType +
			"   currentLineType = " + currentLineType);
		// if current is OBS, then should match lookingFor
		if ( ! (lookingForLineType == null) &&
			 ! (currentLineType.equals(lookingForLineType)) ) {
			// if current line is OBS and lookingFor is not null
			// AND lookingFor not null and is not OBS
			// then throw an exception.
			throw new OpenAzParseException(msg, curLineCtx.getLineNumber());
		}
		
		// loop to collect a set of Obligations:
		// we already have the OBS, subsequent lines should be OB
		// so set lookingFor to OB
		curLineCtx.setLookingForLineType(LineType.OB);
		boolean tryForMoreRecords = true;
		tryForMoreRecords = true;
		while (tryForMoreRecords) {
			log.trace(
			"\nTRACE: *****************************************");
			log.trace(
				"TRACE: processObligations:OBS CALLING into readAzLine() using" +
				"\n\t\t\tparentLineNumber = " + myLineNumber);
			if ( (azObject == null) ) { 
				// first time thru, so no readAhead	
				curLineCtx.setReadAhead(false);
				curLineCtx.setReadAheadLine(null);
				azObject = readAzLine(curLineCtx);
			} else { 
				// use azObject to set the readAhead data
				curLineCtx.setReadAhead(azObject.getReadAhead());
				curLineCtx.setReadAheadLine((String)azObject.getReadAheadObject());
				azObject = readAzLine(curLineCtx);
			}
			log.trace(
				"TRACE: SL2-PL:OBS: RETURNing to processObligations() " +
				"SL2-PL:OBS loop from readAzLine(" +
				"\n\t\t\tparentLineNumber(myLineNumber) = " + 
						 myLineNumber +
				"\n\t\t\tparentLineType(currentLineType) = " +
						currentLineType +
				"\n\t\t\tlookingForType = " + null +
				"\n\t\t\treadAhead flag = " + azObject.getReadAhead());
			LineType recordLineType = azObject.getLineType();
			// set this to enable continuation (extensionLineType)
			curLineCtx.setLookingForLineType(recordLineType);
			log.trace(
				"TRACE: OpenAzXacmlObject LineType read " + 
				"under Target loop for more records = " + 
				recordLineType);
			
			// SL3-PL:OBS: Switch level 3:
			switch (recordLineType) {
			//SL3-PL:OBS:OB:
			case OB:
				log.trace(
					"TRACE: SL3-PL:OBS:OB switch level 3");
				Obligation obligation = 
					(Obligation) azObject.getObject();
				obligationSet.add(obligation);
				break;
			default:
				log.trace(
					"TRACE: PL:TG_PL:default  switch level 3: " + 
						azObject.getLineType());
				tryForMoreRecords = false; // terminate the loop
				break;
			} // end switch (recordLineType)
		} // end obligationList loop (while (tryForMoreRecords))
		log.trace("TRACE:  " + 
			"\n\tLine# " + curLineCtx.getLineNumber() +
			": Creating Obligations to include in resultObject - " +
			"available tokens: \n\t\t" + curLineCtx.getTokens());
		resultObject = new OpenAzXacmlObject(
				currentLineType, obligationSet, 
				true, azObject.getReadAheadObject());
		return resultObject;
    } // end processObligations

    /**
     * Returns an OpenAzXacmlObject containing a List of Rules
     * for SunXacml.
     * @param curLineCtx
     * @return an OpenAzXacmlObject containing a SunXacml Rule List
     * @throws OpenAzParseException
     */
    public OpenAzXacmlObject processRuleList(
    			OpenAzLineContext curLineCtx)
    		throws OpenAzParseException {
    	
		OpenAzXacmlObject resultObject = null;
		OpenAzXacmlObject azObject = null;
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		LineType currentLineType = curLineCtx.getLineType();
		int myLineNumber = curLineCtx.getLineNumber();
		String msg = curLineCtx.getMessage();
	   	List ruleList = new ArrayList();
	   	
	    // "PL:RLS" should = curLineCtx.getStateDescriptor()
	   	// TODO: possibly use the above as basis of set of "asserts"
		log.trace(
				"TRACE: SL2-" + curLineCtx.getStateDescriptor() + 
				" case");
		log.trace(
			"TRACE: SL2-" + curLineCtx.getStateDescriptor() + 
				" lookingForType = " + lookingForLineType +
			"   currentLineType = " + currentLineType);
		
		// if current is RLS, then should match lookingFor
		if ( ( ! (lookingForLineType == null) ) &&
			 ( ! (currentLineType.equals(lookingForLineType)) ) ) {
			// if current line is RLS and lookingFor is not null
			// AND lookingFor not null and is not TG_PL
			// then throw an exception.
			throw new OpenAzParseException(msg, curLineCtx.getLineNumber());
		}
		
		// loop to build ruleList:
		// we already have the RLS, subsequent lines should be RL
		//LineType ruleLineType = LineType.RL;
		curLineCtx.setLookingForLineType(LineType.RL);
		boolean tryForMoreRecords = true;
		tryForMoreRecords = true;
		while (tryForMoreRecords) {
			log.trace(
			"\nTRACE: *****************************************");
			log.trace(
				"TRACE: SL2-" + curLineCtx.getStateDescriptor() + 
				": processRuleList:RLS CALLING into readAzLine() using" +
				"\n\t\t\tparentLineNumber = " + myLineNumber);
			if ( (azObject == null) ) { 
				// first time thru, so no readAhead	
				azObject = 
					readAzLine(curLineCtx);
			} else { 
				// use azObject to set the readAhead data
			    curLineCtx.setReadAhead(azObject.getReadAhead()); 
			    curLineCtx.setReadAheadLine(
			    		(String)azObject.getReadAheadObject());
				azObject = readAzLine(curLineCtx);
			}
			log.trace(
				"TRACE: SL2-" + curLineCtx.getStateDescriptor() + 
				": RETURNing to processRuleList() " +
				"SL2-PL:RLS loop from readAzLine(" +
				"\n\t\t\tparentLineNumber(myLineNumber) = " + 
						 myLineNumber +
				"\n\t\t\tparentLineType(currentLineType) = " +
						currentLineType +
				"\n\t\t\tlookingForType = " + null +
				"\n\t\t\treadAhead flag = " + azObject.getReadAhead());
			LineType recordLineType = azObject.getLineType();
			// set this to enable continuation (extensionLineType)
			curLineCtx.setLookingForLineType(recordLineType);
			log.trace(
				"TRACE: OpenAzXacmlObject LineType read " + 
				"under Target loop for more records = " + 
				recordLineType);
			
			// SL3-PL:RLS: Switch level 3:
			switch (recordLineType) {
			//SL3-PL:RLS:RL:
			case RL:
				log.trace(
					"TRACE: SL3-PL:RLS:RL switch level 3");
				Rule rule = (Rule) azObject.getObject();
				ruleList.add(rule);
				break;
			default:
				log.trace(
					"TRACE: PL:TG_PL:default  switch level 3: " + 
						azObject.getLineType());
				tryForMoreRecords = false; // terminate the loop
				break;
			} // end switch (recordLineType)
		} // end ruleList loop (while (tryForMoreRecords))
		log.trace("TRACE:  " + 
			"\n\tLine# " + curLineCtx.getLineNumber() +
			": Creating Rules to include in resultObject - " +
			"available tokens: \n\t\t" + curLineCtx.getTokens());
		resultObject = new OpenAzXacmlObject(
				currentLineType, ruleList, 
				true, azObject.getReadAheadObject());
		return resultObject;
    } // end processRuleList
    
    /**
     * TODO: This is now general for all Targets, rename to just processTarget,
     * but retain the info of which type of Target being processed
     * in the logging. i.e. this processes: TG_PL, TG_PS, and TG_RL.
     * @param curLineCtx
     * @return an OpenAzXacmlObject containing a SunXacml Target
     * @throws OpenAzParseException
     */
    public OpenAzXacmlObject processTargetPolicy(
	    		OpenAzLineContext curLineCtx)
	    	throws OpenAzParseException {
    	
		OpenAzXacmlObject resultObject = null;
		OpenAzXacmlObject azObject = null;
		LineType lookingForLineType = curLineCtx.getLookingForLineType();
		LineType currentLineType = curLineCtx.getLineType();
		String msg = curLineCtx.getMessage();
		int myLineNumber = curLineCtx.getLineNumber();
		// if current is TG_PL, then should match lookingFor
		if ( (lookingForLineType == null) ||
			 ! (currentLineType.equals(lookingForLineType)) ) {
			// if current line is TG_PS and lookingFor is null
			// OR lookingFor not null and is not TG_PL (TG_*,
			// since this module generalized for all Target types)
			// then throw an exception.
			throw new OpenAzParseException(msg, curLineCtx.getLineNumber());
		}
		// basically if currentLineType is TG_PL then we 
		// must be looking for it, ow exception.
		//log.trace("TRACE: PL:TG_P* switch level 2");
		log.trace(
			"TRACE: PL:" + curLineCtx.getLineType() +" switch level 2");
		
		// Structures to accumulate Target records
	    // use Lists to keep attrs in order read from policy
	    List<AttributeMatchExpression> subjectAttrsList = 
	    	new ArrayList<AttributeMatchExpression>();
	    List<AttributeMatchExpression> resourceAttrsList = 
	    	new ArrayList<AttributeMatchExpression>();
	    List<AttributeMatchExpression> actionAttrsList = 
	    	new ArrayList<AttributeMatchExpression>();

	    // loop until all Target records have been read:
	    log.trace("TRACE: Try to read more records");
		boolean tryForMoreRecords = true;
		// this loop looks for child records of a Target(Policy)
		curLineCtx.setLookingForLineType(null); // extensionLineType
		while (tryForMoreRecords) {
			log.trace(
			"\nTRACE: *****************************************");
			log.trace(
				"TRACE: " + curLineCtx.getStateDescriptor() + 
				" processTargetPolicy:TG_PL CALLING into readAzLine() " +
				"using\n\t\t\tparentLineNumber = " + myLineNumber +
				" parentLineType = " + currentLineType);
			azObject = 
				readAzLine(curLineCtx); 
			log.trace(
				"TRACE: " + curLineCtx.getStateDescriptor() + 
				" processTargetPolicy:TG_PL RETURNed from readAzLine(" +
				"\n\t\t\tparentLineNumber(myLineNumber) = " + 
						 myLineNumber +
				"\n\t\t\tparentLineType(currentLineType) = " +
						currentLineType +
				"\n\t\t\tlookingForType = " + null +
				"\n\t\t\treadAhead flag = " + azObject.getReadAhead());
			AttributeMatchExpression ame =
				(AttributeMatchExpression) azObject.getObject();
			if ( ! (ame == null)) {
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() + 
					" Line# " + curLineCtx.getLineNumber() + 
						": processTargetPolicy obtained AME: " + 
						"\n\t\tame.getId() =" + ame.getId() + 
						"\n\t\tame.getValue() = " + ame.getValue());
			} else {
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() + 
					" Line# " + curLineCtx.getLineNumber() +
					" processTargetPolicy did not get an AME, null obj returned");
			}
			
			// Set curLineCtx.setLookingForLineType to enable continuation
			// ie. continuation or extensionLineType = recordLineType;
			LineType recordLineType = azObject.getLineType();
			curLineCtx.setLookingForLineType(recordLineType);
			log.trace(
				"TRACE: OpenAzXacmlObject LineType read " + 
				"under Target loop for more records = " + 
				recordLineType);
			
			// SL3-PL:TG_PL: Switch level 3:
			switch (recordLineType) {
			case TS:
				log.trace(
					"TRACE: PL:TG_PL:TS switch level 3: adding subject match");
				//subjectAttrs.add(ame);
				subjectAttrsList.add(ame);
				break;
			case TR:
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() + 
					" PL:TG_PL:TR switch level 3: adding resource match");
				//resourceAttrs.add(ame);
				resourceAttrsList.add(ame);
				break;
			case TA:
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() + 
					" PL:TG_PL:TA switch level 3: adding action match");
				//actionAttrs.add(ame);
				actionAttrsList.add(ame);
				break;
			case TE:
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() + 
					" PL:TG_PL:TE switch level 3: " + 
						"tbd: not handling env match");
				break;
			default:
				log.trace(
					"TRACE: " + curLineCtx.getStateDescriptor() + 
					" PL:TG_PL:default  switch level 3: " + 
						azObject.getLineType());
				tryForMoreRecords = false; // terminate the loop
				break;
			} // end switch (recordLineType)
		} // end while (tryForMoreRecords)
		try {
			log.trace("TRACE:  " + 
				"\n\tLine# " + curLineCtx.getLineNumber() +
				": Creating Target(" +  curLineCtx.getStateDescriptor() + 
				") to include in resultObject - " +
				"available tokens: \n\t\t" + curLineCtx.getTokens());
			log.trace(
				"TRACE: " + curLineCtx.getStateDescriptor() + 
					" PL:TG_PL create a Target for Policy");
			
			// Consider a 2 dim matrix, where the 1st (left) index is
			// the row #, and the 2nd (right) index is the col #;
			// for example:
			//    [01][01]  [01][02]  [01][03]  ...    
			//    [02][01]  [02][02]  [02][03]  ...    
			//    [03][01]  [03][02]  [03][03]  ...  
			//	     ...       ...       ...    ...
			//   AND'ing is done within a row (i.e. across populated columns)
			//    OR'ing is done between rows (i.e. all you need is to satisfy
			//									one of the rows, but you must
			//								 	satisfy all reqts in that row.
			//
			//  Continuation statements ("+" instead of LineType) are added
			//  to the row that was started by the previous statement that
			//  had an explicit LineType. (Note this could also be done or
			//  have been done with a token explicitly, which is obvious
			//  because the technique utilized the token to enable the
			//  semantics implied by the syntax.
			
			// with the lists, AMEs come in order, and the AND'd AMEs
			// have usingParentLineType set to true. To get required 2-d
			// array dimensions, need to scan the list once and calculate
			// the 2 dimensions
			
			// TBD: in retrospect, probably should have designed 
			// XacmlPolicyBuilder (xpb), to take Lists of Lists rather 
			// than these 2-d arrays, but that can always be done in the future.

			List<Integer> arraySize = null;
		    AttributeMatchExpression[][] subjectAttrMatchExprs = null;
		    AttributeMatchExpression[][] resourceAttrMatchExprs = null;
		    AttributeMatchExpression[][] actionAttrMatchExprs = null;
			
			arraySize = calculateArraySize(curLineCtx, subjectAttrsList);
			subjectAttrMatchExprs = // move data from the list to 2-d array
				moveData(curLineCtx, arraySize, subjectAttrsList);

			arraySize = calculateArraySize(curLineCtx, resourceAttrsList);
			resourceAttrMatchExprs = // move data from the list to 2-d array
				moveData(curLineCtx, arraySize, resourceAttrsList);

			arraySize = calculateArraySize(curLineCtx, actionAttrsList);
			actionAttrMatchExprs = // move data from the list to 2-d array
				moveData(curLineCtx, arraySize, actionAttrsList);
						
			Target policyTarget = 
				xpb.createTargetPolicyOrRule(
						subjectAttrMatchExprs, 
						resourceAttrMatchExprs, 
						actionAttrMatchExprs, 
						lookingForLineType);
			log.trace(
				"TRACE: PL:TG_PL put Target in OpenAzXacmlObject " + 
				"for return");
			log.trace(
				"TRACE: PL:TG_PL put Target in OpenAzXacmlObject " +
				"for return; also set the readAheadObject, with" +
				"the readAheadObject created by the last line read");
			log.trace(
				"TRACE: Line #: " + curLineCtx.getLineNumber() +
				",  readAheadObjectLineType = " + 
				azObject.getLineType() +
				"\n\t\t\treadAheadObject = " + 
				azObject.getReadAheadObject());
			resultObject = 
				new OpenAzXacmlObject(
					currentLineType, policyTarget, 
					true, azObject.getReadAheadObject());
		} catch (URISyntaxException uri) {
			log.trace("URISyntaxException: (" +
					"curLineCtx.getLineNumber() = " + myLineNumber + 
					")" +
					uri.getMessage());
		}
		return resultObject;
    } // end processTargetPolicy
  
    
    public AttributeMatchExpression[][] moveData(
    			OpenAzLineContext curLineCtx, 
    			List<Integer> arraySize, 
    			List<AttributeMatchExpression> attrsMatchExprsList) {
    	
    	// declare a return 2-d array
    	AttributeMatchExpression[][] attrMatchExprs = null;
    	Iterator<AttributeMatchExpression> itAmeItems = 
			attrsMatchExprsList.iterator();
    	/*
		int orSize = arraySize[0];		// number of rows
		int andMaxSize = arraySize[1];  // max number of columns
		int totalSize = arraySize[2];	// total items (AMEs) to store
		*/
    	int totalSize = arraySize.size();
    	Iterator<Integer> itSize = arraySize.iterator();
		//if (orSize > 0) {
		if (totalSize > 0) {
			// allocate the number of rows first
			attrMatchExprs = 
				new AttributeMatchExpression[totalSize][];
			// init the indexes; init row to -1 as "init indicator"
			int orIndex = -1; 	// (row number - left index)
			int andIndex = 0; 	// (column number - right index)
			AttributeMatchExpression[] currentAmes = null;
			AttributeMatchExpression currentAme = null;
			// iterator over the integer lengths of the subarrays
			// using each length as the length to allocate for subarray
			//for (int j=0;j<totalSize;j++){
			while (itSize.hasNext()) {
				orIndex++; // from init -1, real start is at zero
				Integer subArrayLength = itSize.next();
				int subLength = subArrayLength.intValue();
				log.trace("\nTRACE: {" + curLineCtx.getLineNumber() + 
						") subLength = " + subLength);
				currentAmes = new AttributeMatchExpression[subLength];
				for (andIndex=0;andIndex<subLength;andIndex++) {
					currentAme = itAmeItems.next();
					currentAmes[andIndex] = currentAme;
					log.trace(
						"TRACE: " + curLineCtx.getStateDescriptor() + 
						" PL:TG_PL: Adding subject AME: " + 
						"\n\t\tto array location [orIndex][andIndex] = [" +
						orIndex + "][" + andIndex + "]" +
						currentAme.getId() +
						"\n\t\t\t value: " +
						currentAme.getValue() +
						"\n\t\t\t dataType: " +
						currentAme.getDataType() +
						"\n\t\t\t matchId: " +
						currentAme.getMatchId() +
						"\n\t\t\t attributeDesignator: " +
						currentAme.getDesignatorType() +
						"\n\t\t\t entity type: " +
						currentAme.getDesignatorEntity());
				}
				
				// put the currentAmes in the designated array location
				log.trace(
					"TRACE: Storing " + currentAme.getDesignatorEntity() + 
					" AMEs of length " + andIndex + 
					" in attrMatchExprs[" + orIndex + "][]");
				attrMatchExprs[orIndex] = currentAmes;					
			}
		}
		return attrMatchExprs;
    } // end moveData

    /**
     * Returns List<Integer> where the list is integers that represent
     * the lengths of each "row" in the array set that will need
     * to be produced from the input list of AMEs, which is an ordered
     * list, where the first entry must have usingParentLineType set
     * to false. Any subsequent AME entries that have usingParentLineType
     * set to true, are placed in the same row as the previous AME having
     * this property set to false. Two falses in sequence simply means the
     * first true represents a row of AMEs w one element.
     * <p>
     * Note: the seemingly odd incrementing thru the list where the first
     * pass thru the loop skips the row processing, and after the last 
     * item in the list is read, another pass thru the loop is enabled,
     * seems to be inherent in the nature of the structure, where it is
     * the "next" item that determines the fate of the current item, and
     * the "next" item includes the null that comes after the last item
     * has been read.
     */
    public List<Integer> calculateArraySize(
    				OpenAzLineContext curLineCtx, 
    				List attrsList) { 
    	
    	List<Integer> intList = new ArrayList<Integer>();
		int andLength = 1; // initialize to 1 for 1st time thru
		int andLengthMax = 0;
		int orLength = 0;
		boolean firstPass = true; // enables init logic on 1st pass thru loop
		boolean lastPass = false; // enables extra pass thru loop for cleanup
		AttributeMatchExpression ameItem = null;
		Iterator<AttributeMatchExpression> itAndOrItems = null;
		
		int totalLength = attrsList.size();
		if (totalLength > 0) {
			itAndOrItems = attrsList.iterator();
			while (itAndOrItems.hasNext() || (lastPass == false)){
				// this only activated "after" no more items to read
				if ( ! itAndOrItems.hasNext() ) {
					log.trace(
						"TRACE: have now completed extra cleanup pass, " +
						"so terminate the loop");
					lastPass = true; // break the loop
				} else {
					ameItem = (AttributeMatchExpression) itAndOrItems.next();
					log.trace(
						"TRACE: " + curLineCtx.getStateDescriptor() + 
						": ame.attributeId = " + ameItem.attributeId +
						",\n\t\t ame.usingParentLineType = " + 
						ameItem.getUsingParentLineType());
				}
				// ! usingParentLineType means start a new row
				if ( ! ameItem.getUsingParentLineType() || lastPass ) {
					// if first pass, then we implicitly have just
					// started the first row; this condition is intended to
					// trigger when we are "both" finishing a prev row,
					// and starting a new one.
					if (firstPass) {
						// it only happens once, after this we will always
						// be finishing a prev row
						log.trace(
							"TRACE: first pass thru loop complete, " + 
							"clear flag to enable row finishing logic");
						log.trace(
							"TRACE: skip row completion logic on first pass, " +
							"since no rows have been completed yet.");
						firstPass = false;
					} else {
						log.trace(
							"TRACE: completing row (orLength) #: " + orLength +
							"  with width (andLength) = " + andLength + "\n");
						// keep a list of the row lengths, which means saving
						// the length of row just completed, and starting a 
						// new row by incrementing orLength
						intList.add(new Integer(andLength));
						orLength++;
						if (lastPass)
							log.trace(
								"TRACE: list of lengths: " + intList);
					}
					andLength = 1; // set/reset andLength
					if (orLength == 1) andLengthMax = 1; // init max
				} else {
					andLength++;
					if (andLength>andLengthMax) {
						andLengthMax = andLength;
					}
					log.trace(
						"TRACE: continue andLength = " + andLength);
				}
			} // end while (itAndOrItems.hasNext() || (lastPass == false))
			log.trace("TRACE: (" + curLineCtx.getLineNumber() +
					"): " + curLineCtx.getLineType() +
					": ame 2-d array " + ameItem.getDesignatorEntity() + 
					" results: " +
					"\n\t\t\torLength = " + orLength +
					"\n\t\t\tandLengthMax = " + andLengthMax +
					"\n\t\t\ttotalLength = " + totalLength +
					"\n\t\t\tfinal andLength = " + andLength); 
		} // end if (totalLength > 0)
		return intList;
    } // end calculateArraySize
	
    /**
     * Thie module should build a Rule and return it. A Rule
     * has a constructor with the following args (from SunXacml javadoc):
     * <pre>
     * Rule(
     * 		URI id, 
     * 		int effect, 
     * 		String description, 
     * 		Target target, 
     * 		Apply condition)
     *  
     * Creates a new Rule object.
     * </pre>
     * @param curLineCtx
     * @return an OpenAzXacmlObject containing a SunXacml Rule
     * @throws OpenAzParseException
     */
	public OpenAzXacmlObject processRule(
				OpenAzLineContext curLineCtx)
			throws OpenAzParseException {
		
		int myLineNumber = curLineCtx.getLineNumber(); 
		LineType currentLineType = curLineCtx.getLineType();

		// Object container for returning results
		OpenAzXacmlObject resultObject = null;

		// have parameters for this Rule above
		// First we are looking for a Rule Target on the next line,
		// which for now we are requiring at least an empty Target,
		// meaning covers all, be included.
		//LineType lookingForLineType = LineType.TG_RL;
		curLineCtx.setLookingForLineType(LineType.TG_RL);
		log.trace(
				"\nTRACE: *****************************************");
		log.trace(
				"TRACE: processRule-1 CALLING into readAzLine() using" +
				"\n\t\t\tparentLineNumber = " + myLineNumber);
		OpenAzXacmlObject rlTarget = readAzLine(curLineCtx);
		log.trace(
				"TRACE: RETURNing to processRule-1 from readAzLine(" +
				"\n\t\t\tparentLineNumber = " + myLineNumber +
				"\n\t\t\tparentLineType = " + currentLineType +
				"\n\t\t\tlookingForType = " + curLineCtx.getLookingForLineType() +
				"\n\t\t\treadAhead flag = " + rlTarget.getReadAhead());
		try {
			Target target = (Target) rlTarget.getObject();
			log.trace("Is this a Target: " + target.getClass().getName());
			log.trace("Was a readAhead done: " + rlTarget.getReadAhead());
			if (rlTarget.getReadAhead()) {
				log.trace("rlTarget.getReadAheadObject = " + 
						rlTarget.getReadAheadObject());
				if ( ! (rlTarget.getReadAheadObject() == null) ) {
					String readAheadObject = 
						(String) rlTarget.getReadAheadObject();
					log.trace("rlTarget.getReadAheadObject = " +
							readAheadObject);
				} else
					log.trace("Where's the readAheadObject????");
			}
			// TODO: Assuming target was found, now need to read another 
			// line to get Conditions, and whatever can terminate
			// the Rule
			
			// There may be a Condition after the Rule Target, but
			// since not required, looking for is null.
			curLineCtx.setLookingForLineType(null);
			curLineCtx.setReadAhead(rlTarget.getReadAhead());
			curLineCtx.setReadAheadLine((String)rlTarget.getReadAheadObject());
			log.trace(
				"\nTRACE: *****************************************");
			log.trace(
				"TRACE: processRule-2 CALLING into readAzLine() using" +
				"\n\t\t\tparentLineNumber = " + myLineNumber);
			OpenAzXacmlObject rlCondition = readAzLine(curLineCtx);
			log.trace(
				"TRACE: RETURNing into processRule-2 from readAzLine(" +
				"\n\t\t\tparentLineNumber = " + myLineNumber +
				"\n\t\t\tparentLineType = " + currentLineType +
				"\n\t\t\tlookingForType = " + curLineCtx.getLookingForLineType() +
				"\n\t\t\treadAhead flag = " + rlCondition.getReadAhead());

			// Check to see if this line is a Condition, in which
			// case we need to include it in the Rule:
    		Apply condition = null;
			condition = (Apply) rlCondition.getObject();
			if ( ! (condition == null) ) {
				log.trace("Is this a Condition: " +
						condition.getClass().getName());
			}
			
			// Allocate variables to collect Rule token data:
			String ruleId = "";
		    String description = "";
		    String ruleEffectStr = "";

		    // Go thru tokens to get metadata values for the Rule
	        String thisObject = "Rule";
	    	Map<String,String> map = curLineCtx.getTokens();
	    	log.trace(
	    		"TRACE: Building a " + thisObject + 
	    		" using these tokens: " + map);
	    	Set<String> keys = map.keySet();
	    	for (String s : keys) {
	    		String sValue = map.get(s);
	    		log.trace("TRACE: loop for " + thisObject + 
	    				":\n\t\t  key: " + s +
	    				",\n\t\t  sValue: " + sValue);
	    		if (sValue.equals("!")) {
	    			log.trace(
	    				"TRACE:\t sValue is a null, skip to next key");
	    			continue;
	    		}
	    		int size = keys.size();
	    		for (int i=0; i<size; i++) {
	    			log.trace(
	    				"TRACE: " + curLineCtx.getStateDescriptor() + 
	    				": Inner loop: (" + i +
	    				") match key then value: key = " + s);
	    			switch (i) {
	    			case 0:
	    				if (s.equals(OPENAZ_RULE_ID)) {
	    					i = size; // force break on loop condition
	    	    			log.trace(
		        				"TRACE:   Inner loop: key matched:" + 
		        				OPENAZ_RULE_ID +
		        				" now test value: sValue = " + sValue);
	    	    			// append line num to ruleId, may remove/option later	    	    		
	    	    			ruleId = sValue + "-Line-" +
	 								new Integer(myLineNumber).toString();;
		    				log.trace(
		    					"TRACE:\t created ruleId: key: " + s +
		    					",  with sValue: " + sValue + 
		    					"\n\t\t\tresults in ruleId\n\t\t\t\t" + ruleId);
	    				}
	    				break;
	    			case 1:
	    				if (s.equals(OPENAZ_RULE_EFFECT)) {
	    					i = size; // force break on loop condition
	    	    			log.trace(
		        				"TRACE:   Inner loop: key matched:" + 
		        				OPENAZ_RULE_EFFECT +
		        				" now test value: sValue = " + sValue);
	    	    			ruleEffectStr = sValue;
		    				log.trace(
		    					"TRACE:\t created rule ruleEffect key: " + s +
		    					",  with sValue: " + sValue + 
		    					"\n\t\t\tresults in ruleEffect\n\t\t\t\t" + 
		    					ruleEffectStr +
		    					" which will be converted to " + 
		    					"lower case for processing");
		    				// make it lower case, so users don't need to worry
		    				ruleEffectStr = ruleEffectStr.toLowerCase();
	    				}
	    				break;

	    			} // end switch (i)
	    		} // end for (i<size)
	    	} // end for (more keys)
	    	
			// Rule dummy metadata
	    	if (ruleId.equals("")) {
				ruleId = 
					"dummyRuleId-LineNumber-" + 
						new Integer(myLineNumber).toString(); // TBD: get from token
	    	}
	    	if (description.equals("")) {
	    		description = 
	    			"dummyRuleDescription"; // TBD: get from token
	    	}
			int ruleEffect = Result.DECISION_DENY; // default
			if (ruleEffectStr.length() > 0) {
				if (ruleEffectStr.equals(OPENAZ_RULE_EFFECT_DENY)) {
					ruleEffect = Result.DECISION_DENY;
					log.trace("TRACE(" + curLineCtx.getLineNumber() + 
						") setting Rule Effect = Deny");
				} else if (ruleEffectStr.equals(OPENAZ_RULE_EFFECT_PERMIT)) {
					ruleEffect = Result.DECISION_PERMIT;
					log.trace("TRACE(" + curLineCtx.getLineNumber() + 
						") setting Rule Effect = Permit");
				}
			}
    		URI ruleIdURI = new URI(ruleId);
    		
			// Create a Rule to return
			log.trace("TRACE:  " + 
				"\n\tLine# " + curLineCtx.getLineNumber() +
				": Creating Rule to include in resultObject - " +
				"available tokens: \n\t\t" + curLineCtx.getTokens());
	        Rule rule = new Rule(ruleIdURI, 
	        					 ruleEffect, 
	        					 null, 
	        					 target, 
	        					 condition);
	        resultObject = new OpenAzXacmlObject(
					currentLineType, rule,
					true, rlCondition.getReadAheadObject());
		} catch (ClassCastException cce) {
			cce.printStackTrace(new PrintWriter(swStatic));
			String msg = "ClassCastException: " + 
					cce.getMessage() + 
					" looking for Target, got something else." + 
					swStatic;
			throw new OpenAzParseException(msg, curLineCtx.getLineNumber());
		} catch (URISyntaxException uri) {
			String msg = "URISyntaxException: " +
					uri.getMessage() +
					"\n\ttrying to create this into a URI: " +
					uri.getInput();
			throw new OpenAzParseException(msg, curLineCtx.getLineNumber());
		}	
		return resultObject;
	} // end processRule
	
	/**
	 * Build a SunXacml Policy. The Policy can have up to 7 constructor
	 * arguments (from sunxacml javadoc):
	 * <pre>
	 * Policy(	
	 * 	URI id,  
	 * 	RuleCombiningAlgorithm combiningAlg,  
	 * 	String description,  
	 * 	String defaultVersion,  
	 * 	Target target,  
	 * 	List rules,  
	 *  Set obligations, 
	 *  
	 *      Creates a new Policy with the required elements 
	 *      	plus some rules, 
	 *      	a String description, 
	 *      	policy defaults, 
	 *      	and obligations. 
	 * </pre>
	 * @param curLineCtx
	 * @return an OpenAzXacmlObject containing a SunXacml Target
	 * @throws OpenAzParseException
	 */
	public OpenAzXacmlObject processPolicy (
				OpenAzLineContext curLineCtx)
			throws OpenAzParseException {
		
		int myLineNumber = curLineCtx.getLineNumber(); 
		LineType currentLineType = curLineCtx.getLineType();

		// Object container for returning results
		OpenAzXacmlObject resultObject = null;
		URI combiningAlgURI = null;
		log.trace("TRACE: entering processPolicy(Line# = " +
				myLineNumber + ", currentLineType = " + currentLineType + ")");
		
		// have parameters for this Policy above
		// First we are looking for a Policy Target on the next line
		curLineCtx.setLookingForLineType(LineType.TG_PL);
		log.trace(
				"\nTRACE: *****************************************");
		log.trace(
				"TRACE: processPolicy-1 CALLING into readAzLine() using" +
				"\n\t\t\tparentLineNumber = " + myLineNumber);
		OpenAzXacmlObject plTarget = readAzLine(curLineCtx);
		log.trace(
				"TRACE: RETURNing to processPolicy-1 from readAzLine(" +
				"\n\t\t\tparentLineNumber = " + myLineNumber +
				"\n\t\t\tparentLineType = " + currentLineType +
				"\n\t\t\tlookingForType = " + curLineCtx.getLookingForLineType() +
				"\n\t\t\treadAhead flag = " + plTarget.getReadAhead() +
				"\n\t\t\treadAheadLine = " + plTarget.getReadAheadObject());
		try {
			Target target = (Target) plTarget.getObject();
			log.trace("Is this a Target: " + target.getClass().getName());
			log.trace("Was a readAhead done: " + plTarget.getReadAhead());
			if (plTarget.getReadAhead()) {
				log.trace("plTarget.getReadAheadObject = " + 
						plTarget.getReadAheadObject());
				if ( ! (plTarget.getReadAheadObject() == null) ) {
					String readAheadObject = 
						(String) plTarget.getReadAheadObject();
					log.trace("plTarget.getReadAheadObject = " +
							readAheadObject);
				} else
					log.trace("Where's the readAheadObject????");
			}
			// TODO: Assuming target was found, now need to read another 
			// line to get Rules, Obligations, and whatever can terminate
			// the Policy
			
			// Next we need to get a rule list, headed by an RLS
			curLineCtx.setLookingForLineType(null);
			curLineCtx.setReadAhead(plTarget.getReadAhead());
			curLineCtx.setReadAheadLine(
					(String)plTarget.getReadAheadObject());
			log.trace(
					"\nTRACE: *****************************************");
			log.trace(
				"TRACE: processPolicy-2 CALLING into readAzLine() using" +
				"\n\t\t\tparentLineNumber = " + myLineNumber);
			OpenAzXacmlObject plRuleList = readAzLine(curLineCtx);
			log.trace(
				"TRACE: RETURNing into processPolicy-2 from readAzLine(" +
				"\n\t\t\tparentLineNumber = " + myLineNumber +
				"\n\t\t\tparentLineType = " + currentLineType +
				"\n\t\t\tlookingForType = " + curLineCtx.getLookingForLineType() +
				"\n\t\t\treadAhead flag = " + plRuleList.getReadAhead() +
				"\n\t\t\treadAheadLine = " + plRuleList.getReadAheadObject());

			
			// need to get real values above for these:
	        List ruleList = null;
	        ruleList = (List) plRuleList.getObject();
	        
	        // TBD: Finally check if there are obligations
	        Set<Obligation> obligationSet = null;
			curLineCtx.setLookingForLineType(null);
			curLineCtx.setReadAhead(plRuleList.getReadAhead());
			curLineCtx.setReadAheadLine((String)plRuleList.getReadAheadObject());
			log.trace(
					"\nTRACE: *****************************************");
			log.trace(
				"TRACE: processPolicy-3 CALLING into readAzLine() using" +
				"\n\t\t\tparentLineNumber = " + myLineNumber);
			// the readAhead should have the OBS record in it
			OpenAzXacmlObject plObligationSet = readAzLine(curLineCtx);
			log.trace(
				"TRACE: RETURNing into processPolicy-3 from readAzLine(" +
				"\n\t\t\tparentLineNumber = " + myLineNumber +
				"\n\t\t\tparentLineType = " + currentLineType +
				"\n\t\t\tlookingForType = " + curLineCtx.getLookingForLineType() +
				"\n\t\t\treadAhead flag = " + plObligationSet.getReadAhead() +
				"\n\t\t\treadAheadLine = " + plObligationSet.getReadAheadObject());
				//"\n\t\t\treadAheadLine = " + plRuleList.getReadAheadObject() +

			// need to get real values above for these:
	        
	        obligationSet = (Set<Obligation>) plObligationSet.getObject();
	        if ( ! (obligationSet == null) ) {
	        	log.trace(
	        		"TRACE: look at returned obligation set");
	        	for (Obligation o: obligationSet) {
	        		log.trace("TRACE: obligationId: " + o.getId());
	        		List<Attribute> la = o.getAssignments();
	        		for (Attribute a: la) {
	        			log.trace(
	        				"TRACE: Obligation Attribute class: " +
	        				a.getClass().getName());
	        		}
	        	}
	        } else {
	        	log.trace(
	        		"TRACE: processPolicy: returned obligation set is null");
	        }
	        
	        // all the stuff above was getting the objects from the
	        // other lines. Here we need to get the tokens for this line:
			// Allocate variables to collect PolicySet token data:
			// PolicySet metadata
			String policyId = "";
		    String description = "Policy at Line Number " +
	    								curLineCtx.getLineNumber();
		    String ruleCombiningAlg = "";
	        String defaultVersion = XPATH_1_0_VERSION;

	        String thisObject = "Policy";
	    	Map<String,String> map = curLineCtx.getTokens();
	    	log.trace(
	    		"TRACE: Building a " + thisObject + 
	    		" using these tokens: " + map);
	    	Set<String> keys = map.keySet();
	    	for (String s : keys) {
	    		String sValue = map.get(s);
	    		log.trace("TRACE: loop for " + thisObject + 
	    				":\n\t\t  key: " + s +
	    				",\n\t\t  sValue: " + sValue);
	    		if (sValue.equals("!")) {
	    			log.trace(
	    				"TRACE:\t sValue is a null, skip to next key");
	    			continue;
	    		}
	    		int size = keys.size();
	    		for (int i=0; i<size; i++) {
	    			log.trace(
	    				"TRACE:   Inner loop: (" + i +
	    				") match key then value: key = " + s);
	    			switch (i) {
	    			case 0:
	    				if (s.equals(OPENAZ_POLICY_SET_ID)) {
	    					i = size; // force break on loop condition
	    	    			log.trace(
		        				"TRACE:   Inner loop: key matched:" + 
		        				OPENAZ_POLICY_SET_ID +
		        				" now test value: sValue = " + sValue);
	    	    			// append line number to policyset, may remove later	    	    		
	    	    			policyId = sValue + "-Line-" +
	 								new Integer(myLineNumber).toString();;
		    				log.trace(
		    					"TRACE:\t created policyId: key: " + s +
		    					",  with sValue: " + sValue + 
		    					"\n\t\t\tresults in policyId" + 
		    					"\n\t\t\t\t" + policyId);
	    				}
	    				break;
	    			case 1:
	    				if (s.equals(OPENAZ_RULE_COMBINING_ALG)) {
	    					i = size; // force break on loop condition
	    	    			log.trace(
		        				"TRACE:   Inner loop: key matched:" + 
		        				OPENAZ_RULE_COMBINING_ALG +
		        				" now test value: sValue = " + sValue);
	    	    			ruleCombiningAlg = 
	    	    				openAzRuleCombAlgToXacmlRuleCombAlgMap.
	    	    					get(sValue);
	    	    			// assume specified explicitly
	    	    			if (ruleCombiningAlg == null)
	    	    				ruleCombiningAlg = sValue; 
		    				log.trace(
		    					"TRACE:\t created policy ruleCombiningAlg key: " +
		    					s + ",  with sValue: " + sValue + 
		    					"\n\t\t\tresults in ruleCombiningAlg\n\t\t\t\t" + 
		    					ruleCombiningAlg);
	    				}
	    				break;
	    			case 2:
	    				if (s.equals(OPENAZ_POLICY_DESCRIPTION)) {
	    					i = size; // force break on loop condition
	    	    			log.trace(
		        				"TRACE:   Inner loop: key matched:" + 
		        				OPENAZ_POLICY_DESCRIPTION +
		        				" now test value: sValue = " + sValue);
	    	    			description += ": (" + sValue + ")";
		    				log.trace(
		    					"TRACE:\t updated policy description using " + 
		    					"key: " + s + ",  with sValue: " + sValue + 
		    					"\n\t\t\tresults in description\n\t\t\t\t" + 
		    					description);
	    				}
	    				break;

	    			} // end switch (i)
	    		} // end for (i<size)
	    	} // end for (more keys)
	    	
			// PolicySet dummy metadata
	    	if (policyId.equals("")) {
	    		policyId = "dummyPolicyId-LineNumber-" + 
					new Integer(myLineNumber).toString();
	    	}
	    	if (description.equals("")) {
				description = "dummyPolicyDescription";
	    	}
	    	
						
	    	String combiningAlgId = null;
	        //combiningAlgId = OrderedPermitOverridesRuleAlg.algId;
	    	combiningAlgId = ruleCombiningAlg;
	        log.trace("TRACE: " + curLineCtx.getStateDescriptor() + 
	        		" combiningAlgId = " + combiningAlgId);
	        if (combiningAlgId.equals("")) {
	        	combiningAlgId = 
	        		openAzRuleCombAlgToXacmlRuleCombAlgMap.get(
	        				OPENAZ_XACML_RULE_COMB_PERM_OVRD);
	        	log.trace(
	        		"TRACE: No \"cb\" token provided, so " + 
	        		"setting rule combining algorithm to default: " +
	        		combiningAlgId);
	        }
	    	combiningAlgURI = new URI(combiningAlgId);
	    	CombiningAlgFactory factory = CombiningAlgFactory.getInstance();
	    	RuleCombiningAlgorithm combiningAlg = null;
	        combiningAlg = (RuleCombiningAlgorithm)
	        	(factory.createAlgorithm(combiningAlgURI));
			
			// need to get to here to create the Policy:
	        // create the policy
			log.trace("TRACE:  " + 
				"\n\tLine# " + curLineCtx.getLineNumber() +
				": Creating Policy to include in resultObject - " +
				"available tokens: \n\t\t" + curLineCtx.getTokens());
	        Policy policy = 
	        	new Policy(new URI(policyId), 
	        				combiningAlg, 
	        				description,
	        				target, 
	        				defaultVersion,
	        				ruleList, 
	        				obligationSet);
	        log.trace(
	        		"TRACE: created Policy w policyId: " + policyId +
	        		"  policy object = " + policy);
			resultObject = new OpenAzXacmlObject(
					currentLineType, policy,
					true, plObligationSet.getReadAheadObject());
	        log.trace(
	        		"TRACE: created OpenAzXacmlObject w policyId: " + policyId +
	        		"  policy object = " + policy);
			
		} catch (ClassCastException cce) {
			cce.printStackTrace(new PrintWriter(swStatic));
			String msg = "ClassCastException: " + 
					cce.getMessage() + 
					" looking for Target, got something else." + 
					swStatic;
			throw new OpenAzParseException(msg, curLineCtx.getLineNumber());
		} catch (URISyntaxException uri) {
			String msg = "URISyntaxException: " +
					uri.getMessage() +
					"\n\ttrying to create this into a URI: " +
					uri.getInput();
			throw new OpenAzParseException(msg, curLineCtx.getLineNumber());
		} catch (UnknownIdentifierException uie) {
			String msg = "UnknownIdentifierException: " +
					uie.getMessage() + 
					"\n\tCheck that CombiningAlgFactory supports " +
					"the requested combiningAlgId: " + 
					combiningAlgURI.toString();
		}
        log.trace(
        		"TRACE: returning from processPolicy-end");
		return resultObject;
	} // end processPolicy
	
	/**
	 * Build a SunXacml PolicySet. The PolicySet has up
	 * to 7 distinct constructor elements (from sunxacml javadoc):
	 * <pre>
	 * 		Target target, 
	 * 		List policies, 
	 * 		Set obligations, 
	 *  	URI id, 
	 * 		PolicyCombiningAlgorithm combiningAlg, 
	 * 		String description, 
	 * 		String defaultVersion 
	 * </pre>
	 * However, since the first 3: target, policies, obligations are handled
	 * by other LineTypes and using readAzLine, all we need tokens for are
	 * the remaining 4. Those are processed below after the first 3 items
	 * are obtained using readAzLine
	 * @param curLineCtx
	 * @return an OpenAzXacmlObject containing a SunXacml PolicySet
	 * @throws OpenAzParseException
	 */
	public  OpenAzXacmlObject processPolicySet (
				OpenAzLineContext curLineCtx)
			throws OpenAzParseException {
		
		int myLineNumber = curLineCtx.getLineNumber(); 
		LineType currentLineType = curLineCtx.getLineType();
		// have parameters for this PolicySet above in the strline tokens
		
		// Object container for returning results
		OpenAzXacmlObject psTarget = null;
		OpenAzXacmlObject psPolicyOrPolicySet = null;
		OpenAzXacmlObject resultObject = null;
		
		// a list to contain all the Policy and PolicySets that will
		// be found in the process of construct this object.
		List policies = new ArrayList();  
	    URI combiningAlgURI = null;
	    
	    // "First", we need to set mLevel

		// First we are looking for a PolicySet Target on the next line
	    curLineCtx.setLookingForLineType(LineType.TG_PS);
		log.trace(
				"\nTRACE: *****************************************");
		log.trace(
			"TRACE: processPolicySet-1 CALLING into readAzLine() using" +
			"\n\t\t\tparentLineNumber = " + myLineNumber);
		psTarget = readAzLine(curLineCtx);
		log.trace(
			"TRACE: RETURNing to processPolicySet-1 from readAzLine(" +
			"\n\t\t\tparentLineNumber = " + myLineNumber +
			"\n\t\t\tparentLineType = " + currentLineType +
			"\n\t\t\tlookingForType = " + curLineCtx.getLookingForLineType() +
			"\n\t\t\treadAhead flag = " + psTarget.getReadAhead());
		try {
			Target target = (Target) psTarget.getObject();
			log.trace("Is this a Target: " + target.getClass().getName());
			log.trace("Was a readAhead done: " + psTarget.getReadAhead());
			if (psTarget.getReadAhead()) {
				log.trace("psTarget.getReadAheadObject = " + 
						psTarget.getReadAheadObject());
				if ( ! (psTarget.getReadAheadObject() == null) ) {
					String readAheadObject = 
						(String) psTarget.getReadAheadObject();
					log.trace(
						"psTarget.getReadAheadObject = " + readAheadObject);
				} else
					log.trace("Where's the readAheadObject????");
			}
			// Assuming target was found, now need to read additional
			// lines to get any policySets or policies contained by
			// the current PolicySet.
			
			//lookingForLineType either PS or PL, so not specific: null
			curLineCtx.setLookingForLineType(null);
			boolean tryToFindMoreRecords = true;
			boolean firstTimeThru = true;
			while (tryToFindMoreRecords) {
				log.trace(
					"\nTRACE: *****************************************");
				log.trace(
					"TRACE: processPolicySet-2 CALLING into readAzLine() using" +
					"\n\t\t\tparentLineNumber = " + myLineNumber);
				if (firstTimeThru) { // psPolicyOrPolicySet is null
					curLineCtx.setReadAhead(
							psTarget.getReadAhead());
					curLineCtx.setReadAheadLine(
							(String)psTarget.getReadAheadObject());
					psPolicyOrPolicySet = readAzLine(curLineCtx);
					firstTimeThru = false;
					log.trace(
						"TRACE: firstTimeThru=true path ");
				} else { // psPolicyOrPolicySet has the readAhead info
					curLineCtx.setReadAhead(
							psPolicyOrPolicySet.getReadAhead());
					curLineCtx.setReadAheadLine(
							(String)psPolicyOrPolicySet.getReadAheadObject());
					psPolicyOrPolicySet = readAzLine(curLineCtx);
					log.trace(
					"TRACE: firstTimeThru=false path ");
				}
				log.trace(
					"TRACE: RETURNing to processPolicySet-2 from readAzLine(" +
						"\n\t\t\tparentLineNumber = " + myLineNumber +
						"\n\t\t\tparentLineType = " + currentLineType +
						"\n\t\t\tlookingForType = " + 
							curLineCtx.getLookingForLineType() +
						"\n\t\t\treadAhead flag = " + 
							psPolicyOrPolicySet.getReadAhead() +
						"\n\t\t\tgetObject() = " + 
							psPolicyOrPolicySet.getObject());				
				if ( ! (psPolicyOrPolicySet.getObject() == null) ) {
					switch (psPolicyOrPolicySet.getLineType()) {
					case PS:
						PolicySet policySet = 
							(PolicySet) psPolicyOrPolicySet.getObject();
						if (policySet == null) {
							log.trace(
								"TRACE: psPolicyOrPolicySet.getObject() is null");
						}
						policies.add(policySet);
						log.trace(
							"TRACE: processPolicySet added new policySet: " + 
								policySet.getId().toString());
						break;					
					case PL:
						Policy policy = (Policy) psPolicyOrPolicySet.getObject();
						policies.add(policy);
						log.trace(
							"TRACE: processPolicySet added new policy: " + 
								policy.getId().toString());
						break;	
					case FINAL:
						log.trace(
							"TRACE: FINAL LineType returned to " + 
								"processPolicySet-2: " + 
								psPolicyOrPolicySet.getLineType());
						tryToFindMoreRecords = false; // terminate the loop
						break;
					default:
						log.trace(
							"TRACE: unexpected LineType returned to " +
								"processPolicySet-2: " + 
								psPolicyOrPolicySet.getLineType());
						log.trace(
							"TRACE: PL:TG_PL:default  switch level 3: " + 
								psPolicyOrPolicySet.getLineType());
						tryToFindMoreRecords = false; // terminate the loop
					}
				} else { // end if ! psPolicyOrPolicySet == null
					log.trace(
						"TRACE: no more policy or policysets found - " + 
							"terminating loop");
					tryToFindMoreRecords = false; // end loop
				}
			} // end while (tryToFindMoreRecords)
			
			// put the List of Policy/PolicySet, as well as last line
			// readAheadObject in the return object
			
	        // all the stuff above was getting the objects from the
	        // other lines. Here we need to get the tokens for this line:
			
			// Allocate variables to collect PolicySet token data:
			// PolicySet metadata
			String policySetId = "";
			int policySetMLevel = -1; // need to get valid value from token
		    String description = "PolicySet at Line Number " +
		    	curLineCtx.getLineNumber();
		    String policyCombiningAlg = "";
	        String defaultVersion = XPATH_1_0_VERSION;

	        String thisObject = "PolicySet";
	    	Map<String,String> map = curLineCtx.getTokens();
	    	log.trace(
	    		"TRACE: Building a " + thisObject + 
	    		" using these tokens: " + map);
	    	Set<String> keys = map.keySet();
	    	for (String s : keys) {
	    		String sValue = map.get(s);
	    		log.trace("TRACE: loop for " + thisObject + 
	    				":\n\t\t  key: " + s +
	    				",\n\t\t  sValue: " + sValue);
	    		if (sValue.equals("!")) {
	    			log.trace(
	    				"TRACE:\t sValue is a null, skip to next key");
	    			continue;
	    		}
	    		int size = keys.size();
	    		for (int i=0; i<size; i++) {
	    			log.trace(
	    				"TRACE:   Inner loop: (" + i +
	    				") match key then value: key = " + s);
	    			switch (i) {
	    			case 0:
	    				if (s.equals(OPENAZ_POLICY_SET_ID)) {
	    					i = size; // force break on loop condition
	    	    			log.trace(
		        				"TRACE:   Inner loop: key matched:" + 
		        				OPENAZ_POLICY_SET_ID +
		        				" now test value: sValue = " + sValue);
	    	    			// append line number to policyset, may remove later	    	    		
	    	    			policySetId = sValue + "-Line-" +
	 								new Integer(myLineNumber).toString();;
		    				log.trace(
			    					"TRACE:\t created policySetId: key: " + s +
			    					",  with sValue: " + sValue + 
			    					"\n\t\t\tresults in policySetId" + 
			    					"\n\t\t\t\t" + policySetId);
	    				}
	    				break;
	    			case 1:
	    				if (s.equals(OPENAZ_POLICY_COMBINING_ALG)) {
	    					i = size; // force break on loop condition
	    	    			log.trace(
		        				"TRACE:   Inner loop: key matched:" + 
		        				OPENAZ_POLICY_COMBINING_ALG +
		        				" now test value: sValue = " + sValue);
	    	    			policyCombiningAlg = 
	    	    				openAzPolicyCombAlgToXacmlPolicyCombAlgMap.
	    	    					get(sValue);
	    	    			if (policyCombiningAlg == null)
	    	    				policyCombiningAlg = sValue;
		    				log.trace(
		    					"TRACE:\t created policySet policyCombiningAlg " + 
		    					"key: " + s + ",  with sValue: " + sValue + 
		    					"\n\t\t\tresults in policyCombiningAlg\n\t\t\t\t" + 
		    					policyCombiningAlg);
	    				}
	    				break;
	    			case 2:
	    				if (s.equals(OPENAZ_POLICY_SET_DESCRIPTION)) {
	    					i = size; // force break on loop condition
	    	    			log.trace(
		        				"TRACE:   Inner loop: key matched:" + 
		        				OPENAZ_POLICY_SET_DESCRIPTION +
		        				" now test value: sValue = " + sValue);
	    	    			description += ": (" + sValue + ")";
		    				log.trace(
		    					"TRACE:\t updated policySet description using " + 
		    					"key: " + s + ",  with sValue: " + sValue + 
		    					"\n\t\t\tresults in description\n\t\t\t\t" + 
		    					description);
	    				}
	    				break;
	    			/* Note: needed to set this earlier, this is too late
	    			case 2:
	    				if (s.equals(OPENAZ_POLICY_SET_M_LEVEL)) {
	    					i = size; // force break on loop condition
	    	    			log.trace(
		        				"TRACE:   Inner loop: key matched:" + 
		        				OPENAZ_POLICY_SET_M_LEVEL +
		        				" now test value: sValue = " + sValue);
	    	    			// convert the string to integer:
	    	    			Integer mLevelInt = new Integer(sValue);
	    	    			int mLevel = mLevelInt.intValue();
	    	    			policySetMLevel = mLevel;
		    				log.trace(
		    					"TRACE:\t created policySetMLevel: key: " + s +
		    					",  with sValue: " + sValue + 
		    					"\n\t\t\tresults in policySetMLevel\n\t\t\t\t" + 
		    					policySetMLevel);
	    				}
	    				break;
	    				*/

	    			} // end switch (i)
	    		} // end for (i<size)
	    	} // end for (more keys)
	    	
			// PolicySet dummy metadata
	    	if (policySetId.equals("")) {
	    		// TBD: get from token
				policySetId = "dummyPolicySetId-LineNumber-" + 
								new Integer(myLineNumber).toString(); 
	    	}
	    	if (description.equals("")) {
	    		// TBD: get from token
	    		description = 
	    			"dummyPolicySetDescription"; 
	    	}

		    CombiningAlgFactory factory = null;
			PolicyCombiningAlgorithm combiningAlg = null;
		    String alg = null;		    
		    alg = policyCombiningAlg;

	        log.trace("TRACE: " + curLineCtx.getStateDescriptor() + 
	        		" combiningAlgId = " + alg);
	        if (alg.equals("")) {
	        	alg = 
	        		openAzPolicyCombAlgToXacmlPolicyCombAlgMap.get(
	        				OPENAZ_XACML_POLICY_COMB_PERM_OVRD);
	        	log.trace(
	        		"TRACE: No \"cb\" token provided, so " + 
	        		"setting policy combining algorithm to default: " +
	        		alg);
	        }
			combiningAlgURI = new URI(alg);
			factory = CombiningAlgFactory.getInstance();
			combiningAlg = (PolicyCombiningAlgorithm)
				(factory.createAlgorithm(combiningAlgURI));
			
			// Create and return the policySet
			log.trace("TRACE:  " +
				"\n\tLine# " + curLineCtx.getLineNumber() +
				": Creating PolicySet to include in resultObject - " +
				"available tokens: \n\t\t" + curLineCtx.getTokens());
			PolicySet currentPolicySet = 
				new PolicySet(new URI(policySetId), 
							  combiningAlg, 
							  description,
							  target, // above from readAzLine looking for TG_PS
							  policies);
			resultObject = new OpenAzXacmlObject(
					currentLineType, currentPolicySet,
					true, psPolicyOrPolicySet.getReadAheadObject());
			
		} catch (ClassCastException cce) {
			cce.printStackTrace(new PrintWriter(swStatic));
			String msg = "ClassCastException: " + 
					cce.getMessage() + 
					" looking for Target, got something else." + 
					swStatic;
			throw new OpenAzParseException(msg, curLineCtx.getLineNumber());
		} catch (URISyntaxException uri) {
			String msg = "URISyntaxException: " +
					uri.getMessage() +
					"\n\ttrying to create this into a URI: " +
					uri.getInput();
			throw new OpenAzParseException(msg, curLineCtx.getLineNumber());
		} catch (UnknownIdentifierException uie) {
			String msg = "UnknownIdentifierException: " +
					uie.getMessage() + 
					"\n\tCheck that CombiningAlgFactory supports " +
					"the requested combiningAlgId: " + 
					combiningAlgURI.toString();
		}
		log.trace("TRACE: Returning from processPolicySet with:" +
				"\n\treadAhead = " + psPolicyOrPolicySet.getReadAhead() +
				"\n\treadAheadObject = " + 
				psPolicyOrPolicySet.getReadAheadObject());
		return resultObject;
	} // end processPolicySet
}