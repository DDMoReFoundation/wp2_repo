/*******************************************************************************
 * Copyright (C) 2016 Mango Business Solutions Ltd, http://www.mango-solutions.com
 *
 * This program is free software: you can redistribute it and/or modify it under
 * the terms of the GNU Affero General Public License as published by the
 * Free Software Foundation, version 3.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License
 * for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along
 * with this program. If not, see <http://www.gnu.org/licenses/agpl-3.0.html>.
 *******************************************************************************/
package eu.ddmore.converter.mdl2json.testutils

import static eu.ddmore.converter.mdl2json.interfaces.MDLPrintable.IDT
import static org.junit.Assert.*

import java.util.regex.Matcher
import java.util.regex.Pattern

import org.apache.commons.io.FileUtils
import org.apache.commons.lang.StringUtils
import org.apache.log4j.Logger

/**
 * The fields and methods below are used in testing a MDL->JSON->MDL pipeline.
 * Some of these are imported and used by the converter toolbox "systemtest" project.
 */
class MdlFileContentTestUtils {

    private static final Logger LOGGER = Logger.getLogger(MdlFileContentTestUtils.class)

    /**
     * This list should include all the block names within all the top-level objects that can appear within an MDL file.
     * <p>
     * It is primarily used in the automatic block extraction and comparison of a 'new' MDL file against an
     * 'original' MDL file, and hence should be a complete list to ensure thorough testing of the MDL content.
     */
    public static List<String> ALL_BLOCK_NAMES = [
        "DECLARED_VARIABLES", // Identical name to Parameter.DECLARED_VARIABLES; this will pick up both for the comparison.
        "DATA_INPUT_VARIABLES",
        "DATA_DERIVED_VARIABLES",
        "SOURCE",
        "STRUCTURAL",
        "VARIABILITY",
        "IDV",
        "COVARIATES",
        "VARIABILITY_LEVELS",
        "STRUCTURAL_PARAMETERS",
        "VARIABILITY_PARAMETERS",
        "RANDOM_VARIABLE_DEFINITION" + /\s*\([A-Za-z0-9=]+\)/, // Note the regex matching for the parameters of the block name. All matching blocks will get picked up for comparison, as per DECLARED_VARIABLES above.
        "INDIVIDUAL_VARIABLES",
        "MODEL_PREDICTION", // Note: Does NOT compare the sub-blocks; must be done explicitly (i.e. by these being listed explicitly - see below).
        "OBSERVATION",
        "GROUP_VARIABLES",
        "ESTIMATE",
        "SIMULATE",
        "OBJECTS",
        "DEQ", // Sub-block within MODEL_PREDICTION
        "COMPARTMENT", // Sub-block within MODEL_PREDICTION
		"POPULATION_PARAMETERS",
		"INFO",
		"EVALUATE",
		"OPTIMISE",
		"TARGET_SETTINGS",
		"FUNCTIONS",
		"INTERVENTION",
		"STUDY_DESIGN",
		"SAMPLING",
		"DESIGN_SPACES",
		"DESIGN_PARAMETERS",
		"PRIOR_PARAMETERS",
		"NON_CANONICAL_DISTRIBUTION",
		"PRIOR_VARIABLE_DEFINITION",
		"INPUT_PRIOR_DATA",
		"PRIOR_SOURCE",
		"POPULATION"
    ]
	
    /**
     * Extract a specified block from an 'original' MDL file and a 'new' MDL file written out from JSON,
     * strip out any whitespace and comments, and compare the text of the two extracted blocks via a
     * standard JUnit {@link org.junit.Assert#assertEquals(String, Object, Object)}.
     * <p>
     * @param origMdlFile - the {@link File} referencing the original MDL file to be compared against
     * @param blockName - the name of the block to be extracted for comparison
     * @param newMdlFile - the {@link File} referencing the written out MDL file that is to be compared against the original
     */
    public static assertMDLBlockEqualityIgnoringWhitespaceAndComments(final File origMdlFile, final String blockName, final File newMdlFile) {
        assertMDLBlockEqualityIgnoringWhitespaceAndComments(origMdlFile, blockName, FileUtils.readFileToString(newMdlFile))
    }

