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
package eu.ddmore.converter.mdl2json;

import static org.junit.Assert.*

import static eu.ddmore.converter.mdl2json.MdlAndJsonFileUtils.*

import java.io.File
import java.io.IOException

import org.apache.commons.io.FileUtils
import org.junit.Before
import org.junit.Rule;
import org.junit.Ignore;
import org.junit.Test
import org.junit.rules.TemporaryFolder;

import eu.ddmore.convertertoolbox.api.response.ConversionReport
import eu.ddmore.convertertoolbox.api.response.ConversionReport.ConversionCode


public class MDLToJSONConverterTest {
    
    @Rule
    public TemporaryFolder workingFolder = new TemporaryFolder()

    private File validMdlFile
    private File jsonValidMdlFile
    private File invalidMdlFile
    private File jsonInvalidMdlFile
    
    private MDLToJSONConverter converter

    @Before
    public void setUp() throws IOException {

        validMdlFile = new File(workingFolder.getRoot(), "UseCase1.mdl")
        jsonValidMdlFile = new File(workingFolder.getRoot(), "UseCase1.json")
        invalidMdlFile = new File(workingFolder.getRoot(), "UseCase1_1.mdl")
        jsonInvalidMdlFile = new File(workingFolder.getRoot(), "UseCase1_1.json")
    
        FileUtils.copyURLToFile(getClass().getResource("/test-models/MDL/Product5/UseCase1.mdl"), validMdlFile)
        FileUtils.copyURLToFile(getClass().getResource("/test-models/MDL/Product4-invalid/UseCase1_1.mdl"), invalidMdlFile)
        
        this.converter = new MDLToJSONConverter()
    }

    @Test
    public void testPerformConvertForValidMdlFile() throws IOException {
        assertFalse("Converted JSON file should not initially exist", jsonValidMdlFile.exists())
        final ConversionReport report = converter.performConvert(validMdlFile, workingFolder.getRoot())
        assertEquals("Checking for successful return code", ConversionCode.SUCCESS, report.getReturnCode())
        assertTrue("Converted JSON file should have been created", jsonValidMdlFile.exists())
    }
    
    @Test
    public void testPerformConvertForInvalidMdlFile() throws IOException {
        assertFalse("Converted JSON file should not initially exist", jsonInvalidMdlFile.exists())
        final ConversionReport report = converter.performConvert(invalidMdlFile, workingFolder.getRoot())
        assertEquals("Checking for failure return code", ConversionCode.FAILURE, report.getReturnCode())
        assertFalse("No converted JSON file should have been created", jsonInvalidMdlFile.exists())
    }

}
