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
package eu.ddmore.convertertoolbox.service;

import java.util.Collection;

import com.google.common.base.Optional;

import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.domain.ConversionStatus;

/**
 * implementations are responsible for holding a registry of Conversions
 */
public interface ConversionRepository {
    
    /**
     * 
     * @param id conversion id
     * @return conversion with given id or absent optional
     */
    Optional<Conversion> getConversion(String id);
    
    /**
     * 
     * @return all conversions
     */
    Collection<Conversion> getConversions();
    
    /**
     * Persists state of the conversion.
     * If conversion is new, the returned conversion should have an id set
     * @param conversion
     * @return persisted conversion
     */
    Conversion save(Conversion conversion);

    /**
     * 
     * @param status
     * @return all jobs with given conversion status
     */
    Collection<Conversion> getConversionsWithStatus(ConversionStatus status);

    /**
     * 
     * @param date
     * @return Conversions which have been completed before given date
     */
    Collection<Conversion> getConversionsCompletedEarlierThan(long date);
    
    /**
     * 
     * @return number of uncompleted conversions
     */
    int countIncompleteConversions();

    /**
     * Removes conversion
     * @param conversion
     */
    void delete(Conversion conversion);
}
