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
package eu.ddmore.convertertoolbox.domain.hal;

/**
 * Holds constants for relations used in links
 */
public enum LinkRelation {
    /**
     * Relation for link which holds conversion's result resource
     */
    RESULT("result"),
    /**
     * Relation for link to delete a conversion
     */
    DELETE("delete"),
    /**
     * Relation for link refering to resource's 'self' location
     */
    SELF("self"),
    /**
     * Relation for link pointing to home location of the service
     */
    HOME("home"),
    /**
     * Relation for link representing submission of a conversion
     */
    SUBMIT("submit"),
    /**
     * Relation for link to a support team's system
     */
    SUPPORT("support");
    
    private final String relation;
    private LinkRelation(String relation) {
        this.relation = relation;
    }
    
    public String getRelation() {
        return relation;
    }
}
