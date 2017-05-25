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
import eu.ddmore.mdl.mdl.ValuePair

/**
 * Represents {@link eu.ddmore.mdl.mdl.AnonymousListStatement}, a subclass of
 * {@link eu.ddmore.mdl.mdl.Statement}, for MDL <-> JSON conversion.
 * <p>
 * Example MDL:
 * <pre>
 *  :: {type is elimination, modelCmt=2, from=CENTRAL, v=V}
 * </pre>
 * item within <code>COMPARMENT</code> sub-block within <code>MODEL_PREDICTION</code> block within <code>mdlObj</code> section.
 * <p>
 * Equivalent JSON:
 * <pre>
 * {"":{"type":"elimination","modelCmt":"2","from":"CENTRAL","v":"V"}}
 * </pre>
 * <p>
 * Note that no <code>.subtype</code> attribute is specified in the JSON; the subtype is
 * inferred from the structure and the content of the JSON.
 */
public class AnonymousListStatement extends AbstractStatement {
    
    public final static String PROPERTY_ATTRS = "attrs"
    
    public final static String UNDEF_VAR_NAME_PLACEHOLDER = ""

    /**
     * Constructor creating from MDL grammar objects.
     * <p>
     * To try and simplify the JSON structure and thus the R representation, we don't specify the
     * subtype here. If no subtype property is found when parsing equivalent JSON, the default
     * subtype is either ListDefinition or AnonymousListStatement, distinguished by whether
     * the variable name, the Key of the single Entry in the Map, is specified or is empty.
     * <p>
     * @param listStmt - {@link eu.ddmore.mdl.mdl.AnonymousListStatement} object from the MDL grammar
     */
    public AnonymousListStatement(final eu.ddmore.mdl.mdl.AnonymousListStatement listStmt) {
        setProperty(UNDEF_VAR_NAME_PLACEHOLDER, KeyValuePairConverter.toMap(listStmt.getList().getAttributes()))
    }
    
    /**
     * Constructor creating from JSON.
     * <p>
     * @param json - {@link Map} of content
     */
    public AnonymousListStatement(final Map json) {
        super(json)
    }
    
    @Override
    public String toMDL() {
        final StringBuffer sb = new StringBuffer()
        sb.append(IDT)
        sb.append(IDT)
        sb.append(" :: {")
        sb.append(KeyValuePairConverter.toMDL(getProperty(UNDEF_VAR_NAME_PLACEHOLDER)))
        sb.append("}")
        sb.toString()
    }
        
}
