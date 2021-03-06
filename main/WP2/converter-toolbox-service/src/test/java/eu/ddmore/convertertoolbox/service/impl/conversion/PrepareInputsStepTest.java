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

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;
import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import eu.ddmore.convertertoolbox.api.conversion.Converter;
import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.service.ConversionRepository;
import eu.ddmore.convertertoolbox.service.impl.ConversionResourcesConvention;


/**
 * Tests { @link PrepareInputsStep }
 */
@RunWith(MockitoJUnitRunner.class)
public class PrepareInputsStepTest {
    private PrepareInputsStep instance = new PrepareInputsStep();
    @Mock
    private ConversionRepository conversionRepository;
    
    @Mock
    private Conversion conversion;
    
    @Mock
    private Converter converter;
    
    private ConversionContext conversionContext;
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Before
    public void setUp() {
        conversionContext = new ConversionContext(converter, conversion, conversionRepository);
    }

    @Test
    public void shouldUnzipInputs() throws IOException {
        File testResource = FileUtils.toFile(PrepareInputsStepTest.class.getResource("test-archive.zip"));
        File inputArchive = new File(tempFolder.getRoot(),testResource.getName());
        FileUtils.copyFile(testResource, inputArchive);
        when(conversion.getInputArchive()).thenReturn(inputArchive);
        
        instance.execute(conversionContext);
        
        assertTrue(new File(tempFolder.getRoot(),ConversionResourcesConvention.INPUTS_DIRECTORY_NAME + "/test-input.txt").exists());
        assertTrue(new File(tempFolder.getRoot(),ConversionResourcesConvention.INPUTS_DIRECTORY_NAME + "/test-input.zip").exists());
        assertTrue(new File(tempFolder.getRoot(),ConversionResourcesConvention.INPUTS_DIRECTORY_NAME + "/empty-directory").exists());
        assertTrue(new File(tempFolder.getRoot(),ConversionResourcesConvention.INPUTS_DIRECTORY_NAME + "/directory-with-contents").exists());
        assertTrue(new File(tempFolder.getRoot(),ConversionResourcesConvention.INPUTS_DIRECTORY_NAME + "/directory-with-contents/test-input.txt").exists());
    }
}
