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

import eu.ddmore.convertertoolbox.api.response.ConversionDetail;
import eu.ddmore.convertertoolbox.api.response.ConversionDetail.Severity;
import eu.ddmore.convertertoolbox.api.response.ConversionReport;

/**
 * A report which contains the outcome of a conversion
 */
public final class ConversionReportImpl implements ConversionReport {

    private ConversionCode returnCode;
    private List<ConversionDetail> details;

    public ConversionReportImpl() {
        details = new ArrayList<ConversionDetail>();
    }

    @Override
    public ConversionCode getReturnCode() {
        return returnCode;
    }

    @Override
    public void setReturnCode(ConversionCode returnCode) {
        this.returnCode = returnCode;
    }

    @Override
    public List<ConversionDetail> getDetails(Severity severity) {
        if (severity == Severity.ALL) {
            return details;
        }
        List<ConversionDetail> res = new ArrayList<ConversionDetail>();
        for (ConversionDetail d : details) {
            if (d.getServerity().getRank() <= severity.getRank()) {
                res.add(d);
            }
        }
        return res;
    }

    @Override
    public void addDetail(ConversionDetail conversionDetail) {
        details.add(conversionDetail);
    }

    @Override
    public String toString() {
        return String.format("ConversionReportImpl [returnCode=%s, details=%s]", returnCode, details);
    }

}
