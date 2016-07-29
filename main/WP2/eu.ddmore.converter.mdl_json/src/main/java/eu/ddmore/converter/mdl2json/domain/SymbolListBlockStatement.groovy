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
public class SymbolListBlockStatement extends BlockStatement<List<String>> {
    
    public SymbolListBlockStatement(final String blockName, final eu.ddmore.mdl.mdl.BlockStatementBody blkStmtBody) {
        final StatementList stmtList = StatementList.fromMDL(blkStmtBody.getStatements())
        setProperty(blockName, stmtList.collect { AbstractStatement stmt -> stmt.getSimplifiedJsonRepresentation() })
    }
    
    public SymbolListBlockStatement(final String blockName, final List<String> blockRepresentation) {
        setProperty(blockName, blockRepresentation)
    }
    
    @Override
    public String toMDL() {
        final StringBuffer sb = new StringBuffer()
        sb.append(IDT)
        sb.append(getBlockName())
        sb.append(" {\n")
        sb.append(getBlockRepresentation().collect {
            IDT + IDT + it
        }.join("\n"))
        sb.append("\n${IDT}}\n")
        sb.toString()
    }
    
}
