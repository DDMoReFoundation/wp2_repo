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
package eu.ddmore.converter.test;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Test;

import eu.ddmore.convertertoolbox.api.exception.ConverterNotFoundException;
import eu.ddmore.convertertoolbox.api.response.ConversionDetail.Severity;
import eu.ddmore.convertertoolbox.api.response.ConversionReport;
import eu.ddmore.convertertoolbox.api.response.ConversionReport.ConversionCode;
import eu.ddmore.convertertoolbox.cli.Main;

/**
 * Test for {@link Main}.
 */
public class MainIT {

    private final static String modelFilePath = "/eu/ddmore/testdata/models/mdl/warfarin_PK_PRED/warfarin_PK_PRED.mdl";
    private final static String WORKING_DIRECTORY = "target/MainIT_Working_Dir/";

    @Before
    public void setUp() throws IOException {
        FileUtils.deleteDirectory(new File(WORKING_DIRECTORY));
        FileUtils.copyInputStreamToFile(
            MainIT.class.getResourceAsStream(modelFilePath),
            new File(WORKING_DIRECTORY, "warfarin_PK_PRED.mdl"));
    }

    @Test
    public void shouldConvertCLI() throws ConverterNotFoundException, IOException {
        String[] args = new String[] { "-in", WORKING_DIRECTORY, "-out", WORKING_DIRECTORY + "output", "-sn", "MDL", "-sv", "5.0.8-succeeding", "-tn", "NMTRAN", "-tv", "7.2.0-succeeding" };
        Main cli = new Main();
        cli.parseArguments(args);
        ConversionReport[] reports = cli.runFromCommandLine();
        assertEquals(ConversionCode.SUCCESS, reports[0].getReturnCode());
        assertEquals(reports[0].getDetails(Severity.ERROR).size(), 0);
    }

    @Test
    public void shouldConvertCLI_AbsolutePath() throws ConverterNotFoundException, IOException {
        String[] args = new String[] { "-in", new File(WORKING_DIRECTORY).getAbsolutePath(), "-out", new File(WORKING_DIRECTORY, "output").getAbsolutePath(), "-sn", "MDL", "-sv", "5.0.8-succeeding", "-tn", "NMTRAN", "-tv", "7.2.0-succeeding" };
        Main cli = new Main();
        cli.parseArguments(args);
        ConversionReport[] reports = cli.runFromCommandLine();
        assertEquals(ConversionCode.SUCCESS, reports[0].getReturnCode());
        assertEquals(reports[0].getDetails(Severity.ERROR).size(), 0);
    }

}
