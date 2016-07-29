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

/**
 * File type information enum for file types used during conversion testing.
 * It contains filetype default extension and version associated with a file type.
 */
public enum FileType {
    MDL("mdl","8.0.0"),
    PharmML("xml","0.8.1"),
    PharmML_060("xml","0.6.0"),
    NMTRAN("ctl","7.3.0"),
    JSON("json","8.0.0");
    
    private String version;
    private String extension;
    
    FileType(String extension, String version){
        this.extension = extension;
        this.version = version;
    }

    public String getExtension(){
        return this.extension;
    }
    
    public String getVersion(){
        return this.version;
    }
    
}
