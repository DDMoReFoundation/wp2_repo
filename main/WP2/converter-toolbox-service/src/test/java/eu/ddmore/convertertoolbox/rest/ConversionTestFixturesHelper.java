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
package eu.ddmore.convertertoolbox.rest;

import java.util.ArrayList;
import java.util.Collection;

import eu.ddmore.convertertoolbox.domain.Conversion;
import eu.ddmore.convertertoolbox.domain.ConversionStatus;
import eu.ddmore.convertertoolbox.domain.LanguageVersion;
import eu.ddmore.convertertoolbox.domain.Version;

/**
 * Helper class used by REST endpoints Integration Tests
 */
public class ConversionTestFixturesHelper {
    
    /**
     * Creates a { @link LanguageVersion } instance with the given name
     * @param languageName
     * @return language version
     */
    public static LanguageVersion from(String languageName) {
        return to(languageName).iterator().next();
    }

    /**
     * Creates a collection of { @link LanguageVersion } with given names
     * @param languageNames
     * @return
     */
    public static Collection<LanguageVersion> to(String... languageNames) {
        Collection<LanguageVersion> result = new ArrayList<LanguageVersion>();
        for(String language : languageNames) {
            result.add(new LanguageVersion(language, new Version(1,0,0,"Q")));
        }
        return result;
    }

    /**
     * Creates a test external representation of conversion with the given:
     * @param id
     * @param form
     * @param to
     * @param inputFile
     * @param status
     * @return new conversion instance
     */
    public static Conversion createExternalTestConversion(String id, String form, String to, String inputFile, ConversionStatus status) {
        Conversion conversion = new Conversion();
        conversion.setId(id);
        conversion.setFrom(new LanguageVersion(form,new Version(1, 0, 0,"Q")));
        conversion.setTo(new LanguageVersion(to,new Version(1, 0, 0,"Q")));
        conversion.setInputFileName(inputFile);
        conversion.setStatus(status);
        return conversion;
    }
    /**
     * Creates a test internal representation of conversion with the given:
     * @param id
     * @param form
     * @param to
     * @param inputFile
     * @param status
     * @return new conversion instance
     */
    public static eu.ddmore.convertertoolbox.domain.internal.Conversion createInternalTestConversion(String id, String form, String to, String inputFile, ConversionStatus status) {
        eu.ddmore.convertertoolbox.domain.internal.Conversion conversion = new eu.ddmore.convertertoolbox.domain.internal.Conversion();
        conversion.setId(id);
        conversion.setFrom(new LanguageVersion(form,new Version(1, 0, 0,"Q")));
        conversion.setTo(new LanguageVersion(to,new Version(1, 0, 0,"Q")));
        conversion.setInputFileName(inputFile);
        conversion.setStatus(status);
        return conversion;
    }
}
