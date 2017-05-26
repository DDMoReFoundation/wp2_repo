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

import javax.xml.bind.GetPropertyAction

import org.apache.log4j.rewrite.PropertyRewritePolicy

import eu.ddmore.converter.mdl2json.utils.KeyValuePairConverter;
import eu.ddmore.mdl.mdl.CategoricalDefinitionExpr;
import eu.ddmore.mdl.mdl.CategoryValueDefinition;
import eu.ddmore.mdl.mdl.ValuePair
import eu.ddmore.converter.mdl2json.utils.MdlExpressionConverter

/**
 * Represents {@link eu.ddmore.mdl.mdl.EnumerationDefinition}, a subclass of
 * {@link eu.ddmore.mdl.mdl.Statement}, for MDL <-> JSON conversion.
 * <p>
 * Example MDL:
 * <pre>
 * SEX withCategories {female, male}
 * </pre>
 * item within <code>COVARIATES</code> block within <code>mdlObj</code> section.
 * <p>
 * Equivalent JSON:
 * <pre>
 * {".subtype":"EnumDefn","name":"SEX","categories":["female","male"]}
 * </pre>
 */
public class EnumerationDefinition extends AbstractStatement {
    
    public final static String PROPERTY_NAME = "name"
    public final static String PROPERTY_CATEGORIES = "categories"
	public final static String PROPERTY_DISTN = "distn"
	

    /**
     * Constructor creating from MDL grammar objects.
     * <p>
     * @param enumDefn - {@link eu.ddmore.mdl.mdl.EnumerationDefinition} object from the MDL grammar
     */
    public EnumerationDefinition(final eu.ddmore.mdl.mdl.EnumerationDefinition enumDefn) {
        setProperty(PROPERTY_SUBTYPE, EStatementSubtype.EnumDefinition.getIdentifierString())
        setProperty(PROPERTY_NAME, enumDefn.getName())
        if (enumDefn.getCatDefn()) {
            setProperty(PROPERTY_CATEGORIES,
                ((CategoricalDefinitionExpr) enumDefn.getCatDefn()).getCategories().collect {
                    CategoryValueDefinition catValDefn -> catValDefn.getName() // No mappedTo in this context
                })
        }
		if(enumDefn.getDistn()){
			setProperty(PROPERTY_DISTN, MdlExpressionConverter.convertToString(enumDefn.getDistn()))
		}
    }
    
    /**
     * Constructor creating from JSON.
     * <p>
     * @param json - {@link Map} of content
     */
    public EnumerationDefinition(final Map json) {
        super(json)
    }
    
    @Override
    public String toMDL() {
        final StringBuffer sb = new StringBuffer()
        sb.append(IDT)
        sb.append(IDT)
        sb.append(getProperty(PROPERTY_NAME))
        if (getProperty(PROPERTY_CATEGORIES)) {
            sb.append(" withCategories {")
            sb.append(getProperty(PROPERTY_CATEGORIES).join(", "))
            sb.append("}")
        }
		if(getProperty(PROPERTY_DISTN)){
			sb.append(" ~ ")
			sb.append(getProperty(PROPERTY_DISTN))
		}
        sb.toString()
    }
        
}
