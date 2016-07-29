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


/**
 * Holds naming convention for Conversion resources
 */
public class ConversionResourcesConvention {
    /**
     * Name of the directory where converters are requested to put conversion outputs to
     */
    public static final String OUTPUTS_DIRECTORY_NAME = "outputs";
    /**
     * Name of the directory where inputs for conversion are extracted to
     */
    public static final String INPUTS_DIRECTORY_NAME = "inputs";
    /**
     * Name of the archive file received with the conversion request
     */
    public static final String INPUTS_ARCHIVE_NAME = "inputs.phex";
    /**
     * Name of the archive file containing 'outputs' directory contents
     */
    public static final String OUTPUTS_ARCHIVE_NAME = "outputs.phex";
}
