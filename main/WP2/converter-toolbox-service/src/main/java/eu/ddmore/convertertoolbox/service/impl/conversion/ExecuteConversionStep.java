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

import java.io.File;

import org.apache.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.google.common.base.Preconditions;

import eu.ddmore.convertertoolbox.api.conversion.Converter;
import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.domain.internal.ConverterToolboxAPIObjectMapper;
import eu.ddmore.convertertoolbox.service.impl.ConversionResourcesConvention;

/**
 * {@link ConversionStep} implementation responsible for executing a { @link Converter } 
 */
@Order(2)
@Component
public class ExecuteConversionStep implements ConversionStep {
    private static final Logger LOG = Logger.getLogger(ExecuteConversionStep.class);
    @Override
    public void execute(ConversionContext conversionContext) {
        Preconditions.checkNotNull(conversionContext, "Conversion context was null");
        Preconditions.checkNotNull(conversionContext.getConversion(), "Conversion in Conversion Context was null");
        Preconditions.checkNotNull(conversionContext.getConverter(), "Converter in Conversion Context was null");
        Conversion conversion = conversionContext.getConversion();
        File inputFile = new File(new File(conversion.getWorkingDirectory(),ConversionResourcesConvention.INPUTS_DIRECTORY_NAME),conversion.getInputFileName());
        Preconditions.checkState(inputFile.exists(), String.format("Conversion [%s] input file %s does not exist", conversion.getId(), inputFile.getAbsolutePath()));
        
        Converter converter = conversionContext.getConverter();
        eu.ddmore.convertertoolbox.api.response.ConversionReport conversionReport = null;
        try {
            File outputDirectory = new File(conversion.getWorkingDirectory(),ConversionResourcesConvention.OUTPUTS_DIRECTORY_NAME);
            outputDirectory.mkdirs();
            LOG.debug(String.format("Conversion [%s] Executing conversion of %s with output directory %s",conversion.getId(), inputFile.getAbsolutePath(), outputDirectory.getAbsolutePath() ));
            conversionReport = converter.convert(inputFile, outputDirectory);
        } catch (Exception e) {
            throw new IllegalStateException("Converter execution failed with unexpected error.",e);
        }
        Preconditions.checkNotNull(conversionReport, "Converter did not return conversion report");
        conversion.setConversionReport(ConverterToolboxAPIObjectMapper.fromOldAPI(conversionReport));
    }

}
