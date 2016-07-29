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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;

import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.service.impl.ConversionResourcesConvention;


/**
 * {@link ConversionStep} implementation responsible for extracting Conversion inputs 
 */
@Order(1)
@Component
public class PrepareInputsStep implements ConversionStep {
    private final static Logger LOG = Logger.getLogger(PrepareInputsStep.class);
    
    @Override
    public void execute(ConversionContext conversionContext) {
        Preconditions.checkNotNull(conversionContext, "Conversion context was null");
        Preconditions.checkNotNull(conversionContext.getConversion(), "Conversion context was null");
        Conversion conversion = conversionContext.getConversion();
        File inputArchive = conversion.getInputArchive();
        File outputDir = new File(inputArchive.getParentFile(),ConversionResourcesConvention.INPUTS_DIRECTORY_NAME);
        outputDir.mkdirs();
        ZipFile zipFile = null;
        LOG.debug(String.format("Conversion [%s]: Extracting %s to %s.",conversion.getId(),inputArchive.getAbsolutePath(), outputDir.getAbsolutePath()));
        try {
            zipFile = new ZipFile(inputArchive);
            Enumeration<ZipArchiveEntry> zipArchiveEntries = zipFile.getEntries();
            
            while(zipArchiveEntries.hasMoreElements()) {
                ZipArchiveEntry en = zipArchiveEntries.nextElement();

                File path = new File(outputDir,en.getName());
                LOG.debug(String.format("Conversion [%s]: Extracting %s",conversion.getId(), path.getAbsolutePath()));
                unzipEntry(zipFile, en,path);
            }
        } catch (IOException e) {
            throw new IllegalStateException(String.format("Could not extract archive file %s.",inputArchive.getAbsolutePath()),e);
        } finally {
            if(zipFile!=null) {
                ZipFile.closeQuietly(zipFile);
            }
        }
    }
    
    private void unzipEntry(ZipFile zipFile, ZipArchiveEntry en, File path) {
        if(en.isDirectory()) {
            path.mkdirs();
        } else {
            InputStream is = null;
            FileOutputStream fos = null;
            try {
                path.createNewFile();
                is = zipFile.getInputStream(en);
                fos = new FileOutputStream(path);
                IOUtils.copy(is, fos);
            } catch (IOException e) {
                throw new IllegalStateException(String.format("Could not unzip %s",path.getAbsolutePath()),e);
            } finally {
                if(is!=null) {
                    IOUtils.closeQuietly(is);
                }
                if(fos!=null) {
                    IOUtils.closeQuietly(fos);
                }
            }
        }
    }

}
