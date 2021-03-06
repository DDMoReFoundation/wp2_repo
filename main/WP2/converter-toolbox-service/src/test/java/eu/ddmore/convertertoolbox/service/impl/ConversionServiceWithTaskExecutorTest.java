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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Collection;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.core.task.TaskExecutor;

import com.google.common.base.Optional;

import eu.ddmore.convertertoolbox.api.conversion.Converter;
import eu.ddmore.convertertoolbox.api.conversion.ConverterManager;
import eu.ddmore.convertertoolbox.api.exception.ConverterNotFoundException;
import eu.ddmore.convertertoolbox.domain.ConversionStatus;
import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.domain.internal.ConverterToolboxAPIObjectMapper;
import eu.ddmore.convertertoolbox.rest.ConversionTestFixturesHelper;
import eu.ddmore.convertertoolbox.service.ConversionRepository;
import eu.ddmore.convertertoolbox.service.ExceededCapacity;
import eu.ddmore.convertertoolbox.service.impl.conversion.ConversionTask;


/**
 * Tests {@link ConversionServiceWithTaskExecutor}
 */
@RunWith(MockitoJUnitRunner.class)
public class ConversionServiceWithTaskExecutorTest {
    
    @InjectMocks
    private final ConversionServiceWithTaskExecutor instance = new ConversionServiceWithTaskExecutor();
    
    @Mock
    private ConversionRepository conversionRepository;
    
    @Mock
    private TaskExecutor conversionTaskExecutor;

    @Mock
    private ConverterManager converterManager;

    @Mock
    private ConversionTaskFactory conversionTaskFactory;
    
    @Mock
    private ConversionRemover conversionRemover;
    
    @Before
    public void setUp() throws Exception {
        instance.setServiceCapacity(1);
    }

    @Test(expected=NullPointerException.class)
    public void schedule_shouldThrowRuntimeExceptionForNullConversion() throws ExceededCapacity {
        instance.schedule(null);
    }

    @Test(expected=NullPointerException.class)
    public void schedule_shouldThrowRuntimeExceptionForNotExistingConversion() throws ExceededCapacity {
        instance.schedule(null);
    }

    @Test(expected=IllegalStateException.class)
    public void add_shouldThrowRuntimeExceptionIfConversionAddedForUnsupportedConversion() throws ExceededCapacity, ConverterNotFoundException {
        Conversion conversion =createTestConversion("NON-EXISTING-FROM", "TO", "mock/input/file");
        Optional<Conversion> absent = Optional.absent();
        when(conversionRepository.getConversion(any(String.class))).thenReturn(absent);
        instance.schedule(conversion);
    }

    @Test(expected = ExceededCapacity.class)
    public void add_shouldThrowExceededCapacityExceptionIfNoSpaceLeftOnTaskExecutor() throws ExceededCapacity {
        Conversion conversion = createTestConversion("FROM", "TO", "mock/input/file");
        when(conversionRepository.countIncompleteConversions()).thenReturn(3);
        instance.setServiceCapacity(3);
        instance.add(conversion);
    }

    @Test
    public void add_shouldPersistConversion() throws ExceededCapacity, ConverterNotFoundException {
        Conversion conversion = createTestConversion("FROM", "TO", "mock/input/file");
        
        when(converterManager.getConverter(eq(ConverterToolboxAPIObjectMapper.toOldAPI(conversion.getFrom())), eq(ConverterToolboxAPIObjectMapper.toOldAPI(conversion.getTo())))).thenReturn(mock(Converter.class));
        when(conversionRepository.save(same(conversion))).thenReturn(conversion);
        
        Conversion result = instance.add(conversion);
        
        verify(conversionRepository).save(same(conversion));
        verify(conversionTaskExecutor, times(0)).execute(any(ConversionTask.class));
        assertNotNull(result);
    }

    @Test
    public void schedule_shouldScheduleConversionForExecution() throws ConverterNotFoundException {
        Conversion conversion = createTestConversion("FROM", "TO", "mock/input/file");
        conversion.setId("MOCK-ID");
        when(converterManager.getConverter(eq(ConverterToolboxAPIObjectMapper.toOldAPI(conversion.getFrom())), eq(ConverterToolboxAPIObjectMapper.toOldAPI(conversion.getTo())))).thenReturn(mock(Converter.class));
        when(conversionRepository.save(same(conversion))).thenReturn(conversion);
        when(conversionRepository.getConversion("MOCK-ID")).thenReturn(Optional.of(conversion));
        Conversion result = instance.schedule(conversion);
        
        verify(conversionTaskExecutor).execute(any(ConversionTask.class));
        verify(conversionRepository).save(same(conversion));
        assertNotNull(result);
    }

    @Test
    public void isFull_shouldReturn_TRUE_IfServiceIsAbleToAcceptConversions() {
        when(conversionRepository.countIncompleteConversions()).thenReturn(3);
        instance.setServiceCapacity(3);
        
        assertTrue(instance.isFull());
    }

    @Test
    public void isFull_shouldReturn_FALSE_IfServiceIsAbleToAcceptConversions() {
        when(conversionRepository.countIncompleteConversions()).thenReturn(2);
        instance.setServiceCapacity(3);
        
        assertFalse(instance.isFull());
    }

    @Test
    public void delete_shouldDelegateToRemover() {
        Conversion conversion = createTestConversion("A","B","mock/input/file");
        instance.delete(conversion);
        verify(conversionRemover).remove(same(conversion));
    }

    @Test
    public void getConversions_shouldDelegateToRepository() {
        @SuppressWarnings("unchecked")
        Collection<Conversion> mockResult = mock(Collection.class);
        when(conversionRepository.getConversions())
            .thenReturn(mockResult);
        assertTrue(instance.getConversions() == mockResult);
    }

    @Test
    public void getCompletedConversions_shouldDelegateToRepository() {
        @SuppressWarnings("unchecked")
        Collection<Conversion> mockResult = mock(Collection.class);
        when(conversionRepository.getConversionsWithStatus(eq(ConversionStatus.Completed)))
            .thenReturn(mockResult);
        assertTrue(instance.getCompletedConversions() == mockResult);
    }

    @Test
    public void getConversionForId_shouldDelegateToRepository() {
        instance.getConversionForId("1234");
        verify(conversionRepository).getConversion("1234");
    }

    private Conversion createTestConversion(String form, String to, String inputFile) {
        return ConversionTestFixturesHelper.createInternalTestConversion("TEST_CONVERSION", form, to, inputFile, ConversionStatus.New);
    }
}
