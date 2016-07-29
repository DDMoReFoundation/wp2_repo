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
package eu.ddmore.converter.mdl2json.interfaces;

/**
 * Interface to be implemented by the top-level blocks in an MDL file: Data, Parameter, Model, Task, Mog.
 */
public interface TopLevelBlock {

    /**
     * @return {@link TopLevelBlock.Identifier} enum value that identifies this top-level block;
     *         it is the string that tags the top-level block in the MDL file
     */
    Identifier getIdentifier();

    /**
     * Enumerates the top-level blocks in an MDL file. The values are the strings that tag the
     * top-level blocks in the MDL file.
     * <p>
     * The order in which the identifier names appear here is important in that this defines
     * the order in which the blocks will appear in a written out MDL file; the top-level blocks
     * are sorted into this order (using the {@link Enum#ordinal()) method on each enum value)
     * before being written out.
     */
    enum Identifier {
        dataobj,
        parobj,
        mdlobj,
        taskobj,
        mogobj // Must appear last in this list - see the JavaDoc comment
    }

}
