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
package eu.ddmore.converter.mdl2json.utils

import org.apache.commons.lang.builder.ToStringBuilder

import eu.ddmore.converter.mdl2json.domain.Distribution
import eu.ddmore.mdl.mdl.AssignPair
import eu.ddmore.mdl.mdl.NamedFuncArguments
import eu.ddmore.mdl.mdl.SubListExpression
import eu.ddmore.mdl.mdl.ValuePair
import eu.ddmore.mdl.mdl.VectorElement
import eu.ddmore.mdl.mdl.VectorLiteral
import eu.ddmore.mdl.utils.MdlExpressionConverter

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
    
    final static Collection<String> IS_REPRESENTATION_ATTR_NAMES = [ "type", "use", "inputFormat", "link", "event", "trans", "target", "algo", "solver", "element", "optAlgo" ]
    final static DISTRIBUTION_OP = "~"
    
    public static String toMDL(final Map.Entry<String, String> entry) {
        toMDL(entry.getKey(), entry.getValue())
    }
    
    /**
     * Converts a map to MDL.
     */
    public static String toMDL(final Map<String, String> map) {
        map.sort().collect { k, v -> toMDL(k, v) }.join(", ")
    }
   
    /**
     * Prints a key-value pair using appropriate assignment operator
     */
    public static String toMDL(final String key, final Object value) {
        String result = null
        if (IS_REPRESENTATION_ATTR_NAMES.contains(key)) {
            result = "${key} is ${value}"
        } else if(value instanceof Distribution) {
            result = "${key} ${DISTRIBUTION_OP} ${value.toMDL()}"
        } else {
            result = key + " = " + toMDL(value)
        }
        return result
    }
    
    /**
     * Produces MDL representation of a list of values (in MDL being a 'vector').
     * 
     * It delegates transformation to appropriate implementation of the 'toMDL' implementation and if such doesn't exist it simply invokes 'toString'.
     */
    public static String toMDL(List value) {
        String result = value.sort().collect {
            if(it.getMetaClass().getMetaMethod("toMDL", null)) {
                return it.toMDL()
            } else if(KeyValuePairConverter.getMetaClass().getMetaMethod("toMDL", it)) {
                if(it instanceof Map) { 
                    return "{" + KeyValuePairConverter.toMDL(it) + "}"
                } else {
                    return KeyValuePairConverter.toMDL(it)
                }
            } else {
                return it.toString()
            }
        }.join(", ")
        
        // if the list is of size less or equal to one skip the square-brackets 
        if(value.size() > 1) {
            return "[" + result + "]"
        } else {
            return result
        }
    }
    
    public static toMDL(Object value) {
        return value.toString()
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
    
    /**
     * Parses MDL's vector as a list.
     */
    private static List<Object> vectorToList(final List<VectorElement> elementsList) {
        final List<Object> valuePairsMap = []
        elementsList.each {
            VectorElement vp -> 
            if(vp.getElement() instanceof SubListExpression) {
                valuePairsMap.add(toMap(vp.getElement().getAttributes()))
            } else {
                valuePairsMap.add(MdlExpressionConverter.convertToString(vp.getElement()))
            }
        }
        valuePairsMap
    }
    
    private static Object toInternalRepresentation(ValuePair valuePair) {
        if(valuePair instanceof AssignPair) {
            return assignPairToInternalRepresentation(valuePair)
        }
        return  MdlExpressionConverter.convertToString(valuePair.getExpression())
    }
    
    private static Object assignPairToInternalRepresentation(AssignPair assignPair) {
        def expr = assignPair.getExpression();
        if (expr instanceof SubListExpression) {
            return toMap(expr.getAttributes())
        }
        if (expr instanceof VectorLiteral) {
            return vectorToList(expr.getExpressions())
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
