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

import eu.ddmore.converter.mdl2json.utils.KeyValuePairConverter;
import eu.ddmore.mdl.mdl.CategoricalDefinitionExpr;
import eu.ddmore.mdl.mdl.CategoryValueDefinition;
import eu.ddmore.mdl.mdl.ValuePair

/**
 * Represents {@link eu.ddmore.mdl.mdl.PropertyStatement}, a subclass of
 * {@link eu.ddmore.mdl.mdl.Statement}, for MDL <-> JSON conversion.
 * <p>
 * Example MDL:
 * <pre>
 * set armSize = 1
 * </pre>
 * <p>
 * Equivalent JSON:
 * <pre>
 * {".subtype":"PropertyStmt", "def": {
                        "armSize": "1"
                    }}
 * </pre>
 */
public class PropertyStatement extends AbstractStatement {
    
    public final static String PROPERTY_VALUE = "def"

    /**
     * Constructor creating from MDL grammar objects.
     * <p>
     * @param enumDefn - {@link eu.ddmore.mdl.mdl.PropertyStatement} object from the MDL grammar
     */
    public PropertyStatement(final eu.ddmore.mdl.mdl.PropertyStatement stmt) {
        setProperty(PROPERTY_SUBTYPE, EStatementSubtype.PropertyStatement.getIdentifierString())
        setProperty(PROPERTY_VALUE, KeyValuePairConverter.toMap(stmt.getProperties()))
    }
    
    /**
     * Constructor creating from JSON.
     * <p>
     * @param json - {@link Map} of content
     */
    public PropertyStatement(final Map json) {
        super(json)
    }
    
    @Override
    public String toMDL() {
        final StringBuffer sb = new StringBuffer()
        sb.append(IDT)
        sb.append(IDT)
        getProperty(PROPERTY_VALUE).each { k,v ->
        sb.append("set ${k} = ${v}")
        }
        sb.toString()
    }
        
}
