/*******************************************************************************
 * Copyright (C) 2016 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
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
            sb.append(content[PROPERTY_BLOCKATTRS].collect { key, value -> "${key}=${value}"} .join("\n"))
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
