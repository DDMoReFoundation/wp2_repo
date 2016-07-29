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

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.File;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.ddmore.convertertoolbox.api.conversion.Converter;
import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.domain.ConversionStatus;
import eu.ddmore.convertertoolbox.service.ConversionRepository;

/**
 * Tests { @list ValidateStep }
 */
@RunWith(MockitoJUnitRunner.class)
public class ValidateStepTest {

    private ValidateStep instance = new ValidateStep();
    @Mock
    private ConversionRepository conversionRepository;
    
    @Mock
    private Conversion conversion;
    
    @Mock
    private Converter converter;
    
    private ConversionContext conversionContext;
    
    @Before
    public void setUp() {
        conversionContext = new ConversionContext(converter, conversion, conversionRepository);
    }
    
    @Test(expected=IllegalStateException.class)
    public void shouldThrowExceptionIfConversionHasNoArchiveSet() {
        when(conversion.getInputFileName()).thenReturn("input-file.txt");
        when(conversion.getStatus()).thenReturn(ConversionStatus.Running);
        instance.execute(conversionContext);
    }

    @Test(expected=IllegalStateException.class)
    public void shouldThrowExceptionIfConversionArchiveDoesNotExist() {
        when(conversion.getInputArchive()).thenReturn(mock(File.class));
        when(conversion.getInputFileName()).thenReturn("input-file.txt");
        when(conversion.getStatus()).thenReturn(ConversionStatus.Running);
        instance.execute(conversionContext);
    }

    @Test(expected=IllegalStateException.class)
    public void shouldThrowExceptionIfConversionHasNoInputFileSpecified() {
        File archive = FileUtils.toFile(ValidateStepTest.class.getResource("/mock-archives/test-input.zip"));
        when(conversion.getInputArchive()).thenReturn(archive);
        when(conversion.getStatus()).thenReturn(ConversionStatus.Running);
        instance.execute(conversionContext);
    }

    @Test(expected=IllegalStateException.class)
    public void shouldThrowExceptionIfConversionIsNotInRunningState() {
        File archive = FileUtils.toFile(ValidateStepTest.class.getResource("/mock-archives/test-input.zip"));
        when(conversion.getInputArchive()).thenReturn(archive);
        when(conversion.getInputFileName()).thenReturn("input-file.txt");
        when(conversion.getStatus()).thenReturn(ConversionStatus.New);
        instance.execute(conversionContext);
    }

    @Test
    public void shouldPassValidation() {
        File archive = FileUtils.toFile(ValidateStepTest.class.getResource("/mock-archives/test-input.zip"));
        when(conversion.getInputArchive()).thenReturn(archive);
        when(conversion.getInputFileName()).thenReturn("input-file.txt");
        when(conversion.getStatus()).thenReturn(ConversionStatus.Running);
        instance.execute(conversionContext);
    }
}
