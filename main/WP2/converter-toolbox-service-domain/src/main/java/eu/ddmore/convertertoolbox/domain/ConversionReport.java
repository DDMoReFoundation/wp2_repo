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
package eu.ddmore.convertertoolbox.domain;

import java.util.ArrayList;
import java.util.List;

/**
 * REST-friendly implementation of { @link ConversionReportImpl }
 */
public final class ConversionReport {

    private ConversionReportOutcomeCode returnCode;
    private List<ConversionDetail> details;

    public ConversionReport() {
        details = new ArrayList<ConversionDetail>();
    }

    public ConversionReportOutcomeCode getReturnCode() {
        return returnCode;
    }

    public void setReturnCode(ConversionReportOutcomeCode returnCode) {
        this.returnCode = returnCode;
    }

    public List<ConversionDetail> getDetails() {
        List<ConversionDetail> result = new ArrayList<ConversionDetail>();
        result.addAll(details);
        return result;
    }

    public void addDetail(ConversionDetail conversionDetail) {
        details.add(conversionDetail);
    }

    @Override
    public String toString() {
        return String.format("ConversionReportImpl [returnCode=%s, details=%s]", returnCode, details);
    }
}
