/*******************************************************************************
 * Copyright (C) 2015 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
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
