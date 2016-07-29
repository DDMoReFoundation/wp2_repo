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
package eu.ddmore.convertertoolbox.systemtest;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.junit.AfterClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.Lists;

/**
 * Run MDL -> JSON conversions over the testdata models within the "MDL" subdirectory.
 */
@RunWith(Parameterized.class)
public class MdlToJsonModelsTest extends ConverterATParent {
    private final static Logger LOG = Logger.getLogger(MdlToJsonModelsTest.class);
    private final static String NAME = "MdlToJsonModelsTest";

    private final static String MODELS_SUBDIRECTORY = "MDL" + File.separator + FileType.MDL.getVersion();

    /**
     * The method that produces the parameters to be passed to each construction of the test class.
     * In this case, the {@link File}s that are the models for which to test the conversion.
     * <p>
     * NB: The JUnit {@link Parameterized} framework requires the parameter-providing method to
     * return an {@link Iterable} of Arrays.
     * <p>
     * @return the models to convert as Iterable of Object[] arrays with the following elements:
     *                  <ol>
     *                      <li>{@link File} - test case's working directory</li>
     *                      <li>String - relative path to a model file</li>
     *                      <li>{@link File} - a path of the source test data directory</li>
     *                   </ol>
     * @throws Exception if collecting models to test failed
     */
    @Parameterized.Parameters(name = "{index}: Model {1}")
    public static Iterable<Object[]> getModelsToTest() throws Exception {
        ModelsTestHelper.prepareTestSystemProperties();
        LOG.info(String.format("Preparing parameters for %s.",MdlToJsonModelsTest.class));
        File atWd = ModelsTestHelper.resolveAcceptanceTestSuiteWorkingDirectory(NAME);
        atWd.mkdirs();
        
        Iterable<Object[]> result = ModelsTestHelper.getModelsToTest(MODELS_SUBDIRECTORY, FileType.MDL.getExtension(),atWd);
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.writeValue(new File(atWd,ModelsTestHelper.TEST_RECORD_FILE), Lists.newArrayList(result));
        return result;
    }

    /**
     * See {@link ConverterATParent}
     */
    public MdlToJsonModelsTest(File workingDirectory, String model, File testDataDir) {
        super(workingDirectory, model, testDataDir);
    }
    
    @Test
    public void convertsMdlToJson() throws IOException {
        prepareWorkingDirectory();
        final ConverterRunner runner = new ConverterRunner(getModelAbsoluteFile(), FileType.JSON.getExtension(),
                FileType.MDL.name(), FileType.MDL.getVersion(), FileType.JSON.name(), FileType.JSON.getVersion(),
                new ConverterJsonOutputFailureChecker());
        runner.run();
    }

    @AfterClass
    public static void tearDown() {
        collectResults(new File(System.getProperty(ModelsTestHelper.AT_WORKING_DIRECTORY_LOCATION_PROP),NAME).getAbsoluteFile());
    }
}
