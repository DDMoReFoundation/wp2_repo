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

import org.eclipse.emf.ecore.util.EcoreUtil;

import eu.ddmore.converter.mdl2json.domain.Mcl
import eu.ddmore.converter.mdl2json.utils.VectorAttributeRewrite
import eu.ddmore.convertertoolbox.api.domain.LanguageVersion
import eu.ddmore.convertertoolbox.api.domain.Version
import eu.ddmore.convertertoolbox.api.response.ConversionReport
import eu.ddmore.convertertoolbox.api.response.ConversionReport.ConversionCode
import eu.ddmore.convertertoolbox.api.spi.ConverterProvider
import eu.ddmore.convertertoolbox.domain.ConversionReportImpl
import eu.ddmore.convertertoolbox.domain.LanguageVersionImpl
import eu.ddmore.convertertoolbox.domain.VersionImpl
import eu.ddmore.mdlparse.MdlParser
import groovy.json.JsonBuilder


/**
 * This is a {@link ConverterProvider} implementation from MDL to JSON, specified versions thereof.
 */
public class MDLToJSONConverter implements ConverterProvider {

    private static final Logger LOG = Logger.getLogger(MDLToJSONConverter.class)

    private static final String MDL_FILE_EXTENSION = ".mdl"
    private static final String JSON_FILE_EXTENSION = ".json"

    private final LanguageVersion source = new LanguageVersionImpl("MDL", new VersionImpl(8, 0, 0))
    private final LanguageVersion target = new LanguageVersionImpl("JSON", new VersionImpl(8, 0, 0))
    // this should be the same as the development stream version as of this Maven module
    private final Version converterVersion = new VersionImpl(0, 3, 0);

	
	@Override
	public ConversionReport performConvertToFile(File src, File outputFile) throws IOException {
		// We know we're going to return a conversion report so create it up front; it is added to at various places in this method
		final ConversionReport report = new ConversionReportImpl()

		final eu.ddmore.mdl.mdl.Mcl orig = new MdlParser().parse(src, report)
		
		if (ConversionCode.FAILURE.equals(report.getReturnCode())) {
			return report // Bail out - couldn't parse the MDL
		}
		
		final eu.ddmore.mdl.mdl.Mcl mcl = expandShorthands(orig)

		final String json = toJSON(mcl)
		
		if (json) {
			outputFile.write(json)
			report.setReturnCode(ConversionCode.SUCCESS);
			return report
		} else {
			throw new RuntimeException("Couldn't write out JSON from MDL parsed from file " + src.getPath())
		}

	}
	
    /**
     * Converter Toolbox required entry point.
     */
	@Override
    public ConversionReport performConvert(File src, File outputDirectory) throws IOException {
        // We know we're going to return a conversion report so create it up front; it is added to at various places in this method
        final ConversionReport report = new ConversionReportImpl()

        final eu.ddmore.mdl.mdl.Mcl orig = new MdlParser().parse(src, report)
        
        if (ConversionCode.FAILURE.equals(report.getReturnCode())) {
            return report // Bail out - couldn't parse the MDL
        }
        
        final eu.ddmore.mdl.mdl.Mcl mcl = expandShorthands(orig)

        final String json = toJSON(mcl)
        
        if (json) {
            def outputFile = new File(outputDirectory.getAbsolutePath(), computeOutputFileName(src.getName()))
            outputFile.write(json)
            report.setReturnCode(ConversionCode.SUCCESS);
            return report
        } else {
            throw new RuntimeException("Couldn't write out JSON from MDL parsed from file " + src.getPath())
        }
    }

    private expandShorthands(eu.ddmore.mdl.mdl.Mcl orig) {
        final eu.ddmore.mdl.mdl.Mcl mcl = EcoreUtil.copy(orig)
        final VectorAttributeRewrite vectArgR = new VectorAttributeRewrite();
        mcl.eAllContents().each {vectArgR.doSwitch(it)}
        return mcl
    }

    /**
     * Convert an MCL object into JSON.
     * 
     * @param mcl
     * @return
     */
    String toJSON(eu.ddmore.mdl.mdl.Mcl mcl) {

        JsonBuilder jb = new JsonBuilder()

        String ret = null
        Mcl f = new Mcl(mcl)
        jb f
        ret = jb.toString()

        return ret;
    }

    private String computeOutputFileName(String name) {
        int dotIndex = name.lastIndexOf(MDL_FILE_EXTENSION)

        if (dotIndex == -1) {
            return name + JSON_FILE_EXTENSION
        } else {
            return name.substring(0, dotIndex) + JSON_FILE_EXTENSION
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
        return String.format("MDL to JSON Converter [source=%s, target=%s, converterVersion=%s]", source, target, converterVersion)
    }
    
}
