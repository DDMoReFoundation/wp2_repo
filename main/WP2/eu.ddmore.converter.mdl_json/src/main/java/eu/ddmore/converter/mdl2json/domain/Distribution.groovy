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

import eu.ddmore.converter.mdl2json.interfaces.MDLPrintable

/**
 * Represents distribution reference
 */
class Distribution extends Expando implements MDLPrintable {
    public final static String PROPERTY_DISTN = "distn";
    
    public Distribution(String distribution) {
        setProperty(PROPERTY_DISTN, distribution)
        setProperty(AbstractStatement.PROPERTY_SUBTYPE, ExtendedDomainSubtype.DistributionDefinition.getIdentifierString())
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
