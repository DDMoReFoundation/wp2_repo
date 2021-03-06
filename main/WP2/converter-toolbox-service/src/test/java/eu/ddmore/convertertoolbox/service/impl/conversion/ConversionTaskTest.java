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
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.ddmore.convertertoolbox.api.conversion.Converter;
import eu.ddmore.convertertoolbox.domain.ConversionDetail;
import eu.ddmore.convertertoolbox.domain.ConversionDetailSeverity;
import eu.ddmore.convertertoolbox.domain.ConversionReport;
import eu.ddmore.convertertoolbox.domain.ConversionReportOutcomeCode;
import eu.ddmore.convertertoolbox.domain.ConversionStatus;
import eu.ddmore.convertertoolbox.domain.internal.Conversion;
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
    public void handleException_shouldCreateNewConversionReportIfConversionDoesntHaveAny() {
        Exception exception = new Exception("Conversion failed", new Exception("Cause"));
        instance.handleException(conversion, exception);
        
        ArgumentCaptor<ConversionReport> conversionReportCaptor = ArgumentCaptor.forClass(ConversionReport.class);
        
        verify(conversion).setConversionReport(conversionReportCaptor.capture());
        
        ConversionReport conversionReport = conversionReportCaptor.getValue();
        assertEquals("Return Code should be set to FAILURE",ConversionReportOutcomeCode.FAILURE,conversionReport.getReturnCode());
        assertTrue("Conversion report should have at least one.", conversionReport.getDetails().size()>0);
        ConversionDetail conversionDetail = conversionReport.getDetails().get(0);
        assertEquals("The first conversion detail should have ERROR severity.", ConversionDetailSeverity.ERROR,conversionDetail.getSeverity());
        assertEquals("The message of the detail should be of the exception being thrown.", "Conversion failed", conversionDetail.getMessage());
        assertEquals("The additional info of with key 'error' should have the message of the exception that caused the exception represented by the detail.","Cause",conversionDetail.getInfo().get("error"));
    }

    @Test
    public void handleException_shouldPopulateExistingConversionReportIfConversionHasOne() {
        Exception exception = new Exception("Conversion failed", new Exception("Cause"));
        ConversionReport conversionReport = createMockConversionReport();
        when(conversion.getConversionReport()).thenReturn(conversionReport);
        
        instance.handleException(conversion, exception);
        
        verify(conversion).setConversionReport(same(conversionReport));
        
        assertEquals("Return Code should be set to FAILURE",ConversionReportOutcomeCode.FAILURE, conversionReport.getReturnCode());
        assertTrue("There should be two conversion details.", conversionReport.getDetails().size()>0);
        ConversionDetail conversionDetail = conversionReport.getDetails().get(1);
        assertEquals("The last conversion detail should have ERROR severity.", ConversionDetailSeverity.ERROR,conversionDetail.getSeverity());
        assertEquals("The message of the detail should be of the exception being thrown.", "Conversion failed", conversionDetail.getMessage());
        assertEquals("The additional info of with key 'error' should have the message of the exception that caused the exception represented by the detail.","Cause",conversionDetail.getInfo().get("error"));
    }

    private ConversionReport createMockConversionReport() {
        ConversionDetail conversionDetail = new ConversionDetail();
        conversionDetail.setSeverity(ConversionDetailSeverity.INFO);
        conversionDetail.setMessage("This is mock human readable message.");
        ConversionReport conversionReport = new ConversionReport();
        conversionReport.addDetail(conversionDetail);
        return conversionReport;
    }
}
