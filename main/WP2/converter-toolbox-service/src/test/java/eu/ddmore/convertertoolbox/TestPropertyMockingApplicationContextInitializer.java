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
package eu.ddmore.convertertoolbox;

import java.io.File;

import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.MutablePropertySources;
import org.springframework.core.env.StandardEnvironment;
import org.springframework.mock.env.MockPropertySource;

import com.google.common.io.Files;

/**
 * A Context initialiser that adds support for test runtime properties
 */
public class TestPropertyMockingApplicationContextInitializer implements ApplicationContextInitializer<ConfigurableApplicationContext> {

    /**
     * Extending classes should not override this method, override the createPropertySource method instead to add test-specific properties to the environment.
     */
    @Override
    public void initialize(ConfigurableApplicationContext applicationContext) {
        MutablePropertySources propertySources = applicationContext.getEnvironment().getPropertySources();
        MockPropertySource mockEnvVars = createPropertySource();
        propertySources.replace(StandardEnvironment.SYSTEM_ENVIRONMENT_PROPERTY_SOURCE_NAME, mockEnvVars);
    }
    
    /**
     * Extending classes should invoke parent class' createPropertySource() and then add their properties.
     * 
     * @return property source with additional properties
     */
    protected MockPropertySource createPropertySource() {
        return new MockPropertySource().withProperty("cts.workingDirectory", ctsWorkingDirectory());
    }

    private String ctsWorkingDirectory() {
        File tmpDir = Files.createTempDir();
        tmpDir.deleteOnExit();
        return tmpDir.getAbsolutePath();
    }
}