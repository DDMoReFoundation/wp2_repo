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

import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

import eu.ddmore.convertertoolbox.domain.ConversionDetail;
import eu.ddmore.convertertoolbox.domain.ConversionDetailSeverity;
import eu.ddmore.convertertoolbox.domain.ConversionReport;
import eu.ddmore.convertertoolbox.domain.ConversionReportOutcomeCode;
import eu.ddmore.convertertoolbox.domain.ConversionStatus;
import eu.ddmore.convertertoolbox.domain.internal.Conversion;

/**
 * Represents a conversion being scheduled and ready to be assigned a working thread to perform conversion
 */
public class ConversionTask implements Runnable {
    private final Logger LOG = Logger.getLogger(ConversionTask.class);
    private final ConversionContext conversionContext;
    private List<ConversionStep> conversionSteps;
    private final static String ERROR_MESSAGE_KEY = "error";
    /**
     * Creates a new instance of conversion task
     * @param conversionContext
     */
    public ConversionTask(ConversionContext conversionContext) {
        super();
        Preconditions.checkNotNull(conversionContext, "Converter context can't be null");
        this.conversionContext = conversionContext;
    }
    
    @Override
    public void run() {
        LOG.debug(String.format("Conversion [%s] Executing conversion from %s to %s using converter %s", 
            conversionContext.getConversion().getId(), conversionContext.getConversion().getFrom(), 
            conversionContext.getConversion().getTo(), conversionContext.getConverter().toString()));
        try {
            main();
        } catch(Exception ex) {
            LOG.error(String.format("Conversion [%s] Exception was thrown during conversion execution",conversionContext.getConversion().getId()), ex);
            handleException(conversionContext.getConversion(), ex);
        } finally {
            Conversion conversion = conversionContext.getConversion();
            conversion.setCompletionTime(new Date().getTime());
            conversion.setStatus(ConversionStatus.Completed);
            conversionContext.updateConversion(conversion);
        }
    }

    @VisibleForTesting
    void handleException(Conversion conversion, Exception ex) {
        ConversionReport conversionReport = generateFailedConversionReport(conversion);
        conversionReport.addDetail(createConversionDetailFor(ex));
        conversion.setConversionReport(conversionReport);
    }

    private ConversionReport generateFailedConversionReport(Conversion conversion) {
        ConversionReport conversionReport = conversion.getConversionReport();
        if(conversionReport==null) {
            conversionReport = new ConversionReport();
        }
        conversionReport.setReturnCode(ConversionReportOutcomeCode.FAILURE);
        return conversionReport;
    }

    private void main() {
        Preconditions.checkNotNull(conversionSteps,"Conversion steps list was null");
        Conversion conversion = conversionContext.getConversion();
        conversion.setStatus(ConversionStatus.Running);
        conversionContext.updateConversion(conversion);
        for(ConversionStep step : conversionSteps) {
            step.execute(conversionContext);
        }
    }
    
    private ConversionDetail createConversionDetailFor(Exception exception) {
        ConversionDetail conversionDetail = new ConversionDetail();
        conversionDetail.setMessage(exception.getMessage());
        conversionDetail.setSeverity(ConversionDetailSeverity.ERROR);
        if(exception.getCause()!=null && exception.getCause().getMessage()!=null) {
            conversionDetail.addInfo(ERROR_MESSAGE_KEY, exception.getCause().getMessage());
        }
        return conversionDetail;
    }
    
    
    public void setConversionSteps(List<ConversionStep> conversionSteps) {
        this.conversionSteps = conversionSteps;
    }
}
