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
package eu.ddmore.convertertoolbox.domain.internal;

/**
 * A mapper object, converts domain objects representations from internal to external
 */
public class ObjectMapper {
    /**
     * Hidden constructor
     */
    private ObjectMapper() {
        
    }
    
    public static eu.ddmore.convertertoolbox.domain.Conversion map(Conversion con) {
        eu.ddmore.convertertoolbox.domain.Conversion result = new eu.ddmore.convertertoolbox.domain.Conversion();
        result.setId(con.getId());
        result.setStatus(con.getStatus());
        result.setFrom(con.getFrom());
        result.setTo(con.getTo());
        result.setInputFileName(con.getInputFileName());
        result.setOutputFileSize(con.getOutputFileSize());
        result.setSubmissionTime(con.getSubmissionTime());
        result.setCompletionTime(con.getCompletionTime());
        result.setConversionReport(con.getConversionReport());
        return result;
    }
    
    public static Conversion map(eu.ddmore.convertertoolbox.domain.Conversion con) {
        Conversion result = new Conversion();
        result.setId(con.getId());
        result.setStatus(con.getStatus());
        result.setFrom(con.getFrom());
        result.setTo(con.getTo());
        result.setInputFileName(con.getInputFileName());
        result.setOutputFileSize(con.getOutputFileSize());
        result.setSubmissionTime(con.getSubmissionTime());
        result.setCompletionTime(con.getCompletionTime());
        result.setConversionReport(con.getConversionReport());
        return result;
    }
}
