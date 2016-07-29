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
package eu.ddmore.convertertoolbox.api.response;

import java.util.List;

import eu.ddmore.convertertoolbox.api.response.ConversionDetail.Severity;

/**
 * A report which contains the outcome of a conversion
 */
public interface ConversionReport {

	/**
	 * Models the conversion code result.
	 */
	public enum ConversionCode {
		SUCCESS, FAILURE;
	}
	
	/**
	 * @return the result associated with this ConversionReport
	 */
	ConversionCode getReturnCode();

	/**
	 * Sets the conversion code result.
	 * @param returnCode
	 */
    void setReturnCode(ConversionCode returnCode);

    /**
     * Get the list of ConversionDetails that match the input severity level.
     * @param severity the severity level
     * @return the list of ConversionDetails that match the input severity level.
     */
    List<ConversionDetail> getDetails(Severity severity);

    /**
     * Adds a ConversionDetail to this ConversionReport
     * @param conversionDetail the ConversionDetail to add
     */
    void addDetail(ConversionDetail conversionDetail);
}
