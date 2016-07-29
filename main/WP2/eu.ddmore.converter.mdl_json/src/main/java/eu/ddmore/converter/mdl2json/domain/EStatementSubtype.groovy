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