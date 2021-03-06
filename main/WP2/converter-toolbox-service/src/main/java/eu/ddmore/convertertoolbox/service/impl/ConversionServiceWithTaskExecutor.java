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

import java.util.Collection;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import eu.ddmore.convertertoolbox.api.conversion.Converter;
import eu.ddmore.convertertoolbox.api.conversion.ConverterManager;
import eu.ddmore.convertertoolbox.api.exception.ConverterNotFoundException;
import eu.ddmore.convertertoolbox.domain.ConversionStatus;
import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.domain.internal.ConverterToolboxAPIObjectMapper;
import eu.ddmore.convertertoolbox.service.ConversionRepository;
import eu.ddmore.convertertoolbox.service.ConversionService;
import eu.ddmore.convertertoolbox.service.ExceededCapacity;
import eu.ddmore.convertertoolbox.service.impl.conversion.ConversionTask;

/**
 * ConversionService implementation that uses a TaskExecutor for executing conversions
 */
@Service
public class ConversionServiceWithTaskExecutor implements ConversionService {
    @Autowired(required=true)
    private ConverterManager converterManager;
    @Autowired(required=true)
    @Qualifier("conversionTaskExecutor")
    private TaskExecutor conversionTaskExecutor;
    @Autowired(required=true)
    private ConversionRepository conversionRepository;
    @Autowired(required=true)
    @Qualifier("conversionTaskFactory")
    private ConversionTaskFactory conversionTaskFactory;
    
    @Value("${cts.serviceCapacity}")
    private int serviceCapacity = -1;
    
    @Autowired(required=true)
    @Qualifier("conversionRemover")
    private ConversionRemover conversionRemover;
    
    @Override
    public Conversion schedule(Conversion conversion) {
        Preconditions.checkNotNull(conversion,"Conversion was null");
        
        Optional<Conversion> conversionCheck = conversionRepository.getConversion(conversion.getId());
        
        if(!conversionCheck.isPresent()) {
            throw new IllegalStateException(String.format("Conversion with id %s has not been added first!", conversion.getId()));
        }

        Converter converter = getConverter(conversion);
        Preconditions.checkNotNull(converter, "Converter returned by Converter Manager was null");
        
        conversion.setStatus(ConversionStatus.Scheduled);
        Conversion scheduledConversion = conversionRepository.save(conversion);
        ConversionTask conversionTask = conversionTaskFactory.create(scheduledConversion, converter);
        conversionTaskExecutor.execute(conversionTask);
        return scheduledConversion;
    }

    @Override
    public boolean isFull() {
        return conversionRepository.countIncompleteConversions()>=serviceCapacity;
    }

    @Override
    public Collection<Conversion> getConversions() {
        return conversionRepository.getConversions();
    }

    @Override
    public Collection<Conversion> getCompletedConversions() {
        return conversionRepository.getConversionsWithStatus(ConversionStatus.Completed);
    }

    @Override
    public Optional<Conversion> getConversionForId(String id) {
        return conversionRepository.getConversion(id);
    }

    
    public void setServiceCapacity(int serviceCapacity) {
        this.serviceCapacity = serviceCapacity;
    }

    @Override
    public synchronized Conversion add(Conversion conversion) throws ExceededCapacity {
        Preconditions.checkNotNull(conversion,"Conversion was null");
        if(isFull()) {
            throw new ExceededCapacity("Exceeded capacity.");
        }
        
        Converter converter = getConverter(conversion);
        Preconditions.checkNotNull(converter, "Converter returned by Converter Manager was null");
        conversion.setSubmissionTime(new Date().getTime());
        Conversion persistedConversion = conversionRepository.save(conversion);
        return persistedConversion;
    }

    @Override
    public void delete(Conversion conversion) {
        conversionRemover.remove(conversion);
    }

    private Converter getConverter(Conversion conversion) {
        Converter converter = null;
        try {
            converter = converterManager.getConverter(ConverterToolboxAPIObjectMapper.toOldAPI(conversion.getFrom()), ConverterToolboxAPIObjectMapper.toOldAPI(conversion.getTo()));
        } catch (ConverterNotFoundException e) {
            throw new IllegalArgumentException("Conversion not supported", e);
        }
        return converter;
    }
}
