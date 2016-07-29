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
package eu.ddmore.convertertoolbox.service.impl.conversion;

import com.google.common.base.Preconditions;

import eu.ddmore.convertertoolbox.api.conversion.Converter;
import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.service.ConversionRepository;

/**
 * Represents holds conversion context information
 */
public class ConversionContext {
    private final Converter converter;
    private Conversion conversion;
    private final ConversionRepository conversionRepository;
    /**
     * Creates new conversion context
     * @param converter
     * @param conversion
     * @param conversionRepository
     */
    public ConversionContext(Converter converter, Conversion conversion, ConversionRepository conversionRepository) {
        super();
        Preconditions.checkNotNull(converter, "Converter can't be null");
        Preconditions.checkNotNull(conversion, "Conversion can't be null");
        Preconditions.checkNotNull(conversionRepository, "Converter Repository can't be null");
        this.converter = converter;
        this.conversion = conversion;
        this.conversionRepository = conversionRepository;
    }
    
    public Conversion getConversion() {
        return conversion;
    }

    public Converter getConverter() {
        return converter;
    }
    
    void updateConversion(Conversion conversion) {
        this.conversion = conversionRepository.save(conversion);
    }
}
