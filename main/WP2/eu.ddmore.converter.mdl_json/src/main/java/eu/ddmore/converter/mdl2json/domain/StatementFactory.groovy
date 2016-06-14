/*******************************************************************************
 * Copyright (C) 2015 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
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
                if (EStatementSubtype.BlockStmt.getIdentifierString().equals(subtype)) {
                    // Special case, have to create via BlockStatementFactory
                    return BlockStatementFactory.fromJSON(json)
                } else if(EStatementSubtype.containsElementForIdentifier(subtype)) {
                    final Class clazz = Class.forName(CONVERTER_DOMAIN_PACKAGE_NAME+"."+EStatementSubtype.findByIdentifier(subtype).getClassName())
                    return clazz.newInstance(json)
                } else if(ExtendedDomainSubtype.containsElementForIdentifier(subtype)) {
                    final Class clazz = Class.forName(CONVERTER_DOMAIN_PACKAGE_NAME+"."+ExtendedDomainSubtype.findByIdentifier(subtype).getClassName())
                    return clazz.newInstance(json)
                }
            }
        }
    }
    public static Map unmarshallDomainObjects(Map json) {
        if (json) {
            json.collectEntries { k, v ->
                if(v instanceof Map) {
                    final String subtype = v.get(AbstractStatement.PROPERTY_SUBTYPE)
                    if(subtype) {
                        if (EStatementSubtype.BlockStmt.getIdentifierString().equals(subtype)) {
                            // Special case, have to create via BlockStatementFactory
                            return [k, BlockStatementFactory.fromJSON(v)]
                        } else if(EStatementSubtype.containsElementForIdentifier(subtype)) {
                            final Class clazz = Class.forName(CONVERTER_DOMAIN_PACKAGE_NAME+"."+EStatementSubtype.findByIdentifier(subtype).getClassName())
                            return [k, clazz.newInstance(v)]
                        } else if(ExtendedDomainSubtype.containsElementForIdentifier(subtype)) {
                            final Class clazz = Class.forName(CONVERTER_DOMAIN_PACKAGE_NAME+"."+ExtendedDomainSubtype.findByIdentifier(subtype).getClassName())
                            return [k, clazz.newInstance(v)]
                        }
                    } else {
                        return [k, unmarshallDomainObjects(v)]
                    }
                } else {
                    return [k, v]
                }
            }
        }
    }
}
