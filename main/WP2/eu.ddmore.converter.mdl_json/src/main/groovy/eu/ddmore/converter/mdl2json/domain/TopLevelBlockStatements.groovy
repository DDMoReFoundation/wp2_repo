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

import eu.ddmore.converter.mdl2json.interfaces.MDLPrintable

/**
 * Represents a list of {@link eu.ddmore.mdl.mdl.BlockStatement} for MDL <-> JSON conversion.
 */
public class TopLevelBlockStatements extends Expando implements MDLPrintable {
    
    /**
     * Constructs the {@link TopLevelBlockStatements} Map, and populates it with the
     * provided set of Map Entries mapping block name to block representation, but
     * merging block representations where the same block appears multiple times at
     * the top level (i.e. RANDOM_VARIABLE_DEFINITION block).
     * <p>
     * @see {@link BlockStatement#merge(StatementList, StatementList)}
     * <p>
     * @param blockNamesAndTheirRepresentations - Collection of Map Entries mapping
     *        block name to block representation
     */
    private TopLevelBlockStatements(final Collection<Map.Entry<String, Object>> blockNamesAndTheirRepresentations) {
        blockNamesAndTheirRepresentations.each { Map.Entry<String, Object> e ->
            final String blockName = e.getKey()
            def thisBlkRepresentation = e.getValue() // Won't be null
            def existingBlkRepresentation = getProperty(blockName) // Often will be null
            setProperty(blockName, BlockStatement.merge(thisBlkRepresentation, existingBlkRepresentation))
        }
    }
    
    public static TopLevelBlockStatements fromMDL(final List<eu.ddmore.mdl.mdl.BlockStatement> blks) {
        new TopLevelBlockStatements(blks.collect {
            final BlockStatement blkStmt = BlockStatementFactory.fromMDL(it)
            new MapEntry(blkStmt.getBlockName(), blkStmt.getBlockRepresentation())
        })
    }
    
    public static TopLevelBlockStatements fromJSON(final Map<String, Object> json) {
        new TopLevelBlockStatements(json.entrySet())
    }
    
    @Override
    public String toMDL() {
        getProperties().collect { String identifier, Object blockRepresentation ->
            BlockStatementFactory.fromJSON([(identifier) : (blockRepresentation)]).toMDL()
        }.join("\n")
    }
    
}
