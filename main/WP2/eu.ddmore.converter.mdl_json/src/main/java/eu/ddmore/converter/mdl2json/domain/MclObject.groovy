/******************************************************************************* * Copyright (C) 2016 Mango Business Solutions Ltd, http://www.mango-solutions.com * * This program is free software: you can redistribute it and/or modify it under * the terms of the GNU Affero General Public License as published by the * Free Software Foundation, version 3. * * This program is distributed in the hope that it will be useful, * but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY * or FITNESS FOR A PARTICULAR PURPOSE. See the GNU Affero General Public License * for more details. * * You should have received a copy of the GNU Affero General Public License along * with this program. If not, see <http://www.gnu.org/licenses/agpl-3.0.html>. *******************************************************************************/package eu.ddmore.converter.mdl2json.domainimport eu.ddmore.converter.mdl2json.interfaces.MDLPrintable;

/**
 * Represents {@link eu.ddmore.mdl.mdl.MclObject} for MDL <-> JSON conversion.
 */
public class MclObject extends Expando implements MDLPrintable {
    
    public static String PROPERTY_NAME = "name"
    public static String PROPERTY_TYPE = "type"
    public static String PROPERTY_BLOCKS = "blocks"

    public MclObject(final eu.ddmore.mdl.mdl.MclObject mclObj) {
        
        setProperty(PROPERTY_NAME, mclObj.getName())
        setProperty(PROPERTY_TYPE, mclObj.getObjId().getName())
        setProperty(PROPERTY_BLOCKS, TopLevelBlockStatements.fromMDL(mclObj.getBlocks()))
        
    }
    
    public MclObject(final Map json) {
        
        setProperty(PROPERTY_NAME, json[PROPERTY_NAME])
        setProperty(PROPERTY_TYPE, json[PROPERTY_TYPE])
        setProperty(PROPERTY_BLOCKS, TopLevelBlockStatements.fromJSON(json[PROPERTY_BLOCKS]))
        
    }    @Override    public String toMDL() {        final StringBuffer sb = new StringBuffer()        sb.append(getProperty(PROPERTY_NAME))        sb.append(" = ")        sb.append(getProperty(PROPERTY_TYPE))        sb.append(" {\n")        sb.append(getProperty(PROPERTY_BLOCKS).toMDL())        sb.append("}\n")        sb.toString()    }    
}
