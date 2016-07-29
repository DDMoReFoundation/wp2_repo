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

import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import com.google.common.base.Optional;
import com.google.common.base.Preconditions;

import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.domain.ConversionStatus;
import eu.ddmore.convertertoolbox.service.ConversionRepository;

/**
 * Default implementation of the conversion remover
 */
@Component
@Qualifier("conversionRemover")
public class DefaultConversionRemover implements ConversionRemover {

    private static final Logger LOG = Logger.getLogger(DefaultConversionRemover.class);
    
    private final ConversionRepository conversionRepository;
    
    @Autowired(required=true)
    public DefaultConversionRemover(ConversionRepository conversionRepository) {
        super();
        this.conversionRepository = conversionRepository;
    }

    @Override
    public void remove(Conversion conversion) {
        Preconditions.checkNotNull(conversion, "Conversion was null");
        LOG.debug(String.format("Removing %s", conversion.getId()));
        
        Optional<Conversion> internalConversion = conversionRepository.getConversion(conversion.getId());
        
        if(!internalConversion.isPresent()) {
            throw new IllegalStateException(String.format("Requested deletion of non-existing conversion %s",conversion.getId()));
        }
        
        if(!ConversionStatus.Completed.equals(internalConversion.get().getStatus())) {
            throw new IllegalStateException(String.format("Requested deletion of uncompleted conversion %s",conversion.getId()));
        }
        try {
            if(conversion.getWorkingDirectory()!=null && conversion.getWorkingDirectory().exists()) {
                FileUtils.deleteDirectory(conversion.getWorkingDirectory());
            }
        } catch (IOException e) {
            throw new RuntimeException(String.format("Conversion [%s] Could not remove working directory %s", conversion.getId(), conversion.getWorkingDirectory().getAbsolutePath()),e);
        }
        
        conversionRepository.delete(conversion);
    }

}
