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

import org.apache.log4j.Logger

/**
 * Creates instances of subclasses of {@link AbstractStatement}.
 */
public class StatementFactory {

    private final static Logger LOG = Logger.getLogger(StatementFactory.class);
    
    private final static String MDLGRAMMAR_DOMAIN_PACKAGE_NAME = eu.ddmore.mdl.mdl.Statement.class.getPackage().getName();
    private final static String CONVERTER_DOMAIN_PACKAGE_NAME = StatementFactory.class.getPackage().getName();
    
    public static AbstractStatement fromMDL(final eu.ddmore.mdl.mdl.Statement stmt) {
        for (final EStatementSubtype e : EStatementSubtype.values()) {
            final Class<? extends eu.ddmore.mdl.mdl.Statement> mdlGrammarDomainClass = Class.forName(MDLGRAMMAR_DOMAIN_PACKAGE_NAME+"."+e.getClassName())
            final Class<? extends StatementFactory> converterDomainClass = Class.forName(CONVERTER_DOMAIN_PACKAGE_NAME+"."+e.getClassName())
            if (stmt instanceof eu.ddmore.mdl.mdl.BlockStatement) {
                // Special case, have to create via BlockStatementFactory
                return BlockStatementFactory.fromMDL(stmt)
            }
            else if (mdlGrammarDomainClass.isAssignableFrom(stmt.getClass())) {
                return converterDomainClass.newInstance(stmt)
            }
        }
        throw new UnsupportedOperationException("Subclass " + stmt.getClass() + " of Statement not currently supported")
    }
    
    public static AbstractStatement fromJSON(final Map json) {
        if (json) { // TODO can remove once all subtypes implemented
            final String subtype = json.get(AbstractStatement.PROPERTY_SUBTYPE)
            if (subtype == null) {
                // Default implementation (if no subtype specified) of Statement is either ListDefinition or AnonymousListStatement,
                // depending on whether the variable name, the Key of the single Entry in the Map, is specified or is empty.
                if (json.containsKey(AnonymousListStatement.UNDEF_VAR_NAME_PLACEHOLDER)) {
                    return new AnonymousListStatement(json)
                } else {
                    return new ListDefinition(json)
                }
            } else {
                return unmarshallDomainObject(json)
            }
        }
    }
    
    private static Object unmarshallDomainObject(Map json) {
        final String subtype = json.get(AbstractStatement.PROPERTY_SUBTYPE)
        if (EStatementSubtype.BlockStmt.getIdentifierString().equals(subtype)) {
            // Special case, have to create via BlockStatementFactory
            return BlockStatementFactory.fromJSON(json)
        } else if(EStatementSubtype.containsElementForIdentifier(subtype)) {
            final Class clazz = Class.forName(CONVERTER_DOMAIN_PACKAGE_NAME+"."+EStatementSubtype.findByIdentifier(subtype).getClassName())
            return clazz.newInstance(json)
        } else if(ExtendedDomainSubtype.containsElementForIdentifier(subtype)) {
            final Class clazz = Class.forName(CONVERTER_DOMAIN_PACKAGE_NAME+"."+ExtendedDomainSubtype.findByIdentifier(subtype).getClassName())
            return clazz.newInstance(json)
        } else {
            throw new IllegalStateException("Unrecognized type ${subtype}.")
        }
    }
    
    public static Map unmarshallDomainObjects(Map json) {
        if (json) {
            json.collectEntries { k, v ->
                def result
                if(v instanceof Map) {
                    final String subtype = v.get(AbstractStatement.PROPERTY_SUBTYPE)
                    if(subtype) {
                        result = [k, unmarshallDomainObject(v)]
                    } else {
                        result = [k, unmarshallDomainObjects(v)]
                    }
                } else if( v instanceof List) {
                    result = [k, unmarshallDomainObjects(v)]
                } else {
                    result = [k, v]
                }
                return result
            }
        }
    }
    
    public static List unmarshallDomainObjects(List list) {
        if (list) {
            list.collect  { v ->
                def result;
                if(v instanceof Map) {
                    final String subtype = v.get(AbstractStatement.PROPERTY_SUBTYPE)
                    if(subtype) {
                        result = unmarshallDomainObject(v)
                    } else {
                        result = unmarshallDomainObjects(v)
                    }
                } else if( v instanceof List) {
                    result = unmarshallDomainObjects(v)
                } else {
                    result = v
                }
                return result
            }
        }
    }
}
