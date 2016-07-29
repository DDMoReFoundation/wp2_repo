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
package eu.ddmore.convertertoolbox.domain.hal;

import java.util.Collection;

import javax.xml.bind.annotation.XmlAnyElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import org.springframework.hateoas.Resources;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;


/**
 * HAL-enabled collection of { @link ConversionResource }
 */
public class ConversionResources extends Resources<ConversionResource>{
    private final Collection<ConversionResource> content;

    @JsonCreator
    public ConversionResources(@JsonProperty("content") Collection<ConversionResource> content) {
        super();
        this.content = content;
    }

    @Override
    @XmlAnyElement
    @XmlElementWrapper
    @JsonProperty("content")
    public Collection<ConversionResource> getContent() {
        return content;
    }
}
