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
package eu.ddmore.converter.mdl2json.domain

import org.apache.log4j.Logger


/**
 * Represents {@link eu.ddmore.mdl.mdl.BlockStatement} for MDL <-> JSON conversion.
 * Is used as both a child object of top-level {@link MclObject} as well as being
 * used as a subclass of {@link eu.ddmore.mdl.mdl.Statement}.
 * <p>
 * The content of a block is represented by this class as a Map containing a single 'main'
 * Entry, the key being the block name and the value being the "block representation"
 * (explained below). There is also a property ({@link AbstractStatement#PROPERTY_SUBTYPE})
 * identifying the subtype of {@link AbstractStatement} that is represented, for use when
 * converting back from JSON (only used when this {@link BlockStatement} is representing
 * a nested block).
 * <p>
 * Note that when representing a top-level block, this class isn't actually incorporated
 * into the domain object graph, since this would introduce an unwanted extra layer
 * of nesting in the JSON representation. Instead, the block name and block
 * representation are extracted by {@link TopLevelBlockStatements} and stored as a Map
 * there instead. In other words, when representing a top-level block, instances of
 * this class are created and destroyed on the fly just as a means to access the
 * block representation that this class provides.
 * <p>
 * By "block representation", we mean the following different 'types' of Statements
 * that a block can represent. The name of the block dictates which representation is
 * used since different blocks encode different MDL constructs.
 * {@link BlockStatementFactory#getRepresentationType(String)} maps block name to
 * {@link EBlockStatementType} enum value.
 * <h3>Statement List</h3>
 * List of {@link StatementList} (i.e. List of attribute Maps}. The default form of
 * representation of a block. Represented by instance of subclass {@link StatementListBlockStatement}.
 * <h3>Symbol List</h3>
 * List of Strings. Used where a block comprises a list of variable names / items which have
 * no attributes or other information that needs to be encoded in the JSON. Represented
 * by instance of subclass {@link SymbolListBlockStatement}.
 * <h3>Task Object Block Content</h3>
 * Map of String -> String. Used explicitly for ESTIMATE and SIMULATE blocks which are
 * essentially sets of name-value attributes. Represented by instance of subclass
 * {@link TaskObjectBlockStatement}.
 * <h3>Content</h3>
 * String. Verbatim MDL, no parsing performed. Represented by instance of subclass
 * {@link TextualContentBlockStatement}.
 * <p>
 */
public abstract class BlockStatement<T> extends AbstractStatement {
    private final Logger LOG = Logger.getLogger(BlockStatement.class)

    BlockStatement() {
        setProperty(PROPERTY_SUBTYPE, EStatementSubtype.BlockStmt.getIdentifierString())
    }
    
    public String getBlockName() {
        getProperties().keySet().find { !it.equals(PROPERTY_SUBTYPE) }
    }
    
    public T getBlockRepresentation() {
        getProperty(getBlockName())
    }
    
    /**
     * Overloaded method to merge two {@link BlockStatement}s' representations.
     * <p>
     * This version merges two {@link StatementList}s of Statements, i.e. for block
     * statement 'type' {@link EBlockStatementType#STATEMENTS}.
     * <p>
     * Used where multiple blocks appear in an MDL file at the top-level.
     * The JSON representation at this level is a Map keyed by block name,
     * hence multiple blocks will overwrite each other in the JSON representation.
     * If there are any block-level attributes then these are assumed to have already been
     * propagated onto the individual variables/items
     * (see {@link StatementList#setBlockAttributesOnIndividualItems(Map)} and related
     * methods) at the point at which this method is called.
     * <p>
     * The method is used by {@link TopLevelBlockStatements} constructors.
     * <p>
     * @param thisStmtList {@link StatementList}
     * @param otherStmtList {@link StatementList}, can be null in which case no modification will be performed
     * @return the modified <code>thisStmtList</code>
     */
    public static StatementList merge(final StatementList thisStmtList, StatementList otherStmtList) {
        if (otherStmtList != null) {
            thisStmtList.addAll(otherStmtList)
        }
        return thisStmtList
    }
    
    /**
     * Overloaded method to merge two {@link BlockStatement}s' representations.
     * <p>
     * This version merges two {@link List}s of String symbol names, i.e. for block
     * statement 'type' {@link EBlockStatementType#SYMBOL_NAMES}.
     * <p>
     * Used where multiple blocks appear in an MDL file at the top-level.
     * The JSON representation at this level is a Map keyed by block name,
     * hence multiple blocks will overwrite each other in the JSON representation.
     * <p>
     * The method is used by {@link TopLevelBlockStatements} constructors.
     * <p>
     * @see {@link #merge(StatementList, StatementList)}
     * <p>
     * @param thisSymbolList {@link List} of {@link String}s
     * @param otherSymbolList {@link List} of {@link String}s, can be null in which case no modification will be performed
     * @return the modified <code>thisSymbolList</code>
     */
    public static List<String> merge(final List<String> thisSymbolList, List<String> otherSymbolList) {
        if (otherSymbolList != null) {
            thisSymbolList.addAll(otherSymbolList)
        }
        return thisSymbolList
    }
    
    /**
     * Overloaded method to merge two {@link BlockStatement}s' representations.
     * <p>
     * This version merges two {@link Map}s of key-value attribute pairs, i.e. for
     * block statement 'type' {@link EBlockStatementType#TASKOBJ_BLOCK}.
     * <p>
     * Used where multiple blocks appear in an MDL file at the top-level.
     * The JSON representation at this level is a Map keyed by block name,
     * hence multiple blocks will overwrite each other in the JSON representation.
     * <p>
     * The method is used by {@link TopLevelBlockStatements} constructors.
     * <p>
     * @see {@link #merge(StatementList, StatementList)}
     * <p>
     * @param thisTaskAttrsMap {@link Map} of {@link String} -> {@link String}
     * @param otherTaskAttrsMap {@link Map} of {@link String} -> {@link String}, can be null in which case no modification will be performed
     * @return the modified <code>thisTaskAttrsMap</code>
     */
    public static Map<String, String> merge(final Map<String, String> thisTaskAttrsMap, Map<String, String> otherTaskAttrsMap) {
        if (otherTaskAttrsMap != null) {
            thisTaskAttrsMap.putAll(otherTaskAttrsMap)
        }
        return thisTaskAttrsMap
    }
    
    /**
     * Overloaded method to merge two {@link BlockStatement}s' representations.
     * <p>
     * This version merges two {@link String}s of content, i.e. for block
     * statement 'type' {@link EBlockStatementType#CONTENT}.
     * <p>
     * Used where multiple blocks appear in an MDL file at the top-level.
     * The JSON representation at this level is a Map keyed by block name,
     * hence multiple blocks will overwrite each other in the JSON representation.
     * <p>
     * The method is used by {@link TopLevelBlockStatements} constructors.
     * <p>
     * @see {@link #merge(StatementList, StatementList)}
     * <p>
     * @param thisContent {@link String}
     * @param otherContent {@link String}, can be null in which case no modification will be performed
     * @return the modified <code>thisContent</code>
     */
    public static String merge(final String thisContent, String otherContent) {
        if (otherContent != null) {
            return thisContent.concat(otherContent)
        }
        return thisContent
    }
    
    /**
     * Represents the different types of representation of a Block Statement.
     */
    protected static enum EBlockStatementType {
        SYMBOL_NAMES,
        STATEMENTS,
        TASKOBJ_BLOCK,
        PROPERTY_STATEMENT,
        CONTENT
    }
    
}
