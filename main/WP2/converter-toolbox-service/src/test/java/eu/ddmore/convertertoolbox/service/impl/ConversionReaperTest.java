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

import java.util.Arrays;
import java.util.Collection;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.service.ConversionRepository;

/**
 * Tests { @link ConversionReaper }
 */
@RunWith(MockitoJUnitRunner.class)
public class ConversionReaperTest {
    @InjectMocks
    private ConversionReaper instance = new ConversionReaper();

    @Mock
    private ConversionRemover conversionRemover;
    
    @Mock
    private ConversionRepository conversionRepository;
    
    @Test
    public void shouldTriggerRemovalForEachConversionReturnedFromRepository() {
        Collection<Conversion> conversions = Arrays.asList(
            mock(Conversion.class), mock(Conversion.class), mock(Conversion.class));
        when(conversionRepository.getConversionsCompletedEarlierThan(any(Long.class))).thenReturn(conversions);
        
        instance.setConversionResultsAvailabilityTimeout(1000);
        
        instance.performCleanup();
        
        for(Conversion conversion : conversions) {
            verify(conversionRemover).remove(conversion);
        }
    }

    @Test
    public void shouldNotRethrowConversionRemoverExceptions() {
        Collection<Conversion> conversions = Arrays.asList(
            mock(Conversion.class), mock(Conversion.class), mock(Conversion.class));
        when(conversionRepository.getConversionsCompletedEarlierThan(any(Long.class))).thenReturn(conversions);
        doThrow(Exception.class).when(conversionRemover).remove(any(Conversion.class));
        
        instance.setConversionResultsAvailabilityTimeout(1000);
        
        instance.performCleanup();
    }
}
