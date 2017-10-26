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
package eu.ddmore.converter.mdl2json

import org.apache.log4j.Logger

import eu.ddmore.converter.mdl2json.domain.Mcl
import eu.ddmore.convertertoolbox.api.domain.LanguageVersion
import eu.ddmore.convertertoolbox.api.domain.Version
import eu.ddmore.convertertoolbox.api.response.ConversionReport
import eu.ddmore.convertertoolbox.api.response.ConversionReport.ConversionCode
import eu.ddmore.convertertoolbox.api.spi.ConverterProvider
import eu.ddmore.convertertoolbox.domain.ConversionReportImpl
import eu.ddmore.convertertoolbox.domain.LanguageVersionImpl
import eu.ddmore.convertertoolbox.domain.VersionImpl
import groovy.json.JsonSlurper


/**
 * This is a {@link ConverterProvider} implementation from JSON to MDL, specified versions thereof.
 */
public class JSONToMDLConverter implements ConverterProvider {

    private static final Logger LOG = Logger.getLogger(JSONToMDLConverter.class);

    private static final String MDL_FILE_EXTENSION = ".mdl"
    private static final String JSON_FILE_EXTENSION = ".json"

    private final LanguageVersion source = new LanguageVersionImpl("JSON", new VersionImpl(8, 0, 0))
    private final LanguageVersion target = new LanguageVersionImpl("MDL", new VersionImpl(8, 0, 0))
    // this should be the same as the development stream version as of this Maven module
    private final Version converterVersion = new VersionImpl(0, 3, 0);

	@Override
	public ConversionReport performConvertToFile(File src, File outputFile){
		JsonSlurper jsonSlurper = new JsonSlurper();
		Mcl mclFile = new Mcl(jsonSlurper.parseText(src.getText()))

		ConversionReport report = new ConversionReportImpl();

		final String mdl = mclFile.toMDL();

		if (mdl) {
			outputFile.write(mdl)
			report.setReturnCode(ConversionCode.SUCCESS);
			return report
		} else {
			throw new RuntimeException("Couldn't parse JSON into MDL from file " + src.getPath())
		}

	}
	
    /**
     * Converter Toolbox required entry point.
     */
	@Override
    public ConversionReport performConvert(File src, File outputDirectory) throws IOException {
        String outputFileName = computeOutputFileName(src.getName())

        JsonSlurper jsonSlurper = new JsonSlurper();
        Mcl mclFile = new Mcl(jsonSlurper.parseText(src.getText()))

        ConversionReport report = new ConversionReportImpl();

        final String mdl = mclFile.toMDL();

        if (mdl) {
            def outputFile = new File(outputDirectory.getAbsolutePath(), outputFileName);
            outputFile.write(mdl)
            report.setReturnCode(ConversionCode.SUCCESS);
            return report
        } else {
            throw new RuntimeException("Couldn't parse JSON into MDL from file " + src.getPath())
        }
    }

    private String computeOutputFileName(String name) {
        int dotIndex = name.lastIndexOf(JSON_FILE_EXTENSION)

        if (dotIndex == -1) {
            return name + MDL_FILE_EXTENSION
        } else {
            return name.substring(0, dotIndex) + MDL_FILE_EXTENSION
        }
    }

    public ConversionReport[] performConvert(File[] src, File outputDirectory) throws IOException {
        ConversionReport[] reports = new ConversionReport[src.length];
        int i = 0;
        src.each { it ->
            reports[i++] = performConvert(it, outputDirectory);
        }
        return reports;
    }

    @Override
    public LanguageVersion getSource() {
        return source;
    }

    @Override
    public LanguageVersion getTarget() {
        return target;
    }

    @Override
    public Version getConverterVersion() {
        return converterVersion;
    }

    @Override
    public String toString() {
        return String.format("JSON to MDL Converter [source=%s, target=%s, converterVersion=%s]", source, target, converterVersion)
    }
}
