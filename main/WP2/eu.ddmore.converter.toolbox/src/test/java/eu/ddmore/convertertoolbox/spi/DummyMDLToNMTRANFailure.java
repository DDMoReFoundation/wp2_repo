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
package eu.ddmore.convertertoolbox.spi;

import java.io.File;
import java.io.IOException;

import eu.ddmore.convertertoolbox.api.domain.LanguageVersion;
import eu.ddmore.convertertoolbox.api.domain.Version;
import eu.ddmore.convertertoolbox.api.response.ConversionReport;
import eu.ddmore.convertertoolbox.api.spi.ConverterProvider;

/**
 * Interface which Converter providers should implement to enable them to be
 * called by clients of the Converter Toolbox.
 */
public class DummyMDLToNMTRANFailure implements ConverterProvider {

    private LanguageVersion source;
    private LanguageVersion target;
    private Version converterVersion;

    @Override
    public ConversionReport performConvertToFile(File src, File tgtFile) throws IOException {
        throw new IOException("DummyMDLToNMTRANFailure");
    }

    @Override
    public ConversionReport performConvert(File src, File outputDirectory) throws IOException {
        throw new IOException("DummyMDLToNMTRANFailure");
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

    public void setSource(LanguageVersion source) {
        this.source = source;
    }

    public void setTarget(LanguageVersion target) {
        this.target = target;
    }

    public void setConverterVersion(Version converterVersion) {
        this.converterVersion = converterVersion;
    }

    @Override
    public String toString() {
        return String.format("DummyMDLToNMTRANFailureConverter [source=%s, target=%s, converterVersion=%s]", source, target, converterVersion);
    }
}
