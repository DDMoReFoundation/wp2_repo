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

import eu.ddmore.converter.mdl2json.utils.KeyValuePairConverter


/**
 * Property statement is a statement of the form:
 * 
 * <code>
 * BLOCK_NAME (attr = "list") {
 *  set key = value
 *  }
 * </code>
 * 
 * The statement's attributes do not follow standard syntax for using 'is' instead of '='.
 */
class PropertyBlockStatement extends BlockStatement<String> {

    public PropertyBlockStatement(String blockName, eu.ddmore.mdl.mdl.BlockStatementBody blkStmtBody, Map<String,String> blockAttrs) {
        def taskAttrsMap = [:]
        blkStmtBody.getStatements().findAll { it instanceof eu.ddmore.mdl.mdl.PropertyStatement }.collect { eu.ddmore.mdl.mdl.PropertyStatement it ->
            taskAttrsMap.putAll(KeyValuePairConverter.toMap(it.getProperties()))
        }
        taskAttrsMap.put(PROPERTY_BLOCKATTRS, blockAttrs)
        setProperty(blockName, taskAttrsMap)
    }
    public PropertyBlockStatement(final String blockName, final Map<String, String> json) {
        setProperty(blockName, json)
    }
    
    
    @Override
    public String toMDL() {
        final StringBuffer sb = new StringBuffer()
        sb.append(IDT)
        sb.append(getBlockName())
        Map content = getBlockRepresentation();
        if(content[PROPERTY_BLOCKATTRS] && content[PROPERTY_BLOCKATTRS].size()>0) {
            sb.append("(")
            //we don't use KeyValuePairConverter here as we don't want the 'is' to substitute '='
            sb.append(content[PROPERTY_BLOCKATTRS].collect { key, value -> "${key}=${value}"} .join(","))
            sb.append(")")
        }
        sb.append(" {\n")
        sb.append(content.findAll {k, v -> !PROPERTY_BLOCKATTRS.equals(k)} .collect {
            k, v -> IDT + IDT + "set " + KeyValuePairConverter.toMDL(k, v)
        }.join("\n"))
        sb.append("\n${IDT}}\n")
        sb.toString()
    }

}
