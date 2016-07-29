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
package eu.ddmore.convertertoolbox.concurrent;

import com.google.common.util.concurrent.FutureCallback;

import eu.ddmore.convertertoolbox.api.conversion.ConversionListener;
import eu.ddmore.convertertoolbox.api.response.ConversionDetail;
import eu.ddmore.convertertoolbox.api.response.ConversionReport;
import eu.ddmore.convertertoolbox.api.response.ConversionReport.ConversionCode;
import eu.ddmore.convertertoolbox.domain.ConversionDetailImpl;
import eu.ddmore.convertertoolbox.domain.ConversionReportImpl;

/**
 * The FutureCallback inplementation for single report, i.e. single file conversion.
 */
public class ConversionCallbackImpl implements FutureCallback<ConversionReport> {
    private ConversionListener listener;
    
    public ConversionCallbackImpl(ConversionListener listener) {
        this.listener = listener;
    }
    
    /**
     * Called if the associated ListenableFuture object successfully terminates its task.
     * @param result the object returned by the call method of the associated ListenableFuture object.
     */
    @Override
    public void onSuccess(ConversionReport result) {
        listener.conversionComplete(result);
    }

    /**
     * Called if the associated ListenableFuture object fails to execute its task due to an error.
     * @param t the error thrown by the call method of the associated ListenableFuture object.
     */
    @Override
    public void onFailure(Throwable t) {
        ConversionReport report = createConversionReport(t);
        listener.conversionComplete(report);
    }

    /**
     * 
     * @param thrown the error thrown by the onFailure method.
     * @return a conversion report marked as 'FAILURE', with additional error detail derived from the input error message.
     */
    private ConversionReport createConversionReport(Throwable thrown) {
        ConversionReport report = new ConversionReportImpl();
        report.setReturnCode(ConversionCode.FAILURE);
        ConversionDetail detail = new ConversionDetailImpl();
        detail.addInfo("Error in reading/writing input/output file", thrown.getMessage());
        report.addDetail(detail);
        return report;
    }

}
