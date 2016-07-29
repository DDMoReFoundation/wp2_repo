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
package eu.ddmore.convertertoolbox.rest.exceptions;


import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

/**
 * Exception representing an error when the conversion input archive was not uploaded.
 */
@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason="Conversion input archive missing.")
public class ConversionInputsNotSpecified extends Exception {

    private static final long serialVersionUID = 1L;

    /**
     * Creates a new instance of the exception with the given message
     * @param message Error message
     */
    public ConversionInputsNotSpecified(String message) {
        super(message);
    }

    /**
     * Creates a new instance of the exception with the given message and root exception
     * @param message Error message
     * @param cause Root exception
     */
    public ConversionInputsNotSpecified(String message, Throwable cause) {
        super(message, cause);
    }

}
