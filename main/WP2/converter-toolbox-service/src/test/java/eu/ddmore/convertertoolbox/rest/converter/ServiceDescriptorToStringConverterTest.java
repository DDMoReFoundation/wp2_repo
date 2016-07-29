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
package eu.ddmore.convertertoolbox.rest.converter;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.junit.Test;

import eu.ddmore.convertertoolbox.domain.ConversionCapability;
import eu.ddmore.convertertoolbox.domain.LanguageVersion;
import eu.ddmore.convertertoolbox.domain.ServiceDescriptor;
import eu.ddmore.convertertoolbox.domain.Version;

/**
 * Tests { @link ServiceDescriptorToStringConverter }
 */
public class ServiceDescriptorToStringConverterTest {
    private static final Logger LOG = Logger.getLogger(ServiceDescriptorToStringConverterTest.class);
    private final ServiceDescriptorToStringConverter converter = new ServiceDescriptorToStringConverter();
    @Test
    public void shouldConvertToStringForNoCapabilities() {
        ServiceDescriptor serviceDescriptor = new ServiceDescriptor("NAME","SERVICE",new ArrayList<ConversionCapability>());
        
        String result = converter.convert(serviceDescriptor);
        
        LOG.debug(result);
        assertTrue(StringUtils.isNotBlank(result));
        
    }
    
    @Test
    public void shouldConvertToStringWhenMoreThanOneCapability() {
        LanguageVersion languageA = new LanguageVersion("A",new Version(1,0,0,null));
        LanguageVersion languageB = new LanguageVersion("B",new Version(1,0,0,null));
        LanguageVersion languageC = new LanguageVersion("C",new Version(1,0,0,null));
        ServiceDescriptor serviceDescriptor = new ServiceDescriptor("NAME","SERVICE",
            Arrays.asList(
                new ConversionCapability(languageA, Arrays.asList(languageB, languageC)),
                new ConversionCapability(languageC, Arrays.asList(languageB))
                )
                );
        
        String result = converter.convert(serviceDescriptor);
        
        LOG.debug(result);
        assertTrue(StringUtils.isNotBlank(result));
        
    }
}
