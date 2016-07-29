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

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import eu.ddmore.convertertoolbox.api.domain.Version;

/**
 * Test for {@link VersionImpl}.
 */
public class VersionImplTest {

    private Version nonmem72Vers;

    @Before
    public void initialize() {
        nonmem72Vers = new VersionImpl(7, 2, 3, "qual");
    }

    @Test
    public void shouldBeEqual() {
        Version nonmem72bVers = new VersionImpl(7, 2, 3, "qual");

        assertEquals(nonmem72Vers, nonmem72bVers);
        assertEquals(nonmem72Vers.hashCode(), nonmem72bVers.hashCode());
    }

    @Test
    public void shouldNotBeEqual() {
        Version nonmem71Vers = new VersionImpl(7, 1, 3);
        assertNotEquals(nonmem71Vers, nonmem72Vers);
    }

    @Test
    public void shouldBeLatestVersion() {
        Version nonmem71Vers = new VersionImpl(7, 1, 3);
        assertTrue(nonmem72Vers.compareTo(nonmem71Vers) > 0);
    }

}
