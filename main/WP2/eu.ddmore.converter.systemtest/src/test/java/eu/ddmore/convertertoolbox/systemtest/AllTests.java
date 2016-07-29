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
package eu.ddmore.convertertoolbox.systemtest;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;
import org.junit.runners.Suite.SuiteClasses;


/**
 * Test Suite to run the different conversion tests e.g. MDL to PharmML, PharmML to NMTRAN.
 * <p>
 * The order is important since the conversion tests for the generated PharmML Models use the
 * output from the conversion tests for the MDL Models.
 */
@RunWith(Suite.class)
@SuiteClasses({
    MdlToPharmmlModelsTest.class,
    PharmmlToNmtranModelsTest.class,
    MdlToJsonModelsTest.class,
    MdlToJsonToMdlModelsTest.class,
    GeneratedPharmmlToNmtranModelsTest.class
})
public class AllTests {}
