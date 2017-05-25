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
 * Represents {@link eu.ddmore.mdl.mdl.ListDefinition}, a subclass of {@link eu.ddmore.mdl.mdl.Statement},
 * for MDL <-> JSON conversion.
 * <p>
 * Example MDL:
 * <pre>
 * DT : { use is doseTime, idvColumn=TIME, amtColumn=AMT }
 * </pre>
 * item within <code>DATA_DERIVED_VARIABLES</code> block within <code>dataObj</code> section.
 * <p>
 * Equivalent JSON:
 * <pre>
 * {"DT":{"use":"doseTime","idvColumn":"TIME","amtColumn":"AMT"}}
 * </pre>
 * Equivalent JSON:
 * <p>
 * Note that no <code>.subtype</code> attribute is specified in the JSON; the subtype is
 * inferred from the structure and the content of the JSON.
 */
public class ListDefinition extends AbstractStatement {

    /**
     * Constructor creating from MDL grammar objects.
     * <p>
     * To try and simplify the JSON structure and thus the R representation, we don't specify the
     * subtype here. If no subtype property is found when parsing equivalent JSON, the default
     * subtype is either ListDefinition or AnonymousListStatement, distinguished by whether
     * the variable name, the Key of the single Entry in the Map, is specified or is empty.
     * <p>
     * @param listDefn - {@link eu.ddmore.mdl.mdl.ListDefinition} object from the MDL grammar
     */
    public ListDefinition(final eu.ddmore.mdl.mdl.ListDefinition listDefn) {
		final eu.ddmore.mdl.mdl.AbstractAttributeList attrList = listDefn.getList()
		if (attrList instanceof eu.ddmore.mdl.mdl.AttributeList) {
			setProperty(listDefn.getName(), KeyValuePairConverter.toMap(((eu.ddmore.mdl.mdl.AttributeList) attrList).getAttributes()))
		} else if (attrList instanceof eu.ddmore.mdl.mdl.ListIfExpression) {
			throw new UnsupportedOperationException("ListIfExpression not currently implemented")
		} else if (attrList instanceof eu.ddmore.mdl.mdl.ListPiecewiseExpression) {
			throw new UnsupportedOperationException("ListPiecewiseExpression not currently implemented")
		} else {
			throw new UnsupportedOperationException("Unknown subclass of eu.ddmore.mdl.mdl.AbstractAttributeList encountered: " + attrList.getClass().getName())
		}
    }
    
    /**
     * Constructor creating from JSON.
     * <p>
     * @param json - {@link Map} of content
     */
    public ListDefinition(final Map json) {
        super(StatementFactory.unmarshallDomainObjects(json))
    }
    
    @Override
    public String toMDL() {
        Map<String, String> props = getProperties()
        props.remove(PROPERTY_SUBTYPE)
        props.remove(PROPERTY_BLOCKATTRS)
        final StringBuffer sb = new StringBuffer()
        sb.append(IDT)
        sb.append(IDT)
        sb.append(props.collect { String symbolName, Map<String, String> attrsMap -> // Should just be one symbolName mapping per ListDefinition
            symbolName + " : {" + KeyValuePairConverter.toMDL(attrsMap) + "}"
        }.join(""))
//        sb.append(getProperty(PROPERTY_NAME))
//        if (getProperty(PROPERTY_ATTRS)) {
//            sb.append(" : {")
//            sb.append(getProperty(PROPERTY_ATTRS).collect {
//                Map.Entry<String, String> e -> KeyValuePairConverter.toMDL(e)
//            }.join(", "))
//            sb.append("}")
//        }
        sb.toString()
    }
        
}
