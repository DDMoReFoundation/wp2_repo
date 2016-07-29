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

import java.io.File;

import eu.ddmore.converter.mdl2json.testutils.MdlFileContentTestUtils;

/**
 * A Converter Output Failure Checker for JSON->MDL conversion; it enhances the standard Converter Output
 * Failure Checker to also check for syntactic and semantic equivalence against a baseline MDL file.
 */
class MdlFileEquivalenceChecker extends DefaultConverterOutputFailureChecker {

    // We'll consider a conversion to have failed if the converted output file has a size that is less than this number of bytes.
    // This is derived from the following minimal skeleton MDL file:
    // d=dataobj{}
    // p=parobj{}
    // m=mdlobj{}
    // t=taskobj{}
    private final static int MDL_FILE_SIZE_THRESHOLD = 45;
    
    private final File baselineMdlFile;

    /**
     * Constructor.
     * @param baselineMdlFile - the baseline MDL file against which to check the generated MDL file
     */
    MdlFileEquivalenceChecker(final File baselineMdlFile) {
        super(MDL_FILE_SIZE_THRESHOLD);
        this.baselineMdlFile = baselineMdlFile;
    }

    @Override
    public void check(File expectedOutputMdlFile, File stdoutFile, File stderrFile) {
        super.check(expectedOutputMdlFile, stdoutFile, stderrFile);
        for (final String blockName : MdlFileContentTestUtils.ALL_BLOCK_NAMES) {
            MdlFileContentTestUtils.assertMDLBlockEqualityIgnoringWhitespaceAndComments(this.baselineMdlFile, blockName, expectedOutputMdlFile);
        }
    }

}
