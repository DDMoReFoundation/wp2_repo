/*******************************************************************************
 * Copyright (C) 2015 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.converter.mdl2json.utils

import eu.ddmore.converter.mdl2json.domain.Distribution
import eu.ddmore.mdl.mdl.AssignPair
import eu.ddmore.mdl.mdl.FuncArguments;
import eu.ddmore.mdl.mdl.NamedFuncArguments
import eu.ddmore.mdl.mdl.SubListExpression
import eu.ddmore.mdl.mdl.ValuePair
import eu.ddmore.mdl.utils.MdlExpressionConverter;

/**
 * Attributes of the form <code>key = value</code> are not always represented
 * in MDL literally as this; for certain attribute names e.g. <code>type</code>
 * the MDL representation would be e.g. <code>type is corr</code>.
 * <p>
 * This class handles the printing of key-value attribute definitions according
 * to the above.
 * <p>
 * It also handles the conversion of a list of MDL Grammar {@link ValuePair}s
 * to a Map for use in the JSON representation.
 */
public class KeyValuePairConverter {
    
    final static Collection<String> IS_REPRESENTATION_ATTR_NAMES = [ "type", "use", "inputFormat", "link", "event", "trans", "target", "algo", "solver", "element" ]
    final static DISTRIBUTION_OP = "~"
    
    public static String toMDL(final Map.Entry<String, String> entry) {
        toMDL(entry.getKey(), entry.getValue())
    }
    
    public static String toMDL(final Map<String, String> map) {
        map.sort().collect { k, v -> toMDL(k, v) }.join(", ")
    }
    
    public static String toMDL(final String key, final Object value) {
        if (IS_REPRESENTATION_ATTR_NAMES.contains(key)) {
            "${key} is ${value}"
        } else if(value instanceof Distribution) {
            "${key} ${DISTRIBUTION_OP} ${value.toMDL()}"
        } else {
            "${key}=${value}"
        }
    }
    
    public static String toMDL(final String key, final Map value) {
        def content = toMDL(value)
        "${key} = { ${content} }"
    }
    
    public static Map<String, String> toMap(final List<ValuePair> valuePairsList) {
        final Map<String, String> valuePairsMap = [:]
        valuePairsList.each {
            ValuePair vp -> valuePairsMap.put(vp.getArgumentName(),toInternalRepresentation(vp))
        }
        valuePairsMap
    }
    
    public static Object toInternalRepresentation(ValuePair valuePair) {
        if(valuePair instanceof AssignPair) {
            return assignToInternalRepresentation(valuePair)
        }
        return  MdlExpressionConverter.convertToString(valuePair.getExpression())
    }
    
    public static Object assignToInternalRepresentation(AssignPair assignPair) {
        def expr = assignPair.getExpression();
        if (expr instanceof SubListExpression) {
            return toMap(expr.getAttributes())
        }
        if(assignPair.assignOp==DISTRIBUTION_OP) {
            return new Distribution(MdlExpressionConverter.convertToString(expr))
        }
        return MdlExpressionConverter.convertToString(expr)
    }
    
    public static Map<String, String> toMap(final NamedFuncArguments funcArgs) {
        toMap(funcArgs.getArguments())
    }
    // else throw new UnsupportedOperationException("FuncArguments other than NamedFuncArguments not currently supported for an EquationDefinition consisting of a BuiltinFunctionCall")
    
}
