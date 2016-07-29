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
import java.nio.file.Path;
import java.util.Map.Entry;
import java.util.Properties;

import org.junit.runners.Parameterized;

import com.google.common.base.Function;
import com.google.common.collect.Iterables;

/**
 * Holds common utility functions and constants used by tests.
 */
public class ModelsTestHelper {
    public final static String TEST_RECORD_FILE = "executedModels.json";
    public final static String AT_WORKING_DIRECTORY_LOCATION_PROP = "at.workingDirectory.location";
    final static String NONMEM_COMPILER_EXECUTABLE_PROP = "nonmem.compiler.executable";
    final static String NONMEM_COMPILER_PARAMETERS_PROP = "nonmem.compiler.parameters";
    /**
     * The method that produces the parameters to be passed to each construction of the calling test class.
     * In this case, the {@link File}s that are the models for which to test the conversion.
     * <p>
     * NB: The JUnit {@link Parameterized} framework requires the parameter-providing method to
     * return an {@link Iterable} of Arrays.
     * <p>
     * @param modelsSubdirectory - subdirectory of target/WorkingDir/test-models from which models will be discovered
     * @param modelsFileExtension - the file extension, excluding the dot, of the model files
     * @param acceptanceTestsWorkingDirectoriesRoot - working directory assigned to a given test case suite.
     * @return the models to convert as Iterable of Object[] arrays with the following elements:
     *                  <ol>
     *                      <li>{@link File} - test case's working directory</li>
     *                      <li>String - relative path to a model file</li>
     *                      <li>{@link File} - a path of the source test data directory</li>
     *                   </ol>s
     */
    public static Iterable<Object[]> getModelsToTest(final String modelsSubdirectory, final String modelsFileExtension, final File acceptanceTestsWorkingDirectoriesRoot) {
        final File modelsDir = new File(getTestModelsDir() + modelsSubdirectory).getAbsoluteFile();
        return Iterables.transform(
            new ModelsDiscoverer(modelsDir, modelsFileExtension).getAllModels(),
            new Function<File, Object[]>() {
                public Object[] apply(final File input) {
                    File atWorkingDir = new File(acceptanceTestsWorkingDirectoriesRoot, input.getName());
                    Path relativeInputFile = modelsDir.toPath().relativize(input.toPath());
                    return new Object[] { atWorkingDir, relativeInputFile.toString(), modelsDir };
                };
            }
        );
    }
    
    /**
     * Method handles the test system properties.
     */
    public static void prepareTestSystemProperties() {
        Properties properties = new Properties();
        try {
            properties.load(PharmmlToNmtranModelsTest.class.getResourceAsStream("/test.properties"));
        } catch (IOException e) {
            throw new IllegalStateException("Could not read properties file.",e);
        }
        for (Entry<Object, Object> en : properties.entrySet()) {
            String key = (String) en.getKey();
            if (System.getProperty(key)==null) {
                System.setProperty(key, (String) en.getValue());
            }
        }
    }
    
    /**
     * @return location of the test models directory
     */
    public static String getTestModelsDir() {
        return System.getProperty("test-models.location");
    }

    /**
     * @param suiteName a name of the acceptance tests suite
     * @return a working directory where acceptance tests of the suite should place their working directories
     */
    public static File resolveAcceptanceTestSuiteWorkingDirectory(String suiteName) {
        return new File(System.getProperty(ModelsTestHelper.AT_WORKING_DIRECTORY_LOCATION_PROP),suiteName).getAbsoluteFile();
    }
}
