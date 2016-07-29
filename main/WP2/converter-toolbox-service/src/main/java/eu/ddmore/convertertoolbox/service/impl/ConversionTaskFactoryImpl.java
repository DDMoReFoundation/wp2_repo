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
package eu.ddmore.convertertoolbox.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import eu.ddmore.convertertoolbox.api.conversion.Converter;
import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.service.ConversionRepository;
import eu.ddmore.convertertoolbox.service.impl.conversion.ConversionContext;
import eu.ddmore.convertertoolbox.service.impl.conversion.ConversionStep;
import eu.ddmore.convertertoolbox.service.impl.conversion.ConversionTask;

/**
 * Default implementation of { @link ConversionTaskFactory }
 */
@Component
@Qualifier("conversionTaskFactory")
public class ConversionTaskFactoryImpl implements ConversionTaskFactory {
    
    @Autowired(required=true)
    private ConversionRepository conversionRepository;

    @Autowired(required=true)
    private List<ConversionStep> conversionSteps;
    
    @Override
    public ConversionTask create(Conversion conversion, Converter converter) {
        ConversionTask conversionTask = new ConversionTask(new ConversionContext(converter, new Conversion(conversion), conversionRepository));
        conversionTask.setConversionSteps(getConversionSteps());
        return conversionTask;
    }

    private List<ConversionStep> getConversionSteps() {
        return conversionSteps;
    }
}
