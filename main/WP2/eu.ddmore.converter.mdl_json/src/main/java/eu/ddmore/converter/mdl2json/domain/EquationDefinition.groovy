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
import eu.ddmore.mdl.mdl.FuncArguments
import eu.ddmore.mdl.mdl.NamedFuncArguments;
import eu.ddmore.mdl.utils.MdlExpressionConverter

/**
 * Represents {@link eu.ddmore.mdl.mdl.EquationDefinition}, a subclass of {@link eu.ddmore.mdl.mdl.Statement},
 * for MDL <-> JSON conversion.
 * <p>
 * Example MDL:
 * <pre>
 * Y1 = combinedError1(additive = RUV_ADD, proportional = RUV_PROP, eps = EPS_Y, prediction = CC)
 * </pre>
 * item within <code>OBSERVATION</code> block within <code>mdlObj</code> section.
 * <p>
 * Equivalent JSON:
 * <pre>
 * {".subtype":"EquationDef","funcArgs":{"additive":"RUV_ADD","proportional":"RUV_PROP","eps":"EPS_Y","prediction":"CC"},"funcName":"combinedError1","name":"Y1"}
 * </pre> 
 */
public class EquationDefinition extends AbstractStatement {
    
    public final static String PROPERTY_NAME = "name"
    public final static String PROPERTY_EXPRESSION = "expr"
    public final static String PROPERTY_FUNCNAME = "funcName"
    public final static String PROPERTY_FUNCARGS = "funcArgs"
	public final static String PROPERTY_TYPESPEC = "typeSpec"
    
    /**
     * Constructor creating from MDL grammar objects.
     * <p>
     * @param eqnDefn - {@link eu.ddmore.mdl.mdl.EquationDefinition} object from the MDL grammar
     */
    public EquationDefinition(final eu.ddmore.mdl.mdl.EquationDefinition eqnDefn) {
        setProperty(PROPERTY_SUBTYPE, EStatementSubtype.EquationDefinition.getIdentifierString())
        setProperty(PROPERTY_NAME, eqnDefn.getName())
        if (eqnDefn.getExpression()) {

// TODO: Commented out since BuiltinFunctionCall no longer exists - rework this
//            if (eqnDefn.getExpression() instanceof BuiltinFunctionCall && ((BuiltinFunctionCall) eqnDefn.getExpression()).getArgList() instanceof NamedFuncArguments) {
//                // This is really only for OBSERVATION block items, where it is nice to parse the combinedError function call and its arguments rather than these being a single string
//                setProperty(PROPERTY_FUNCNAME, ((BuiltinFunctionCall) eqnDefn.getExpression()).getFunc())
//                setProperty(PROPERTY_FUNCARGS, KeyValuePairConverter.toMap(((BuiltinFunctionCall) eqnDefn.getExpression()).getArgList()))
//            } else {
                setProperty(PROPERTY_EXPRESSION, MdlExpressionConverter.convertToString(eqnDefn.getExpression()))
//            }
        }
		if (eqnDefn.getTypeSpec()) { // This is for DECLARED_VARIABLES block where as of Product 5, variables now have type e.g. GUT::dosingTarget
			setProperty(PROPERTY_TYPESPEC, eqnDefn.getTypeSpec().getTypeName().getName())
		}
    }
    
    /**
     * Constructor creating from JSON.
     * <p>
     * @param json - {@link Map} of content
     */
    public EquationDefinition(final Map json) {
        super(json)
    }
    
    @Override
    boolean hasSimplifiedJsonRepresentation() {
        (getProperty(PROPERTY_EXPRESSION) == null) && (getProperty(PROPERTY_FUNCNAME) == null)
    }
    
    @Override
    String getSimplifiedJsonRepresentation() {
         getProperty(PROPERTY_NAME)
    }
    
    @Override
    public String toMDL() {
        final StringBuffer sb = new StringBuffer()
        sb.append(IDT)
        sb.append(IDT)
        sb.append(getProperty(PROPERTY_NAME))
        if (getProperty(PROPERTY_EXPRESSION)) {
            sb.append(" = ")
            sb.append(getProperty(PROPERTY_EXPRESSION))
        } else if (getProperty(PROPERTY_FUNCNAME)) {
            sb.append(" = ")
            sb.append(getProperty(PROPERTY_FUNCNAME))
            sb.append("(")
            sb.append(KeyValuePairConverter.toMDL(getProperty(PROPERTY_FUNCARGS)))
            sb.append(")")
        }
		if (getProperty(PROPERTY_TYPESPEC)) {
			sb.append(" :: ")
			sb.append(getProperty(PROPERTY_TYPESPEC))
		}
        sb.toString()
    }
        
}
