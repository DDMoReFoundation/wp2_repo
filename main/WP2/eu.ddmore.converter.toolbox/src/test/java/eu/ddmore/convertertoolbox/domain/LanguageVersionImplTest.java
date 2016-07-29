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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import org.junit.Before;
import org.junit.Test;

import eu.ddmore.convertertoolbox.api.domain.LanguageVersion;
import eu.ddmore.convertertoolbox.api.domain.Version;

/**
 * Test for {@link LanguageVersionImpl}.
 */
public class LanguageVersionImplTest {

    private LanguageVersion nonmem72;

    @Before
    public void initialize() {
        Version nonmem72Vers = new VersionImpl(7, 2, 0, "qual");
        nonmem72 = new LanguageVersionImpl("NMTRAN", nonmem72Vers);
    }

    @Test
    public void shouldBeEqual() {
        Version nonmem72bVers = new VersionImpl(7, 2, 0, "qual");
        LanguageVersion nonmem72b = new LanguageVersionImpl("NMTRAN", nonmem72bVers);

        assertEquals(nonmem72, nonmem72b);
        assertEquals(nonmem72.hashCode(), nonmem72b.hashCode());
    }

    @Test
    public void shouldNotBeEqualLowerCase() {
        Version nonmem72bVers = new VersionImpl(7, 2, 0, "qual");
        LanguageVersion nonmem72b = new LanguageVersionImpl("nmtran", nonmem72bVers);

        assertNotEquals(nonmem72, nonmem72b);
    }

    @Test
    public void shouldNotBeEqual() {
        Version nonmem71Vers = new VersionImpl(7, 1, 0, "qual");
        LanguageVersion nonmem71 = new LanguageVersionImpl("NMTRAN", nonmem71Vers);

        assertNotEquals(nonmem71, nonmem72);
    }

}
