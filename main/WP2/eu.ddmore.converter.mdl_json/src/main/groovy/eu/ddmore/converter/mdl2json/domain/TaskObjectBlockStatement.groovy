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
import eu.ddmore.converter.mdl2json.domain.StatementFactory

/**
 * Represents a task object statement. The task object statement consists of a list of key-value attributes and optional sub-blocks (of type {@link PropertyStatement}):
 * 
 * <code>
 * warfarin_PK_ODE_taskEval_Explicit = taskObj {
    EVALUATE{ 
# Suggested names of common options (options that would make sense to different software)
        set fim = "P", # FIMtype ?
        previousFim = "",
        graphOnly = false,
        typeIError = 0.05,
        powerComparison = false,
        nSubjectsComparison = false,
        equivalenceRange = [ln(0.8),ln(1.25)],
        powerEquivalence = false,
        nSubjectsEquivalence = false,
        typeIIError = 0.9,
        
# Graphical options (can be moved to SETTINGS if they only make sense to PFIM)       
        graphLogical = true,
        logLogical = false, 
        namesDataX = ["Time"],
        namesDataY = ["Concentration"],
        graphInfA = [0],
        graphSupA = [360] /#,
        yRangeA = []#/
        
        # Define tool specific settings in block
        TARGET_SETTINGS(target = "PFIM"){ # here we explicit all elements of the task
            set project = "warfarin_PK_ODE",
            output = "Stdout_warfarin_PK_ODE_eval.R",
            outputFIM = "",         
            option = 1,
            subjectsInput = 1,
            covariateModel = false,
            covariate_occModel = false
        }
    }
} # end of task object
 * </code>
 */
public class TaskObjectBlockStatement extends BlockStatement<String> {
    
    private static final SUBBLOCKS = "blocks"
    
    public TaskObjectBlockStatement(final String blockName, final eu.ddmore.mdl.mdl.BlockStatementBody blkStmtBody) {
        def taskAttrsMap = [:]
        blkStmtBody.getStatements().findAll { it instanceof eu.ddmore.mdl.mdl.PropertyStatement }.collect { eu.ddmore.mdl.mdl.PropertyStatement stmt ->
            taskAttrsMap.putAll(KeyValuePairConverter.toMap(stmt.getProperties()))
        }
        def blocks = []
        blkStmtBody.getStatements().findAll { it instanceof eu.ddmore.mdl.mdl.BlockStatement }.collect { eu.ddmore.mdl.mdl.BlockStatement stmt ->
            blocks << BlockStatementFactory.fromMDL(stmt)
        }
        taskAttrsMap.put(SUBBLOCKS, blocks)
        setProperty(blockName, taskAttrsMap)
    }
    
    public TaskObjectBlockStatement(final String blockName, final Map<String, String> json) {
        def attrs = json.findAll {k,v->!SUBBLOCKS.equals(k)}
        attrs.put(SUBBLOCKS,json[SUBBLOCKS].collect { 
                def t = StatementFactory.fromJSON(it) 
                return t})
        setProperty(blockName,  attrs)
    }
    
    @Override
    public String toMDL() {
        final StringBuffer sb = new StringBuffer()
        sb.append(IDT)
        sb.append(getBlockName())
        sb.append(" {\n")
        sb.append(getBlockRepresentation().findAll {k, v -> !SUBBLOCKS.equals(k)} .collect {
            k, v -> IDT + IDT + "set " + KeyValuePairConverter.toMDL(k, v)
        }.join("\n"))
        sb.append("\n")
        sb.append(buildSubBlocks())
        sb.append("\n${IDT}}\n")
        sb.toString()
    }
    
    private String buildSubBlocks() {
        getBlockRepresentation().findAll {k, v -> SUBBLOCKS.equals(k) && v.size()>0} .collect {
            k, v ->
            v.collect {
                it.toMDL() }.join("\n")
        }.join("\n")
    }
}
