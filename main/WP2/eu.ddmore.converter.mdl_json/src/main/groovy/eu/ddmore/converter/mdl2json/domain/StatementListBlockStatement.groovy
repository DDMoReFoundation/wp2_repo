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


/**
 *
 */
public class StatementListBlockStatement extends BlockStatement<StatementList> {
    
    public StatementListBlockStatement(final String blockName, final eu.ddmore.mdl.mdl.BlockStatementBody blkStmtBody, final Map<String, String> blkAttrsMap) {
        final StatementList stmtList = StatementList.fromMDL(blkStmtBody.getStatements())
        stmtList.setBlockAttributesOnIndividualItems(blkAttrsMap)
        setProperty(blockName, stmtList)
    }
    
    public StatementListBlockStatement(final String blockName, final List<Map> blockRepresentation) {
        setProperty(blockName, StatementList.fromJSON(blockRepresentation))
    }
    
    @Override
    public String toMDL() {
        final StringBuffer sb = new StringBuffer()
        sb.append(stmtListToMDL())
        sb.append("\n")
        sb.toString()
    }
    
    /**
     * This {@link StatementList} may actually contain statements corresponding to different
     * blocks having the same name but different block-level attributes (i.e.
     * RANDOM_VARIABLE_DEFINITION), that have been stored in the JSON representation on the
     * individual items; split these back out into individual Lists of {@link StatementList}
     * and write out each one in turn.
     * <p>
     * @return the MDL textual representation i.e. as per the original MDL file
     */
    private String stmtListToMDL() {
        getBlockRepresentation().splitByBlockAttributesFromIndividualItems().collect { StatementList stmtList ->
            final StringBuffer sb1 = new StringBuffer()
            sb1.append(IDT)
            sb1.append(getBlockName())
            // Retrieve block attributes that had been propagated onto individual items
            Map<String, String> blkAttrs = stmtList.getBlockAttributesFromIndividualItems()
            if (blkAttrs) {
                sb1.append("(")
                sb1.append(blkAttrs.collect {
                    k, v -> k + "=" + v
                }.join(", "))
                sb1.append(")")
            }
            // Now append the actual MDL block text
            sb1.append(" {\n")
            sb1.append(stmtList.toMDL())
            sb1.append("${IDT}}")
            sb1.toString()
        }.join("\n\n")
    }
        
}
