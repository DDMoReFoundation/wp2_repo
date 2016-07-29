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

/**
 * Interface defining the functionality to heuristically and generically check the output from a
 * converter execution, i.e. the generated model file, the standard output file and the standard
 * error file, to attempt to determine if the conversion process failed.
 * <p>
 * <b>Note that even if this checking determines that the process didn't fail, it doesn't mean
 * that the conversion was actually successful, since garbage could have been written out to the
 * generated model file with no errors produced, and this wouldn't know any different!</b>
 */
public interface ConverterOutputFailureChecker {

    /**
     * Heuristically check if the conversion did not fail.
     * <p>
     * If the conversion failed then fail the calling test; else the conversion <i>might</i>
     * have been successful so just return.
     * <p>
     * @param expectedOutputFile - the model {@link File} that should be created from the conversion process
     * @param stdoutFile - {@link File} to which the stdout was piped
     * @param stderrFile - {@link File} to which the stderr was piped
     */
    public void check(final File expectedOutputFile, final File stdoutFile, final File stderrFile);
    
}
