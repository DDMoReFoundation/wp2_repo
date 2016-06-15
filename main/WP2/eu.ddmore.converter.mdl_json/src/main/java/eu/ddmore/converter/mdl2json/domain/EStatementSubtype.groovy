/*******************************************************************************
 * Copyright (C) 2015 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.converter.mdl2json.domain


/**
 * Enumerates the different subclasses of {@link eu.ddmore.mdl.mdl.Statement}.
 */
public enum EStatementSubtype {

    EquationDefinition("EquationDef", "EquationDefinition"),
    ListDefinition("ListDef", "ListDefinition"),
    RandomVarDefinition("RandVarDefn", "RandomVariableDefinition"),
    TransDefinition("TransDefn", "TransformedDefinition"),
    EnumDefinition("EnumDefn", "EnumerationDefinition"),
    AnonymousListDefinition("AnonListDefn", "AnonymousListStatement"),
    BlockStmt("BlockStmt", "BlockStatement"),
    PropertyStatement("PropertyStmt", "PropertyStatement"),
    
    EStatementSubtype(final String identifierString, final String className) {
        this.identifierString = identifierString
        this.className = className
    }
    
    public String getIdentifierString() {
        return this.identifierString
    }
    
    public String getClassName() {
        return this.className
    }
    
    public static boolean containsElementForIdentifier(String identifier) {
        for(EStatementSubtype e : EStatementSubtype.values()) {
            if(e.getIdentifierString().equals(identifier)) {
                return true;
            }
        }
        return false;
    }
    
    
    public static EStatementSubtype findByIdentifier(String identifier) {
        for(EStatementSubtype e : EStatementSubtype.values()) {
            if(e.getIdentifierString().equals(identifier)) {
                return e;
            }
        }
        throw new EnumConstantNotPresentException();
    }
    
    private String identifierString
    private String className
}