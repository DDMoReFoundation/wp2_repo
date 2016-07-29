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
package eu.ddmore.convertertoolbox.api.conversion;

import java.io.File;
import java.io.IOException;

import eu.ddmore.convertertoolbox.api.domain.LanguageVersion;
import eu.ddmore.convertertoolbox.api.domain.Version;
import eu.ddmore.convertertoolbox.api.response.ConversionReport;
import eu.ddmore.convertertoolbox.api.spi.ConverterProvider;

/**
 * Represents a converter available in the toolbox
 */
public interface Converter {

	/**
     * Convert the source file and put the output file(s) in the output directory. 
	 * @param src the source file to be converted
	 * @param outputDirectory the output directory where the converted file will be stored
     * @return a conversion report containing the details of the requested conversion 
     * @throws IOException if there is some error in file reading/writing
	 */
    ConversionReport convert(File src, File outputDirectory) throws IOException;
    
    /**
     * Convert the source file and put the output file(s) in the output directory. 
     * This method will run asynchronously, though it will return immediately. 
     * The listener will be called after the conversion completes (failure or success).
	 * @param src the source file to be converted
	 * @param outputDirectory the output directory where the converted files will be stored
     * @param listener a callback hook, if specified the listener will be notified by the converter when certain states are reached  
     * @throws IOException  if there is some error in file reading/writing
     */
    void convert(File src, File outputDirectory, ConversionListener listener) throws IOException;
    
    /**
     * @return the converter provider associated with this Converter
     */
    ConverterProvider getConverterProvider();
    
    /**
     * @return the target language and version associated with this Converter
     */
    LanguageVersion getTarget();
    
    /**
     * @return the source language and version associated with this Converter
     */
    LanguageVersion getSource();
    
    /**
     * @return the version of the converter associated with this Converter
     */
    Version getConverterVersion();
}
