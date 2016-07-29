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
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

/**
 * Tests { @link ServiceWorkingDirectory }
 */
@RunWith(MockitoJUnitRunner.class)
public class ServiceWorkingDirectoryIntegrationTest {
    private String identityFile = ".IDENTITY";

    private File workingDirectory = null;

    private ServiceWorkingDirectory instance = new ServiceWorkingDirectory();
    
    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();
    
    @Before
    public void setUp() throws Exception {
        workingDirectory = tempFolder.newFolder("test-wd");
        instance.setIdentityFileName(identityFile);
        instance.setWorkingDirectory(workingDirectory);
    }

    @Test(expected=NullPointerException.class)
    public void shouldThrowRuntimeExceptionIfWorkingDirectoryNotSet() {
        instance.setWorkingDirectory(null);

        instance.initialise();
    }

    @Test(expected=IllegalStateException.class)
    public void shouldThrowRuntimeExceptionIfWorkingDirectoryIsBlank() {
        instance.setWorkingDirectory(new File(""));

        instance.initialise();
    }

    @Test(expected=IllegalStateException.class)
    public void shouldThrowRuntimeExceptionIfIdentityFileNameIsBlank() {
        instance.setIdentityFileName("");

        instance.initialise();
    }


    @Test(expected=IllegalStateException.class)
    public void shouldThrowRuntimeExceptionIfWorkingDirectoryIsNotAdirectory() throws IOException {
        File dir = new File(tempFolder.getRoot(),"test-wd-2");
        dir.createNewFile();
        instance.setWorkingDirectory(dir);
        instance.initialise();
    }
    
    @Test(expected=IllegalStateException.class)
    public void shouldThrowExceptionIfWorkingDirectoryDoesntContainIdentityFile() throws IOException {
        new File(workingDirectory, "dir1").mkdir();
        new File(workingDirectory, "dir2").mkdir();
        new File(workingDirectory, "dir3").mkdir();
        new File(workingDirectory, "file1").createNewFile();
        
        instance.initialise();
    }
    

    @Test
    public void shouldRemoveAllFilesOtherThanIdentity() throws IOException {
        File dir1 = new File(workingDirectory, "dir1");
        dir1.mkdir();
        File dir2 = new File(workingDirectory, "dir2");
        dir2.mkdir();
        File dir3 = new File(workingDirectory, "dir3");
        dir3.mkdir();
        new File(dir3, "sub-file").createNewFile();
        
        File file1 = new File(workingDirectory, "file1");
        file1.createNewFile();
        File identity = new File(workingDirectory, identityFile);
        identity.createNewFile();
        
        instance.initialise();
        
        assertFalse(String.format("Directory %s should not exist", dir1),dir1.exists());
        assertFalse(String.format("Directory %s should not exist", dir2),dir2.exists());
        assertFalse(String.format("Directory %s should not exist", dir3),dir3.exists());
        assertFalse(String.format("File %s should not exist", file1),file1.exists());
        assertTrue(String.format("File %s should exist", identity),identity.exists());
    }

    @Test
    public void shouldCreateNewDirectoryWithIdentityFile() {
        File workingDir = new File(tempFolder.getRoot(),"shouldCreateNewDirectoryWithIdentityFile");
        File identity = new File(workingDir, identityFile);
        
        instance.setWorkingDirectory(workingDir);
        instance.initialise();

        assertTrue(String.format("Directory %s should exist", workingDir),workingDir.exists());
        assertTrue(String.format("File %s should exist", identity),identity.exists());
    }

    @Test
    public void shouldCreateIdentityFileForEmptyWorkingDirectory() throws IOException {
        File workingDir = tempFolder.newFolder("shouldCreateIdentityFileForEmptyWorkingDirectory");
        File identity = new File(workingDir, identityFile);
        
        instance.setWorkingDirectory(workingDir);
        instance.initialise();

        assertTrue(String.format("Directory %s should exist", workingDir),workingDir.exists());
        assertTrue(String.format("File %s should exist", identity),identity.exists());
    }

    @Test
    public void newDirectory_shouldCreateANewDirectory() {
        File file = instance.newDirectory("my-directory");
        
        assertTrue("Should be directory", file.isDirectory());
        assertTrue("Should exist", file.exists());
    }

    @Test(expected = NullPointerException.class)
    public void newDirectory_shouldThrowExceptionIfNullDirectoryName() {
        instance.newDirectory(null);
    }
}
