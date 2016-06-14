/*******************************************************************************
 * Copyright (C) 2016 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.converter.mdl2json.domain

import eu.ddmore.converter.mdl2json.interfaces.MDLPrintable;
import groovy.util.Expando;

/**
 * Represents distribution reference
 */
class Distribution extends Expando implements MDLPrintable {
    public final static String PROPERTY_DISTN = "distn";
    
    public Distribution(String distribution) {
        setProperty(PROPERTY_DISTN, distribution)
        setProperty(AbstractStatement.PROPERTY_SUBTYPE, EStatementSubtype.DistributionDefinition.getIdentifierString())
    }
    
    public Distribution(Map json) {
        setProperty(PROPERTY_DISTN, json.get(PROPERTY_DISTN))
        setProperty(AbstractStatement.PROPERTY_SUBTYPE, json.get(AbstractStatement.PROPERTY_SUBTYPE))
    }
    
    @Override
    public String toMDL() {
        return getProperty(PROPERTY_DISTN)
    }
}
