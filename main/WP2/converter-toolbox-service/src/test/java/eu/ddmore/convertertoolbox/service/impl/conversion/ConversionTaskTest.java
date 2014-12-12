/*******************************************************************************
 * Copyright (C) 2002 Mango Solutions Ltd - All rights reserved.
 ******************************************************************************/
package eu.ddmore.convertertoolbox.service.impl.conversion;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.same;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.ddmore.convertertoolbox.api.conversion.Converter;
import eu.ddmore.convertertoolbox.api.response.ConversionDetail.Severity;
import eu.ddmore.convertertoolbox.api.response.ConversionReport.ConversionCode;
import eu.ddmore.convertertoolbox.domain.Conversion;
import eu.ddmore.convertertoolbox.domain.ConversionReport;
import eu.ddmore.convertertoolbox.domain.ConversionStatus;
import eu.ddmore.convertertoolbox.service.ConversionRepository;


/**
 * Tests { @link ConversionTask }
 */
@RunWith(MockitoJUnitRunner.class)
public class ConversionTaskTest {
    
    
    private ConversionTask instance;
    
    @Mock
    private Conversion conversion;
    
    @Mock
    private Converter converter;

    @Mock
    private ConversionRepository conversionRepository;
    
    @Before
    public void setUp() throws Exception {
        instance = new ConversionTask(new ConversionContext(converter, conversion, conversionRepository));
        when(conversionRepository.save(conversion)).thenReturn(conversion);
    }

    @Test(expected = NullPointerException.class)
    public void new_shouldThrowExceptionIfConvertersionContextIsNull() {
        new ConversionTask(null);
    }
    
    @Test(expected = NullPointerException.class)
    public void new_shouldThrowExceptionIfConverterIsNull() {
        new ConversionTask(new ConversionContext(null, conversion, conversionRepository));
    }
    
    @Test(expected = NullPointerException.class)
    public void new_shouldThrowExceptionIfConversionIsNull() {
        new ConversionTask(new ConversionContext(converter, null, conversionRepository));
    }

    @Test(expected = NullPointerException.class)
    public void new_shouldThrowExceptionIfConversionRepositoryIsNull() {
        new ConversionTask(new ConversionContext(converter, conversion, null));
    }
    
    @Test
    public void shouldPerformAllConversionSteps() {
        List<ConversionStep> conversionSteps = Arrays.asList(mock(ConversionStep.class),
            mock(ConversionStep.class),mock(ConversionStep.class));
        
        instance.setConversionSteps(conversionSteps);
        
        instance.run();
        
        for(ConversionStep step : conversionSteps) {
            verify(step).execute(any(ConversionContext.class));
        }
        
        verify(conversion).setCompletionTime(any(Long.class));
        verify(conversion).setStatus(eq(ConversionStatus.Completed));
        verify(conversionRepository, times(2) /* 1 - change status to 'Running', 2 - final save */).save(same(conversion));
        
    }
    
    @Test
    public void run_shouldFailTheConversionInCaseOfAnyException_AND_persistIt() {
        ConversionStep conversionStep = mock(ConversionStep.class);
        doThrow(Exception.class).when(conversionStep).execute(any(ConversionContext.class));
        instance.setConversionSteps(Arrays.asList(conversionStep));
        
        instance.run();
        
        verify(conversion).setCompletionTime(any(Long.class));
        verify(conversion).setStatus(eq(ConversionStatus.Completed));
        verify(conversionRepository, times(2)  /* 1 - change status to 'Running', 2 - final save */).save(same(conversion));
        verify(conversion).setConversionReport(any(ConversionReport.class));
    }
    
    @Test
    public void shouldGenerateFailedProcessingErrorReport() {
        
        Exception exception = new Exception("Conversion failed", new Exception("Cause"));
        
        ConversionReport conversionReport = instance.generateFailedProcessingErrorReport(exception);
        
        assertEquals(ConversionCode.FAILURE,conversionReport.getReturnCode());
        assertTrue(conversionReport.getDetails().size()>0);
        assertTrue(conversionReport.getDetails().get(0).getServerity()==Severity.ERROR);
        assertTrue(conversionReport.getDetails().get(0).getMessage().equals("Conversion failed"));
        assertTrue(conversionReport.getDetails().get(0).getInfo().get("error").equals("Cause"));
        
    }
}