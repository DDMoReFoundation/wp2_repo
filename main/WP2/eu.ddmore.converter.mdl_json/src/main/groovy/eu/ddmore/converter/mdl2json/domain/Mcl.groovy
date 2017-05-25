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
 * Represents {@link eu.ddmore.mdl.mdl.Mcl} for MDL <-> JSON conversion.
 */
public class Mcl extends ArrayList<MclObject> implements MDLPrintable {

    public Mcl(final eu.ddmore.mdl.mdl.Mcl mcl) {
        
        super(mcl.getObjects().collect { new MclObject(it) })
        
    }
    
    public Mcl(final List<Map> json) {
        
        super(json.collect { new MclObject(it) })
        
    }

    @Override
    public String toMDL() {
        collect { MclObject it ->
            it.toMDL()
        }.join("\n")
    }
    
}
