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
package eu.ddmore.convertertoolbox.service.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Optional;

import eu.ddmore.convertertoolbox.domain.ConversionStatus;
import eu.ddmore.convertertoolbox.domain.internal.Conversion;
import eu.ddmore.convertertoolbox.rest.ConversionTestFixturesHelper;
import eu.ddmore.convertertoolbox.service.ExceededCapacity;

/**
 * Tests {@link MapBackedConversionRepository}
 */
public class MapBackedConversionRepositoryTest {

    private MapBackedConversionRepository instance = new MapBackedConversionRepository();
    @Before
    public void setUp() throws Exception {
        instance.save(createTestConversion("1", "FROM", "TO", "mock/input/file", ConversionStatus.Completed,1));
        instance.save(createTestConversion("2", "FROM", "TO", "mock/input/file", ConversionStatus.Completed,2));
        instance.save(createTestConversion("3", "FROM", "TO", "mock/input/file", ConversionStatus.Completed,3));
        instance.save(createTestConversion("4", "FROM", "TO", "mock/input/file", ConversionStatus.Scheduled,0));
        instance.save(createTestConversion("5", "FROM", "TO", "mock/input/file", ConversionStatus.Scheduled,0));
        instance.save(createTestConversion("6", "FROM", "TO", "mock/input/file", ConversionStatus.Scheduled,0));
    }

    @Test(expected = NullPointerException.class)
    public void delete_shouldThrowRuntimeExceptionForNullConversion() {
        instance.delete(null);
    }

    @Test
    public void delete_shouldDeleteConversionWithGivenId() {
    	assertTrue(instance.getConversion("2").isPresent());
        Conversion conversion = new Conversion();
        conversion.setId("2");
        instance.delete(conversion);
        assertFalse(instance.getConversion("2").isPresent());
    }

    @Test(expected = NullPointerException.class)
    public void save_shouldThrowRuntimeExceptionForNullConversion() {
        instance.save(null);
    }

    @Test
    public void save_shouldPersistTheConversionAndReturnTheInstance() {
        Conversion conversion = createTestConversion(null, "FROM", "TO", "mock/input/file", ConversionStatus.New, 0);
        Conversion result = instance.save(conversion);
        assertNotNull("Save should return not null Conversion instance", result);
        assertTrue("The result Conversion should have an id set", StringUtils.isNotBlank(result.getId()));
    }
    
    @Test
    public void getConversions_shouldReturnCollectionOfAllConversions() throws ExceededCapacity {
        assertEquals(instance.getConversions().size(),6);
    }

    @Test
    public void getCompletedConversions_shouldReturnACollectionOfCompletedConversions() throws ExceededCapacity {
        assertEquals(instance.getConversionsWithStatus(ConversionStatus.Completed).size(),3);
    }

    @Test
    public void getConversionForId_shouldReturnOptionalWithConversionForSpecifiedId() throws ExceededCapacity {
        Conversion existingConversion = instance.getConversions().iterator().next();
        Optional<Conversion> conversion = instance.getConversion(existingConversion.getId());
        assertTrue(conversion.isPresent());
        assertEquals(existingConversion, conversion.get());
    }

    @Test
    public void getConversionForId_shouldReturnOptionalWithNoConversionIfSpecifiedIdDoesntMatchConversion() {
        Optional<Conversion> conversion = instance.getConversion("non-existing-conversion");
        assertFalse(conversion.isPresent());
    }
    

    @Test
    public void getConversionsCompletedEarlierThan_shouldReturnConversionsCompletedBeforeGivenDate() {
        assertEquals(instance.getConversionsCompletedEarlierThan(3).size(),2);
    }
    

    @Test
    public void countIncompletedConversions_shouldReturnTheNumberOfNotCompletedConversions() {
        assertEquals(instance.countIncompleteConversions(),3);
    }

    private Conversion createTestConversion(String id, String form, String to, String inputFile, ConversionStatus status, long completionTime) {
        Conversion conversion = ConversionTestFixturesHelper.createInternalTestConversion(id, form, to, inputFile, status);
        if(ConversionStatus.Completed.equals(status)) {
                conversion.setCompletionTime(completionTime);
        }
        return conversion;
    }
    
}