    /**
     * Extract a specified block from an 'original' MDL file and the content of a 'new' MDL file written
     * out from JSON, strip out any whitespace and comments, and compare the text of the two extracted
     * blocks via a standard JUnit {@link org.junit.Assert#assertEquals(String, Object, Object)}.
     * <p>
     * @param origMdlFile - the {@link File} referencing the original MDL file to be compared against
     * @param blockName - the name of the block to be extracted for comparison
     * @param newMdlFileContent - the String contents of the {@link File} referencing the written out MDL file
     *                            that is to be compared against the original
     */
    public static assertMDLBlockEqualityIgnoringWhitespaceAndComments(final File origMdlFile, final String blockName, final String newMdlFileContent) {
        def String origMdlFileContent = readInAndStripCommentsAndSemicolons(origMdlFile)

        // Note that extractSpecificBlock() returns a list to allow for multiple matching blocks e.g. for DECLARED_VARIABLES, RANDOM_VARIABLE_DEFINITION
        def String origMdlFileBlockContent =
            extractSpecificBlock(origMdlFileContent, blockName).collect { String rawBlockText ->
                putParameterListsIntoKnownOrder(rawBlockText)
            }.join("\n")
        def String newMdlFileBlockContent =
            extractSpecificBlock(newMdlFileContent, blockName).collect { String rawBlockText ->
                putParameterListsIntoKnownOrder(rawBlockText)
            }.join("\n")

        if (!StringUtils.isEmpty(origMdlFileBlockContent) || !StringUtils.isEmpty(newMdlFileBlockContent)) { // Check that we actually have something to compare
            LOGGER.info("Verifying block " + blockName + "...")

            // Special additional preprocessing for blocks where their content can be in any order,
            // so put the items of the original and new blocks into a known order
            if (blockName == "OBJECTS") {
                origMdlFileBlockContent = putMogObjectsBlockContentInKnownOrder(origMdlFileBlockContent)
                newMdlFileBlockContent = putMogObjectsBlockContentInKnownOrder(newMdlFileBlockContent)
            } else if (blockName == "ESTIMATE" || blockName == "SIMULATE"||blockName == "OPTIMISE" || blockName == "EVALUATE") {
                origMdlFileBlockContent = putTaskObjectBlocksContentInKnownOrder(blockName, origMdlFileBlockContent)
                newMdlFileBlockContent = putTaskObjectBlocksContentInKnownOrder(blockName, newMdlFileBlockContent)
            }

            // Trim off whitespace from both the expected and the actual
            origMdlFileBlockContent = origMdlFileBlockContent.replaceAll(/(?s)\s*/, "")
            newMdlFileBlockContent = newMdlFileBlockContent.replaceAll(/(?s)\s*/, "")
            
			// check if the same.  If not assume block is omitted if it is empty.
			if(!origMdlFileBlockContent.equals(newMdlFileBlockContent)){
	            // The original MDL file might have an empty block(s) for this blockName; this is treated the same
	            // as if the block is absent, and the written out MDL won't have this empty block(s), so remove it
	            origMdlFileBlockContent = origMdlFileBlockContent.replaceAll(blockName + /\{\}/, "")
			}
            assertEquals("Checking the content of the block " + blockName, origMdlFileBlockContent, newMdlFileBlockContent)
        }
    }

    /**
     * Comments are denoted by the hash character #.
     * <p>
     * Semicolons can be used to separate variable / item definitions (but this is optional, just whitespace
     * is usually used in the given Use Cases). They do not have any other use hence we strip these out too.
     * <p>
     * @param origMdlFile - {@link File} of MDL content to read in
     * @return the processed text
     */
    private static String readInAndStripCommentsAndSemicolons(final File origMdlFile) {
        final BufferedReader rdr = new BufferedReader(new FileReader(origMdlFile));
        final StringBuffer strBuf = new StringBuffer();
        rdr.eachLine() { String str ->
            removeCommentFromLineOfMDL(str, 0, strBuf)
        }
        strBuf.toString().replaceAll(/(?![^\[\]]*\]\]);\s*/, " ")
