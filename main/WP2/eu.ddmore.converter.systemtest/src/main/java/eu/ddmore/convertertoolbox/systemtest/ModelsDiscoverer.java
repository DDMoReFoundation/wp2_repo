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
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;


public class ModelsDiscoverer {
    private final static Logger LOG = Logger.getLogger(ModelsDiscoverer.class);
    private final File modelsRoot;
    private final String modelFileExtension;
    
    ModelsDiscoverer(final File modelsRoot, final String modelFileExtension) {
        this.modelsRoot = modelsRoot;
        this.modelFileExtension = modelFileExtension;
    }
    
    Collection<File> getAllModels() {
        LOG.info("Discovering models with file extension ." + modelFileExtension + " within subdirectory \"" + this.modelsRoot + "\" ...");
        final Collection<File> allModelFiles = FileUtils.listFiles(modelsRoot, new String[] {this.modelFileExtension}, true);
        Iterator<File> modelFilesItr = allModelFiles.iterator();
        while (modelFilesItr.hasNext()) {
        	File modelFile = modelFilesItr.next();
        	if(modelFile.getParent().endsWith("targetblock")){
        		modelFilesItr.remove();
        	}else{
        		LOG.info("Found model file: " + modelFile);
        	}
        }
        List<File> result = new ArrayList<>();
        result.addAll(allModelFiles);
        Collections.sort(result);
        return result;
    }

}
