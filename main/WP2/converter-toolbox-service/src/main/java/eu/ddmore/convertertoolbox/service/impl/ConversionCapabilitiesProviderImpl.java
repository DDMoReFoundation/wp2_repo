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
import java.util.HashSet;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import eu.ddmore.convertertoolbox.api.conversion.ConverterManager;
import eu.ddmore.convertertoolbox.domain.ConversionCapability;
import eu.ddmore.convertertoolbox.domain.LanguageVersion;
import eu.ddmore.convertertoolbox.domain.internal.ConverterToolboxAPIObjectMapper;
import eu.ddmore.convertertoolbox.service.ConversionCapabilitiesProvider;


/**
 * Default implementation of the { @link ConversionCapabilitiesProvider } which queries {@link ConverterManager} for 
 * capabilities and converts the returned Map to Domain representation
 */
@Service
public class ConversionCapabilitiesProviderImpl implements ConversionCapabilitiesProvider {

    private final ConverterManager converterManager;
    
    @Autowired(required=true)
    public ConversionCapabilitiesProviderImpl(ConverterManager converterManager) {
        this.converterManager = converterManager;
    }
    
    @Override
    public Collection<ConversionCapability> getCapabilities() {
        Map<eu.ddmore.convertertoolbox.api.domain.LanguageVersion, Collection<eu.ddmore.convertertoolbox.api.domain.LanguageVersion>> capabilities = converterManager.getCapabilities();
        Collection<ConversionCapability> result = new HashSet<ConversionCapability>();
        
        for(Map.Entry<eu.ddmore.convertertoolbox.api.domain.LanguageVersion, Collection<eu.ddmore.convertertoolbox.api.domain.LanguageVersion>> en : capabilities.entrySet()) {
            Collection<LanguageVersion> tos = new HashSet<LanguageVersion>();
            
            for(eu.ddmore.convertertoolbox.api.domain.LanguageVersion language : en.getValue()) {
                tos.add(ConverterToolboxAPIObjectMapper.fromOldAPI(language));
            }
            
            result.add(new ConversionCapability(ConverterToolboxAPIObjectMapper.fromOldAPI(en.getKey()), tos));
        }
        return result;
    }
}