//        strBuf.toString().replaceAll(/(?s);\s*/, " ")
    }

    /**
     * Strip off hash-character-commencing comment, if present, from the input string;
     * but ignore hash characters appearing within quoted strings.
     * <p>
     * @param fullStr - the full line of text read in from the MDL file
     * @param fromIndex - the zero-based index in the string at which to start processing
     * @param strBuf - the string buffer to which to append the processed line of text
     */
    private static void removeCommentFromLineOfMDL(final String fullStr, final int fromIndex, final StringBuffer strBuf) {
        def hashCharPos = fullStr.indexOf("#", fromIndex)
        if (hashCharPos < 0) {
            strBuf.append(fullStr.substring(fromIndex))
            strBuf.append("\n")
        } else {
            // There is a # character somewhere in the line
            String subStr = fullStr.substring(fromIndex, hashCharPos)
            if (subStr.count("\"") % 2 == 1) {
                // Hash char is within a quoted string (may be multiple quoted strings on a single line)
                strBuf.append(subStr)
                def closingQuotePos = fullStr.indexOf("\"", hashCharPos)
                strBuf.append(fullStr.substring(hashCharPos, closingQuotePos + 1)) // Ensure we include the closing quote
                removeCommentFromLineOfMDL(fullStr, closingQuotePos + 1, strBuf) // Repeat until the end of the string is reached
            } else {
                // Hash char is not within a quoted string so is most likely starting a comment
                if (!subStr.matches(/\s*$/)) { // Not just whitespace
                    strBuf.append(subStr)
                    strBuf.append("\n")
                }
            }
        }
    }

    /**
     * Given a string consisting of the text of an MDL file (with comments stripped out),
     * and the name of a block (which is treated as a regular expression), extract the text
     * of that block from the full MDL text.
     * <p>
     * We return a {@link List} of {@link String}s rather than a single {@link String}
     * to allow for multiple matching blocks being found, i.e. in the case of
     * DECLARED_VARIABLES (which appears in both the Data object and the Parameter object)
     * and RANDOM_VARIABLE_DEFINITION (of which there can be multiple blocks, distinguished
     * by "(level=XX)" appended to the block names).
     * <p>
     * @param mdlFileContent - the text of an MDL file (with comments stripped out)
     * @param blockName - regular expression for the name of the block(s) to extract
     * @return {@link List} of {@link String}s containing the text of the matched blocks
     */
    private static List<String> extractSpecificBlock(final String mdlFileContent, final String blockName) {
        
        final List<String> extractedBlocks = []
        
        final StringBuffer strBuf
        
        def found = false;
        def interestedIn = false
        def nestingLevel = -1
        mdlFileContent.eachLine { str ->
            if (str.matches("^\\s*" + blockName + "\\s*\\{.*")) {
                // The start of the block
                strBuf = new StringBuffer()
                found = true
                interestedIn = true
                // There might be open curly brackets before the name of the block name; the '-' sign on the RHS is deliberate
                nestingLevel = - StringUtils.countMatches(str.split(blockName)[0], "{")
            }
            if (interestedIn) {
                // Append this line to the string to be returned
                strBuf.append(str)
                strBuf.append("\n")
                // Update how deep in the { } nesting we are so we know when to escape eventually
                nestingLevel = nestingLevel + StringUtils.countMatches(str, "{")
                nestingLevel = nestingLevel - StringUtils.countMatches(str, "}")
                // Have we reached the end of the block we're interested in?
                if (nestingLevel <= 0) {
                    extractedBlocks.add(strBuf.toString())
                    interestedIn = false
                    nestingLevel = -1;
                }
            }
        }
        
        if (!found) {
            LOGGER.info("Block \"" + blockName + "\" was not found in the MDL")
        }
        
        // A MCL file can have multiple top-level objects of the same type (dataObj, parObj, mdlObj, taskObj);
        // these can be written out by the JSON->MDL in any order, hence we need to sort multiple matching
        // blocks into some predictable order. But whitespace causes inconsistent ordering, hence we remove
        // this before sorting.
        extractedBlocks.sort(true) { String b1, String b2 ->
            b1.replaceAll("\\s", "").compareTo(b2.replaceAll("\\s", ""))
        }
    }

    /**
     * Find all occurrences of lists of parameters within the given MDL block text, and rewrite these lists
     * to be alphabetically ordered by key in order to facilitate comparison of the MDL fragment.
     * Parameter lists can exist in several forms, with different brackets used for different purposes,
     * e.g. the following 7 examples, taken from real MDL use cases.
     * <pre>
     * logtWT : {type = continuous, use = covariate}
     * OMEGA : {params = [ETA_CL, ETA_V], value = [0.01], type = CORR
     * CL : {type = linear, trans = log, pop = POP_CL, fixEff = {coeff=BETA_CL_WT, cov=logtWT}, ranEff = ETA_CL}}
     * ETA_CL ~ Normal(mean = 0, sd = PPV_CL)
     * CENTRAL : {deriv = (RATEIN - CL * CENTRAL / V), init = 0, x0 = 0, wrt = TIME}
     * Y : {type = continuous, error = combinedError1(additive = RUV_ADD, proportional = RUV_PROP, f = CC), eps = EPS_Y, prediction = CC} 
     * { macro = elimination, from = FOO4, cl = CL }
     * </pre>
     * Note the nested brackets; these need to be handled with care, such that they don't influence the
     * parameter parsing and sorting which must only be applied to 'top-level' parameters and not
     * parameters 'nested' as value of top-level parameters.
     * <p>
     * @param blockText - original MDL block text
     * @return MDL block text that is identical to the input apart from the ordering of aforementioned lists of parameters
     */
    private static String putParameterListsIntoKnownOrder(final String blockText) {
        if (StringUtils.isBlank(blockText)) {
            return "";
        }
        
        String blockName = "__DUMMY_BLOCK__"
        String blockTextContent = "__DUMMY_CONTENT__"
        
        final Matcher blockTextContentMatcher = blockText =~ /(?s)\s*([A-Za-z0-9_\(\)=\s]+?)\s*\{(.*)\}/
        while (blockTextContentMatcher.find()) { // Only one match expected
            blockName = blockTextContentMatcher.group(1)
            if ("MODEL_PREDICTION".equals(blockName) || "NON_CANONICAL_DISTRIBUTION".equals(blockName)) {
                blockTextContent = replaceModelPredictionSubBlocksWithPlaceholders(blockTextContentMatcher.group(2))
            } else {
                blockTextContent = blockTextContentMatcher.group(2)
            }
        }
        if (blockName == "__DUMMY_BLOCK__") {
            throw new RuntimeException("Unable to recognise the following text as an MDL block:\n" + blockText)
        }
        
        String outStr1 = replaceCurlyBracesInParameterValues(blockTextContent)
        
        if (blockName == "INDIVIDUAL_VARIABLES") {
            outStr1 = replaceParenthesesInTransformedDefinitionRHS(outStr1)
        }
        
        // Explanation of this regex:
        // The (?s) is the "dot-all" instruction to the matcher to match newline characters,
        // so parameter definitions spread over multiple lines will match.
        // The \{\s*(.+?)\s*\} captures the list of parameters of a variable, within { }
        // brackets; the "?" after the ".+" instructs the matcher to lazily match rather than
        // greedily match, otherwise the matcher would keep going until it found the last closing
        // bracket rather than the one that matched the opening bracket.
        def outStr2 = sortParameterList(outStr1, ( outStr1 =~ /(?s)\{\s*([^\}]+)\s*\}/ ))
        // Explanation of this regex:
        // This regex is a more complicated version of the previous one, that captures the list
        // of distribution parameters i.e. for a distribution variable "VAR ~ Normal(...)",
        def outStr3 = sortParameterList(outStr2, ( outStr2 =~ /(?s)[A-Za-z0-9_]+\s*\~\s*[A-Za-z0-9]+\s*\(\s*(.+?)\s*\)/ ))
        // Explanation of this regex:
        // This regex is again a slightly more complicated version of the previous one, that
        // captures the list of function arguments for a transformed definition i.e. for an
        // Individual Variable "ln(TLAG) = linear(...)"
        def outStr4 = sortParameterList(outStr3, ( outStr3 =~ /(?s)[a-z]+\([A-Za-z0-9_]+\)\s*\=\s*[A-Za-z0-9]+\s*\(\s*(.+?)\s*\)/ ))
        def outStr5
        if ("OBSERVATION".equals(blockName)) {
            // Explanation of this regex:
            // This regex is another variant of the previous ones, that captures the list of function
            // arguments in an Observation item's error model, i.e. "MYVAR_obs = combinedError1(...)"
            outStr5 = sortParameterList(outStr4, ( outStr4 =~ /(?s)[A-Za-z0-9_]+\s*\=\s*[A-Za-z0-9]+\s*\(\s*(.+?)\s*\)/ ))
        } else {
            outStr5 = outStr4
        }
        
        LOGGER.trace("Block text before sorting of parameter lists:\n" + blockText)
        LOGGER.trace("Block text after sorting of parameter lists:\n" + blockName + " {" + outStr5 + "}\n")
        
        blockName + " {" + outStr5 + "}\n"
    }
    
    /**
     * Trying to capture a list of parameters enclosed within curly braces, is problematic
     * if any curly braces appear within any of the values of the parameters themselves.
     * E.g. in the following variable definition:
     * <pre>
     * CL : {type = linear, trans = log, pop = POP_CL, fixEff = {coeff=BETA_CL_WT, cov=logtWT}, ranEff = ETA_CL}}
     * </pre>
     * We solve this by identifying any of the following formats of parameter value appearing
     * in a list of parameters such as the one above, and replacing any curly braces within
     * these parameter values, with angle brackets < > instead.
     * <pre>
     * fixEff = {coeff=BETA_CL_WT, cov=logtWT}
     * fixEff = [{coeff=POP_BETA_CL_WT, cov=logtWT}]
     * define=[{female, 1}, {male, 0}]
     * </pre>
     * <p>
     * The resulting MDL may then be syntactically incorrect, but for the purposes of comparison
     * of MDL text, this is ok. (If parameter values were actually being written out with square
     * brackets when they should be using curly braces, this would get picked up by unit tests).
     * <p>
     * @param blockText - original MDL block text
     * @return MDL block text that is identical to the input apart from nested curly braces
     *         being replaced by square brackets
     */
    private static String replaceCurlyBracesInParameterValues(final String blockText) {
        def outStr = blockText
        
        // This regex captures any parameters that consist of a parameter name mapping
        // to a list of attributes enclosed in curly braces {...}, e.g.
        // fixEff = {coeff=BETA_CL_WT, cov=logtWT}
        final Matcher matcherForCurlyBraceEnclosedParamValue = blockText =~ /(?s)([A-Za-z0-9]+)\s*=\s*\{(.+?)\}/
        
        while (matcherForCurlyBraceEnclosedParamValue.find()) {
            outStr = outStr.replace(matcherForCurlyBraceEnclosedParamValue.group(0),
                matcherForCurlyBraceEnclosedParamValue.group(1) + " = <" + matcherForCurlyBraceEnclosedParamValue.group(2) + ">")
        }
        
        // There are also similar use cases that have parameter lists enclosed in square brackets that themselves
        // contain curly braces, i.e. [{...}, {...}], e.g.
        // fixEff = [{coeff=POP_BETA_CL_WT, cov=logtWT}]
        // define=[{female, 1}, {male, 0}]
        final Matcher matcherForSquareBracketEnclosedParamValue = blockText =~ /(?s)([A-Za-z0-9]+)\s*=\s*\[\s*(\{.+?\})\s*\]/
        
        while (matcherForSquareBracketEnclosedParamValue.find()) {
            LOGGER.trace("Found square bracket enclosed parameter value: "
                + matcherForSquareBracketEnclosedParamValue.group(1) + " = [" + matcherForSquareBracketEnclosedParamValue.group(2) + "]")
            
            final List<String> subAttrs = []
            
            final Matcher matcherForCurlyBraceEnclosedSubAttrOfParamValue = matcherForSquareBracketEnclosedParamValue.group(2) =~ /\{(.+?)\}/
            while (matcherForCurlyBraceEnclosedSubAttrOfParamValue.find()) {
                subAttrs.add("<" + matcherForCurlyBraceEnclosedSubAttrOfParamValue.group(1) + ">")
            }
            
            outStr = outStr.replace(matcherForSquareBracketEnclosedParamValue.group(0), matcherForSquareBracketEnclosedParamValue.group(1) + " = [" + subAttrs.join(", ") + "]" )
        }
        
        LOGGER.trace("Block before replacement of curly braces in parameter values: " + blockText)
        LOGGER.trace("Block after replacement of curly braces in parameter values: " + outStr)
        outStr
    }
    
    /**
     * Trying to capture a list of arguments, in a pair of parentheses, to a function on the RHS of a
     * Transformed Definition, is problematic where one of the variables on the RHS is transformed
     * by a function, since regular expressions can't handle the resulting nested parentheses.
     * E.g. in the following variable definition:
     * <pre>
     * ln(KA) = linear(pop = ln(POP_KA), ranEff = ETA_KA)
     * </pre>
     * We solve this by rewriting a function of a variable or number, with the function name and the
     * variable or number separated with a tilde ~ instead i.e. <code>ln(POP_KA)</code> becomes <code>ln~POP_KA</code>.
     * The resulting MDL will then be syntactically incorrect, but for the purposes of comparison
     * of MDL text, this is ok.
     * <p>
     * This is only intended to be applied to items within the INDIVIDUAL_VARIABLE block since this
     * is the block that contains such Transformed Definitions. Applying this to text of other
     * blocks will rewrite fragments of MDL unnecessarily e.g. within the RHS of Equation Definitions.
     * <p>
     * @param blockText - original MDL block text
     * @return MDL block text that is identical to the input apart from parentheses in the RHS of a
     *         Transformed Definition being replaced by angle brackets
     */
    private static String replaceParenthesesInTransformedDefinitionRHS(final String blockText) {
        final String outStr = blockText.replaceAll(/=\s*([a-z]+)\(([A-Za-z0-9_]+)\)/, "= \$1~\$2")
        LOGGER.trace("Block before replacement of parentheses in RHS of Transformed Definitions: " + blockText)
        LOGGER.trace("Block after replacement of parentheses in RHS of Transformed Definitions: " + outStr)
        outStr
    }

    /**
     * Find parameter lists within the provided MDL block text, that match the specified
     * regular expression; some parameter lists are enclosed by {...}, some by (...).
     * <p>
     * For each such parameter list encountered, parse the text from left to right,
     * tracking the nesting depth of parentheses (whether round, curly or square)
     * encountered on the way. This allows us to identify the comma-separated
     * 'top-level' parameters, skipping over 'nested' parameter lists that are values
     * of 'top-level' parameters. Sort the identified list of parameters, and replace
     * the parameter list in the original MDL with this sorted list of parameters. 
     * <p>
     * Once all lists of parameters have been identified and processed, return the
     * resulting processed MDL block text.
     * <p>
     * @param blockTextBeingProcessed - MDL block text, that has already had certain
     *                                  processing applied (e.g. removal of comments)
     *                                  and that will be further processed by this method
     * @param matcher - {@link Matcher} created from the regular expression that is to be
     *                  used to identify lists of parameters, applied to the MDL block text
     */
    private static String sortParameterList(final String blockTextBeingProcessed, Matcher matcher) {
        def outStr = blockTextBeingProcessed
        
        while (matcher.find()) {
            final String paramsStr = matcher.group(1)
            LOGGER.trace("Parameter List found: " + paramsStr)
            
            final List<String> parameters = []
            final StringBuffer currParam = new StringBuffer()
            short bracketNestingCount = 0
            paramsStr.collect { String c ->
                switch(c) {
                    // NB: No distinguishing btwn different bracket types; unit tests should pick up such otherwise syntactically incorrect MDL
                    case '(':
                    case '[':
                    case '{':
                        currParam.append(c)
                        bracketNestingCount++
                        break
                    case ')':
                    case ']':
                    case '}':
                        currParam.append(c)
                        bracketNestingCount--
                        break
                    case ',':
                        if (bracketNestingCount == 0) {
                            // Move on to the next parameter
                            parameters.add(currParam.toString())
                            currParam = new StringBuffer()
                        } else {
                            // Within a parameter value so this comma isn't an parameter separator
                            currParam.append(c)
                        }
                        break
                    case ' ':
                    case '\t':
                    case '\n':
                        // skip
                        break
                    default:
                        currParam.append(c)
                }
            }
            // Add the final parameter to the list
            parameters.add(currParam.toString())
            
            // Sanity check that we haven't mangled the parameters
            if (!paramsStr.replaceAll(/\s*/, "").equals(parameters.join(","))) {
                LOGGER.error("Original parameters string: " + paramsStr)
                LOGGER.error("Parsed parameters string: " + parameters.join(","))
                throw new RuntimeException("Parsed parameters string is not identical to the original parameters string. These have been printed to log output for debugging purposes.")
            }
            
            // Remove trailing zeros on e.g. lo=0.10 to enable comparison with written out MDL
            parameters = removeLeadingAndTrailingZeros(parameters)
            
            parameters.sort() // Mutates the list
            LOGGER.trace("Parsed and sorted parameter list:    " + parameters.join("    "))
            
            // We need to find and replace the entire parameter list to avoid replacing a subset of
            // some other parameter list that cannot then be matched and replaced itself.
            // To ensure we do capture the entire parameter list, we need to include the enclosing
            // brackets - either curly, round, or square (the latter from the preprocessing applied by
            // replaceCurlyBracesInParameterValues()) - hence the rather complicated looking regex-ifying
            // of what was originally just: outStr.replace(paramsStr, parameters.join(", "))
            outStr = outStr.replaceAll("([\\{|\\(|\\[])\\s*" + Pattern.quote(paramsStr) + "\\s*([\\}|\\)|\\]])", "\$1" + parameters.join(", ") + "\$2")
        }
        
        outStr
    }
    
    /**
     * In a parameter definition <code>attr = value</code>, where <code>value</code>
     * is a real value, then remove any trailing zeros, otherwise e.g. <code>lo=0.1</code>
     * would not be equivalent to <code>lo=0.10</code>. Similarly remove any leading
     * zeros, so that <code>lo=0.1</code> is equivalent to <code>.1</code>.
     * <p>
     * @param parameters - List of Strings, zero or more of which may be <code>attr = real_value</code>
     *                     format and will thus be processed
     * @return List of Strings, identical to the input apart from trailing zeros having been droped
     */
    private static List<String> removeLeadingAndTrailingZeros(final List<String> parameters) {
        parameters.collect { String paramStr ->
            // Explanation of the ( ) demarcated groups in this regular expression:
            // 1 = variable name LHS plus equals sign
            // 2 = minus prefix on the real value if it is negative
            // 3 = the real value without sign prefix
            final String newParamStr = paramStr.replaceAll(/^([A-Za-z0-9_]+=)(-?)0*([0-9]*\.[0-9]*?)0*$/, "\$1\$2\$3")
            if (LOGGER.isTraceEnabled() && !paramStr.equals(newParamStr)) {
                LOGGER.trace("Trimmed leading and trailing zeros: " + paramStr + " -> " + newParamStr)
            }
            final String newParamStr2 = newParamStr.replaceAll(/^([A-Za-z0-9_]+=)(-?)0*(:?([0-9]*\.[0-9]*[1-9]+)|([0-9]+)\.)0*[Ee](-?)0*([0-9]+)$/, "\$1\$2\$4\$5E\$6\$7")
            if (LOGGER.isTraceEnabled() && !newParamStr.equals(newParamStr2)) {
                LOGGER.trace("Trimmed sceintific notation leading and trailing zeros: " + newParamStr + " -> " + newParamStr2)
            }
            newParamStr
        }
    }

    /**
     * Special additional preprocessing for the "OBJECTS" block within the "mogObj" top-level block.
     * The items within this block can be in any order so put the lines of the original and new blocks into a known order.
     * <p>
     * @param blockText - the string comprising the block name and its unordered content
     * @return the string comprising the block name and its reordered content
     */
    private static String putMogObjectsBlockContentInKnownOrder(final String blockText) {
        // The third parameter is the regular expression to extract the individual attributes / items within the content of the block.
        // This regex is complicated by the fact that the object names can either be listed without any attributes, or
        // an object name can have its type explicitly specified i.e. myModel_dat : { type is dataObj }
        putBlockContentInKnownOrder("OBJECTS", blockText, /\s*(?:\S+\s*)(?::\s*\{.*\})?/)
    }
    
    /**
     * Special additional preprocessing for the "ESTIMATE" and "SIMULATE" blocks within the "taskObj" top-level block.
     * The items within this block can be in any order so put the lines of the original and new blocks into a known order.
     * <p>
     * @param blockName - the name of the block
     * @param blockText - the string comprising the block name and its unordered content
     * @return the string comprising the block name and its reordered content
     */
    private static String putTaskObjectBlocksContentInKnownOrder(final String blockName, final String blockText) {
        // The third parameter is the regular expression to extract the individual attributes / items within the content of the block.
        // The attributes are all prefixed with "set" keyword so use this to split the attributes.
        putBlockContentInKnownOrder(blockName, blockText, /\s*(?:set\s*.+)/)
    }
    
    /**
     * Called from {@link #putMogObjectsBlockContentInKnownOrder(String)} and {@link #putTaskObjectBlocksContentInKnownOrder(String, String)}.
     * <p>
     * @param blockName - the name of the block
     * @param blockText - the string comprising the block name and its unordered content
     * @param itemsRegex - the regular expression to extract the individual attributes / items within the content of the block
     * @return the string comprising the block name and its reordered content
     */
    private static String putBlockContentInKnownOrder(final String blockName, final String blockText, final String itemsRegex) {
        
        // Similar regex behaviour to those in putParameterListsIntoKnownOrder()
        final Matcher outerMatcher = ( blockText =~ /(?s)/ + blockName + /\s*\{(\s*.+\s*)\}/ ) // Greedily match until last '}' (safe because blockText is already just the OBJECTS block)

        def outStr = blockText

        while (outerMatcher.find()) { // Should only be one match
            final String blockContent = outerMatcher.group(1)

            final Matcher itemsMatcher = ( blockContent =~ itemsRegex )

            outStr = outStr.replace(blockContent, "\n${IDT*2}" + itemsMatcher.collect{ it -> it.trim() }.sort().join("\n${IDT*2}") + "\n${IDT}" )
        }

        outStr
    }
    
    /**
     * When doing {@link #putParameterListsIntoKnownOrder(String)}, sub-blocks that themselves
     * contain lists of parameters, i.e. specifically for the MODEL_PREDICTION block, will
     * cause the parameter capturing and sorting for the main block itself to go awry.
     * <p>
     * Sub-blocks containing parameter lists must instead be processed separately.
     * For the main block, we still want to verify that the sub-blocks appear where they should
     * do, so we replace sub-blocks with just the sub-block name, with __PLACEHOLDER__ appended,
     * for the purposes of the comparison of MDL text.
     * <p>
     * @param blockTextContent - original MDL text for MODEL_PREDICTION block
     * @return MDL text for MODEL_PREDICTION block but with sub-blocks replaced by placeholders
     *         as described above
     */
    private static String replaceModelPredictionSubBlocksWithPlaceholders(final String blockTextContent) {
        String outStr = blockTextContent
        
        for (final String modelPredictionSubBlockName : [ "DEQ", "COMPARTMENT", "INPUT_PRIOR_DATA", "PRIOR_SOURCE" ]) {
            
            // Note that extractSpecificBlock() actually returns a list to allow for multiple matching blocks
            final List<String> subBlockTextFragments = extractSpecificBlock(blockTextContent, modelPredictionSubBlockName)
            
            if (!subBlockTextFragments.isEmpty()) {
                outStr = outStr.replace(subBlockTextFragments.get(0), "        " + modelPredictionSubBlockName + "__PLACEHOLDER__\n")
            }
        }
        
        outStr
    }
    
}
