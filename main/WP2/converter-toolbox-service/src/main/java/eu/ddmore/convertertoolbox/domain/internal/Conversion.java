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
package eu.ddmore.convertertoolbox.domain.internal;

import java.io.File;

/**
 * Conversion resource
 */
public class Conversion extends eu.ddmore.convertertoolbox.domain.Conversion {
    private transient File inputArchive;
    private transient File outputArchive;
    private transient File workingDirectory;

    /**
     * Empty constructor
     */
    public Conversion() {
        super();
    }
    /**
     * Creates a new instance by copying state of the other conversion instance
     * @param other
     */
    public Conversion(Conversion other) {
        super();
        this.setId(other.getId());
        this.setStatus(other.getStatus());
        this.setFrom(other.getFrom());
        this.setTo(other.getTo());
        this.setInputFileName(other.getInputFileName());
        this.setOutputFileSize(other.getOutputFileSize());
        this.setSubmissionTime(other.getSubmissionTime());
        this.setCompletionTime(other.getCompletionTime());
        this.setInputArchive(other.inputArchive);
        this.setOutputArchive(other.outputArchive);
        this.setWorkingDirectory(other.workingDirectory);
        //FIXME we must ensure that this is immutable
        this.setConversionReport(other.getConversionReport());
    }


    public File getOutputArchive() {
        return outputArchive;
    }

    public void setOutputArchive(File outputArchive) {
        this.outputArchive = outputArchive;
    }

    public File getInputArchive() {
        return inputArchive;
    }

    public void setInputArchive(File inputArchive) {
        this.inputArchive = inputArchive;
    }

    public void setWorkingDirectory(File workingDirectory) {
        this.workingDirectory = workingDirectory;
    }

    public File getWorkingDirectory() {
        return workingDirectory;
    }
}
