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
import eu.ddmore.mdl.mdl.NamedFuncArguments
import eu.ddmore.mdl.mdl.ValuePair

/**
 * Represents {@link eu.ddmore.mdl.mdl.TransformedDefinition}, a subclass of {@link eu.ddmore.mdl.mdl.Statement},
 * for MDL <-> JSON conversion.
 * <p>
 * Example MDL:
 * <pre>
 * ln(TLAG) = linear(trans is ln, pop = POP_TLAG, ranEff = [ETA_TLAG])
 * </pre>
 * item within <code>INDIVIDUAL_VARIABLES</code> block within <code>mdlObj</code> section.
 * <p>
 * Equivalent JSON:
 * <pre>
 * {"transform":"ln",".subtype":"TransDefn","args":{"trans":"ln","pop":"POP_TLAG","ranEff":"[ETA_TLAG]"}
 * </pre>
 */
public class TransformedDefinition extends AbstractStatement {
    
    public final static String PROPERTY_NAME = "name"
    public final static String PROPERTY_TRANSFORM = "transform"
    public final static String PROPERTY_FUNCTION = "func"
    public final static String PROPERTY_ARGS = "args"

    /**
     * Constructor creating from MDL grammar objects.
     * <p>
     * @param transDefn - {@link eu.ddmore.mdl.mdl.TransformedDefinition} object from the MDL grammar
     */
    public TransformedDefinition(final eu.ddmore.mdl.mdl.TransformedDefinition transDefn) {
        setProperty(PROPERTY_SUBTYPE, EStatementSubtype.TransDefinition.getIdentifierString())
        setProperty(PROPERTY_NAME, transDefn.getName())
        setProperty(PROPERTY_TRANSFORM, transDefn.getTransform())
// TODO: Commented out as BuiltinFunctionCall no longer exists - rework this
//        BuiltinFunctionCall funcCall = transDefn.getExpression()
//        if (funcCall.getFunc()) {
//            setProperty(PROPERTY_FUNCTION, funcCall.getFunc())
//        }
        setProperty(PROPERTY_ARGS, KeyValuePairConverter.toMap(funcCall.getArgList()))
    }
    
    /**
     * Constructor creating from JSON.
     * <p>
     * @param json - {@link Map} of content
     */
    public TransformedDefinition(final Map json) {
        super(json)
    }
    
    @Override
    public String toMDL() {
        final StringBuffer sb = new StringBuffer()
        sb.append(IDT)
        sb.append(IDT)
        sb.append(getProperty(PROPERTY_TRANSFORM))
        sb.append("(")
        sb.append(getProperty(PROPERTY_NAME))
        sb.append(") = ")
        if (getProperty(PROPERTY_FUNCTION)) {
            sb.append(getProperty(PROPERTY_FUNCTION))
            sb.append("(")
        }
        sb.append(KeyValuePairConverter.toMDL(getProperty(PROPERTY_ARGS)))
        if (getProperty(PROPERTY_FUNCTION)) {
            sb.append(")")
        }
//        if (getProperty(PROPERTY_ATTRS)) {
//            sb.append(" : {")
//            sb.append(getProperty(PROPERTY_ATTRS).collect { // List of Maps, each Map containing one Entry
//                Map m -> m.collect {
//                    Map.Entry ent -> KeyValuePairMdlPrinter.toMDL(ent)
//                }.join("")
//            }.join(", "))
//            sb.append("}")
//        }
        sb.toString()
    }
        
}
