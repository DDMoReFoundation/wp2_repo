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
public class TextualContentBlockStatement extends BlockStatement<String> {
    
    
    public TextualContentBlockStatement(final String blockName, final eu.ddmore.mdl.mdl.BlockTextBody blkBody) {
        setProperty(blockName, blkBody.getText())
    }
    
    public TextualContentBlockStatement(final String blockName, final String blockContent) {
        setProperty(blockName, blockContent)
    }
    
    @Override
    public String toMDL() {
        final StringBuffer sb = new StringBuffer()
        sb.append(IDT)
        sb.append(getBlockName())
        sb.append(getBlockRepresentation())
        sb.append("\n")
        sb.toString()
    }
        
}
