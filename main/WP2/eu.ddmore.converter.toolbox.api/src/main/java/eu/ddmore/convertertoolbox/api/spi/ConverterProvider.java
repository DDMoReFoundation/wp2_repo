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
package eu.ddmore.convertertoolbox.api.spi;

import java.io.File;
import java.io.IOException;

import eu.ddmore.convertertoolbox.api.domain.LanguageVersion;
import eu.ddmore.convertertoolbox.api.domain.Version;
import eu.ddmore.convertertoolbox.api.response.ConversionReport;

/**
 * Interface which Converter providers should implement to enable them to be called by clients of the Converter Toolbox. 
 */
public interface ConverterProvider {

	/**
     * Convert the source file and put the specified file. 
	 * @param src the source file to be converted
	 * @param tgtFile the file to write output to.
     * @return a conversion report containing the details of the requested conversion 
     * @throws IOException if there is some error in file reading/writing
	 */
    ConversionReport performConvertToFile(File src, File tgtFile) throws IOException;

    /**
	 * Converts the source file using the languages and versions associated with this ConverterProvider.
	 * @param src the source file to be converted
	 * @param outputDirectory the output directory where the converted file will be stored
	 * @return a conversion report containing the details of the requested conversion
     * @throws IOException if there is some error in file reading/writing
	 */
    ConversionReport performConvert(File src, File outputDirectory) throws IOException;

    /**
     * @return the source language and version
     */
    LanguageVersion getSource();

    /**
     * @return the target language and version
     */
    LanguageVersion getTarget();

    /**
     * @return the converter version
     */
    Version getConverterVersion();
}
