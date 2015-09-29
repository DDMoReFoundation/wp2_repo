/*******************************************************************************
 * Copyright (C) 2015 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.converter.mdl2json.domain

import eu.ddmore.converter.mdl2json.utils.KeyValuePairConverter;
import eu.ddmore.mdl.mdl.CategoricalDefinitionExpr;
import eu.ddmore.mdl.mdl.CategoryValueDefinition;
import eu.ddmore.mdl.mdl.ValuePair

/**
 * Represents {@link eu.ddmore.mdl.mdl.EnumerationDefinition}, a subclass of
 * {@link eu.ddmore.mdl.mdl.Statement}, for MDL <-> JSON conversion.
 * <p>
 * Example MDL:
 * <pre>
 * SEX withCategories {female, male, MISSING}
 * </pre>
 * item within <code>COVARIATES</code> block within <code>mdlobj</code> section.
 */
public class EnumerationDefinition extends AbstractStatement {
    
    public final static String PROPERTY_NAME = "name"
    public final static String PROPERTY_CATEGORIES = "categories"

    public EnumerationDefinition(final eu.ddmore.mdl.mdl.EnumerationDefinition enumDefn) {
        setProperty(PROPERTY_SUBTYPE, EStatementSubtype.EnumDefinition.getIdentifierString())
        setProperty(PROPERTY_NAME, enumDefn.getName())
        if (enumDefn.getCatDefn()) {
            setProperty(PROPERTY_CATEGORIES,
                ((CategoricalDefinitionExpr) enumDefn.getCatDefn()).getCategories().collect {
                    CategoryValueDefinition catValDefn -> catValDefn.getName() // No mappedTo in this context
                })
        }
    }
    
    public EnumerationDefinition(final Map json) {
        setProperty(PROPERTY_SUBTYPE, EStatementSubtype.EnumDefinition.getIdentifierString())
        setProperty(PROPERTY_NAME, json[PROPERTY_NAME])
        setProperty(PROPERTY_CATEGORIES, json[PROPERTY_CATEGORIES])
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
        sb.toString()
    }
        
}
