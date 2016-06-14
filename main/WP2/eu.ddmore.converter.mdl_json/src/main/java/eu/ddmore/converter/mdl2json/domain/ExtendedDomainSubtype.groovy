/*******************************************************************************
 * Copyright (C) 2015 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.converter.mdl2json.domain


/**
 * Enumerates the different classes extending MDL domain.
 */
public enum ExtendedDomainSubtype {

    DistributionDefinition("DistrDefn", "Distribution")
    
    ExtendedDomainSubtype(final String identifierString, final String className) {
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
        for(ExtendedDomainSubtype e : ExtendedDomainSubtype.values()) {
            if(e.getIdentifierString().equals(identifier)) {
                return true;
            }
        }
        return false;
    }
    
    public static ExtendedDomainSubtype findByIdentifier(String identifier) {
        for(ExtendedDomainSubtype e : ExtendedDomainSubtype.values()) {
            if(e.getIdentifierString().equals(identifier)) {
                return e;
            }
        }
        throw new EnumConstantNotPresentException();
    }
    
    private String identifierString
    private String className
}